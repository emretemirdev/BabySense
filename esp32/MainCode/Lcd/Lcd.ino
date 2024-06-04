#include <Wire.h>
#include <LiquidCrystal_I2C.h>
#include <Arduino.h>
#include <Firebase_ESP_Client.h>
#include "addons/TokenHelper.h"
#include "addons/RTDBHelper.h"
#include <DHT.h>
#include <SoftwareSerial.h>
#include <DFRobotDFPlayerMini.h>

#if defined(ESP32)
  #include <WiFi.h>
#elif defined(ESP8266)
  #include <ESP8266WiFi.h>
#endif

#define DHTPIN 23         // DHT11 sensörünün veri pini
#define DHTTYPE DHT11     // Sensör tipi

#define WIFI_SSID "İlkcan53"
#define WIFI_PASSWORD "ilkcan5353"
#define API_KEY "AIzaSyDC1tdIUc93KMK5igXftqZFQnZLoZgJwKM"
#define DATABASE_URL "https://babymonitorwithesp32-default-rtdb.europe-west1.firebasedatabase.app"

LiquidCrystal_I2C lcd(0x27, 16, 2);
DHT dht(DHTPIN, DHTTYPE);

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
unsigned long sendDataPrevMillis = 0;
bool signupOK = false;
const long interval = 5000;  // Verileri her 5 saniyede bir gönder
unsigned long lastSendTime = 0;

int fanPinEN = 5;  // PWM 
int fanPinIN1 = 17; // Direction pin 1
int fanPinIN2 = 16; // Direction pin 2

int motorPinEN = 25;  // PWM 
int motorPinIN1 = 32; // Direction pin 1
int motorPinIN2 = 33; // Direction pin 2

const int RX_PIN = 18;  // ESP32'nin RX pini
const int TX_PIN = 19;  // ESP32'nin TX pini

int upButton = 27;
int downButton = 14;
int selectButton = 12;
int menu = 1;

int carbonSensorPin = 34;
int microphonePin = 35;
int buzzerPin = 26;

SoftwareSerial mySoftwareSerial(RX_PIN, TX_PIN);
DFRobotDFPlayerMini myDFPlayer;

void setupWiFi() {
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  lcd.print("      Wi-Fi     ");
  lcd.setCursor(0, 1);
  lcd.print("  Baglaniyor... ");

  while (WiFi.status() != WL_CONNECTED) {
    delay(300);
    Serial.print(".");
  }
  Serial.println();
  Serial.print("Bağlanılan IP: ");
  Serial.println(WiFi.localIP());
}

void setupFirebase() {
  config.api_key = API_KEY;
  config.database_url = DATABASE_URL;

  if (Firebase.signUp(&config, &auth, "", "")) {
    signupOK = true;
    Serial.println("Firebase: Kayit basarili");
  } else {
    Serial.printf("Firebase: Kayit basarisiz, %s\n", config.signer.signupError.message.c_str());
  }
  config.token_status_callback = tokenStatusCallback;
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);
}

void setup() {
  lcd.init();
  lcd.backlight();
  lcd.print("   Baby Sense");
  delay(3000);
  lcd.clear();

  pinMode(upButton, INPUT_PULLUP);
  pinMode(downButton, INPUT_PULLUP);
  pinMode(selectButton, INPUT_PULLUP);

  dht.begin();
  Serial.begin(115200);
  mySoftwareSerial.begin(9600);

  pinMode(motorPinEN, OUTPUT);
  pinMode(motorPinIN1, OUTPUT);
  pinMode(motorPinIN2, OUTPUT);
  pinMode(fanPinEN, OUTPUT);
  pinMode(fanPinIN1, OUTPUT);
  pinMode(fanPinIN2, OUTPUT);
  pinMode(DHTPIN, INPUT);
  pinMode(carbonSensorPin, INPUT);
  pinMode(microphonePin, INPUT);
  pinMode(buzzerPin, OUTPUT);

  setupWiFi();
  setupFirebase();

  myDFPlayer.begin(mySoftwareSerial);
  myDFPlayer.volume(25);
  Serial.println("DFPlayer Mini baglandi.");

  updateMenu();
}

