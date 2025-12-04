package com.cs407.unify.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun RegistrationPage(
    uid: String,
    onRegistrationComplete: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var university by remember { mutableStateOf("") }
    var securityAnswer by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val currentUser = FirebaseAuth.getInstance().currentUser
    val email = currentUser?.email ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Registration", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = university,
            onValueChange = { university = it },
            label = { Text("University") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = securityAnswer,
            onValueChange = { securityAnswer = it },
            label = { Text("What is your mother's maiden name?") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Email: $email")

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (isLoading) return@Button

                if (username.isBlank() || university.isBlank() || securityAnswer.isBlank()) {
                    error = "Please fill in all fields."
                    return@Button
                }

                isLoading = true
                error = null

                val db = FirebaseFirestore.getInstance()

                // First, check if username already exists
                db.collection("users")
                    .whereEqualTo("username", username)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (!querySnapshot.isEmpty) {
                            // Username already exists
                            isLoading = false
                            error = "Username taken"
                        } else {
                            // Username is unique, proceed with registration
                            val profileData = hashMapOf(
                                "uid" to uid,
                                "email" to email,
                                "username" to username,
                                "university" to university,
                                "securityAnswer" to securityAnswer
                            )

                            db.collection("users")
                                .document(uid)
                                .set(profileData)
                                .addOnCompleteListener { task ->
                                    isLoading = false

                                    if (task.isSuccessful) {
                                        onRegistrationComplete()
                                    } else {
                                        error = task.exception?.message ?: "Failed to save profile."
                                    }
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        isLoading = false
                        error = "Failed to check username availability: ${e.message}"
                    }
            },
            enabled = !isLoading && username.isNotBlank() && university.isNotBlank() && securityAnswer.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isLoading) "Saving..." else "Finish")
        }
    }
}