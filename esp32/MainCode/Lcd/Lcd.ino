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

#define DHTPIN 2         // DHT11 sensörünün veri pini
#define DHTTYPE DHT11     // Sensör tipi

#define WIFI_SSID "İlkcan53"
#define WIFI_PASSWORD "ilkcan5353"
#define API_KEY "AIzaSyDC1tdIUc93KMK5igXftqZFQnZLoZgJwKM"
#define DATABASE_URL "https://babymonitorwithesp32-default-rtdb.europe-west1.firebasedatabase.app" 

LiquidCrystal_I2C lcd(0x27, 16, 2);
DHT dht(2, DHT11);

FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
unsigned long sendDataPrevMillis = 0;
bool signupOK = false;
const long interval = 5000;  // Verileri her 5 saniyede bir gönder
unsigned long lastSendTime = 0;

///PİNLER  
int fanPinEN = 5;  // PWM 
int fanPinIN1 = 16; // Direction pin 1
int fanPinIN2 = 17; // Direction pin 2

int motorPinEN = 25;  // PWM 
int motorPinIN1 = 32; // Direction pin 1
int motorPinIN2 = 33; // Direction pin 2

const int RX_PIN = 18;  // ESP32'nin RX pini
const int TX_PIN = 19;  // ESP32'nin TX pini

int upButton = 27;
int downButton = 14;
int selectButton =12;
int menu = 1;

int carbonSensorPin = 34;

SoftwareSerial mySoftwareSerial(RX_PIN, TX_PIN); // RX, TX
DFRobotDFPlayerMini myDFPlayer;


void setup() 
{
  lcd.init();
  lcd.backlight();
  lcd.print("   Baby Sense");
  delay(3000);
  lcd.clear();
  pinMode(upButton, INPUT_PULLUP);
  pinMode(downButton, INPUT_PULLUP);
  pinMode(selectButton, INPUT_PULLUP);
  dht.begin();
  updateMenu();
  Serial.begin(115200);
  mySoftwareSerial.begin(9600);

  pinMode(motorPinEN, OUTPUT);  // Set the PWM pin as output
  pinMode(motorPinIN1, OUTPUT); // Set the direction pin 1 as output
  pinMode(motorPinIN2, OUTPUT); // Set the direction pin 2 as output
  pinMode(fanPinEN, OUTPUT);  // Set the PWM pin as output
  pinMode(fanPinIN1, OUTPUT); // Set the direction pin 1 as output
  pinMode(fanPinIN2, OUTPUT); // Set the direction pin 2 as output
  pinMode(DHTPIN,OUTPUT);
  pinMode(carbonSensorPin, OUTPUT); //carbondioksit. sensor
  pinMode(RX_PIN, OUTPUT);
  pinMode(TX_PIN, OUTPUT); 

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
  myDFPlayer.begin(mySoftwareSerial);
  /*if (!myDFPlayer.begin(mySoftwareSerial)) {
    Serial.println("DFPlayer Mini'ye baglanirken bir hata olustu. Modulu kontrol edin.");
    while (true);
  }*/
  Serial.println("DFPlayer Mini baglandi.");
  myDFPlayer.volume(25);
}

void updateMenu() {
  switch (menu) {
    case 0:
      menu = 1;
      break;
    case 1:
      lcd.clear();
      lcd.print(">Oto sal. On/Off");
      lcd.setCursor(0, 1);
      lcd.print(" Fan On/Off");
      break;
    case 2:
      lcd.clear();
      lcd.print(" Oto sal. On/Off");
      lcd.setCursor(0, 1);
      lcd.print(">Fan On/Off");
      break;
    case 3:
      lcd.clear();
      lcd.print(">Ort. degerleri");
      lcd.setCursor(0, 1);
      lcd.print(" Ninni On/Off");
      break;
    case 4:
      lcd.clear();
      lcd.print(" Ort. degerleri");
      lcd.setCursor(0, 1);
      lcd.print(">Ninni On/Off");
      break;
    case 5:
      menu = 4;
      break;
  }
}

void executeAction() {
  switch (menu) {
    case 1:
      action1();
      break;
    case 2:
      action2();
      break;
    case 3:
      action3();
      break;
    case 4:
      action4();
      break;
  }
}

void action1() 
{
  lcd.clear();
  if (Firebase.RTDB.getInt(&fbdo, "/sensorESP32/motorControl")) 
  {
    int motorStatus = fbdo.intData();
    if (motorStatus == 1) 
    {
      lcd.print("Oto sal. kapali");
      Firebase.RTDB.setInt(&fbdo, "/sensorESP32/motorControl", 0);
    } 
      else 
      {
        lcd.print("  Oto sal. acik");
        Firebase.RTDB.setInt(&fbdo, "/sensorESP32/motorControl", 1);
      }
  }
  delay(1500);
}
void action2() 
{
  lcd.clear();
  if (Firebase.RTDB.getInt(&fbdo, "/sensorESP32/fanControl")) 
  {
    int FanStatus = fbdo.intData();
    if (FanStatus == 1) 
    {
      lcd.print("   Fan kapali");
      Firebase.RTDB.setInt(&fbdo, "/sensorESP32/fanControl", 0);
    } 
      else 
      {
        lcd.print("    Fan acik");
        Firebase.RTDB.setInt(&fbdo, "/sensorESP32/fanControl", 1);
      }
  }
  delay(1500);
}
void action3() 
{
  lcd.clear();
  if (Firebase.RTDB.getInt(&fbdo, "/sensorESP32/isi")) 
  {
    int temp = fbdo.intData();
    lcd.print("Sicaklik: ");
    lcd.print(temp);
    lcd.print(" C");
    lcd.setCursor(0,1);
  }
  if (Firebase.RTDB.getInt(&fbdo, "/sensorESP32/co2")) 
  {
    int quality = fbdo.intData();
    lcd.print("Co2: ");
    lcd.print(quality);
  }
  delay(5000);
}
void action4() 
{
  lcd.clear();
  if (Firebase.RTDB.getInt(&fbdo, "/sensorESP32/sound")) 
  {
    int SoundStatus = fbdo.intData();
    if (SoundStatus == 1) 
    {
      lcd.print("   Ninni kapali");
      Firebase.RTDB.setInt(&fbdo, "/sensorESP32/sound", 0);
    } 
      else 
      {
        lcd.print("    Ninni acik");
        Firebase.RTDB.setInt(&fbdo, "/sensorESP32/sound", 1);
      }
  }
  delay(1500);
}

