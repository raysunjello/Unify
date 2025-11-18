package com.cs407.unify.data

data class UserState(
    val uid: String = "",
    val email: String = "",
    val username: String = "",
    val university: String = "",
    val isLoggedIn: Boolean = false
)