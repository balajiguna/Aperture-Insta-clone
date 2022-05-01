package com.wolfython.aperture.main

import android.graphics.drawable.Icon
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import com.wolfython.aperture.DestinationScreen
import com.wolfython.aperture.R

enum class BottomNavigationItem(val icon:Int,val navDestination: DestinationScreen){
FEED(R.drawable.ic_home_24,DestinationScreen.Feed),
  SEARCH(R.drawable.ic_search_24,DestinationScreen.Search)  ,
POSTS(R.drawable.ic_person_24,DestinationScreen.MyPosts)


}

@Composable
fun BottomNavigationMenu(SelectedItem: BottomNavigationItem,navController: NavController){
    Row(modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(top = 4.dp)
        .background(Color.White)) {


        for (item in BottomNavigationItem.values()){

            Image(painter = painterResource(id = item.icon), contentDescription = null,
            modifier = Modifier.size(40.dp)
                .padding(5.dp)
                .weight(1f)
                .clickable {

                    navigateTo(navController,item.navDestination)

                },
            colorFilter = if(item == SelectedItem) ColorFilter.tint(Color.Black) else ColorFilter.tint(
                Color.Gray)

                )
        }

    }
}