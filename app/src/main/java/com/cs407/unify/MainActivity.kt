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
import com.cs407.unify.ui.screens.HomePage.SearchPage
import com.cs407.unify.ui.screens.LoginPage
import com.cs407.unify.ui.screens.MainFeedPage
import com.cs407.unify.ui.screens.MarketPage
import com.cs407.unify.ui.screens.PostPage
import com.cs407.unify.ui.screens.profile.ProfilePage
import com.cs407.unify.ui.components.threads.ThreadPage
import com.cs407.unify.ui.screens.profile.ProfilePagePosts


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
                onNavigateToMarketPage = { navController.navigate("market") },
                onNavigateToProfilePage = { navController.navigate("profile") },
                onNavigateToSearchPage = { navController.navigate("search") }
            )
        }

        composable("post"){
            PostPage(
                onNavigateToMainFeedPage = { navController.navigate("mainfeed") },
                onNavigateToMarketPage = { navController.navigate("market") },
                onNavigateToProfilePage = { navController.navigate("profile") },
                onNavigateToSearchPage = { navController.navigate("search") }
            )
        }

        composable("market"){
            MarketPage(
                onNavigateToPostPage = { navController.navigate("post") },
                onNavigateToMainFeedPage = { navController.navigate("mainfeed") },
                onNavigateToProfilePage = { navController.navigate("profile") },
                onNavigateToSearchPage = { navController.navigate("search") }
            )
        }

        composable("profile"){
            ProfilePage(
                onNavigateToPostPage = { navController.navigate("post") },
                onNavigateToMarketPage = { navController.navigate("market") },
                onNavigateToMainFeedPage = { navController.navigate("mainfeed") },
                onNavigateToSearchPage = { navController.navigate("search") },
                onNavigateToMyPosts = { navController.navigate("my_posts")}
            )
        }

        composable("search"){
            SearchPage(
                onNavigateToPostPage = { navController.navigate("post") },
                onNavigateToMarketPage = { navController.navigate("market")},
                onNavigateToMainFeedPage = { navController.navigate("mainfeed") },
                onNavigateToProfilePage = { navController.navigate("profile") }
            )
        }

        composable("my_posts") {
            ProfilePagePosts (
                onExit = {navController.navigate("profile")},
                onClick = {navController.navigate("thread")}
            )

        }

        composable("thread") {
            ThreadPage(
                onExit = {navController.navigate("my_posts")}
            )
        }



    }

}