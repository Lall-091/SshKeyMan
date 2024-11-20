@echo off
cd ../
adb -s %1 install app\build\outputs\apk\debug\app-debug.apk
adb -s %1 shell monkey -p com.catpuppyapp.sshkeyman -c android.intent.category.LAUNCHER 1
