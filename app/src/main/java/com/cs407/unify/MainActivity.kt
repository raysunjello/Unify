package com.cs407.unify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import com.cs407.unify.ui.theme.UnifyTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cs407.unify.ui.screens.LoginPage
import com.cs407.unify.ui.screens.MainFeedPage
import com.cs407.unify.ui.screens.MarketPage
import com.cs407.unify.ui.screens.PostPage


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UnifyTheme {
                AppNavigation()
            }
        }
    }
}

//composable function responsible for navigation between screens
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login" // display landing page on app start
    ){
        composable("login"){
            LoginPage(
                onNavigateToMainFeedPage = { navController.navigate("mainfeed") }
            )
        }

        composable("mainfeed"){
            MainFeedPage(
                onNavigateToPostPage = { navController.navigate("post") },
                onNavigateToMarketPage = { navController.navigate("market") }
            )
        }

        composable("post"){
            PostPage(
                onNavigateToMainFeedPage = { navController.navigate("mainfeed") },
                onNavigateToMarketPage = { navController.navigate("market") }
            )
        }

        composable("market"){
            MarketPage(
                onNavigateToPostPage = { navController.navigate("post") },
                onNavigateToMainFeedPage = { navController.navigate("mainfeed") }
            )
        }



    }

}