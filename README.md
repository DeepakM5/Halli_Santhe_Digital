# Halli Santhe Digital

A lightweight offline-first Android marketplace app built with Jetpack Compose that allows vendors and customers to interact and manage products locally.

---

## 🚀 Features

- Role-based access (Vendor / Customer)
- Phone OTP authentication (Firebase Auth support)
- Product listing and browsing
- Category filtering and search
- Product detail view
- WhatsApp direct chat integration
- Customer reviews and ratings
- Clean Jetpack Compose UI
- Offline-first experience (local in-memory state)

---

## 🧰 Technologies Used

Kotlin, Jetpack Compose, Android SDK (API 24+), Material Design 3, Firebase Authentication (Phone OTP), Cloud Firestore, Firebase Storage, Coil, WhatsApp Intent API, Android Studio, Git, GitHub

---

## 🗄️ Data Handling

- Firebase Authentication → User login (OTP-based)
- Cloud Firestore → Optional backend sync (not required for offline usage)
- Firebase Storage → Image storage (if enabled)
- Local App State → Primary data handling during offline usage

---

## 📱 App Behavior

- App works offline using local in-memory state
- Data persists only during active session
- Firebase services are optional for cloud sync
- No Room Database or local SQL storage used

---

## 🔐 Authentication Flow

1. User selects role (Vendor / Customer)
2. Enters phone number
3. OTP verification (Firebase optional)
4. User session created locally
5. Redirect to dashboard

---

## 📦 Product Flow

- Vendors create products in-app
- Products stored in local state (offline mode)
- Customers browse available products instantly
- WhatsApp used for external communication

---

## 📊 Requirements

- Android 7.0 (API 24+)
- No mandatory internet required (offline mode supported)

---

## 📌 Status

Offline-first MVP completed with core marketplace features.

---

## 📄 License

For educational and development purposes.
