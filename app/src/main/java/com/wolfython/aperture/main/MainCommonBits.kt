package com.wolfython.aperture.main

import android.app.Notification
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.wolfython.aperture.IgViewModel

@Composable
fun NotificationMessage(vm: IgViewModel){
    val notifState = vm.popupNotification.value
    val notifiMessage = notifState?.getContentOrNull()
    if(notifiMessage != null){
        Toast.makeText(LocalContext.current, notifiMessage, Toast.LENGTH_LONG).show()
    }
}