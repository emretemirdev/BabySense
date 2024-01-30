package com.emretemir.babymonitorwithesp32

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(userProfile: User?, onSignOut: () -> Unit) {

    Column {
        userProfile?.let {
            Text(text = "Hoşgeldin, ${it.firstName} ${it.lastName}")
        } ?: Text(text = "Hoşgeldin")

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { onSignOut() }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Çıkış Yap")
        }
    }
}
