package com.cs407.unify.ui.screens.profile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cs407.unify.ViewModels.UserViewModel
import com.cs407.unify.ui.theme.ThemeManager

@Composable
fun SettingsPage (
    navController: NavController,
    userViewModel: UserViewModel
){
    val systemInDarkMode = isSystemInDarkTheme()
    val canToggle = ThemeManager.canToggleDarkMode()
    var darkModeEnabled by ThemeManager.userDarkModePreference

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
                contentDescription = "Back Arrow",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp, bottom = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Dark Mode Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Dark Mode",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    if (systemInDarkMode) {
                        Text(
                            text = "Locked to system setting",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }

                Switch(
                    checked = if (systemInDarkMode) true else darkModeEnabled,
                    onCheckedChange = {
                        if (canToggle) {
                            darkModeEnabled = it
                        }
                    },
                    enabled = canToggle,

                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
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
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Text("Log out")
            }
        }
    }
}