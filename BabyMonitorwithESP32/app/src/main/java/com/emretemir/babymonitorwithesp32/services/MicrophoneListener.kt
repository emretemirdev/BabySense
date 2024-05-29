package com.emretemir.babymonitorwithesp32

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.*
import kotlinx.coroutines.*

class MicrophoneListener(private val context: Context, private val database: FirebaseDatabase) {
    private var valueEventListener: ValueEventListener? = null
    private var job: Job? = null

    fun startListening() {
        val ref = database.getReference("sensorESP32/mikrofon")

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val microphoneValue = snapshot.getValue(Int::class.java) ?: return
                if (microphoneValue == 1) {
                    // Bebek ağlaması algılandı
                    database.getReference("sensorESP32/motorControl").setValue(1)
                    database.getReference("sensorESP32/sound").setValue(1)
                    sendNotification("Bebek ağlaması algılandı", "Beşik sallanıyor ve ninni çalıyor.")

                    // 5 dakika sonra motorControl ve sound değerlerini 0 yap
                    job = CoroutineScope(Dispatchers.IO).launch {
                        delay(1 * 30 * 1000) // 1 dakika
                        database.getReference("sensorESP32/motorControl").setValue(0)
                        database.getReference("sensorESP32/sound").setValue(0)
                        database.getReference("sensorESP32/mikrofon").setValue(0) //yeni
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MicrophoneListener", "Error: ${error.message}")
            }
        }
        ref.addValueEventListener(valueEventListener as ValueEventListener)
    }

    fun stopListening() {
        valueEventListener?.let {
            database.getReference("sensorESP32/mikrofon").removeEventListener(it)
        }
        job?.cancel() // Gelecek iptali
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "BABY_MONITOR_CHANNEL")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        notificationManager.notify(1, notification)
    }
}
