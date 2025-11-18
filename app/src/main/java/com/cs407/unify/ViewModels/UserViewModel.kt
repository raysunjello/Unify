package com.cs407.unify.ViewModels

import androidx.lifecycle.ViewModel
import com.cs407.unify.data.UserState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserViewModel : ViewModel() {
    // Private mutable state (only ViewModel can modify)
    private val _userState = MutableStateFlow(UserState())

    // Firebase authentication instance
    private val auth: FirebaseAuth = Firebase.auth

    // Public read-only state (UI observes this)
    val userState = _userState.asStateFlow()

    init {
        // Listen for authentication state changes
        auth.addAuthStateListener { auth ->
            val user = auth.currentUser
            if (user == null) {
                // User logged out, reset state
                setUser(UserState())
            }
        }
    }

    // Function to update user state
    fun setUser(state: UserState) {
        _userState.update { state }
    }

    fun clearUser() {
        _userState.value = UserState()
    }
}