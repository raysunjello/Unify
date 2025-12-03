package com.cs407.unify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import com.cs407.unify.ui.theme.UnifyTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.cs407.unify.ViewModels.UserViewModel
import com.cs407.unify.data.AppDatabase
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
import com.cs407.unify.ui.screens.profile.SettingsPage
import androidx.lifecycle.ViewModelProvider
import com.cs407.unify.ui.screens.profile.SavedThreadsPage


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


    val context = LocalContext.current

    val appDatabase = remember { AppDatabase.getDatabase(context) }
    val userDao = remember { appDatabase.userDao() }
    val deleteDao = remember { appDatabase.deleteDao() }

    val userViewModel: UserViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return UserViewModel(userDao, deleteDao) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    val userState by userViewModel.userState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = "login" // display landing page on app start
    ){
        composable("login"){
            LoginPage(
                userViewModel = userViewModel,
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
                userState = userState,
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
                onNavigateToMyPosts = { navController.navigate("my_posts")},
                onNavigateToSettingsPage = {navController.navigate("settings")},
                onNavigateToSavedStuff = { navController.navigate("saved_stuff")}
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
                userState = userState,
                onExit = {navController.navigate("profile")},
                onClick = { thread ->
                    // Store selected thread temporarily
                    ThreadStore.selectedThread = thread
                    navController.navigate("thread/my_posts")
                }
            )
        }

        composable(
            route = "thread/{source}",
            arguments = listOf(
                navArgument("source") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val source = backStackEntry.arguments?.getString("source") ?: "my_posts"
            val selectedThread = ThreadStore.selectedThread
            if (selectedThread != null) {
                ThreadPage(
                    thread = selectedThread,
                    onExit = {
                        when (source) {
                            "saved_stuff" -> navController.navigate("saved_stuff")
                            "my_posts" -> navController.navigate("my_posts")
                            else -> navController.navigate("my_posts")
                        }
                    },
                    userState = userState,
                )
            } else {
                // Handle case where no thread is selected
                // Navigate back or show error
                LaunchedEffect(Unit) {
                    navController.navigate("my_posts")
                }
            }
        }

        composable("settings") {
            SettingsPage(
                navController = navController,
                userViewModel = userViewModel)
        }

        composable("saved_stuff") {
            SavedThreadsPage(
                onExit = { navController.navigate("profile") },
                onClick = { thread ->
                    ThreadStore.selectedThread = thread
                    navController.navigate("thread/saved_stuff")
                }
            )
        }
    }

}
