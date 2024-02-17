package com.emretemir.babymonitorwithesp32
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun VideoStreamScreen(userProfile: User?) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Canlı görüntü aktarma mantığı buraya entegre edilecek.

        // Varsayılan Placeholder İçerik (Örnek):
        Text(text = "Canlı İzle")
    }
}