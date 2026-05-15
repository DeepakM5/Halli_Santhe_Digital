# Halli-Santhe Digital

A hyper-local Android marketplace application developed using modern Android technologies to connect local sellers and customers through a simple, user-friendly, and interactive digital platform.

---

## 🚀 Features

- Role-based access (Seller / Customer)
- Phone OTP authentication
- Seller product upload and management
- Product browsing and product details
- Category filtering and product search
- Customer reviews and ratings
- WhatsApp seller communication
- Responsive and vibrant UI
- Firebase-based product management
- Offline support through Firebase caching

---

## 🧰 Technologies Used

Kotlin, Jetpack Compose, Android SDK, Material Design 3, Firebase Authentication, Cloud Firestore, Firebase Storage, Navigation Compose, MVVM Architecture, Coil, WhatsApp Intent Integration, Android Studio, Git, GitHub

---

## 🗄️ Data Handling

- Firebase Authentication → User login and role verification
- Cloud Firestore → Product, review, and seller data management
- Firebase Storage → Product image storage
- Firebase Offline Persistence → Cached product access during limited offline usage

---

## 📱 App Behavior

- Sellers can upload and manage products
- Customers can browse and search products
- Products are filtered dynamically by category
- Customers can contact sellers through WhatsApp
- Reviews and ratings improve marketplace interaction
- Firebase services manage cloud-based synchronization

---

## 🔐 Authentication Flow

1. User selects role (Seller / Customer)
2. User enters phone number
3. OTP verification is performed
4. User session is authenticated
5. User is redirected to the respective dashboard

---

## 📦 Product Workflow

- Sellers upload products with images and details
- Product data is stored in Firebase Firestore
- Product images are stored in Firebase Storage
- Customers browse products dynamically
- Search and filtering improve product discoverability
- WhatsApp integration enables direct seller communication

---

## 📊 Requirements

- Android 7.0 (API 24+) or above
- Internet connection required for Firebase synchronization
- Android Studio for development and testing

---

## 📌 Status

Marketplace MVP completed with seller management, customer browsing, review handling, Firebase integration, and WhatsApp communication features.

---

## 📄 License

Developed for educational, internship, and learning purposes.
