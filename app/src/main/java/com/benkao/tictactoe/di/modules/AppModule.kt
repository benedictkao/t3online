package com.benkao.tictactoe.di.modules

import com.benkao.tictactoe.network.websocket.WebSocketProvider
import com.benkao.tictactoe.network.websocket.WebSocketProviderImpl
import com.benkao.tictactoe.ui.base.RxViewCollector
import com.benkao.tictactoe.utils.NetworkUtils
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    fun provideRxViewCollector(): RxViewCollector {
        return RxViewCollector()
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkUtils.HTTP_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideWebSocketProvider(
        client: OkHttpClient,
        request: Request
    ): WebSocketProvider {
        return WebSocketProviderImpl(client, request)
    }

    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    fun provideWebSocketRequest(): Request {
        return Request.Builder()
            .url(NetworkUtils.SOCKET_URL)
            .build()
    }
}