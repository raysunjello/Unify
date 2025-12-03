package com.cs407.unify.ui.screens.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.unify.R
import com.cs407.unify.ViewModels.UserViewModel
import com.google.firebase.auth.FirebaseAuth
import com.cs407.unify.auth.EmailResult
import com.cs407.unify.auth.PasswordResult
import com.cs407.unify.auth.checkEmail
import com.cs407.unify.auth.checkPassword
import com.cs407.unify.auth.signIn
import com.google.firebase.firestore.FirebaseFirestore
import com.cs407.unify.data.UserState
import com.cs407.unify.data.UserProfile

@Composable
fun LoginPage(
    userViewModel: UserViewModel,
    onNavigateToMainFeedPage: () -> Unit,
    onNavigateToRegistrationPage: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        //logo
        Image(
            painter = painterResource(id = R.drawable.unify_logo),
            contentDescription = "Unify Logo",
            modifier = Modifier
                .size(300.dp)
                .padding(bottom = 16.dp)
                .clip(CircleShape),
            contentScale = ContentScale.FillBounds,
            colorFilter = null
        )

        //username field
        TextField(
            value = email,
            onValueChange = { email = it },
            placeholder = {
                Text(
                    text = "Email",
                    color = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE8E8E8),
                unfocusedContainerColor = Color(0xFFE8E8E8),
                disabledContainerColor = Color(0xFFE8E8E8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        //password field
        TextField(
            value = password,
            onValueChange = { password = it },
            placeholder = {
                Text(
                    text = "Password...",
                    color = Color.Gray
                )
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE8E8E8),
                unfocusedContainerColor = Color(0xFFE8E8E8),
                disabledContainerColor = Color(0xFFE8E8E8),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        //Forgot password button
        Text(
            text = "Forgot Password?",
            color = Color.Black,
            modifier = Modifier.clickable{
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
            }
        )

        error?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Login/Signup button
        Button(
            onClick = {
                if (isLoading) return@Button


                val emailResult = checkEmail(email)
                if (emailResult != EmailResult.Valid) {
                    error = when (emailResult) {
                        EmailResult.Empty -> "Email cannot be empty"
                        EmailResult.Invalid -> "Please enter a valid email address"
                        else -> null
                    }
                    return@Button
                }


                val passwordResult = checkPassword(password)
                if (passwordResult != PasswordResult.Valid) {
                    error = when (passwordResult) {
                        PasswordResult.Empty -> "Password cannot be empty"
                        PasswordResult.Short -> "Password must be at least 5 characters"
                        PasswordResult.Invalid ->
                            "Password must contain upper, lower case letters and a digit"
                        PasswordResult.Valid -> null
                    }
                    return@Button
                }


                error = null
                isLoading = true

                signIn(
                    email = email,
                    password = password,
                    onExistingUser = { uid ->
                        isLoading = false

                        val auth = FirebaseAuth.getInstance()
                        val currentEmail = auth.currentUser?.email ?: email
                        val db = FirebaseFirestore.getInstance()

                        db.collection("users")
                            .document(uid)
                            .get()
                            .addOnSuccessListener { document ->
                                val profile = document.toObject(UserProfile::class.java)

                                if (profile != null) {
                                    userViewModel.setUser(
                                        UserState(
                                            uid = profile.uid,
                                            email = profile.email,
                                            username = profile.username,
                                            university = profile.university,
                                            isLoggedIn = true
                                        )
                                    )
                                } else {
                                    userViewModel.setUser(
                                        UserState(
                                            uid = uid,
                                            email = currentEmail,
                                            isLoggedIn = true
                                        )
                                    )
                                }

                                onNavigateToMainFeedPage()
                            }
                            .addOnFailureListener {
                                userViewModel.setUser(
                                    UserState(
                                        uid = uid,
                                        email = currentEmail,
                                        isLoggedIn = true
                                    )
                                )
                                onNavigateToMainFeedPage()
                            }
                    },
                    onNewUser = { uid ->
                        isLoading = false

                        val auth = FirebaseAuth.getInstance()
                        val currentEmail = auth.currentUser?.email ?: email
                        userViewModel.setUser(
                            UserState(
                                uid = uid,
                                email = currentEmail,
                                isLoggedIn = true
                            )
                        )

                        onNavigateToRegistrationPage(uid)
                    },
                    onFailure = { msg ->
                        isLoading = false
                        error = msg
                    }
                )
            },
            enabled = !isLoading,
            modifier = Modifier
                .width(250.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = if (isLoading) "Loading..." else "Login/Signup",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 2.sp
            )
        }
    }
}
