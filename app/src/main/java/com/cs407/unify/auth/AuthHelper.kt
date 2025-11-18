package com.cs407.unify.auth

import com.google.firebase.auth.FirebaseAuth

// ============================================
// Email Validation
// ============================================

enum class EmailResult {
    Valid,
    Empty,
    Invalid,
}

fun checkEmail(email: String): EmailResult {
    if (email.isEmpty()) return EmailResult.Empty

    val pattern = Regex("^[\\w.]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")
    return if (pattern.matches(email)) EmailResult.Valid else EmailResult.Invalid
}

// ============================================
// Password Validation
// ============================================

enum class PasswordResult {
    Valid,
    Empty,
    Short,
    Invalid
}

fun checkPassword(password: String): PasswordResult {
    if (password.isEmpty()) return PasswordResult.Empty
    if (password.length < 5) return PasswordResult.Short
    if (Regex("\\d+").containsMatchIn(password) &&
        Regex("[a-z]+").containsMatchIn(password) &&
        Regex("[A-Z]+").containsMatchIn(password)
    ) {
        return PasswordResult.Valid
    }
    return PasswordResult.Invalid
}

// ============================================
// Firebase Authentication Functions
// ============================================

fun signIn(
    email: String,
    password: String,
    onExistingUser: (String) -> Unit,
    onNewUser: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    val auth = FirebaseAuth.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    onExistingUser(user.uid)
                } else {
                    onFailure("User not found after sign-in.")
                }
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { createTask ->
                        if (createTask.isSuccessful) {
                            val newUser = auth.currentUser
                            if (newUser != null) {
                                onNewUser(newUser.uid)
                            } else {
                                onFailure("Account created, but user is null.")
                            }
                        } else {
                            onFailure(
                                createTask.exception?.message
                                    ?: "Account creation failed."
                            )
                        }
                    }
            }
        }
}
