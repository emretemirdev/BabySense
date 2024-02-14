package com.emretemir.babymonitorwithesp32


import android.content.Context
import android.net.ConnectivityManager
import androidx.compose.runtime.*
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay

class MessageViewModel : ViewModel() {
    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    fun updateMessage(newMessage: String) {
        _message.value = newMessage
    }
}
class MainActivity : ComponentActivity() {
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var functions: FirebaseFunctions
    private val messageViewModel by viewModels<MessageViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); //bu kod şu işe yarar: https://stackoverflow.com/questions/60361276/what-does-superoncreatebundlesavedinstancestate-do-in-android
        permissionsManager = PermissionsManager(this)
        permissionsManager.setupPermissions()
        functions = Firebase.functions


        FirebaseMessaging.getInstance().subscribeToTopic("sensorUpdates")
            .addOnCompleteListener { task ->
                var msg = "Salamslar successful"
                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }
                Log.d("Subscription", msg)
            }

        functions.getHttpsCallable("helloWorld").call()
            .addOnSuccessListener {result ->
                val data = result.data
                println(data)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Bağlantı hatası", Toast.LENGTH_SHORT).show()
            }

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