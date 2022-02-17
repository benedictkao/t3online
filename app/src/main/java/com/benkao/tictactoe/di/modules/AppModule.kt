package com.benkao.tictactoe.di.modules

import com.benkao.tictactoe.ui.base.RxViewCollector
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
object AppModule {
    private const val BASE_URL = "https://reqres.in/"

    @Provides
    fun provideRxViewCollector(): RxViewCollector {
        return RxViewCollector()
    }

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }
}