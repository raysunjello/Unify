package com.cs407.unify.ui.screens.login

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ForgotPasswordPage(
    onNavigateBack: () -> Unit,
) {
    var usernameOrEmail by remember { mutableStateOf("") }
    var maidenName by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Back arrow
        IconButton(
            onClick = onNavigateBack,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back to Login"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Reset Password",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Username or Email field
            OutlinedTextField(
                value = usernameOrEmail,
                onValueChange = { usernameOrEmail = it },
                label = { Text("Username or Email") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )

            // Mother's maiden name field
            OutlinedTextField(
                value = maidenName,
                onValueChange = { maidenName = it },
                label = { Text("Mother's Maiden Name") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Error message
            error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Reset Password button
            Button(
                onClick = {
                    if (isLoading) return@Button

                    error = null
                    isLoading = true

                    val db = FirebaseFirestore.getInstance()

                    // Query by email first
                    db.collection("users")
                        .whereEqualTo("email", usernameOrEmail)
                        .get()
                        .addOnSuccessListener { emailSnapshot ->
                            if (!emailSnapshot.isEmpty) {
                                // Found by email
                                val doc = emailSnapshot.documents[0]
                                verifyAndResetPassword(
                                    doc.getString("email") ?: "",
                                    doc.getString("securityAnswer") ?: "",
                                    maidenName,
                                    context,
                                    onError = { msg ->
                                        isLoading = false
                                        error = msg
                                    }
                                )
                            } else {
                                // Try finding by username
                                db.collection("users")
                                    .whereEqualTo("username", usernameOrEmail)
                                    .get()
                                    .addOnSuccessListener { usernameSnapshot ->
                                        if (!usernameSnapshot.isEmpty) {
                                            val doc = usernameSnapshot.documents[0]
                                            verifyAndResetPassword(
                                                doc.getString("email") ?: "",
                                                doc.getString("securityAnswer") ?: "",
                                                maidenName,
                                                context,
                                                onError = { msg ->
                                                    isLoading = false
                                                    error = msg
                                                }
                                            )
                                        } else {
                                            isLoading = false
                                            error = "Username or email not found"
                                        }
                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        error = "Error verifying username"
                                    }
                            }
                        }
                        .addOnFailureListener {
                            isLoading = false
                            error = "Error verifying email"
                        }
                },
                enabled = !isLoading &&
                        usernameOrEmail.isNotBlank() &&
                        maidenName.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    text = if (isLoading) "Resetting..." else "Reset Password",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun verifyAndResetPassword(
    email: String,
    storedSecurityAnswer: String,
    inputMaidenName: String,
    context: android.content.Context,
    onError: (String) -> Unit
) {
    // Verify security answer (case-insensitive comparison)
    if (storedSecurityAnswer.trim().lowercase() != inputMaidenName.trim().lowercase()) {
        onError("Mother's maiden name is incorrect")
        return
    }

    val auth = FirebaseAuth.getInstance()

    // Security answer verified, send password reset email
    auth.sendPasswordResetEmail(email)
        .addOnSuccessListener {
            Toast.makeText(
                context,
                "Identity verified! Password reset email sent.",
                Toast.LENGTH_LONG
            ).show()

            onError("Please check your email to reset your password.")
        }
        .addOnFailureListener { emailError ->
            onError("Verification successful, but failed to send reset email: ${emailError.message}")
        }
}