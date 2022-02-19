package com.benkao.tictactoe.di.modules

import androidx.lifecycle.ViewModel
import com.benkao.tictactoe.ui.login.LoginViewModel
import com.benkao.tictactoe.di.core.ViewModelKey
import com.benkao.tictactoe.network.retrofit.api.LoginApi
import com.benkao.tictactoe.network.retrofit.service.LoginService
import com.benkao.tictactoe.network.retrofit.service.LoginServiceImpl
import com.benkao.tictactoe.ui.base.RxViewCollector
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import retrofit2.Retrofit

@Module
object LoginModule {

    @Provides
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun provideLoginViewModel(
        service: LoginService,
        viewCollector: RxViewCollector
    ): ViewModel {
        return LoginViewModel(
            service,
            viewCollector
        )
    }

    @Provides
    fun provideLoginApi(retrofit: Retrofit): LoginApi {
        return retrofit.create(LoginApi::class.java)
    }

    @Provides
    fun provideLoginService(loginApi: LoginApi): LoginService {
        return LoginServiceImpl(loginApi)
    }
}