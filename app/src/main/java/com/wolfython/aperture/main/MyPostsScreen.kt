package com.wolfython.aperture.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.wolfython.aperture.IgViewModel

@Composable

fun MyPostsScreen(navController: NavController,vm:IgViewModel){

    Column(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "Mypost Screen")

        }

        BottomNavigationMenu(SelectedItem = BottomNavigationItem.POSTS,
            navController = navController )

    }

}