package com.cs407.unify.auth

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task

/**
 * MILESTONE 2: Authentication Helper
 * Contains all business logic for Firebase authentication
 */

// ============================================
// Email Validation
// ============================================

enum class EmailResult {
    Valid,
    Empty,
    Invalid,
}

fun checkEmail(email: String): EmailResult {
    if (email.isEmpty()){
        return EmailResult.Empty
    }

    // 1. username of email should only contain "0-9, a-z, _, A-Z, ."
    // 2. there is one and only one "@" between username and server address
    // 3. there are multiple domain names with at least one top-level domain
    // 4. domain name "0-9, a-z, -, A-Z" (could not have "_" but "-" is valid)
    // 5. multiple domain separate with '.'
    // 6. top level domain should only contain letters and at lest 2 letters
    // this email check only valid for this course
    val pattern = Regex("^[\\w.]+@([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}$")
    return if (pattern.matches(email)) {
        EmailResult.Valid
    } else {
        EmailResult.Invalid
    }

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
    // 1. password should contain at least one uppercase letter, lowercase letter, one digit
    // 2. minimum length: 5
    if (password.isEmpty()) {
        return PasswordResult.Empty
    }
    if (password.length < 5) {
        return PasswordResult.Short
    }
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

/**
 * Sign in existing user with email and password
 * If sign-in fails, automatically attempts to create new account
 */
fun signIn(
    email: String,
    password: String,
    onSuccess: (String) -> Unit,
    onFailure: (String) -> Unit
) {
    val auth = Firebase.auth

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //sign in success
                //get current user from the response and propagate it
                val user = auth.currentUser
                if (user != null) {
                    onSuccess(user.uid)
                }
            } else {
                //sign in failed, try creating account
                //call createAccount function
                createAccount(email, password, onSuccess, onFailure)
            }
        }
}

/**
 * Create new Firebase account with email and password
 */
fun createAccount(
    email: String,
    password: String,
    onSuccess: (String) -> Unit,  //callback with Firebase UID on success
    onFailure: (String) -> Unit   //callback with error message on failure
) {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    auth.createUserWithEmailAndPassword(email, password)
        .addOnSuccessListener { result ->
            val user = auth.currentUser
            //logic for success response
            if (user != null) {
                onSuccess(user.uid)  //pass Firebase UID to the success callback
            }
        }
        .addOnFailureListener { exception ->
            //error in creation of account
            onFailure(exception.message ?: "Account creation failed")
        }
}

/**
 * Update Firebase Auth displayName
 * Used in Milestone 3 for username collection
 */
fun updateName(name: String) {
    val user = Firebase.auth.currentUser

    val profileUpdates = userProfileChangeRequest {
        displayName = name
    }

    user!!.updateProfile(profileUpdates)

    //TODO create a request object to update the display name and then call updateProfile() function
}
