package com.benkao.tictactoe.di.modules

import androidx.lifecycle.ViewModel
import com.benkao.tictactoe.ui.login.LoginViewModel
import com.benkao.tictactoe.di.core.ViewModelKey
import com.benkao.tictactoe.network.retrofit.api.ReqresApi
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
    fun provideReqresApi(retrofit: Retrofit): ReqresApi {
        return retrofit.create(ReqresApi::class.java)
    }

    @Provides
    fun provideLoginService(reqresApi: ReqresApi): LoginService {
        return LoginServiceImpl(reqresApi)
    }
}