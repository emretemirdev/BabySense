package com.emretemir.babymonitorwithesp32.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.emretemir.babymonitorwithesp32.User


@Composable
fun HomeScreen(userProfile: User?, onSignOut: () -> Unit) {
    Column {
        userProfile?.let {
            Text(text = "Hoşgeldin, ${it.firstName} ${it.lastName}")
        } ?: Text(text = "Hoşgeldin")

        Spacer(modifier = Modifier.height(8.dp))

        // Sıcaklık Kartı
        TemperatureCard(temperature = 25.5f)


        Button(onClick = { onSignOut() }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Çıkış Yap")
        }
    }
}


@Composable
fun TemperatureCard(temperature: Float) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .size(width = 240.dp, height = 100.dp)
    ) {
        Text(
            text = "23.5C",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}