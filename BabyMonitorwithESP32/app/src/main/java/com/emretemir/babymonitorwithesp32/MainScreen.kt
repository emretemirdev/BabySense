package com.emretemir.babymonitorwithesp32

import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigation
import androidx.compose.material3.Scaffold

import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseUser

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(user: FirebaseUser, onSignOut: () -> Unit) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigation(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.background,
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                BottomNavigationItem(
                    icon = { Icon(imageVector = Icons.Default.Home, contentDescription = null) },
                    label = { Text(text = "Ana Ekran", modifier = Modifier.padding(4.dp)) },
                    selected = currentRoute == "home",
                    onClick = {
                        navController.navigate("home")
                    }
                )

                BottomNavigationItem(
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                    label = { Text(text = "Profil", modifier = Modifier.padding(4.dp)) },
                    selected = currentRoute == "profile",
                    onClick = {
                        navController.navigate("profile")
                    }
                )
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
                HomeScreen (user){
                    onSignOut()
                }
            }
            composable("profile") {
                ProfileScreen(user)
            }
        }
    }
}

@Composable
fun HomeScreen(user: FirebaseUser, onSignOut: () -> Unit) {
    Column {
        Text(text = "Hoşgeldin, ${user.displayName ?: user.email}")
        // Diğer içerikler...

        Button(
            onClick = { onSignOut() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Çıkış Yap")
        }
    }
}
@Composable
fun ProfileScreen(user: FirebaseUser) {
    Text(text = "Profil: ${user.displayName ?: user.email}")
    // Diğer içerikleriniz...
}
