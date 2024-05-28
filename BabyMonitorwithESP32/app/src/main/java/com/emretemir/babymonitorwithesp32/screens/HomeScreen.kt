package com.emretemir.babymonitorwithesp32.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emretemir.babymonitorwithesp32.R
import com.emretemir.babymonitorwithesp32.User
import com.google.firebase.database.*

val WhiteCard = Color(0xFFFFFFFF) // Beyaz kart rengi

@Composable
fun HomeScreen(userProfile: User?, onSignOut: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.back_img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            WelcomeCard(userProfile = userProfile, onSignOut = onSignOut)

            Spacer(modifier = Modifier.height(16.dp))

            SensorDataGrid()
        }
    }
}

@Composable
fun WelcomeCard(userProfile: User?, onSignOut: () -> Unit) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp), // Kart yüksekliğini artırdım
        colors = CardDefaults.elevatedCardColors(containerColor = WhiteCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile), // Profil fotoğrafı ikonu
                contentDescription = null,
                modifier = Modifier
                    .size(60.dp) // Fotoğraf boyutunu artırdım
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f) // Ortalamak için ağırlık verdim
            ) {
                Text(
                    text = "Hoşgeldin",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                userProfile?.let {
                    Text(
                        text = "${it.firstName} ${it.lastName}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.Black
                    )
                } ?: Text(
                    text = "Misafir",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.Black
                )
            }

            IconButton(onClick = { onSignOut() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_logout), // Çıkış ikonu
                    contentDescription = "Çıkış Yap",
                    tint = Color.Black, // İkon rengi
                    modifier = Modifier.size(34.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SensorDataGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { CO2Card() }
        item { TemperatureCard() }
        item { HumidityCard() }
        item { FanControlCard() }
        item { MotorControlCard() }
        item { SoundCard() }
    }
}

@Composable
fun CO2Card() {
    val co2Data = remember { mutableStateOf(0f) }
    val databaseReference = FirebaseDatabase.getInstance().getReference("sensorESP32/co2")

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                co2Data.value = dataSnapshot.getValue(Float::class.java) ?: 0f
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        }
        databaseReference.addValueEventListener(listener)
        onDispose {
            databaseReference.removeEventListener(listener)
        }
    }

    val co2Icon: Painter = painterResource(id = R.drawable.co2_icon)

    SensorCard(title = "CO2", value = "${co2Data.value} PPM", color = Color.Blue, icon = co2Icon)
}

@Composable
fun TemperatureCard() {
    val temperatureData = remember { mutableStateOf(0f) }
    val databaseReference = FirebaseDatabase.getInstance().getReference("sensorESP32/isi")

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                temperatureData.value = dataSnapshot.getValue(Float::class.java) ?: 0f
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        }
        databaseReference.addValueEventListener(listener)
        onDispose {
            databaseReference.removeEventListener(listener)
        }
    }

    val tempIcon: Painter = painterResource(id = R.drawable.ic_temp)

    SensorCard(title = "Sıcaklık", value = "${temperatureData.value}°C", color = Color.Blue, icon = tempIcon)
}

@Composable
fun HumidityCard() {
    val humidityData = remember { mutableStateOf(0f) }
    val databaseReference = FirebaseDatabase.getInstance().getReference("sensorESP32/nemOrani")

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                humidityData.value = dataSnapshot.getValue(Float::class.java) ?: 0f
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        }
        databaseReference.addValueEventListener(listener)
        onDispose {
            databaseReference.removeEventListener(listener)
        }
    }

    val humidityIcon: Painter = painterResource(id = R.drawable.ic_humidity)

    SensorCard(title = "Nem Oranı", value = "${humidityData.value}%", color = Color.Blue, icon = humidityIcon)
}

@Composable
fun FanControlCard() {
    var fanControlData by remember { mutableStateOf(0) }
    val databaseReference = FirebaseDatabase.getInstance().getReference("sensorESP32/fanControl")

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                fanControlData = dataSnapshot.getValue(Int::class.java) ?: 0
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        }
        databaseReference.addValueEventListener(listener)
        onDispose {
            databaseReference.removeEventListener(listener)
        }
    }

    val fanIcon: Painter = painterResource(id = R.drawable.ic_fan)

    SensorCard(
        title = "Fan Kontrol",
        value = if (fanControlData == 1) "Açık" else "Kapalı",
        color = if (fanControlData == 1) Color.Green else Color.Red,
        icon = fanIcon,
        onToggle = {
            val newValue = if (fanControlData == 1) 0 else 1
            databaseReference.setValue(newValue)
            fanControlData = newValue
        }
    )
}

@Composable
fun MotorControlCard() {
    var motorControlData by remember { mutableStateOf(0) }
    val databaseReference = FirebaseDatabase.getInstance().getReference("sensorESP32/motorControl")

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                motorControlData = dataSnapshot.getValue(Int::class.java) ?: 0
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        }
        databaseReference.addValueEventListener(listener)
        onDispose {
            databaseReference.removeEventListener(listener)
        }
    }

    val motorIcon: Painter = painterResource(id = R.drawable.ic_motor)

    SensorCard(
        title = "Motor Kontrol",
        value = if (motorControlData == 1) "Açık" else "Kapalı",
        color = if (motorControlData == 1) Color.Green else Color.Red,
        icon = motorIcon,
        onToggle = {
            val newValue = if (motorControlData == 1) 0 else 1
            databaseReference.setValue(newValue)
            motorControlData = newValue
        }
    )
}

@Composable
fun SoundCard() {
    var soundData by remember { mutableStateOf(0) }
    val databaseReference = FirebaseDatabase.getInstance().getReference("sensorESP32/sound")

    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                soundData = dataSnapshot.getValue(Int::class.java) ?: 0
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
            }
        }
        databaseReference.addValueEventListener(listener)
        onDispose {
            databaseReference.removeEventListener(listener)
        }
    }

    val soundIcon: Painter = painterResource(id = R.drawable.ic_sound)

    SensorCard(
        title = "Ses",
        value = if (soundData == 1) "Açık" else "Kapalı",
        color = if (soundData == 1) Color.Green else Color.Red,
        icon = soundIcon,
        onToggle = {
            val newValue = if (soundData == 1) 0 else 1
            databaseReference.setValue(newValue)
            soundData = newValue
        }
    )
}

@Composable
fun SensorCard(title: String, value: String, color: Color, icon: Painter? = null, onToggle: (() -> Unit)? = null) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable { onToggle?.invoke() }, // Kartın üzerine tıklama işlevi ekledim
        colors = CardDefaults.elevatedCardColors(containerColor = WhiteCard)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black // Metin rengi
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}
