package com.cs407.unify.ui.screens

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

@Composable
fun LoginPage(onNavigateToMainFeedPage: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }


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
            value = username,
            onValueChange = { username = it },
            placeholder = {
                Text(
                    text = "Username...",
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
            modifier = Modifier.clickable{ /* TODO */}
        )

        Spacer(modifier = Modifier.height(16.dp))

        //Login/Signup button
        Button(
            onClick = { onNavigateToMainFeedPage() },
            modifier = Modifier
                .width(250.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {
            Text(
                text = "Login/Signup",
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 2.sp
            )
        }
    }
}
