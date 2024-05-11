package com.emretemir.babymonitorwithesp32.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.emretemir.babymonitorwithesp32.User
import androidx.compose.runtime.*

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Composable
fun HomeScreen(userProfile: User?, onSignOut: () -> Unit) {
    Column {
        userProfile?.let {
            Text(text = "Hoşgeldin, ${it.firstName} ${it.lastName}")
        } ?: Text(text = "Hoşgeldin")

        Spacer(modifier = Modifier.height(8.dp))
        AirQualityCard()

        // Sıcaklık Kartı
        TemperatureCard(temperature = 25.5f)


        Button(onClick = { onSignOut() }, modifier = Modifier.padding(top = 16.dp)) {
            Text("Çıkış Yap")
        }
    }
}

@Composable
fun AirQualityCard() {
    // State variables to hold air quality data
    var airQuality by remember { mutableStateOf(0f) }
    val maxAirQuality = 4096f
    val minAirQuality = 0f
    val safeThreshold = 1800f

    // Firebase reference to the CO2 data
    val databaseReference = FirebaseDatabase.getInstance().getReference("sensorESP32/co2")

    // Fetch data from Firebase
    DisposableEffect(Unit) {
        val listener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(Float::class.java)?.let {
                    airQuality = it
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w("AirQualityCard", "loadAirQuality:onCancelled", databaseError.toException())
            }
        }
        databaseReference.addValueEventListener(listener)
        onDispose {
            databaseReference.removeEventListener(listener)
        }
    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color(0xFFE1F5FE))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Hava Kalitesi - CO2",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Göstergeyi basitçe simüle ediyoruz.
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val sweepAngle = (airQuality - minAirQuality) / (maxAirQuality - minAirQuality) * 180f
                    drawArc(
                        color = if (airQuality <= safeThreshold) Color.Green else Color.Red,
                        startAngle = 180f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        size = this.size,
                        topLeft = Offset(0f, 0f),
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Text(
                    text = "${airQuality.toInt()} PPM",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (airQuality <= safeThreshold) Color.Green else Color.Red
                )
            }
        }
    }
}
@Composable
fun TemperatureCard(temperature: Float) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .size(width = 240.dp, height = 100.dp)
    ) {
        Text(
            text = "23.5C",
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center,
        )
    }
}