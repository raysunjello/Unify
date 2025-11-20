package com.cs407.unify.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cs407.unify.ViewModels.UserViewModel

@Composable
fun SettingsPage (
    navController: NavController,
    userViewModel: UserViewModel
){
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        IconButton(
            onClick = { navController.popBackStack()},
            modifier = Modifier
                .align(Alignment.TopStart)
        ){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back Arrow"
            )
        }

        Text(
            text = "Settings",
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        )

        // Logout button
        Button(
            onClick = {
                userViewModel.logout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }  // remove home from backstack
                }
            },
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
        ) {
            Text("Log out")
        }
    }
}