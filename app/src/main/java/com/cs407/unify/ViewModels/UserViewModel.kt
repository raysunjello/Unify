package com.cs407.unify.ViewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cs407.unify.data.UserState
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.cs407.unify.data.UserDao
import com.cs407.unify.data.DeleteDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(
    private val userDao: UserDao,
    private val deleteDao: DeleteDao
) : ViewModel() {
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

    /**
     * Logout:
     * 1. Get current UID from userstate
     * 2. Sign out from firebase
     * 3. Delete row from local DB
     * 4. Clear in-memory UserState
     */
    fun logout(){
        //1
        val uidToClear = _userState.value.uid

        //2
        auth.signOut()

        //3 (done on background thread)
        viewModelScope.launch(Dispatchers.IO) {
            if (uidToClear.isNotBlank()) {
                val user = userDao.getByUID(uidToClear)
                if (user != null) {
                    deleteDao.delete(user.userId)
                }
            }
        }

        //4
        clearUser()
    }
}