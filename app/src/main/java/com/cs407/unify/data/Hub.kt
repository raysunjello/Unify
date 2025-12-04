package com.cs407.unify.data

data class Hub(
    val id: String = "",
    val name: String = "",
    val nameLowercase: String = "",
    val createdAt: Long = System.currentTimeMillis()
)