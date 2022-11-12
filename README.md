# vixen_sms_control

## download ngrok
https://dashboard.ngrok.com/get-started/setup

check the version with `ngrok version`

## login to ngrok
`ngrok_login.bat` will tell you the URL
![image](https://user-images.githubusercontent.com/590535/201448976-f2e7f531-a9f5-4ddd-b951-61cefe5bd3e8.png)

## install the twilio-cli
https://www.twilio.com/docs/twilio-cli/getting-started/install

I also needed the VC++ 2010 Redistributables

check the version with `twilio version`

## login to twilio
`twilio_login.bat`

## update the webhook location
https://console.twilio.com/

paste in the URL from the ngrok login

![image](https://user-images.githubusercontent.com/590535/201449316-2c88c288-db84-4ca6-8d7b-5a60acde8e42.png)

## rebuild the app
`build.bat`

## run the app 
`run_sms.bat`

vixen web interface
http://localhost:8888/
