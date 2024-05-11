#include <Arduino.h>
#if defined(ESP32)
  #include <WiFi.h>
#elif defined(ESP8266)
  #include <ESP8266WiFi.h>
#endif
#include <Firebase_ESP_Client.h>

#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"

#define WIFI_SSID "İlkcan53"
#define WIFI_PASSWORD "ilkcan5353"
#define API_KEY "AIzaSyDC1tdIUc93KMK5igXftqZFQnZLoZgJwKM"
#define DATABASE_URL "https://babymonitorwithesp32-default-rtdb.europe-west1.firebasedatabase.app" 

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
unsigned long sendDataPrevMillis = 0;
bool signupOK = false;
 
int motorPinEN = 14;  // PWM 
int motorPinIN1 = 26; // Direction pin 1
int motorPinIN2 = 27; // Direction pin 2

void setup() {
  Serial.begin(115200);
  pinMode(motorPinEN, OUTPUT);  // Set the PWM pin as output
  pinMode(motorPinIN1, OUTPUT); // Set the direction pin 1 as output
  pinMode(motorPinIN2, OUTPUT); // Set the direction pin 2 as output
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Wi-Fi Bağlanıyor");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Bağlanılan IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("ok");
    signupOK = true;
  }
  else {
    Serial.printf("%s\n", config.signer.signupError.message.c_str());
  }
  config.token_status_callback = tokenStatusCallback;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void loop() {
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 10000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();
    if (Firebase.RTDB.getInt(&fbdo, "/sensorData/motorControl")) { // Yolu "/motorControl" olarak güncelledim
       int motorStatus = fbdo.intData();
         Serial.println(motorStatus);
      if (motorStatus == 1) {
        digitalWrite(motorPinIN1, HIGH);
        digitalWrite(motorPinIN2, LOW);
        analogWrite(motorPinEN, 255); // Motoru çalıştır
        Serial.println("Motor Çalışıyor");
      } else {
        digitalWrite(motorPinIN1, LOW);
        digitalWrite(motorPinIN2, LOW);
        analogWrite(motorPinEN, 0); // Motoru durdur
        Serial.println("Motor Durdu!!!");
      }
    } else {
      Serial.print("Hata: ");
      Serial.println(fbdo.errorReason());
    }
  }
}