# BantayBahay: IoT Smart Home Security System

BantayBahay is a comprehensive IoT security solution combining an Android App (Kotlin) and an ESP32 firmware (C++). It offers real-time door monitoring, global arm/disarm capabilities, and secure device ownership management via Firebase.

## 🚀 Features

*   **Real-time Monitoring**: Instant updates on Door Open/Closed status.
*   **Global Arm/Disarm**: One-tap control to arm all your registered devices.
*   **Secure Device Lifecycle**:
    *   **Pairing**: Secure WiFi provisioning via ESP32 AP mode.
    *   **Removal**: Secure "Factory Reset" command that wipes the device credentials when removed from the app.
*   **Activity Log**: Detailed history of events with device names and timestamps.
*   **Multi-Device Support**: Manage multiple sensors from a single account.

## 🛠️ Hardware Setup (ESP32)

1.  **Hardware**: ESP32 Dev Module, Reed Switch (Pin 22), Magnet.
2.  **Firmware**:
    *   Open `arduino.txt` (rename to `.ino` if using Arduino IDE, or copy content).
    *   **Dependencies**:
        *   `Firebase ESP32 Client` by Mobizt
        *   `ArduinoJson` (v6)
    *   **Flash**: Upload to your ESP32.
3.  **Operation**:
    *   On first boot, the device creates a WiFi Access Point (e.g., `ESP32-1A2B3C`).
    *   The App connects to this AP to send your WiFi credentials and Firebase Token.

## 📱 App Setup (Android)

1.  Open the project in **Android Studio**.
2.  **Sync Gradle**: Ensure all dependencies are downloaded.
3.  **google-services.json**: Place your Firebase configuration file in `app/`.
4.  **Run**: Build and deploy to your Android device or emulator.

## ⚠️ Critical Operational Notes

### Removing & Re-pairing Devices
To ensure security, the system uses a **Cryptographic Token** exchange.

*   **To Remove a Device**:
    1.  Ensure the ESP32 is **POWERED ON and ONLINE**.
    2.  Go to **Settings** -> **Remove Device**.
    3.  The App sends a `RESET` command.
    4.  The ESP32 receives this, **wipes its internal memory**, and restarts in Pairing Mode.
*   **WARNING**: If you remove a device while it is **OFFLINE**, it will *not* receive the reset command and will remain locked to the old account. You would need to re-flash the firmware to fix this.

## 🔧 Troubleshooting

*   **"Unable to delete build directory"**:
    *   This is a common Windows file lock issue.
    *   **Fix**: Stop Gradle (`./gradlew --stop`), Kill Java processes, or Restart Android Studio.
*   **Device shows "Unknown Device"**:
    *   Go to **Settings** -> Long Press the device -> **Rename**.

## 👨‍💻 Tech Stack

*   **Android**: Kotlin, XML Layouts, Firebase Realtime Database, Firebase Auth.
*   **Firmware**: C++, Arduino Framework, ESP32 WiFi & SPIFFS.