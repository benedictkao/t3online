package com.benkao.tictactoe.utils

import androidx.datastore.preferences.core.stringPreferencesKey

object PreferencesUtils {
    const val USER_PREFERENCES = "user_preferences"
    val USER_ID_KEY = stringPreferencesKey("user_id")
}