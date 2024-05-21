#include <WiFi.h>
#include <Firebase_ESP_Client.h>

// WiFi ve Firebase ayarları
#define WIFI_SSID "Demir"
#define WIFI_PASSWORD "5472fe77"
#define FIREBASE_HOST "https://babymonitorwithesp32-default-rtdb.europe-west1.firebasedatabase.app" 
#define FIREBASE_AUTH "AIzaSyDC1tdIUc93KMK5igXftqZFQnZLoZgJwKM"

// Sensör ve diğer donanım ayarları
#define SENSOR_PIN 34  // MQ2'nin bağlı olduğu analog pin
#define BUZZER_PIN 15  // Buzzer için kullanılacak pin

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
unsigned long lastSendTime = 0;
const long interval = 2000;  // Verileri her 2 saniyede bir gönder

void setup() {
  Serial.begin(115200);
  pinMode(SENSOR_PIN, INPUT);
  pinMode(BUZZER_PIN, OUTPUT);
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("WiFi'ye bağlanıyor...");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }
  Serial.println("Bağlandı!");
  Serial.print("IP Adresi: ");
  Serial.println(WiFi.localIP());
  
  config.api_key = FIREBASE_AUTH;
  config.database_url = FIREBASE_HOST;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  // Anonim olarak oturum aç
  if (Firebase.signUp(&config, &auth, "", "")) {
    Serial.println("Anonim oturum açma başarılı");
  } else {
    Serial.println("Anonim oturum açma başarısız: ");
  }
}

void loop() {
  if (millis() - lastSendTime > interval) {
    int sensorValue = analogRead(SENSOR_PIN);
    Serial.println(sensorValue);

    if (sensorValue > 2000) {
      digitalWrite(BUZZER_PIN, HIGH);
    } else {
      digitalWrite(BUZZER_PIN, LOW);
    }

    if (Firebase.ready() && auth.token.uid.c_str()) { // Kontrol edin ki oturum açılmış olsun
      if (!Firebase.RTDB.setInt(&fbdo, "/sensorESP32/co2", sensorValue)) {
        Serial.print("Firebase gönderim hatası: ");
        Serial.println(fbdo.errorReason());
      }
    } else {
      Serial.println("Firebase servisi veya oturum hazır değil.");
    }

    lastSendTime = millis();
  }
}
