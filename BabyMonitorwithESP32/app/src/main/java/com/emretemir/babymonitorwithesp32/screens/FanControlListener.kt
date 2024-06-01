package com.emretemir.babymonitorwithesp32

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.*

class FanControlListener(private val context: Context, private val database: FirebaseDatabase) {
    private var temperatureListener: ValueEventListener? = null
    private var co2Listener: ValueEventListener? = null

    fun startListening() {
        val temperatureRef = database.getReference("sensorESP32/isi")
        val co2Ref = database.getReference("sensorESP32/co2")
        val settingsRef = database.getReference("settings")

        temperatureListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temperature = snapshot.getValue(Double::class.java) ?: return
                settingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(settingsSnapshot: DataSnapshot) {
                        val maxTemperature = settingsSnapshot.child("maxTemperature").getValue(Double::class.java) ?: Double.MAX_VALUE
                        val maxCO2 = settingsSnapshot.child("maxCO2").getValue(Int::class.java) ?: Int.MAX_VALUE
                        controlFan(temperature, null, maxTemperature, maxCO2)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FanControlListener", "Settings read error: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FanControlListener", "Temperature read error: ${error.message}")
            }
        }

        co2Listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val co2 = snapshot.getValue(Int::class.java) ?: return
                settingsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(settingsSnapshot: DataSnapshot) {
                        val maxTemperature = settingsSnapshot.child("maxTemperature").getValue(Double::class.java) ?: Double.MAX_VALUE
                        val maxCO2 = settingsSnapshot.child("maxCO2").getValue(Int::class.java) ?: Int.MAX_VALUE
                        controlFan(null, co2, maxTemperature, maxCO2)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("FanControlListener", "Settings read error: ${error.message}")
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FanControlListener", "CO2 read error: ${error.message}")
            }
        }

        temperatureRef.addValueEventListener(temperatureListener as ValueEventListener)
        co2Ref.addValueEventListener(co2Listener as ValueEventListener)
    }

    private fun controlFan(temperature: Double?, co2: Int?, maxTemperature: Double, maxCO2: Int) {
        val fanRef = database.getReference("sensorESP32/fanControl")

        if (temperature != null) {
            if (temperature > maxTemperature) {
                fanRef.setValue(1)
                sendNotification("Fan Açıldı", "Yüksek sıcaklık nedeniyle fan açıldı.")
                Log.d("FanControlListener", "Fan turned ON due to high temperature")
                return
            }
        }

        if (co2 != null) {
            if (co2 > maxCO2) {
                fanRef.setValue(1)
                sendNotification("Fan Açıldı", "Yüksek CO2 seviyesi nedeniyle fan açıldı.")
                Log.d("FanControlListener", "Fan turned ON due to high CO2")
                return
            }
        }

        fanRef.setValue(0)
        sendNotification("Fan Kapandı", "Sıcaklık ve CO2 seviyeleri normale döndü, fan kapandı.")
        Log.d("FanControlListener", "Fan turned OFF")
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "BABY_MONITOR_CHANNEL")
            .setSmallIcon(R.drawable.ic_notifications) // Bildirim ikonu
            .setContentTitle(title) // Bildirim başlığı
            .setContentText(message) // Bildirim içeriği
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Bildirimin önceliği
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    fun stopListening() {
        temperatureListener?.let {
            database.getReference("sensorESP32/isi").removeEventListener(it)
        }
        co2Listener?.let {
            database.getReference("sensorESP32/co2").removeEventListener(it)
        }
    }
}
