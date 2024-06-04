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
                val listenerEnabledRef = database.getReference("settings/microphoneListenerEnabled")

                listenerEnabledRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(listenerSnapshot: DataSnapshot) {
                        val listenerEnabled = listenerSnapshot.getValue(Boolean::class.java) ?: true

                        if (microphoneValue == 1) {
                            if (listenerEnabled) {
                                // Bebek ağlaması algılandı ve dinleyici etkin
                                database.getReference("sensorESP32/motorControl").setValue(1)
                                database.getReference("sensorESP32/sound").setValue(1)
                                sendNotification(
                                    "Bebek ağlaması algılandı",
                                    "Beşik 1 Dakikalığına sallanıyor ve ninni çalınıyor."
                                )

                                job = CoroutineScope(Dispatchers.IO).launch {
                                    delay(1 * 30 * 1000) // 1 dakika
                                    database.getReference("sensorESP32/motorControl").setValue(0)
                                    database.getReference("sensorESP32/sound").setValue(0)
                                    database.getReference("sensorESP32/mikrofon").setValue(0)
                                }
                            } else {
                                // Bebek ağlaması algılandı fakat dinleyici etkin değil
                                sendNotification(
                                    "Bebek ağlaması algılandı",
                                    "Beşik ve ninni çalınmadı çünkü dinleyici etkin değil."
                                )
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("MicrophoneListener", "Listener okunurken hata error: ${error.message}")
                    }
                })
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
        job?.cancel()
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "BABY_MONITOR_CHANNEL")
            .setSmallIcon(R.drawable.ic_notifications) // Bildirim ikonu
            .setContentTitle(title) // Bildirim başlığı
            .setContentText(message) // Bildirim içeriği
            .setPriority(NotificationCompat.PRIORITY_MAX) // Bildirimin önceliği
            .build()

        notificationManager.notify(1, notification)
    }
}
