package com.emretemir.babymonitorwithesp32

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat

class PermissionsManager(private val activity: ComponentActivity) {
    private lateinit var postNotificationPermissionLauncher: ActivityResultLauncher<String>

    fun setupPermissions() {
        postNotificationPermissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // İzin verildi (Permission Granted)
                // Here you can perform actions that require the POST_NOTIFICATIONS permission
                Log.d("PermissionsManager", "Post notifications permission granted!")

                // Example actions (adjust to your app's specific needs):
                // - Initialize notification features
                // - Schedule a notification
            } else {
                // İzin verilmedi (Permission Denied)
                Log.d("PermissionsManager", "Post notifications permission denied!")

                // Handle permission denial - common actions include:
                // - Explain to the user why the permission is important
                // - Optionally, provide a way to re-request the permission
                // - Disable features that rely on the permission if necessary
            }
        }

        checkAndRequestPostNotificationPermission()
    }

    private fun checkAndRequestPostNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    activity, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED) {

                // İzin isteme dialogunu göster
                postNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
