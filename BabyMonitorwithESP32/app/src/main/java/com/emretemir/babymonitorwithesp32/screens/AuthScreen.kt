package com.emretemir.babymonitorwithesp32.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emretemir.babymonitorwithesp32.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore

var signInAttempts = 0

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(onSignedIn: (FirebaseUser) -> Unit) {
    val auth: FirebaseAuth by lazy { Firebase.auth }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isSignIn by remember { mutableStateOf(true) }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var myErrorMessage by remember { mutableStateOf<String?>(null) }
    var missingInfoMessage by remember { mutableStateOf<String?>(null) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    val imagePainter: Painter = painterResource(id = R.drawable.back_img)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(400.dp) // Kartın genişliğini belirleyin
                    .padding(25.dp)
                    .clip(RoundedCornerShape(16.dp)),
                elevation = CardDefaults.cardElevation()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (!isSignIn) {
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            label = { Text("Ad") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            label = { Text("Soyad") }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                        visualTransformation = VisualTransformation.None
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = { Text("Şifre") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                val icon = if (isPasswordVisible) Icons.Default.Lock else Icons.Default.Search
                                Icon(imageVector = icon, contentDescription = "Şifre Görünürlüğünü Değiştir")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    if (myErrorMessage != null) {
                        Text(
                            text = myErrorMessage!!,
                            color = Color.Blue,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Button(
                            onClick = {
                                if (email.isEmpty() || password.isEmpty()) {
                                    missingInfoMessage = "Lütfen E-posta ve Şifrenizi Girin."
                                } else {
                                    isLoading = true
                                    if (isSignIn) {
                                        signIn(
                                            auth, email, password,
                                            onSignedIn = { signedInUser ->
                                                onSignedIn(signedInUser)
                                                isLoading = false
                                            },
                                            onSignInError = { errorMessage ->
                                                myErrorMessage = errorMessage
                                                isLoading = false
                                            }
                                        )
                                    } else {
                                        if (firstName.isEmpty() || lastName.isEmpty()) {
                                            missingInfoMessage = "Lütfen tüm alanları doldurun."
                                        } else {
                                            signUp(
                                                auth, email, password, firstName, lastName,
                                                onSignedIn = { signedInUser ->
                                                    onSignedIn(signedInUser)
                                                    isLoading = false
                                                },
                                                onSignUpError = { errorMessage ->
                                                    myErrorMessage = errorMessage
                                                    isLoading = false
                                                }
                                            )
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .padding(8.dp),
                        ) {
                            Text(
                                text = if (isSignIn) "Giriş Yap" else "Kaydol",
                                fontSize = 18.sp,
                            )
                        }
                    }

                    missingInfoMessage?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }

                    ClickableText(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Blue)) {
                                append(if (isSignIn) "Hesabınız yok mu? Kaydol" else "Zaten bir hesabınız var mı? Giriş yap")
                            }
                        },
                        onClick = {
                            myErrorMessage = null
                            email = ""
                            password = ""
                            isSignIn = !isSignIn
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ClickableText(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Blue)) {
                                append("Şifrenizi mi unuttunuz?")
                            }
                        },
                        onClick = {
                            showForgotPasswordDialog = true
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    if (showForgotPasswordDialog) {
                        ForgotPasswordDialog(
                            onDismiss = { showForgotPasswordDialog = false },
                            onSendEmail = { email ->
                                isLoading = true
                                sendPasswordResetEmail(auth, email, onComplete = { isSuccess ->
                                    isLoading = false
                                    showForgotPasswordDialog = false
                                    if (isSuccess) {
                                        myErrorMessage = "Şifre sıfırlama e-postası gönderildi."
                                    } else {
                                        myErrorMessage = "Şifre sıfırlama e-postası gönderilemedi."
                                    }
                                })
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordDialog(onDismiss: () -> Unit, onSendEmail: (String) -> Unit) {
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Şifre Sıfırlama") },
        text = {
            Column {
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (email.isNotEmpty()) {
                        onSendEmail(email)
                    }
                }
            ) {
                Text("Gönder")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("İptal")
            }
        }
    )
}

private fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignedIn: (FirebaseUser) -> Unit,
    onSignInError: (String) -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                onSignedIn(user!!)
                signInAttempts = 0 // Başarılı girişte sıfırlayın
            } else {
                signInAttempts++
                if (signInAttempts >= 3) {
                    onSignInError("Çok fazla hatalı giriş denemesi. Lütfen bir süre sonra tekrar deneyin.")
                } else {
                    onSignInError("Geçersiz email veya şifre")
                }
            }
        }
}

private fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    onSignedIn: (FirebaseUser) -> Unit,
    onSignUpError: (String) -> Unit // Kayıt hatası için geri çağrı
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser

                // Firestore'da kullanıcı profili oluşturuluyor
                val userProfile = hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "email" to email
                )

                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("users")
                    .document(user!!.uid)
                    .set(userProfile)
                    .addOnSuccessListener {
                        onSignedIn(user)
                    }
                    .addOnFailureListener { e ->
                        // Kayıt hatası durumunda hata mesajını alın
                        val errorMessage = e.message ?: "Bir hata meydana geldi"
                        onSignUpError(errorMessage)
                    }
            } else {
                // Kayıt hatası durumunda hata mesajını alın
                val errorMessage = task.exception?.message ?: "Bir hata meydana geldi"
                onSignUpError(errorMessage)
            }
        }
}

private fun sendPasswordResetEmail(auth: FirebaseAuth, email: String, onComplete: (Boolean) -> Unit) {
    auth.sendPasswordResetEmail(email)
        .addOnCompleteListener { task ->
            onComplete(task.isSuccessful)
        }
}
