package com.emretemir.babymonitorwithesp32

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

import androidx.compose.ui.unit.dp
import com.google.firebase.messaging.FirebaseMessaging

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

        // Abone olma butonu
        Button(onClick = { subscribeToTopic() }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Bildirimlere Abone Ol")
        }
    }
}

fun subscribeToTopic() {
    FirebaseMessaging.getInstance().subscribeToTopic("new_user_forms")
        .addOnCompleteListener { task ->
            val msg = if (task.isSuccessful) {
                "Abonelik başarılı!"
            } else {
                "Abonelik başarısız."
            }
            Log.d("SubscribeToTopic", msg)
        }
}