void action5() 
{  
  Serial.print("ACTİONA GİRDİM");
  float humidity = dht.readHumidity();
  float temperature = dht.readTemperature();

  if (isnan(humidity) || isnan(temperature)) {
    Serial.println("Sensörden veri okunamadı!");
    return;
  }

  Serial.println("Nem Orani"); 
  Firebase.RTDB.setInt(&fbdo, "/sensorESP32/nemOrani", humidity);     
  Serial.println("Sıcaklık"); 
  Firebase.RTDB.setInt(&fbdo, "/sensorESP32/sicaklik", temperature);

}

int previousSoundStatus = -1; // Başlangıçta geçersiz bir değer

void checkSound() {
  if (Firebase.RTDB.getInt(&fbdo, "/sensorESP32/sound")) {
    int soundStatus = fbdo.intData();
    if (soundStatus != previousSoundStatus) {
      if (soundStatus == 1) {
        myDFPlayer.play(1);
        delay(1000);
        Serial.println("Müzik Çalıyor");
      } else {
        Serial.println("Girdim");
        myDFPlayer.stop();
        delay(1000);
        Serial.println("Müzik Durdu");
      }
      previousSoundStatus = soundStatus; // Önceki durumu güncelle
    }
  } else {
    Serial.print("Ses kontrolü hatası: ");
    Serial.println(fbdo.errorReason());
  }
}

///////////////////////////////////////////////////////////////////////////


void loop() 
{
  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 10000 || sendDataPrevMillis == 0)) 
  {
    //action5();
    checkSound();  // Ses kontrolünü gerçekleştir
    sendDataPrevMillis = millis();
    if (Firebase.RTDB.getInt(&fbdo, "/sensorESP32/motorControl")) 
    {
       int motorStatus = fbdo.intData();
         Serial.println(motorStatus);
      if (motorStatus == 1) 
      {
        
        digitalWrite(motorPinIN1, HIGH);
        digitalWrite(motorPinIN2, LOW);
        analogWrite(motorPinEN, 100); // Motoru çalıştır
        Serial.println("Motor Çalışıyor");
      } 
      else 
      { 
        digitalWrite(motorPinIN1, LOW);
        digitalWrite(motorPinIN2, LOW);
        analogWrite(motorPinEN, 0); // Motoru durdur
        Serial.println("Motor Durdu!!!");
      }
    } 
    else 
    {
      Serial.print("Hata: ");
      Serial.println(fbdo.errorReason());
    }
    
    if (Firebase.RTDB.getInt(&fbdo, "/sensorESP32/fanControl")) {
       int fanStatus = fbdo.intData();
         Serial.println(fanStatus);
      if (fanStatus == 1) {
        digitalWrite(fanPinIN1, HIGH);
        digitalWrite(fanPinIN2, LOW);
        analogWrite(fanPinEN, 255);
        Serial.println("Fan Çalışıyor");
      } else {
        digitalWrite(fanPinIN1, LOW);
        digitalWrite(fanPinIN2, LOW);
        analogWrite(fanPinEN, 0);
        Serial.println("Fan Durdu!!!");
      }
    } else {
      Serial.print("Hata: ");
      Serial.println(fbdo.errorReason());
    }
    //checkSound();  // Ses kontrolünü gerçekleştir

  }

  /*if (millis() - lastSendTime > interval) {
    int sensorValue = analogRead(carbonSensorPin);
    Serial.println(sensorValue);

    if (Firebase.ready() && auth.token.uid.c_str()) { // Kontrol edin ki oturum açılmış olsun
      if (!Firebase.RTDB.setInt(&fbdo, "/sensorESP32/co2", sensorValue)) {
        Serial.print("Firebase gönderim hatası: ");
        Serial.println(fbdo.errorReason());
      }
    } else {
      Serial.println("Firebase servisi veya oturum hazır değil.");
    }

    lastSendTime = millis();
  }*/


  /*if (!digitalRead(downButton))
  {
    menu++;
    updateMenu();
    delay(100);
    while (!digitalRead(downButton));
  }
  if (!digitalRead(upButton))
  {
    menu--;
    updateMenu();
    delay(100);
    while(!digitalRead(upButton));
  }
  if (!digitalRead(selectButton))
  {
    executeAction();
    updateMenu();
    delay(100);
    while (!digitalRead(selectButton));
  }*/
}