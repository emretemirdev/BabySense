package com.emretemir.babymonitorwithesp32.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emretemir.babymonitorwithesp32.FanControlListener
import com.emretemir.babymonitorwithesp32.MicrophoneListener
import com.emretemir.babymonitorwithesp32.R
import com.emretemir.babymonitorwithesp32.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordChangeDialog(auth: FirebaseAuth, onDismiss: () -> Unit) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    fun updatePassword() {
        val user = auth.currentUser
        if (user != null) {
            val email = user.email

            if (email != null) {
                val credential = EmailAuthProvider.getCredential(email, currentPassword)

                user.reauthenticate(credential)
                    .addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            user.updatePassword(newPassword)
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        successMessage = "Şifre başarıyla güncellendi."
                                    } else {
                                        errorMessage = "Şifre güncellenirken hata oluştu: ${updateTask.exception?.message}"
                                    }
                                }
                        } else {
                            errorMessage = "Mevcut şifre doğrulanamadı: ${reauthTask.exception?.message}"
                        }
                    }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Mevcut Şifre")
        TextField(
            value = currentPassword,
            onValueChange = { currentPassword = it },
            label = { Text("Mevcut Şifre") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Yeni Şifre")
        TextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("Yeni Şifre") },
            visualTransformation = PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { updatePassword() }) {
            Text("Şifreyi Güncelle")
        }
        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        successMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsDialog(onDismiss: () -> Unit) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var emailNotifications by remember { mutableStateOf(true) }
    var pushNotifications by remember { mutableStateOf(true) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Bildirim Ayarları")

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
            Text(text = "Bildirimler Etkin")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = emailNotifications,
                onCheckedChange = { emailNotifications = it }
            )
            Text(text = "E-posta Bildirimleri")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = pushNotifications,
                onCheckedChange = { pushNotifications = it }
            )
            Text(text = "Push Bildirimleri")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onDismiss() }) {
            Text("Kaydet")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userProfile: User?, database: FirebaseDatabase) {
    val auth: FirebaseAuth by remember { mutableStateOf(FirebaseAuth.getInstance()) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogContent by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }
    var microphoneListenerEnabled by remember { mutableStateOf(true) }
    var maxTemperature by remember { mutableStateOf(30.0) }
    var maxCO2 by remember { mutableStateOf(1000) }

    val context = LocalContext.current
    val microphoneListener = remember { MicrophoneListener(context, database) }
    val fanControlListener = remember { FanControlListener(context, database) }

    val settingsRef = database.getReference("settings")

    // Dinleyicinin durumunu Firebase'den okuma
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            settingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    microphoneListenerEnabled = snapshot.child("microphoneListenerEnabled").getValue(Boolean::class.java) ?: true
                    maxTemperature = snapshot.child("maxTemperature").getValue(Double::class.java) ?: 30.0
                    maxCO2 = snapshot.child("maxCO2").getValue(Int::class.java) ?: 1000

                    if (microphoneListenerEnabled) {
                        microphoneListener.startListening()
                    }
                    fanControlListener.startListening()
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ProfileScreen", "Settings read error: ${error.message}")
                }
            })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        userProfile?.let {
            Text(
                text = "Ayarlar",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SettingCard(
                title = "Kişisel Bilgiler",
                description = "Profil bilgilerinizi görüntüleyin ve güncelleyin",
                icon = painterResource(id = R.drawable.ic_user)
            ) {
                showDialog = true
                dialogContent = {
                    PersonalInfoDialog(userProfile = it) { showDialog = false }
                }
            }

            SettingCard(
                title = "Şifre Değiştir",
                description = "Şifrenizi güncelleyin",
                icon = painterResource(id = R.drawable.ic_password)
            ) {
                showDialog = true
                dialogContent = {
                    PasswordChangeDialog(auth = auth) { showDialog = false }
                }
            }

            SettingCard(
                title = "Bildirim Ayarları",
                description = "Bildirim tercihlerinizi yönetin",
                icon = painterResource(id = R.drawable.ic_notifications)
            ) {
                showDialog = true
                dialogContent = {
                    NotificationSettingsDialog { showDialog = false }
                }
            }

            SettingCard(
                title = "Mikrofon Dinleyici",
                description = "Mikrofon dinleyiciyi etkinleştirerek otonom beşik sallayıcısını ve ninni çaları kullanın",
                icon = painterResource(id = R.drawable.ic_microphone)
            ) {
                showDialog = true
                dialogContent = {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Mikrofon Dinleyici")
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = microphoneListenerEnabled,
                                onCheckedChange = { enabled ->
                                    microphoneListenerEnabled = enabled
                                    settingsRef.child("microphoneListenerEnabled").setValue(enabled)
                                    if (enabled) {
                                        microphoneListener.startListening()
                                    } else {
                                        microphoneListener.stopListening()
                                    }
                                }
                            )
                            Text(text = "Mikrofon Dinleyici Etkin")
                        }
                        Button(onClick = { showDialog = false }) {
                            Text("Kapat")
                        }
                    }
                }
            }

            SettingCard(
                title = "Fan Kontrol Ayarları",
                description = "Fan kontrol ayarlarını yapın",
                icon = painterResource(id = R.drawable.ic_fan)
            ) {
                showDialog = true
                dialogContent = {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Fan Kontrol Ayarları")
                        TextField(
                            value = maxTemperature.toString(),
                            onValueChange = { value ->
                                maxTemperature = value.toDoubleOrNull() ?: maxTemperature
                            },
                            label = { Text("Maksimum Isı") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = maxCO2.toString(),
                            onValueChange = { value ->
                                maxCO2 = value.toIntOrNull() ?: maxCO2
                            },
                            label = { Text("Maksimum CO2") }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            settingsRef.child("maxTemperature").setValue(maxTemperature)
                            settingsRef.child("maxCO2").setValue(maxCO2)
                            showDialog = false
                        }) {
                            Text("Kaydet")
                        }
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    text = { dialogContent?.invoke() },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Kapat")
                        }
                    }
                )
            }
        } ?: Text(text = "Ayarlar Yükleniyor. Lütfen Bekleyiniz")
    }
}

@Composable
fun SettingCard(
    title: String,
    description: String,
    icon: Painter,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = description, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoDialog(userProfile: User, onDismiss: () -> Unit) {
    var firstName by remember { mutableStateOf(userProfile.firstName) }
    var lastName by remember { mutableStateOf(userProfile.lastName) }
    var email by remember { mutableStateOf(userProfile.email) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val firestore = FirebaseFirestore.getInstance()
    val userDocRef = firestore.collection("users").document(FirebaseAuth.getInstance().currentUser?.uid ?: "")

    fun updateProfile() {
        val updatedProfile = hashMapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "email" to email
        )

        userDocRef.update(updatedProfile as Map<String, Any>)
            .addOnSuccessListener {
                successMessage = "Profil başarıyla güncellendi."
            }
            .addOnFailureListener { e ->
                errorMessage = "Profil güncellenirken hata oluştu: ${e.message}"
            }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Kişisel Bilgiler", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(8.dp))

        firstName?.let {
            TextField(
                value = it,
                onValueChange = { firstName = it },
                label = { Text("Ad") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        lastName?.let {
            TextField(
                value = it,
                onValueChange = { lastName = it },
                label = { Text("Soyad") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("E-posta") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { updateProfile() }) {
            Text("Güncelle")
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        successMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary)
        }
    }
}
