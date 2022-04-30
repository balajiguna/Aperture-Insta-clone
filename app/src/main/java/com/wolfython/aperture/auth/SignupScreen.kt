package com.wolfython.aperture.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.R
import com.wolfython.aperture.DestinationScreen
import com.wolfython.aperture.IgViewModel
import com.wolfython.aperture.main.CheckForSignedIn
import com.wolfython.aperture.main.CommonProgressSpinner
import com.wolfython.aperture.main.navigateTo

@Composable
fun SignupScreen(navController: NavController, vm: IgViewModel){
    CheckForSignedIn(vm = vm, navController =navController)
    val focus = LocalFocusManager.current
   Box(modifier = Modifier.fillMaxSize()){

       Column(modifier = Modifier
           .fillMaxWidth()
           .wrapContentHeight()
           .verticalScroll(
               rememberScrollState()
           ), horizontalAlignment = Alignment.CenterHorizontally) {

         val usernameState = remember{ mutableStateOf(TextFieldValue()) }
           val emailState = remember{ mutableStateOf(TextFieldValue()) }
           val passState = remember{ mutableStateOf(TextFieldValue()) }

           Image(painter = painterResource(id = com.wolfython.aperture.R.drawable.apeture_logo),
               contentDescription = null,
           modifier = Modifier
               .width(250.dp)
               .padding(top = 16.dp)
               .padding(8.dp)
           
               )
           Text(
               text = "Signup",
               modifier = Modifier.padding(8.dp),
               fontSize = 30.sp,
               fontFamily = FontFamily.SansSerif
           )


            OutlinedTextField(value = usernameState.value,
                onValueChange = {usernameState.value= it},
                modifier = Modifier.padding(8.dp),
                label = { Text(text ="Username")})

           OutlinedTextField(value = emailState.value,
               onValueChange = {emailState.value= it},
               modifier = Modifier.padding(8.dp),
               label = { Text(text ="Email")})

           OutlinedTextField(value = passState.value,
               onValueChange = {passState.value= it},
               modifier = Modifier.padding(8.dp),
               label = { Text(text ="Password")},
               visualTransformation = PasswordVisualTransformation()
           )
               Button(onClick = {
                   focus.clearFocus(force = true)
                                vm.onSignup(

                                    usernameState.value.text,
                                    emailState.value.text,
                                    passState.value.text

                                )

               },
                   //added custom Rgb color using decimal code
               modifier = Modifier.padding(8.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(255,165,0))

               ) {
                        Text(text = "SIGN UP")


               }
           Text(text = "Already a user? Go to login ->",
               color = Color.Red,
               modifier = Modifier
                   .padding(8.dp)
                   .clickable {

                       navigateTo(navController, DestinationScreen.Login)
                   }
           )
       }



       val isloading = vm.inProgress.value
       if (isloading){
           CommonProgressSpinner()
       }
   }
}