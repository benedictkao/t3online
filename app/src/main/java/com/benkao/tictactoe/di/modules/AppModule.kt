package com.benkao.tictactoe.di.modules

import android.app.Application
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder
import androidx.datastore.rxjava3.RxDataStore
import com.benkao.tictactoe.network.websocket.WebSocketProvider
import com.benkao.tictactoe.network.websocket.WebSocketProviderImpl
import com.benkao.tictactoe.storage.UserPreferences
import com.benkao.tictactoe.ui.base.ScreenNavigator
import com.benkao.tictactoe.ui.base.ScreenNavigatorImpl
import com.benkao.tictactoe.ui.base.RxViewCollector
import com.benkao.tictactoe.utils.NetworkUtils
import com.benkao.tictactoe.utils.PreferencesUtils
import dagger.Module
import dagger.Provides
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    fun provideRxViewCollector(): RxViewCollector {
        return RxViewCollector()
    }

    @Provides
    fun provideScreenNavigator(): ScreenNavigator {
        return ScreenNavigatorImpl()
    }

    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkUtils.HTTP_BASE_URL)
            .client(client)
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

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient {
        val connectionPool = ConnectionPool(10, 5L, TimeUnit.MINUTES)

        return OkHttpClient.Builder()
            .connectionPool(connectionPool)
            .build()
    }

    @Provides
    fun provideWebSocketRequest(): Request {
        return Request.Builder()
            .url(NetworkUtils.SOCKET_URL)
            .build()
    }

    @Singleton
    @Provides
    fun provideUserPreferences(dataStore: RxDataStore<Preferences>): UserPreferences {
        return UserPreferences(dataStore)
    }

    @Provides
    fun provideRxDataStore(application: Application): RxDataStore<Preferences> {
        return RxPreferenceDataStoreBuilder(
            application,
            PreferencesUtils.USER_PREFERENCES)
            .build()
    }
}