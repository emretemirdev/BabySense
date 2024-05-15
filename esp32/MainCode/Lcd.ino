#include <Wire.h>
#include <LiquidCrystal_I2C.h>
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

LiquidCrystal_I2C lcd(0x27, 16, 2);
FirebaseData fbdo;
FirebaseAuth auth;
FirebaseConfig config;
unsigned long sendDataPrevMillis = 0;
bool signupOK = false;
 
int fanPinEN = 5;  // PWM 
int fanPinIN1 = 16; // Direction pin 1
int fanPinIN2 = 17; // Direction pin 2

int motorPinEN = 25;  // PWM 
int motorPinIN1 = 32; // Direction pin 1
int motorPinIN2 = 33; // Direction pin 2

int upButton = 16;
int downButton = 17;
int selectButton =18;
int menu = 1;

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
  updateMenu();
  Serial.begin(115200);
  pinMode(motorPinEN, OUTPUT);  // Set the PWM pin as output
  pinMode(motorPinIN1, OUTPUT); // Set the direction pin 1 as output
  pinMode(motorPinIN2, OUTPUT); // Set the direction pin 2 as output
  pinMode(fanPinEN, OUTPUT);  // Set the PWM pin as output
  pinMode(fanPinIN1, OUTPUT); // Set the direction pin 1 as output
  pinMode(fanPinIN2, OUTPUT); // Set the direction pin 2 as output
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

void loop() 
{

  if (Firebase.ready() && signupOK && (millis() - sendDataPrevMillis > 10000 || sendDataPrevMillis == 0)) 
  {
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
  }


  if (!digitalRead(downButton))
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
  }
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
      lcd.print(" MenuItem4");
      break;
    case 4:
      lcd.clear();
      lcd.print(" Ort. degerleri");
      lcd.setCursor(0, 1);
      lcd.print(">MenuItem4");
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
void action4() {
  lcd.clear();
  lcd.print(">Executing #4");
  delay(1500);
}