# 🚗 Car Rental Speed Monitor (Android App)

An Android application that tracks vehicle speed in real-time and alerts the rental company if a renter exceeds a predefined speed limit. Built using Jetpack Compose, MVVM architecture, Firebase, and Hilt DI.

---

## ✨ Features

- 📱 **Phone Number Authentication** (via Firebase)
- 📍 **Real-time Speed Tracking** using Google Play Location Services
- 🚨 **Speed Limit Alerts** shown to user + sent to Firebase Firestore
- 🧠 **Per-User Speed Limit** stored and retrieved from Firestore
- ☁️ **Firebase First**, AWS support extendable
- 🔄 **MVVM Architecture** with Hilt for DI and clean code structure

---

## 🔧 Tech Stack

- 🧩 Jetpack Compose
- 🚀 Firebase Authentication & Firestore
- 📡 Google Play Services (FusedLocationProviderClient)
- 🛠 Hilt Dependency Injection
- 📦 Kotlin + MVVM

---

## 🚨 How It Works

1. User logs in with their phone number
2. App fetches the allowed speed limit from Firestore
3. If user exceeds speed, app:
    - Shows warning alert
    - Sends violation data to Firestore (`alerts` collection)

---

## 📂 Firebase Setup

1. Create a Firebase project
2. Enable **Phone Number Authentication**
3. Create a Firestore collection:
4. Add your `google-services.json` to the `/app` directory

---
