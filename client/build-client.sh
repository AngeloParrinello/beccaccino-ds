#!/usr/bin/env sh

APK_NAME="app-debug.apk"
APK_PATH=$(pwd)

rm "$APK_PATH/$APK_NAME"

./gradlew clean

./gradlew assembleDebug

chmod 777 "$APK_PATH"

mv app/build/outputs/apk/debug/$APK_NAME "$APK_PATH"

echo "APK file created successfully."
