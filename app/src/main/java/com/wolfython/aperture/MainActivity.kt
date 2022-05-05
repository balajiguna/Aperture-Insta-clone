package com.wolfython.aperture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.wolfython.aperture.auth.LoginScreen
import com.wolfython.aperture.auth.ProfileScreen
import com.wolfython.aperture.auth.SignupScreen
import com.wolfython.aperture.main.FeedScreen
import com.wolfython.aperture.main.MyPostsScreen
import com.wolfython.aperture.main.NotificationMessage
import com.wolfython.aperture.main.SearchScreen
import com.wolfython.aperture.ui.theme.ApertureTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ApertureTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    InstagramApp()
                }
            }
        }
    }
}

sealed class  DestinationScreen(val route: String){
    object Signup: DestinationScreen("signup")
    object Login: DestinationScreen("Login")
    object Feed: DestinationScreen("Feed")
    object Search: DestinationScreen("Search")
    object MyPosts: DestinationScreen("MyPosts")
    object Profile: DestinationScreen("Profile")
}


@Composable
fun InstagramApp(){
    val vm = hiltViewModel<IgViewModel>()
    val navController = rememberNavController() 
    
    NotificationMessage(vm = vm)
        
    NavHost(navController = navController, startDestination = DestinationScreen.Signup.route ){
     composable(DestinationScreen.Signup.route){
         SignupScreen(navController = navController, vm = vm )

     }
        composable(DestinationScreen.Login.route){
            LoginScreen(navController = navController, vm = vm)
        }
        composable(DestinationScreen.Feed.route){
            FeedScreen(navController = navController, vm = vm )
        }
        composable(DestinationScreen.Search.route){
            SearchScreen(navController = navController, vm = vm )
        }
        composable(DestinationScreen.MyPosts.route){
            MyPostsScreen(navController = navController, vm = vm )
        }

        composable(DestinationScreen.Profile.route){
            ProfileScreen(navController = navController, vm = vm )
        }

    }

}


@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ApertureTheme {

    }
}