package com.emretemir.babymonitorwithesp32.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.emretemir.babymonitorwithesp32.User


@Composable
fun ProfileScreen(userProfile: User?) {


    Column(modifier = Modifier.padding(16.dp)) {
        userProfile?.let {
            Text(text = "Profil: ${it.firstName} ${it.lastName}")
            Text(text = "E-posta: ${it.email}")
        } ?: Text(text = "Profil bilgileri yüklenemedi.")
    }
}