void updateMenu() {
  lcd.clear();
  switch (menu) {
    case 1:
      lcd.print(">Oto sal. On/Off");
      lcd.setCursor(0, 1);
      lcd.print(" Fan On/Off");
      break;
    case 2:
      lcd.print(" Oto sal. On/Off");
      lcd.setCursor(0, 1);
      lcd.print(">Fan On/Off");
      break;
    case 3:
      lcd.print(">Ort. degerleri");
      lcd.setCursor(0, 1);
      lcd.print(" Ninni On/Off");
      break;
    case 4:
      lcd.print(" Ort. degerleri");
      lcd.setCursor(0, 1);
      lcd.print(">Ninni On/Off");
      break;
    default:
      menu = 1;
      updateMenu();
      break;
  }
  Serial.print("Menu guncellendi: ");
  Serial.println(menu);
}

void toggleControl(const char* path, const char* onMsg, const char* offMsg) {
  lcd.clear();
  if (Firebase.RTDB.getInt(&fbdo, path)) {
    int status = fbdo.intData();
    lcd.print(status == 1 ? offMsg : onMsg);
    Firebase.RTDB.setInt(&fbdo, path, status == 1 ? 0 : 1);
    Serial.print(path);
    Serial.print(" degistirildi: ");
    Serial.println(status == 1 ? 0 : 1);
  } else {
    Serial.print("Firebase'den veri alinamadi: ");
    Serial.println(path);
  }
  delay(1500);
}

void displaySensorValues() {
  lcd.clear();
  if (Firebase.RTDB.getInt(&fbdo, "/sensorESP32/isi")) {
    lcd.print("Sicaklik: ");
    lcd.print(fbdo.intData());
    lcd.print(" C");
    lcd.setCursor(0, 1);
    Serial.print("Sicaklik: ");
    Serial.print(fbdo.intData());
    Serial.println(" C");
  }
  if (Firebase.RTDB.getInt(&fbdo, "/sensorESP32/co2")) {
    lcd.print("Co2: ");
    lcd.print(fbdo.intData());
    Serial.print("CO2: ");
    Serial.println(fbdo.intData());
  }
  delay(5000);
}

void handleMotorControl(int motorPinIN1, int motorPinIN2, int motorPinEN, const char* path) {
  if (Firebase.RTDB.getInt(&fbdo, path)) {
    int status = fbdo.intData();
    digitalWrite(motorPinIN1, status == 1 ? HIGH : LOW);
    digitalWrite(motorPinIN2, status == 1 ? LOW : LOW);
    analogWrite(motorPinEN, status == 1 ? 155 : 0);
    Serial.print(path);
    Serial.print(" kontrol durumu: ");
    Serial.println(status == 1 ? "ACIK" : "KAPALI");
  } else {
    Serial.print("Firebase'den veri alinamadi: ");
    Serial.println(path);
  }
}

void readDHTSensor() {
  float humidity = dht.readHumidity();
  float temperature = dht.readTemperature();

  if (isnan(humidity) || isnan(temperature)) {
    Serial.println("DHT11: Sensorden veri okunamadi!");
    lcd.clear();
    lcd.print("DHT11 HATA");
    delay(2000);
    return;
  }

  Firebase.RTDB.setFloat(&fbdo, "/sensorESP32/nemOrani", humidity);     
  Firebase.RTDB.setFloat(&fbdo, "/sensorESP32/isi", temperature);
  Serial.print("Nem Orani: "); 
  Serial.println(humidity);
  Serial.print("Sicaklik: "); 
  Serial.println(temperature); 
}

