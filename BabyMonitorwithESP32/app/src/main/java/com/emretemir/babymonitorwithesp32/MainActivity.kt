package com.emretemir.babymonitorwithesp32

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.runtime.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.emretemir.babymonitorwithesp32.screens.AuthScreen
import com.emretemir.babymonitorwithesp32.screens.MainScreen
import com.emretemir.babymonitorwithesp32.screens.SplashScreen
import com.emretemir.babymonitorwithesp32.services.StartupManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); //bu kod şu işe yarar: https://stackoverflow.com/questions/60361276/what-does-superoncreatebundlesavedinstancestate-do-in-android
        val startupManager = StartupManager(this)
        startupManager.setup()
        createNotificationChannel()
        setContent {
            AppContent(auth)
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Baby Monitor Bildirim Kanalı"
            val descriptionText = "Bebek ağlaması algılandığında bildirim gönderir."
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("BABY_MONITOR_CHANNEL", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @Composable
    fun AppContent(auth: FirebaseAuth) {
        var showSplashScreen by remember { mutableStateOf(true) }
        var showRetryDialog by remember { mutableStateOf(false) }
        var isCheckingConnection by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = showSplashScreen) {
            delay(2000)
            if (!isNetworkAvailable()) {
                showRetryDialog = true
            }
            showSplashScreen = false
        }

        if (showRetryDialog && !isCheckingConnection) {
            RetryDialog(onRetry = {
                isCheckingConnection = true
            })
        }

        if (isCheckingConnection) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator()
            }

            LaunchedEffect(key1 = Unit) {
                if (isNetworkAvailable()) {
                    showRetryDialog = false
                }
                isCheckingConnection = false
            }
        }

        Crossfade(
            targetState = showSplashScreen,
            label = "SplashScreenCrossfade" // Lable tanımlamak
        ) { isSplashScreenVisible ->
            if (isSplashScreenVisible) {
                SplashScreen {
                    showSplashScreen = false
                }
            } else {
                AuthOrMainScreen(auth)
            }
        }
    }

    // İnternet bağlantısını kontrol eden fonksiyon
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    // "Tekrar Dene" diyalogunu gösteren Composable fonksiyon
    @Composable
    fun RetryDialog(onRetry: () -> Unit) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("İnternet Bağlantısı Yok") },
            text = { Text("Lütfen internet bağlantınızı kontrol edin ve tekrar deneyin.") },
            confirmButton = {
                Button(onClick = onRetry) {
                    Text("Tekrar Dene")
                }
            }
        )
    }

    // Giriş ekranı veya ana ekranı gösteren ana Composable fonksiyon
    @Composable
    fun AuthOrMainScreen(auth: FirebaseAuth) {
        var user by remember { mutableStateOf(auth.currentUser) }

        if (user == null) {
            AuthScreen(
                onSignedIn = { signedInUser ->
                    user = signedInUser
                }
            )
        } else {
            MainScreen(
                user = user!!,  // Kullanıcı bilgisini MainScreen'e iletiyoruz
                onSignOut = {
                    auth.signOut()
                    user = null
                }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun PreviewAuthOrMainScreen() {
        AuthOrMainScreen(Firebase.auth)
    }
}