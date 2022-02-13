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
    fun observeLogin(): Completable = Single.zip(
        usernameText,
        passwordText,
        loginButton,
        errorText
    ) { userName, password, button, error -> LoginViews(userName, password, button, error) }
        .flatMapCompletable { views ->
            views.loginButton.observeClick()
                .switchMapCompletable {
                    Single.zip(
                        views.userName.observeText().first(StringUtils.EMPTY),
                        views.password.observeText().first(StringUtils.EMPTY))
                    { userName, password -> Pair(userName, password) }
                        .doOnSuccess {
                            views.errorText.setVisible(true)
                            views.errorText.setText(
                                "UserName is \"${it.first}\". Password is \"${it.second}\""
                            )
                            hideKeyboard()
                        }
                        .ignoreElement()
                }
        }

    @StartToStop
    fun observeTextInputClick(): Completable = Single.zip(
        usernameText,
        passwordText,
        errorText
    ) { username, password, error -> Triple(username, password, error) }
        .flatMapCompletable { texts ->
            Observable.mergeArray(
                texts.first.observeClick(),
                texts.second.observeClick()
            ).doOnNext { texts.third.setVisible(false) }
                .ignoreElements()
        }

}

data class LoginViews(
    val userName: RxEditText,
    val password: RxEditText,
    val loginButton: RxButton,
    val errorText: RxTextView
)