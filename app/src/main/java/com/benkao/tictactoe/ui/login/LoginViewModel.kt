package com.benkao.tictactoe.ui.login

import com.benkao.annotations.InitToClear
import com.benkao.annotations.LifecycleViewModel
import com.benkao.annotations.StartToStop
import com.benkao.tictactoe.R
import com.benkao.tictactoe.ui.base.*
import com.benkao.tictactoe.utils.StringUtils
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@LifecycleViewModel
class LoginViewModel(
    viewCollector: RxViewCollector
): RxViewModel(viewCollector) {

    override val streams: LifecycleStreams
        get() = LoginViewModel_LifecycleStreamsFactory.create(this)

    private val usernameText = viewCollector.addView(R.id.input_email_text, RxEditText::class)
    private val passwordText = viewCollector.addView(R.id.input_password_text, RxEditText::class)
    private val loginButton = viewCollector.addView(R.id.login_button, RxButton::class)
    private val errorText = viewCollector.addView(R.id.login_error_text, RxTextView::class)

    @InitToClear
    fun observeLoginClick(): Completable = Single.zip(
        usernameText,
        passwordText,
        loginButton,
        errorText
    ) { userName, password, button, error -> LoginViews(userName, password, button, error) }
        .flatMapCompletable { views ->
            views.loginButton.observeClick()
                .switchMapCompletable {
                    handleButtonClick(views)
                }
        }

    private fun handleButtonClick(
        views: LoginViews
    ): Completable = Single.zip(
        views.username.observeText().first(StringUtils.EMPTY),
        views.password.observeText().first(StringUtils.EMPTY))
    { userName, password -> Pair(userName, password) }
        .doOnSuccess { hideKeyboard() }
        .flatMapCompletable {
            getInputError(it)
                ?.run { showErrorMessage(views.errorText, this) }
                ?:  // send http request
                showErrorMessage(
                    views.errorText,
                    "UserName is \"${it.first}\". Password is \"${it.second}\""
                )
        }

    private fun showErrorMessage(
        errorTV: RxTextView,
        message: String
    ): Completable = Completable.fromAction {
        errorTV.run {
            setVisible(true)
            setText(message)
        }
    }

    private fun getInputError(loginPair: Pair<String, String>): String?
            = when {
        (loginPair.first.isBlank()) -> "Username can't be blank"
        (loginPair.second.isBlank()) -> "Password can't be empty"
        else -> null
    }

    @StartToStop
    fun observeInputTextFocus(): Completable = Single.zip(
        usernameText,
        passwordText,
        errorText
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
    val username: RxEditText,
    val password: RxEditText,
    val loginButton: RxButton,
    val errorText: RxTextView
)