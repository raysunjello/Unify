package com.cs407.unify.data

data class Post(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val hub: String = "",
    val isAnonymous: Boolean = false,

    val authorUid: String = "",
    val authorUsername: String? = null,
    val authorUniversity: String? = null,

    val createdAt: Long = System.currentTimeMillis(),
    val comments: List<Comment> = emptyList()
)