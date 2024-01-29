package com.emretemir.babymonitorwithesp32

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

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
    // Hata mesajları için state değişkenleri
    var myErrorMessage by remember { mutableStateOf<String?>(null) }
    // Eksik bilgi uyarısı için state değişkeni
    var missingInfoMessage by remember { mutableStateOf<String?>(null) }
    // Arka plan resmini yüklüyoruz (kendi resminizi eklemelisiniz)
    val imagePainter: Painter = painterResource(id = R.drawable.back_img)

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Arka plan resmi
        Image(
            painter = imagePainter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Yuvarlatılmış köşelere sahip, transparan bir kart oluşturuyoruz
        Card(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.25f))
                .padding(25.dp)
                .clip(RoundedCornerShape(16.dp)),
            elevation = CardDefaults.cardElevation()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // İlk Ad TextField'i (kaydolma ekranında görünür)
                if (!isSignIn) {
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = {
                            Text("Ad")
                        },
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Soyad TextField'i (kaydolma ekranında görünür)
                    TextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        label = {
                            Text("Soyad")
                        },
                    )
                }

                // Email TextField'i
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = {
                        Text("Email")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null)
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Email
                    ),
                    visualTransformation = if (isSignIn) VisualTransformation.None else VisualTransformation.None
                )

                // Şifre TextField'i
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = {
                        Text("Şifre")
                    },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Password
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible }
                        ) {
                            val icon = if (isPasswordVisible) Icons.Default.Lock else Icons.Default.Search
                            Icon(
                                imageVector = icon,
                                contentDescription = "Şifre Görünürlüğünü Değiştir"
                            )
                        }
                    }
                )

                // ... (diğer içerikler)
                Spacer(modifier = Modifier.height(16.dp))

                // Hata Mesajı
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

                Button(
                    onClick = {
                        if (isSignIn) {
                            if (email.isEmpty() || password.isEmpty()) {
                                missingInfoMessage = "Lütfen E-posta ve Şifrenizi Girin."
                            } else {
                                signIn(auth, email, password,
                                    onSignedIn = { signedInUser ->
                                        onSignedIn(signedInUser)
                                    },
                                    onSignInError = { errorMessage ->
                                        // Giriş hatası durumunda hata mesajını göster
                                        myErrorMessage = errorMessage
                                    }
                                )
                            }
                        } else {
                            if (email.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                                missingInfoMessage = "Lütfen tüm alanları doldurun."
                            } else {
                                signUp(auth, email, password, firstName, lastName) { signedInUser ->
                                    onSignedIn(signedInUser)
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

                // Eksik bilgi uyarısını göstermek için Text bileşeni ekleyin
                missingInfoMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }

                // Tıklanabilir Metin
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .padding(8.dp),
                ) {
                    ClickableText(
                        text = AnnotatedString(buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Blue)) {
                                append(if (isSignIn) "Hesabınız yok mu? Kaydol" else "Zaten bir hesabınız var mı? Giriş yap")
                            }
                        }.toString()),
                        onClick = {
                            myErrorMessage = null
                            email = ""
                            password = ""
                            isSignIn = !isSignIn
                        },
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

private fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignedIn: (FirebaseUser) -> Unit,
    onSignInError: (String) -> Unit // Giriş hatası için geri çağrı
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                onSignedIn(user!!)
            } else {
                // Giriş başarısız olduğunda burası çalışır
                onSignInError("Geçersiz email veya şifre")
            }
        }
}
private fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    firstName: String,
    lastName: String,
    onSignedIn: (FirebaseUser) -> Unit
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
                    .addOnFailureListener {
                        // Hata durumunu işleyin

                    }
            } else {
                // Kayıt başarısız olduysa burası çalışır

            }
        }
}