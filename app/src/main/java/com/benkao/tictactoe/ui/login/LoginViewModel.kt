package com.benkao.tictactoe.ui.login

import com.benkao.annotations.InitToClear
import com.benkao.annotations.LifecycleViewModel
import com.benkao.annotations.StartToStop
import com.benkao.tictactoe.R
import com.benkao.tictactoe.network.retrofit.model.LoginRequest
import com.benkao.tictactoe.network.retrofit.service.LoginService
import com.benkao.tictactoe.storage.UserPreferences
import com.benkao.tictactoe.ui.base.*
import com.benkao.tictactoe.ui.main.HomeActivity
import com.benkao.tictactoe.utils.StringUtils
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import java.net.SocketTimeoutException

@LifecycleViewModel
class LoginViewModel(
    private val service: LoginService,
    private val userPreferences: UserPreferences,
    screenNavigator: ScreenNavigator,
    viewCollector: RxViewCollector
): RxViewModel(screenNavigator, viewCollector) {

    override val streams: LifecycleStreams
        get() = LoginViewModel_LifecycleStreamsFactory.create(this)

    private val usernameText = viewCollector.addView(R.id.input_email_text, RxEditText::class)
    private val passwordText = viewCollector.addView(R.id.input_password_text, RxEditText::class)
    private val loginButtonObs = viewCollector.addView(R.id.login_button, RxButton::class)
    private val resultTextObs = viewCollector.addView(R.id.login_error_text, RxTextView::class)

    @InitToClear
    fun observeLoginClick(): Completable = Single.zip(
        usernameText,
        passwordText,
        loginButtonObs,
        resultTextObs,
    ) { userName, password, button, error ->
        LoginViews(userName, password, button, error) }
        .flatMapCompletable { views ->
            views.loginButton.observeClick()
                .switchMapCompletable {
                    Timber.d("Login button click")
                    handleButtonClick(views)
                }
        }

    private fun handleButtonClick(
        views: LoginViews
    ): Completable = Single.zip(
        views.usernameText.observeText().first(StringUtils.EMPTY),
        views.passwordText.observeText().first(StringUtils.EMPTY))
    { userName, password -> LoginRequest(userName, password) }
        .doOnSuccess { hideKeyboard() }
        .flatMapCompletable { loginRequest ->
            getInputError(loginRequest)
                ?.run { Completable.fromAction {
                    showErrorMessage(views.resultText, this)
                } }
                ?: Completable.mergeArray(
                    attemptLogin(),
                    Completable.fromAction{ showLoadingUi(views, true) }
                )
                    .doOnError {
                        showLoadingUi(views, false)
                        Timber.e(it.message)
                        showErrorMessage(
                            views.resultText,
                            when (it) {
                                is SocketTimeoutException -> "Login timed out"
                                else -> "Login failed!"
                            }
                        )
                    }
                    .onErrorComplete()
        }

    private fun showLoadingUi(
        views: LoginViews,
        loading: Boolean
    ) {
        views.usernameText.setEnabled(!loading)
        views.passwordText.setEnabled(!loading)
        views.loginButton.setEnabled(!loading)
        views.resultText.run {
            setVisible(loading)
            if (loading) {
                setText("Attempting to log in...")
            }
        }
    }

    private fun attemptLogin(): Completable =
        service.login()
            .doOnSuccess { Timber.d("Login id is: $it") }
            .flatMapCompletable { userPreferences.setUserId(it) }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                //navigate to home screen
                screenNavigator
                    .planActivity(HomeActivity::class)
                    .start(true)
            }

    private fun showErrorMessage(
        errorTV: RxTextView,
        message: String
    ) =
        errorTV.run {
            setVisible(true)
            setText(message)
        }

    private fun getInputError(loginRequest: LoginRequest): String? =
        when {
            loginRequest.username.isBlank() -> "Username can't be blank"
            loginRequest.password.isBlank() -> "Password can't be empty"
            else -> null
        }

    @StartToStop
    fun observeInputTextFocus(): Completable =
        Single.zip(
            usernameText,
            passwordText,
            resultTextObs
        ) { username, password, error -> Triple(username, password, error) }
            .flatMapCompletable { texts ->
                Observable.mergeArray(
                    texts.first.observeFocus(),
                    texts.second.observeFocus()
                ).filter { it }
                    .doOnNext { texts.third.setVisible(false) }
                    .ignoreElements()
            }

}

data class LoginViews(
    val usernameText: RxEditText,
    val passwordText: RxEditText,
    val loginButton: RxButton,
    val resultText: RxTextView
)