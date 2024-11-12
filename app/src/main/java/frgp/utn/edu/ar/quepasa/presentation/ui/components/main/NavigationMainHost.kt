package frgp.utn.edu.ar.quepasa.presentation.ui.components.main

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import frgp.utn.edu.ar.quepasa.data.model.User
import frgp.utn.edu.ar.quepasa.presentation.ui.components.events.CreateEventScreen
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.PostCreateScreen
import frgp.utn.edu.ar.quepasa.presentation.ui.components.posts.PostEditScreen
import frgp.utn.edu.ar.quepasa.presentation.ui.components.users.profile.UserProfileScreen

@Composable
fun NavigationMainHost(navController: NavHostController, user: User?) { // TODO: Change to User (non-nullable) after login is implemented
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") { MainPage(navController, user) }
        composable("userProfile") { UserProfileScreen(navController, user) }
        composable("postCreate") { PostCreateScreen(navController, user) }
        composable("postEdit") { PostEditScreen(navController, user) }
        composable("eventCreate") { CreateEventScreen(navController, user) }
        composable("trends") { TrendsScreen() }
    }
}
