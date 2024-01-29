package com.emretemir.babymonitorwithesp32


import androidx.compose.runtime.*
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
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

    // Uygulama içeriği başladığında görüntülenecek olan Splash Screen
    @Composable
    fun AppContent(auth: FirebaseAuth) {
        var showSplashScreen by remember { mutableStateOf(true) }

        LaunchedEffect(showSplashScreen) {
            delay(2000) // 2 saniye bekleyerek Splash Screen'i gösteriyoruz
            showSplashScreen = false
        }

        Crossfade(targetState = showSplashScreen, label = "") { isSplashScreenVisible ->
            if (isSplashScreenVisible) {
                SplashScreen {
                    showSplashScreen = false
                }
            } else {
                AuthOrMainScreen(auth)
            }
        }
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