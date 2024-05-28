package com.emretemir.babymonitorwithesp32.screens

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
import com.emretemir.babymonitorwithesp32.MicrophoneListener
import com.emretemir.babymonitorwithesp32.R
import com.emretemir.babymonitorwithesp32.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

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

@Composable
fun ProfileScreen(userProfile: User?, database: FirebaseDatabase) {
    val auth: FirebaseAuth by remember { mutableStateOf(FirebaseAuth.getInstance()) }
    var showDialog by remember { mutableStateOf(false) }
    var dialogContent by remember { mutableStateOf<(@Composable () -> Unit)?>(null) }
    var microphoneListenerEnabled by remember { mutableStateOf(true) } // Varsayılan olarak true

    val context = LocalContext.current
    val microphoneListener = remember { MicrophoneListener(context, database) }

    LaunchedEffect(Unit) {
        if (microphoneListenerEnabled) {
            microphoneListener.startListening()
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
        } ?: Text(text = "Profil bilgileri yüklenemedi.")
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
