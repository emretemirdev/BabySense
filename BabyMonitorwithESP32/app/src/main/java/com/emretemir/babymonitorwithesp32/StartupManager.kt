package com.emretemir.babymonitorwithesp32

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class StartupManager(private val activity: ComponentActivity) {

    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private lateinit var permissionsManager: PermissionsManager
    private lateinit var functions: FirebaseFunctions

    fun setup() {
        setupPermissions()
        setupFirebaseFunctions()
        subscribeToSensorUpdates()


    }

    private fun setupPermissions() {
        permissionsManager = PermissionsManager(activity)
        permissionsManager.setupPermissions()
    }

    private fun setupFirebaseFunctions() {
        functions = Firebase.functions
    }

    private fun subscribeToSensorUpdates() {
        FirebaseMessaging.getInstance().subscribeToTopic("sensorUpdates")
            .addOnCompleteListener { task ->
                var msg = "Subscription successful"
                if (!task.isSuccessful) {
                    msg = "Subscription failed"
                }
                Log.d("Baslangic Hatası", msg)
            }
    }
}
