package com.benkao.tictactoe.utils

object LoginUtils {
    fun getInputError(username: String, password: String): String
    = when {
        (username.isBlank()) -> "Username can't be blank"
        (password.isBlank()) -> "Password can't be empty"
        else -> StringUtils.EMPTY
    }
}