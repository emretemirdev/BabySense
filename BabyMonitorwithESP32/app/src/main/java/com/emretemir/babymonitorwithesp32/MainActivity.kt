package com.emretemir.babymonitorwithesp32


import android.content.Context
import android.net.ConnectivityManager
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay


class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            AppContent(auth)
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

        Crossfade(targetState = showSplashScreen) { isSplashScreenVisible ->
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
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
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