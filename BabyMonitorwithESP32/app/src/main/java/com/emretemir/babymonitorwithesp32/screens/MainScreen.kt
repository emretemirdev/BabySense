
package com.emretemir.babymonitorwithesp32.screens
import VideoStreamScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.emretemir.babymonitorwithesp32.User
import com.google.firebase.firestore.FirebaseFirestore




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(user: FirebaseUser, onSignOut: () -> Unit) {
    val navController = rememberNavController()
    // Kullanıcı profil bilgilerini saklamak için bir state
    val userProfile = remember { mutableStateOf<User?>(null) }

    // Firestore'dan kullanıcı profilini çek
    LaunchedEffect(key1 = user) {
        val firestore = FirebaseFirestore.getInstance()
        val userDocRef = firestore.collection("users").document(user.uid)

        userDocRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val firstName = document.getString("firstName") ?: ""
                val lastName = document.getString("lastName") ?: ""
                userProfile.value = User(firstName, lastName, user.email ?: "")
            }
        }
    }


    Scaffold(
        bottomBar = {
            BottomNavigation(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.background
            ) {
                val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Ana Ekran") },
                    label = { Text("Ana Ekran") },
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
                    label = { Text("Profil") },
                    selected = currentRoute == "profile",
                    onClick = { navController.navigate("profile") }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Face, contentDescription = "Canlı") },
                    label = { Text("Canlı") },
                    selected = currentRoute == "VideoStreamScreen",
                    onClick = { navController.navigate("VideoStreamScreen") }
                )
                // Diğer bottom navigation item'ları ekleyin...
            }
        }

    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            composable("home") {

                    HomeScreen(userProfile.value,
                        onSignOut) // Virgülden sonra
            }
            composable("profile") {
                ProfileScreen(userProfile.value)
            }
            composable("VideoStreamScreen") {
                VideoStreamScreen(userProfile.value)
            }

        }
    }
}