void checkSound() {
  static int previousSoundStatus = -1;
  if (Firebase.RTDB.getInt(&fbdo, "/sensorESP32/sound")) {
    int soundStatus = fbdo.intData();
    if (soundStatus != previousSoundStatus) {
      if (soundStatus == 1) {
        myDFPlayer.play(1);
        Serial.println("Muzik caliyor");
      } else {
        myDFPlayer.stop();
        Serial.println("Muzik durdu");
      }
      previousSoundStatus = soundStatus;
    }
  } else {
    Serial.print("Ses kontrolu hatasi: ");
    Serial.println(fbdo.errorReason());
  }
}

void checkMicrophone() {
  static unsigned long lastCheckTime = 0;
  static int soundCount = 0;

  if (millis() - lastCheckTime >= 1000) {
    lastCheckTime = millis();
    
    int soundLevel = digitalRead(microphonePin);
    if (soundLevel == HIGH) {
      soundCount++;
    }

    if (soundCount > 3) {
      Firebase.RTDB.setInt(&fbdo, "/sensorESP32/mikrofon", 1);
      Serial.println("Mikrofon aktif");
    } else {
      Firebase.RTDB.setInt(&fbdo, "/sensorESP32/mikrofon", 0);
      Serial.println("Mikrofon pasif");
    }

    static unsigned long lastResetTime = 0;
    if (millis() - lastResetTime >= 10000) {
      lastResetTime = millis();
      soundCount = 0;
    }
  }
}

void handleMenuSelection() {
  switch (menu) {
    case 1:
      toggleControl("/sensorESP32/motorControl", "  Oto sal. acik", "Oto sal. kapali");
      break;
    case 2:
      toggleControl("/sensorESP32/fanControl", "    Fan acik", "   Fan kapali");
      break;
    case 3:
      displaySensorValues();
      break;
    case 4:
      toggleControl("/sensorESP32/sound", "    Ninni acik", "   Ninni kapali");
      break;
  }
  updateMenu();
}

void readCO2Sensor() {
  const int numReadings = 10;
  int readings[numReadings];
  int total = 0;
  int average = 0;

  for (int i = 0; i < numReadings; i++) {
    readings[i] = analogRead(carbonSensorPin);
    total += readings[i];
    delay(50);
  }

  average = total / numReadings;

  if (Firebase.ready() && auth.token.uid.c_str()) {
    if (!Firebase.RTDB.setInt(&fbdo, "/sensorESP32/co2", average)) {
      Serial.print("Firebase gonderim hatasi: ");
      Serial.println(fbdo.errorReason());
    }
  } else {
    Serial.println("Firebase servisi veya oturum hazir degil.");
  }

  Serial.print("CO2 sensor degeri: ");
  Serial.println(average);

  if (average > 2000) { 
    digitalWrite(buzzerPin, HIGH);
    Serial.println("Buzzer acik");
  } else {
    digitalWrite(buzzerPin, LOW);
    Serial.println("Buzzer kapali");
  }
}

void loop() {
  checkMicrophone();
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 10000 || sendDataPrevMillis == 0)) {
    sendDataPrevMillis = millis();
    readDHTSensor();
    handleMotorControl(motorPinIN1, motorPinIN2, motorPinEN, "/sensorESP32/motorControl");
    handleMotorControl(fanPinIN1, fanPinIN2, fanPinEN, "/sensorESP32/fanControl");
    checkSound();
  }

  if (millis() - lastSendTime > interval) {
    readCO2Sensor();
    lastSendTime = millis();
  }

  if (!digitalRead(downButton)) {
    menu++;
    updateMenu();
    delay(100);
    while (!digitalRead(downButton));
  }
  if (!digitalRead(upButton)) {
    menu--;
    updateMenu();
    delay(100);
    while(!digitalRead(upButton));
  }
  if (!digitalRead(selectButton)) {
    handleMenuSelection();
    delay(100);
    while (!digitalRead(selectButton));
  }
}
