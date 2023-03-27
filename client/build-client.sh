#!/usr/bin/env sh

# Dichiarazione delle variabili per il percorso del progetto e del file APK
APK_NAME="app-debug.apk"
APK_PATH="/client"

# Pulisci la cartella dei file generati in precedenza
./gradlew clean

# Genera il file APK
./gradlew assembleDebug

# Sposta il file APK nella cartella di destinazione
# mv app/build/outputs/apk/debug/$APK_NAME $APK_PATH

# Stampa un messaggio di conferma
echo "Il file APK è stato generato e spostato nella cartella di destinazione."
