package com.emretemir.babymonitorwithesp32.services

import android.app.NotificationManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import com.emretemir.babymonitorwithesp32.R
import com.google.firebase.database.*

class FanControlListener(private val context: Context, private val database: FirebaseDatabase) {

    private val temperatureRef = database.getReference("sensorESP32/isi")
    private val co2Ref = database.getReference("sensorESP32/co2")
    private val fanRef = database.getReference("sensorESP32/fanControl")
    private val settingsRef = database.getReference("settings")

    private var temperatureListener: ValueEventListener? = null
    private var co2Listener: ValueEventListener? = null
    private var fanStatusListener: ValueEventListener? = null
    private var settingsListener: ValueEventListener? = null
    private var isFanManuallyControlled: Boolean = false
    private var isFanOn: Boolean = false
    private var maxTemperature: Double = Double.MAX_VALUE
    private var maxCO2: Int = Int.MAX_VALUE
    private val handler = Handler(Looper.getMainLooper())

    fun startListening() {
        addSettingsListener()
        addFanStatusListener()
        addTemperatureListener()
        addCO2Listener()
    }

    private fun addSettingsListener() {
        settingsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                maxTemperature = snapshot.child("maxTemperature").getValue(Double::class.java) ?: Double.MAX_VALUE
                maxCO2 = snapshot.child("maxCO2").getValue(Int::class.java) ?: Int.MAX_VALUE
            }

            override fun onCancelled(error: DatabaseError) {
                logError("Settings read error", error)
            }
        }
        settingsRef.addValueEventListener(settingsListener as ValueEventListener)
    }

    private fun addFanStatusListener() {
        fanStatusListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                isFanOn = snapshot.getValue(Int::class.java) == 1
                isFanManuallyControlled = snapshot.hasChild("manual")
            }

            override fun onCancelled(error: DatabaseError) {
                logError("Fan status read error", error)
            }
        }
        fanRef.addValueEventListener(fanStatusListener as ValueEventListener)
    }

    private fun addTemperatureListener() {
        temperatureListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val temperature = snapshot.getValue(Double::class.java) ?: return
                controlFan(temperature, null)
            }

            override fun onCancelled(error: DatabaseError) {
                logError("Temperature read error", error)
            }
        }
        temperatureRef.addValueEventListener(temperatureListener as ValueEventListener)
    }

    private fun addCO2Listener() {
        co2Listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val co2 = snapshot.getValue(Int::class.java) ?: return
                controlFan(null, co2)
            }

            override fun onCancelled(error: DatabaseError) {
                logError("CO2 read error", error)
            }
        }
        co2Ref.addValueEventListener(co2Listener as ValueEventListener)
    }

    private fun controlFan(temperature: Double?, co2: Int?) {
        if (isFanManuallyControlled) {
            Log.d("FanControlListener", "Fan is manually controlled, no automatic actions taken.")
            return
        }

        var shouldTurnOn = false
        var reason = ""

        if (temperature != null && temperature > maxTemperature) {
            shouldTurnOn = true
            reason = "Yüksek sıcaklık nedeniyle fan açıldı."
            handleFanOnTimeout("Sıcaklık hala düşmedi. Lütfen beşiği kontrol ediniz!", temperature)
        }

        if (co2 != null && co2 > maxCO2) {
            shouldTurnOn = true
            reason = "Yüksek CO2 seviyesi nedeniyle fan açıldı."
            handleFanOnTimeout("CO2 seviyesi hala yüksek. Lütfen beşiği kontrol ediniz!", co2)
        }

        if (shouldTurnOn && !isFanOn) {
            updateFanStatus(true, reason)
        } else if (!shouldTurnOn && isFanOn) {
            updateFanStatus(false, "Sıcaklık ve CO2 seviyeleri normale döndü, fan kapandı.")
        }
    }

    private fun handleFanOnTimeout(notificationMessage: String, currentValue: Number) {
        handler.postDelayed({
            // Yeniden sıcaklık veya CO2 değerini kontrol et
            val currentTemperature = maxTemperature // Değeri Firebaseden al
            val currentCO2 = maxCO2 // Değeri Firebaseden al

            if (currentValue.toDouble() > maxTemperature || currentValue.toInt() > maxCO2) {
                sendNotification("Fan Açık", notificationMessage)
            } else {
                updateFanStatus(false, "Sıcaklık ve CO2 seviyeleri normale döndü, fan kapandı.")
            }
        }, 30000) // 30 saniye sonra kontrol
    }

    private fun updateFanStatus(turnOn: Boolean, message: String) {
        val fanRef = database.getReference("sensorESP32/fanControl")
        fanRef.setValue(if (turnOn) 1 else 0)
        sendNotification(if (turnOn) "Fan Açıldı" else "Fan Kapandı", message)
        isFanOn = turnOn
        Log.d("FanControlListener", "Fan turned ${if (turnOn) "ON" else "OFF"}")
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "BABY_MONITOR_CHANNEL")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun logError(message: String, error: DatabaseError) {
        Log.e("FanControlListener", "$message: ${error.message}")
    }

    fun stopListening() {
        temperatureListener?.let { temperatureRef.removeEventListener(it) }
        co2Listener?.let { co2Ref.removeEventListener(it) }
        fanStatusListener?.let { fanRef.removeEventListener(it) }
        settingsListener?.let { settingsRef.removeEventListener(it) }
    }
}