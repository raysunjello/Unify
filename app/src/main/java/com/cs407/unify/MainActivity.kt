package com.cs407.unify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import com.cs407.unify.ui.theme.UnifyTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cs407.unify.ui.screens.HomePage.SearchPage
import com.cs407.unify.ui.screens.login.LoginPage
import com.cs407.unify.ui.screens.MainFeedPage
import com.cs407.unify.ui.screens.MarketPage
import com.cs407.unify.ui.screens.PostPage
import com.cs407.unify.ui.screens.profile.ProfilePage
import com.cs407.unify.ui.components.threads.ThreadPage
import com.cs407.unify.ui.components.threads.ThreadStore
import com.cs407.unify.ui.screens.profile.ProfilePagePosts
import com.cs407.unify.ui.screens.login.RegistrationPage


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
                onNavigateToMainFeedPage = { navController.navigate("mainfeed") },
                onNavigateToRegistrationPage = { uid -> navController.navigate("register/$uid") }
            )
        }

        composable(
            route = "register/{uid}",
            arguments = listOf(
                navArgument("uid") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uid = backStackEntry.arguments?.getString("uid") ?: ""

            RegistrationPage(
                uid = uid,
                onRegistrationComplete = {
                    navController.navigate("mainfeed") {
                        popUpTo("login") { inclusive = true }
                    }
                }
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
                onClick = { thread ->
                    // Store selected thread temporarily
                    ThreadStore.selectedThread = thread
                    navController.navigate("thread")
                }
            )
        }

        composable("thread") {
            val selectedThread = ThreadStore.selectedThread
            if (selectedThread != null) {
                ThreadPage(
                    thread = selectedThread,
                    onExit = {navController.navigate("my_posts")}
                )
            } else {
                // Handle case where no thread is selected
                // Navigate back or show error
                LaunchedEffect(Unit) {
                    navController.navigate("my_posts")
                }
            }
        }



    }

}