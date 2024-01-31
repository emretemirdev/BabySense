package com.emretemir.babymonitorwithesp32

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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
                // İzin verildi
            } else {
                // İzin verilmedi
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
