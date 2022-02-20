package com.benkao.tictactoe.storage

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.rxjava3.RxDataStore
import com.benkao.tictactoe.utils.PreferencesUtils
import com.benkao.tictactoe.utils.StringUtils
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

class UserPreferences(
    private val dataStore: RxDataStore<Preferences>
) {

    fun setUserId(userId: String): Completable =
        dataStore.updateDataAsync {
            val mutablePreferences = it.toMutablePreferences()
            mutablePreferences[PreferencesUtils.USER_ID_KEY] = userId
            Single.just(mutablePreferences)
        }.ignoreElement()

    fun clearUserId(): Completable =
        dataStore.updateDataAsync {
            val mutablePreferences = it.toMutablePreferences()
            mutablePreferences.remove(PreferencesUtils.USER_ID_KEY)
            Single.just(mutablePreferences)
        }.ignoreElement()

    fun getUserId(): Single<String> =
        dataStore.data()
            .map { it[PreferencesUtils.USER_ID_KEY].orEmpty() }
            .first(StringUtils.EMPTY)
}