#include <SoftwareSerial.h>       //Software Serial library
SoftwareSerial espSerial(10, 11);   //Pin 2 and 3 act as RX and TX. Connect them to TX and RX of ESP8266      
#define DEBUG true
String mySSID = "bharathiDurga";       // WiFi SSID
String myPWD = "12345678"; // WiFi Password
String myAPI = "ZPFJUIWSIL7UN5IA";   // API Key
String myHOST = "api.thingspeak.com";
String myPORT = "80";
//String myFIELD = "field1"; 

void cloudPost(String, double, double, int);
long cm;

int level;
const int trigPin = 7;
const int echoPin = 8;

void setup()
{
  Serial.begin(9600);
  espSerial.begin(115200);
  
  espData("AT+RST", 1000, DEBUG);                      //Reset the ESP8266 module
  espData("AT+CWMODE=1", 1000, DEBUG);                 //Set the ESP mode as station mode
  espData("AT+CWJAP=\""+ mySSID +"\",\""+ myPWD +"\"", 1000, DEBUG);   //Connect to WiFi network
  /*while(!esp.find("OK")) 
  {          
      //Wait for connection
  }*/
  delay(1000);
  
}

  void loop()
  {
    /* Here, I'm using the function random(range) to send a random value to the 
     ThingSpeak API. You can change this value to any sensor data
     so that the API will show the sensor data  
    */
    
    long duration, inches;
  double longitude = 80.5290;
  double latitude = 16.3606;
  int BinID = 1201;
  // The sensor is triggered by a HIGH pulse of 10 or more microseconds.
  // Give a short LOW pulse beforehand to ensure a clean HIGH pulse:
  pinMode(trigPin, OUTPUT);
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  // Read the signal from the sensor: a HIGH pulse whose
  // duration is the time (in microseconds) from the sending
  // of the ping to the reception of its echo off of an object.
  pinMode(echoPin, INPUT);
  duration = pulseIn(echoPin, HIGH);

  // convert the time into a distance
  inches = microsecondsToInches(duration);
  cm = microsecondsToCentimeters(duration);
  
  Serial.print("Bin Level:: ");
  Serial.print(cm);
  Serial.println(" cm");
  delay(1000);

//Level Calibration

if (cm <= 5)
  {
    level = 100;
    Serial.print("Measure: ");
    Serial.println(cm);
    Serial.print("Level: ");
    Serial.println(level);
  }
  else if (cm > 5 && cm <= 9)
  {
    level = 90;
    Serial.print("Measure: ");
    Serial.println(cm);
    Serial.print("Level: ");
    Serial.println(level);
  }
  else if (cm > 9 && cm <= 14)
  {
    level = 80;
    Serial.print("Measure: ");
    Serial.println(cm);
    Serial.print("Level: ");
    Serial.println(level);
  }
  else if (cm > 14 && cm <= 18)
  {
    level = 70;
    Serial.print("Measure: ");
    Serial.println(cm);
    Serial.print("Level: ");
    Serial.println(level);
  }
  else if (cm > 19 && cm <= 23)
  {
    level = 60;
    Serial.print("Measure: ");
    Serial.println(cm);
    Serial.print("Level: ");
    Serial.println(level);
  }
  else if (cm > 23 && cm <= 28)
  {
    level = 50;
    Serial.print("Measure: ");
    Serial.println(cm);
    Serial.print("Level: ");
    Serial.println(level);
  }
  else if (cm > 28 && cm <= 32)
  {
    level = 40;
    Serial.print("Measure: ");
    Serial.println(cm);
    Serial.print("Level: ");
    Serial.println(level);
  }
  else if (cm > 32 && cm <= 36)
  {
    level = 30;
    Serial.print("Measure: ");
    Serial.println(cm);
    Serial.print("Level: ");
    Serial.println(level);
  }
  else if (cm > 36 && cm <= 40)
  {
    level = 20;
    Serial.print("Measure: ");
    Serial.println(cm);
    Serial.print("Level: ");
    Serial.println(level);
  }
  else if (cm > 40 && cm <= 45)
  {
    level = 10;
    Serial.print("Measure: ");
    Serial.println(cm);
    Serial.print("Level: ");
    Serial.println(level);
  }
  else if (cm > 45)
  {
    level = 0;
    Serial.println("Bin Empty");
  }

//************************End Code for Ultrasonic Sensor **********************************
  
  delay(5000);
    String sendData = "GET /update?api_key="+ myAPI +"&field1=" + BinID +"&field2=" + String(latitude) + "&field3=" + String(longitude) +"&field4=" + level ;
    espData("AT+CIPMUX=1", 1000, DEBUG);       //Allow multiple connections
    espData("AT+CIPSTART=0,\"TCP\",\""+ myHOST +"\","+ myPORT, 1000, DEBUG);
    espData("AT+CIPSEND=0," +String(sendData.length()+4),1000,DEBUG);  
    espSerial.find(">"); 
    espSerial.println(sendData);
    Serial.print("Value to be sent: ");
 // Serial.println(sendVal);
     
    espData("AT+CIPCLOSE=0",1000,DEBUG);
    delay(15000);
  }

  String espData(String command, const int timeout, boolean debug)
  {
    Serial.print("AT Command ==> ");
    Serial.print(command);
    Serial.println("     ");
    
    String response = "";
    espSerial.println(command);
    long int time = millis();
  while ( (time + timeout) > millis())
  {
    while (espSerial.available())
    {
      char c = espSerial.read();
      response += c;
    }
  }
  if (debug)
  {
    //Serial.print(response);
  }
  return response;
}
long microsecondsToInches(long microseconds)
{
  // According to Parallax's datasheet for the PING))), there are
  // 73.746 microseconds per inch (i.e. sound travels at 1130 feet per
  // second).  This gives the distance travelled by the ping, outbound
  // and return, so we divide by 2 to get the distance of the obstacle.
  // See: http://www.parallax.com/dl/docs/prod/acc/28015-PING-v1.3.pdf
  return microseconds / 74 / 2;
}

long microsecondsToCentimeters(long microseconds)
{
  // The speed of sound is 340 m/s or 29 microseconds per centimeter.
  // The ping travels out and back, so to find the distance of the
  // object we take half of the distance travelled.
  return microseconds / 29 / 2;
}

