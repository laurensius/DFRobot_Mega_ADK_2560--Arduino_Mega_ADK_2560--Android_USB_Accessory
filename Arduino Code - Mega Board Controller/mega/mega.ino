#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#include <Servo.h>

#define PANJANG_DATA 10

#define lampu1 2
#define lampu2 3
#define lampu3 4
#define servo 5
#define gripper 6

AndroidAccessory acc("3Gen-ITDev", /* Manufaktur*/
		     "ADK",        /* Model */
		     "Arduino Mega 2560 ADK and Android", /* Deskripsi */
		     "1.0",        /* Versi */
		     "http://laurensius.3gen-itdev.com",  /* URL */
		     "0000000023071990");  /* Serial */

byte rcvmsg[255];

String LAMPU_1_ON = "LAMPU 1 ON";
String LAMPU_1_OFF = "LAMPU 1 OFF";
String LAMPU_2_ON = "LAMPU 2 ON";
String LAMPU_2_OFF = "LAMPU 2 OFF";
String LAMPU_3_ON = "LAMPU 3 ON";
String LAMPU_3_OFF = "LAMPU 3 OFF";

byte COMMAND_TEXT; 
byte TARGET_LED; 
byte TARGET_SERVO;
byte TARGET_GRIPPER;  

Servo myServo,myGripper;

void setup(){
  Serial.begin(9600);
  acc.powerOn();
  pinMode(lampu1,OUTPUT);
  pinMode(lampu2,OUTPUT);
  pinMode(lampu3,OUTPUT);
  myServo.attach(servo);
  myGripper.attach(gripper);
  
  COMMAND_TEXT = byte(0x0);
  TARGET_LED = byte(0x1);
  TARGET_SERVO = byte(0x2);
  TARGET_GRIPPER = byte(0x3);

}

void loop(){
  if(acc.isConnected()){
    int data_masuk = acc.read(rcvmsg, sizeof(rcvmsg),1);
    if(data_masuk>0){
      String data_masuk;
      if(rcvmsg[0]==COMMAND_TEXT){
        if(rcvmsg[1]==TARGET_LED){
          byte pjg_data_masuk = rcvmsg[2];
          int stop_loop = 3 + pjg_data_masuk;
          for(int x=3;x<stop_loop;x++){
            String byteToString;
            byteToString =  String(char(rcvmsg[x]));
            data_masuk.concat(byteToString);
          }
          if(data_masuk.equals(LAMPU_1_ON)==true){
             digitalWrite(lampu1,1);
             Serial.println("Menyalakan lampu");
          }else
          if(data_masuk.equals(LAMPU_1_OFF)==true){
             digitalWrite(lampu1,0);
             Serial.println("Mematikan lampu");
          }
          if(data_masuk.equals(LAMPU_2_ON)==true){
             digitalWrite(lampu2,1);
             Serial.println("Menyalakan lampu");
          }else
          if(data_masuk.equals(LAMPU_2_OFF)==true){
             digitalWrite(lampu2,0);
             Serial.println("Mematikan lampu");
          }
          if(data_masuk.equals(LAMPU_3_ON)==true){
             digitalWrite(lampu3,1);
             Serial.println("Menyalakan lampu");
          }else
          if(data_masuk.equals(LAMPU_3_OFF)==true){
             digitalWrite(lampu3,0);
             Serial.println("Mematikan lampu");
          }
        }
        if(rcvmsg[1]==TARGET_SERVO){
          byte pjg_data_masuk = rcvmsg[2];
          int stop_loop = 3 + pjg_data_masuk;
          for(int x=3;x<stop_loop;x++){
            String byteToString;
            byteToString =  String(char(rcvmsg[x]));
            data_masuk.concat(byteToString);
          }
          int toServo;
          toServo = data_masuk.toInt();
          myServo.write(toServo);
          Serial.print("Memutar servo ke posisi : ");
          Serial.println(toServo);
        }
        if(rcvmsg[1]==TARGET_GRIPPER){
          byte pjg_data_masuk = rcvmsg[2];
          int stop_loop = 3 + pjg_data_masuk;
          for(int x=3;x<stop_loop;x++){
            String byteToString;
            byteToString =  String(char(rcvmsg[x]));
            data_masuk.concat(byteToString);
          }
          int toGripper;
          toGripper = data_masuk.toInt();
          myGripper.write(toGripper);
          Serial.print("Bukaan gripper : ");
          Serial.println(toGripper);
        }
      }
    Serial.print("rcvmsg[1] : "); Serial.println(rcvmsg[1]);
    Serial.print("Data masuk : "); Serial.println(data_masuk);
    }
  }//end of isConnected
}

