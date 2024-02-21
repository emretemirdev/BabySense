
package com.emretemir.babymonitorwithesp32.screens

import androidx.compose.runtime.Composable
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.emretemir.babymonitorwithesp32.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navigateToAuthOrMainScreen: () -> Unit) {
    // Resmin dönüş efekti için kullanılan değişken
    var rotationState by remember { mutableStateOf(0f) }

    // Auth veya Main ekranına geçiş için simulasyon
    LaunchedEffect(true) {
        delay(2000) // 2 saniye bekletiyoruz
        navigateToAuthOrMainScreen()
    }

    // Döndürme efekti animasyonu
    LaunchedEffect(rotationState) {
        while (true) {
            delay(16) // Dönme hızını kontrol etmek için gecikme ayarlayabilirsiniz
            rotationState += 1f
        }
    }

    // Splash Screen'in UI'su ve efektleri
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = TweenSpec(durationMillis = 500), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo), // Logo resmini kullanıyoruz
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .scale(scale)
                .rotate(rotationState) // Döndürme efektini uyguluyoruz
        )
    }
}