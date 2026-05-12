Halli SANTHE DIGITAL MARKET APP

( Internship app )



COMPLETE SOP AND DEVELOPMENT GUIDE 





Kotlin app



Vendor-Customer Marketplace App with OTP Authentication



📋 TABLE OF CONTENTS



1. Project Overview
2. App Architecture \& User Flow
3. Complete Screen-by-Screen Specifications
4. Backend \& Database Structure
5. Authentication System
6. Feature Requirements (Functional \& Non-Functional)
7. Technology Stack
8. API \& Third-Party Integrations
9. Development Timeline
10. Testing Checklist
11. Deployment Instructions



1\. PROJECT OVERVIEW



App Name: Halli Santhe 

Purpose:

A hyper-local marketplace connecting vendors/sellers with customers. Vendors can upload products, manage listings, and view reviews. Customers can browse products, view details (price, freshness, origin), contact vendors directly via WhatsApp, and leave ratings/reviews.



Problem Solved:
-
* Vendors lack digital presence to showcase products
* Customers cannot easily discover local products
* No direct communication channel between buyers and sellers
* No transparency in product quality/ratings



Key Differentiators:

* OTP-based authentication (no passwords)
* Role-based access (Vendor vs Customer)
* Direct WhatsApp integration for negotiations
* Persistent user data across sessions
* Review \& rating system for quality assurance



2\. APP ARCHITECTURE \& USER FLOW

A. High-Level Architecture

┌─────────────────────────────────────────┐

│         MOBILE APP (Android)            │

│  ┌───────────────────────────────────┐  │

│  │   Authentication Layer (OTP)      │  │

│  └───────────────────────────────────┘  │

│              ↓                          │

│  ┌───────────────────────────────────┐  │

│  │   Role Detection \& Routing        │  │

│  │   (Vendor/Seller vs Customer)     │  │

│  └───────────────────────────────────┘  │

│         ↓                    ↓          │

│  ┌──────────────┐    ┌──────────────┐  │

│  │ Vendor UI    │    │ Customer UI  │  │

│  │ - Upload     │    │ - Browse     │  │

│  │ - Manage     │    │ - Search     │  │

│  │ - Reviews    │    │ - Chat       │  │

│  └──────────────┘    └──────────────┘  │

└─────────────────────────────────────────┘

&#x20;             ↓

┌─────────────────────────────────────────┐

│      BACKEND (Firebase)                 │

│  ┌───────────────────────────────────┐  │

│  │ Authentication (Phone Auth)       │  │

│  │ Firestore Database               │  │

│  │ Storage (Product Images)         │  │

│  │ Cloud Functions (optional)       │  │

│  └───────────────────────────────────┘  │

└─────────────────────────────────────────┘

&#x20;             ↓

┌─────────────────────────────────────────┐

│   THIRD-PARTY INTEGRATIONS              │

│   - WhatsApp Intent                     │

│   - Image Compression Library           │

└─────────────────────────────────────────┘



B. Complete User Journey Map



\[APP LAUNCH]

&#x20;    ↓

\[SCREEN 1: Role Selection]

&#x20;    ↓

\[SCREEN 2: Phone Number Input]

&#x20;    ↓

\[SCREEN 3: OTP Verification]

&#x20;    ↓

\[User Type Check in Database]

&#x20;    ↓           ↓

&#x20;VENDOR      CUSTOMER

&#x20;    ↓           ↓

\[Vendor      \[Customer

Dashboard]   Dashboard]



3\. COMPLETE SCREEN-BY-SCREEN SPECIFICATIONS



SCREEN 1: ROLE SELECTION (Splash/Entry Screen)

Purpose: Identify user type before authentication



UI Elements:



┌──────────────────────────────────────┐

│                                      │

│         \[APP LOGO]                   │

│                                      │

│    Welcome to VendorConnect          │

│                                      │

│    "I am a..."                       │

│                                      │

│  ┌────────────────────────────────┐  │

│  │     🛒 VENDOR/SELLER          │  │

│  │  (Upload and manage products) │  │

│  └────────────────────────────────┘  │

│                                      │

│  ┌────────────────────────────────┐  │

│  │     🛍️ CUSTOMER               │  │

│  │  (Browse and buy products)    │  │

│  └────────────────────────────────┘  │

│                                      │

└──────────────────────────────────────┘



Behavior:

* User taps one button
* Selected role is stored temporarily (passed to next screen)
* Navigation: → Screen 2 (Phone Auth)



Technical Implementation:

// RoleSelectionActivity.kt

class RoleSelectionActivity : AppCompatActivity() {

&#x20;   

&#x20;   override fun onCreate(savedInstanceState: Bundle?) {

&#x20;       super.onCreate(savedInstanceState)

&#x20;       setContentView(R.layout.activity\_role\_selection)

&#x20;       

&#x20;       btnVendor.setOnClickListener {

&#x20;           val intent = Intent(this, PhoneAuthActivity::class.java)

&#x20;           intent.putExtra("USER\_ROLE", "VENDOR")

&#x20;           startActivity(intent)

&#x20;       }

&#x20;       

&#x20;       btnCustomer.setOnClickListener {

&#x20;           val intent = Intent(this, PhoneAuthActivity::class.java)

&#x20;           intent.putExtra("USER\_ROLE", "CUSTOMER")

&#x20;           startActivity(intent)

&#x20;       }

&#x20;   }

}



Layout (activity\_role\_selection.xml):



<?xml version="1.0" encoding="utf-8"?>

<LinearLayout 

&#x20;   xmlns:android="http://schemas.android.com/apk/res/android"

&#x20;   android:layout\_width="match\_parent"

&#x20;   android:layout\_height="match\_parent"

&#x20;   android:orientation="vertical"

&#x20;   android:gravity="center"

&#x20;   android:padding="24dp"

&#x20;   android:background="#F5F5F5">

&#x20;   

&#x20;   <ImageView

&#x20;       android:id="@+id/imgLogo"

&#x20;       android:layout\_width="120dp"

&#x20;       android:layout\_height="120dp"

&#x20;       android:src="@drawable/app\_logo"

&#x20;       android:layout\_marginBottom="32dp"/>

&#x20;   

&#x20;   <TextView

&#x20;       android:layout\_width="wrap\_content"

&#x20;       android:layout\_height="wrap\_content"

&#x20;       android:text="Welcome to VendorConnect"

&#x20;       android:textSize="24sp"

&#x20;       android:textStyle="bold"

&#x20;       android:layout\_marginBottom="16dp"/>

&#x20;   

&#x20;   <TextView

&#x20;       android:layout\_width="wrap\_content"

&#x20;       android:layout\_height="wrap\_content"

&#x20;       android:text="I am a..."

&#x20;       android:textSize="18sp"

&#x20;       android:layout\_marginBottom="32dp"/>

&#x20;   

&#x20;   <Button

&#x20;       android:id="@+id/btnVendor"

&#x20;       android:layout\_width="match\_parent"

&#x20;       android:layout\_height="wrap\_content"

&#x20;       android:text="🛒 VENDOR/SELLER"

&#x20;       android:textSize="16sp"

&#x20;       android:padding="16dp"

&#x20;       android:layout\_marginBottom="16dp"

&#x20;       android:backgroundTint="#FF6F00"/>

&#x20;   

&#x20;   <Button

&#x20;       android:id="@+id/btnCustomer"

&#x20;       android:layout\_width="match\_parent"

&#x20;       android:layout\_height="wrap\_content"

&#x20;       android:text="🛍️ CUSTOMER"

&#x20;       android:textSize="16sp"

&#x20;       android:padding="16dp"

&#x20;       android:backgroundTint="#388E3C"/>

&#x20;   

</LinearLayout>





SCREEN 2: PHONE NUMBER INPUT

Purpose: Collect phone number for OTP authentication



UI Elements:



┌──────────────────────────────────────┐

│  ← Back                              │

│                                      │

│  Enter Your Phone Number             │

│                                      │

│  We'll send you a verification code  │

│                                      │

│  ┌────────────────────────────────┐  │

│  │ +91 |\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_|       │  │

│  │     (10-digit number)          │  │

│  └────────────────────────────────┘  │

│                                      │

│  ┌────────────────────────────────┐  │

│  │     SEND OTP                   │  │

│  └────────────────────────────────┘  │

│                                      │

│  Selected: VENDOR/CUSTOMER           │

│                                      │

└──────────────────────────────────────┘



Behavior:



* Auto-fills country code (+91 or detect from device)
* Validates 10-digit phone number
* On "Send OTP":

  * Show loading indicator
  * Trigger Firebase Phone Authentication
  * Navigate to OTP screen





Technical Implementation:



// PhoneAuthActivity.kt

class PhoneAuthActivity : AppCompatActivity() {

&#x20;   

&#x20;   private lateinit var auth: FirebaseAuth

&#x20;   private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

&#x20;   private var verificationId: String? = null

&#x20;   private var userRole: String = ""

&#x20;   

&#x20;   override fun onCreate(savedInstanceState: Bundle?) {

&#x20;       super.onCreate(savedInstanceState)

&#x20;       setContentView(R.layout.activity\_phone\_auth)

&#x20;       

&#x20;       auth = FirebaseAuth.getInstance()

&#x20;       userRole = intent.getStringExtra("USER\_ROLE") ?: "CUSTOMER"

&#x20;       

&#x20;       txtRole.text = "Selected: $userRole"

&#x20;       

&#x20;       setupPhoneAuthCallbacks()

&#x20;       

&#x20;       btnSendOTP.setOnClickListener {

&#x20;           val phoneNumber = "+91${etPhone.text.toString().trim()}"

&#x20;           

&#x20;           if (validatePhoneNumber(phoneNumber)) {

&#x20;               sendVerificationCode(phoneNumber)

&#x20;           } else {

&#x20;               Toast.makeText(this, "Enter valid 10-digit number", Toast.LENGTH\_SHORT).show()

&#x20;           }

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun validatePhoneNumber(phone: String): Boolean {

&#x20;       return phone.length == 13 \&\& phone.substring(3).all { it.isDigit() }

&#x20;   }

&#x20;   

&#x20;   private fun setupPhoneAuthCallbacks() {

&#x20;       callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

&#x20;           

&#x20;           override fun onVerificationCompleted(credential: PhoneAuthCredential) {

&#x20;               // Auto-verification (rare on real devices)

&#x20;               signInWithPhoneAuthCredential(credential)

&#x20;           }

&#x20;           

&#x20;           override fun onVerificationFailed(e: FirebaseException) {

&#x20;               Toast.makeText(this@PhoneAuthActivity, 

&#x20;                   "Verification failed: ${e.message}", 

&#x20;                   Toast.LENGTH\_LONG).show()

&#x20;           }

&#x20;           

&#x20;           override fun onCodeSent(

&#x20;               verificationId: String,

&#x20;               token: PhoneAuthProvider.ForceResendingToken

&#x20;           ) {

&#x20;               this@PhoneAuthActivity.verificationId = verificationId

&#x20;               

&#x20;               val intent = Intent(this@PhoneAuthActivity, OTPVerificationActivity::class.java)

&#x20;               intent.putExtra("VERIFICATION\_ID", verificationId)

&#x20;               intent.putExtra("USER\_ROLE", userRole)

&#x20;               intent.putExtra("PHONE\_NUMBER", etPhone.text.toString())

&#x20;               startActivity(intent)

&#x20;           }

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun sendVerificationCode(phoneNumber: String) {

&#x20;       val options = PhoneAuthOptions.newBuilder(auth)

&#x20;           .setPhoneNumber(phoneNumber)

&#x20;           .setTimeout(60L, TimeUnit.SECONDS)

&#x20;           .setActivity(this)

&#x20;           .setCallbacks(callbacks)

&#x20;           .build()

&#x20;       PhoneAuthProvider.verifyPhoneNumber(options)

&#x20;   }

&#x20;   

&#x20;   private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

&#x20;       auth.signInWithCredential(credential)

&#x20;           .addOnCompleteListener(this) { task ->

&#x20;               if (task.isSuccessful) {

&#x20;                   // Navigate to dashboard

&#x20;                   navigateToDashboard()

&#x20;               }

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   private fun navigateToDashboard() {

&#x20;       val intent = when(userRole) {

&#x20;           "VENDOR" -> Intent(this, VendorDashboardActivity::class.java)

&#x20;           else -> Intent(this, CustomerDashboardActivity::class.java)

&#x20;       }

&#x20;       startActivity(intent)

&#x20;       finish()

&#x20;   }

}





Layout (activity\_phone\_auth.xml):



<?xml version="1.0" encoding="utf-8"?>

<LinearLayout 

&#x20;   xmlns:android="http://schemas.android.com/apk/res/android"

&#x20;   android:layout\_width="match\_parent"

&#x20;   android:layout\_height="match\_parent"

&#x20;   android:orientation="vertical"

&#x20;   android:padding="24dp"

&#x20;   android:background="#FFFFFF">

&#x20;   

&#x20;   <TextView

&#x20;       android:layout\_width="wrap\_content"

&#x20;       android:layout\_height="wrap\_content"

&#x20;       android:text="Enter Your Phone Number"

&#x20;       android:textSize="20sp"

&#x20;       android:textStyle="bold"

&#x20;       android:layout\_marginBottom="8dp"/>

&#x20;   

&#x20;   <TextView

&#x20;       android:layout\_width="wrap\_content"

&#x20;       android:layout\_height="wrap\_content"

&#x20;       android:text="We'll send you a verification code"

&#x20;       android:textSize="14sp"

&#x20;       android:textColor="#666666"

&#x20;       android:layout\_marginBottom="32dp"/>

&#x20;   

&#x20;   <LinearLayout

&#x20;       android:layout\_width="match\_parent"

&#x20;       android:layout\_height="wrap\_content"

&#x20;       android:orientation="horizontal"

&#x20;       android:layout\_marginBottom="24dp">

&#x20;       

&#x20;       <TextView

&#x20;           android:layout\_width="wrap\_content"

&#x20;           android:layout\_height="wrap\_content"

&#x20;           android:text="+91"

&#x20;           android:textSize="18sp"

&#x20;           android:padding="12dp"/>

&#x20;       

&#x20;       <EditText

&#x20;           android:id="@+id/etPhone"

&#x20;           android:layout\_width="0dp"

&#x20;           android:layout\_height="wrap\_content"

&#x20;           android:layout\_weight="1"

&#x20;           android:hint="10-digit number"

&#x20;           android:inputType="phone"

&#x20;           android:maxLength="10"

&#x20;           android:padding="12dp"/>

&#x20;   </LinearLayout>

&#x20;   

&#x20;   <Button

&#x20;       android:id="@+id/btnSendOTP"

&#x20;       android:layout\_width="match\_parent"

&#x20;       android:layout\_height="wrap\_content"

&#x20;       android:text="SEND OTP"

&#x20;       android:textSize="16sp"

&#x20;       android:padding="16dp"

&#x20;       android:backgroundTint="#FF6F00"/>

&#x20;   

&#x20;   <TextView

&#x20;       android:id="@+id/txtRole"

&#x20;       android:layout\_width="wrap\_content"

&#x20;       android:layout\_height="wrap\_content"

&#x20;       android:text="Selected: VENDOR"

&#x20;       android:textSize="12sp"

&#x20;       android:textColor="#999999"

&#x20;       android:layout\_marginTop="16dp"/>

&#x20;   

</LinearLayout>



#### SCREEN 3: OTP VERIFICATION

Purpose: Verify phone number with 6-digit OTP



UI Elements:



┌──────────────────────────────────────┐

│  ← Back                              │

│                                      │

│  Verification Code                   │

│                                      │

│  Enter the 6-digit code sent to      │

│  +91 9876543210                      │

│                                      │

│  ┌───┐ ┌───┐ ┌───┐ ┌───┐ ┌───┐ ┌───┐│

│  │ 1 │ │ 2 │ │ 3 │ │ 4 │ │ 5 │ │ 6 ││

│  └───┘ └───┘ └───┘ └───┘ └───┘ └───┘│

│                                      │

│  ┌────────────────────────────────┐  │

│  │     VERIFY \& CONTINUE          │  │

│  └────────────────────────────────┘  │

│                                      │

│  Didn't receive? Resend (45s)        │

│                                      │

└──────────────────────────────────────┘



Behavior:



* Auto-focus on first digit
* Auto-advance to next field after digit entry
* Enable Verify button only when all 6 digits entered
* Countdown timer for resend (60 seconds)
* On successful verification:

  * Check if user exists in Firestore
  * If new user: Create user document with role
  * If existing: Load user data
  * Navigate to appropriate dashboard



Technical Implementation:



// OTPVerificationActivity.kt

class OTPVerificationActivity : AppCompatActivity() {

&#x20;   

&#x20;   private lateinit var auth: FirebaseAuth

&#x20;   private lateinit var db: FirebaseFirestore

&#x20;   private var verificationId: String? = null

&#x20;   private var userRole: String = ""

&#x20;   private var phoneNumber: String = ""

&#x20;   

&#x20;   override fun onCreate(savedInstanceState: Bundle?) {

&#x20;       super.onCreate(savedInstanceState)

&#x20;       setContentView(R.layout.activity\_otp\_verification)

&#x20;       

&#x20;       auth = FirebaseAuth.getInstance()

&#x20;       db = FirebaseFirestore.getInstance()

&#x20;       

&#x20;       verificationId = intent.getStringExtra("VERIFICATION\_ID")

&#x20;       userRole = intent.getStringExtra("USER\_ROLE") ?: "CUSTOMER"

&#x20;       phoneNumber = intent.getStringExtra("PHONE\_NUMBER") ?: ""

&#x20;       

&#x20;       txtPhoneDisplay.text = "Enter the code sent to +91 $phoneNumber"

&#x20;       

&#x20;       setupOTPFields()

&#x20;       startResendTimer()

&#x20;       

&#x20;       btnVerify.setOnClickListener {

&#x20;           val otp = "${et1.text}${et2.text}${et3.text}${et4.text}${et5.text}${et6.text}"

&#x20;           verifyCode(otp)

&#x20;       }

&#x20;       

&#x20;       txtResend.setOnClickListener {

&#x20;           // Resend OTP logic

&#x20;           resendVerificationCode()

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun setupOTPFields() {

&#x20;       val fields = listOf(et1, et2, et3, et4, et5, et6)

&#x20;       

&#x20;       fields.forEachIndexed { index, editText ->

&#x20;           editText.addTextChangedListener(object : TextWatcher {

&#x20;               override fun afterTextChanged(s: Editable?) {

&#x20;                   if (s?.length == 1 \&\& index < fields.size - 1) {

&#x20;                       fields\[index + 1].requestFocus()

&#x20;                   }

&#x20;                   checkAllFieldsFilled()

&#x20;               }

&#x20;               override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

&#x20;               override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

&#x20;           })

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun checkAllFieldsFilled() {

&#x20;       val allFilled = listOf(et1, et2, et3, et4, et5, et6)

&#x20;           .all { it.text.toString().isNotEmpty() }

&#x20;       btnVerify.isEnabled = allFilled

&#x20;   }

&#x20;   

&#x20;   private fun verifyCode(code: String) {

&#x20;       val credential = PhoneAuthProvider.getCredential(verificationId!!, code)

&#x20;       signInWithPhoneAuthCredential(credential)

&#x20;   }

&#x20;   

&#x20;   private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

&#x20;       auth.signInWithCredential(credential)

&#x20;           .addOnCompleteListener(this) { task ->

&#x20;               if (task.isSuccessful) {

&#x20;                   val user = task.result?.user

&#x20;                   user?.let {

&#x20;                       checkUserInDatabase(it.uid)

&#x20;                   }

&#x20;               } else {

&#x20;                   Toast.makeText(this, "Invalid OTP", Toast.LENGTH\_SHORT).show()

&#x20;               }

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   private fun checkUserInDatabase(uid: String) {

&#x20;       db.collection("users").document(uid).get()

&#x20;           .addOnSuccessListener { document ->

&#x20;               if (document.exists()) {

&#x20;                   // Existing user - load their data

&#x20;                   val savedRole = document.getString("role")

&#x20;                   navigateToDashboard(savedRole ?: userRole)

&#x20;               } else {

&#x20;                   // New user - create document

&#x20;                   createNewUser(uid)

&#x20;               }

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   private fun createNewUser(uid: String) {

&#x20;       val userData = hashMapOf(

&#x20;           "uid" to uid,

&#x20;           "role" to userRole,

&#x20;           "phoneNumber" to "+91$phoneNumber",

&#x20;           "createdAt" to FieldValue.serverTimestamp(),

&#x20;           "profileComplete" to false

&#x20;       )

&#x20;       

&#x20;       db.collection("users").document(uid).set(userData)

&#x20;           .addOnSuccessListener {

&#x20;               navigateToDashboard(userRole)

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   private fun navigateToDashboard(role: String) {

&#x20;       val intent = when(role) {

&#x20;           "VENDOR" -> Intent(this, VendorDashboardActivity::class.java)

&#x20;           else -> Intent(this, CustomerDashboardActivity::class.java)

&#x20;       }

&#x20;       intent.flags = Intent.FLAG\_ACTIVITY\_NEW\_TASK or Intent.FLAG\_ACTIVITY\_CLEAR\_TASK

&#x20;       startActivity(intent)

&#x20;       finish()

&#x20;   }

&#x20;   

&#x20;   private fun startResendTimer() {

&#x20;       object : CountDownTimer(60000, 1000) {

&#x20;           override fun onTick(millisUntilFinished: Long) {

&#x20;               txtResend.text = "Resend (${millisUntilFinished / 1000}s)"

&#x20;               txtResend.isEnabled = false

&#x20;           }

&#x20;           

&#x20;           override fun onFinish() {

&#x20;               txtResend.text = "Resend Code"

&#x20;               txtResend.isEnabled = true

&#x20;           }

&#x20;       }.start()

&#x20;   }

&#x20;   

&#x20;   private fun resendVerificationCode() {

&#x20;       // Trigger phone auth again

&#x20;       Toast.makeText(this, "Sending new code...", Toast.LENGTH\_SHORT).show()

&#x20;   }

}



#### SCREEN 4A: VENDOR DASHBOARD

Purpose: Main hub for vendors to manage products and reviews



UI Elements:



┌──────────────────────────────────────┐

│  ☰ Menu              \[Profile Icon]  │

│                                      │

│  Welcome, Vendor Name                │

│  +91 9876543210                      │

│                                      │

│  ┌────────────────────────────────┐  │

│  │  📤 UPLOAD PRODUCT             │  │

│  │  Add new items to your store   │  │

│  └────────────────────────────────┘  │

│                                      │

│  ┌────────────────────────────────┐  │

│  │  💬 MESSAGES                   │  │

│  │  View customer inquiries       │  │

│  └────────────────────────────────┘  │

│                                      │

│  ┌────────────────────────────────┐  │

│  │  ⭐ REVIEWS \& RATINGS          │  │

│  │  Average: 4.5 ★ (23 reviews)   │  │

│  └────────────────────────────────┘  │

│                                      │

│  ┌────────────────────────────────┐  │

│  │  📦 MY PRODUCTS (15)           │  │

│  │  Manage your listings          │  │

│  └────────────────────────────────┘  │

│                                      │

└──────────────────────────────────────┘



Navigation Flow:



* Upload Product → Screen 5
* Messages → Opens WhatsApp business view
* Reviews \& Ratings → Screen 6
* My Products → Screen 7 (Product Management)



Technical Implementation:



// VendorDashboardActivity.kt

class VendorDashboardActivity : AppCompatActivity() {

&#x20;   

&#x20;   private lateinit var auth: FirebaseAuth

&#x20;   private lateinit var db: FirebaseFirestore

&#x20;   private var vendorData: HashMap<String, Any>? = null

&#x20;   

&#x20;   override fun onCreate(savedInstanceState: Bundle?) {

&#x20;       super.onCreate(savedInstanceState)

&#x20;       setContentView(R.layout.activity\_vendor\_dashboard)

&#x20;       

&#x20;       auth = FirebaseAuth.getInstance()

&#x20;       db = FirebaseFirestore.getInstance()

&#x20;       

&#x20;       loadVendorProfile()

&#x20;       loadVendorStats()

&#x20;       

&#x20;       btnUploadProduct.setOnClickListener {

&#x20;           startActivity(Intent(this, UploadProductActivity::class.java))

&#x20;       }

&#x20;       

&#x20;       btnMessages.setOnClickListener {

&#x20;           openWhatsAppBusiness()

&#x20;       }

&#x20;       

&#x20;       btnReviews.setOnClickListener {

&#x20;           val intent = Intent(this, VendorReviewsActivity::class.java)

&#x20;           startActivity(intent)

&#x20;       }

&#x20;       

&#x20;       btnMyProducts.setOnClickListener {

&#x20;           val intent = Intent(this, VendorProductsActivity::class.java)

&#x20;           startActivity(intent)

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun loadVendorProfile() {

&#x20;       val uid = auth.currentUser?.uid ?: return

&#x20;       

&#x20;       db.collection("users").document(uid).get()

&#x20;           .addOnSuccessListener { document ->

&#x20;               vendorData = document.data as? HashMap<String, Any>

&#x20;               txtVendorName.text = "Welcome, ${vendorData?.get("name") ?: "Vendor"}"

&#x20;               txtPhone.text = vendorData?.get("phoneNumber") as? String ?: ""

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   private fun loadVendorStats() {

&#x20;       val uid = auth.currentUser?.uid ?: return

&#x20;       

&#x20;       // Count products

&#x20;       db.collection("products")

&#x20;           .whereEqualTo("vendorId", uid)

&#x20;           .get()

&#x20;           .addOnSuccessListener { documents ->

&#x20;               txtProductCount.text = "MY PRODUCTS (${documents.size()})"

&#x20;           }

&#x20;       

&#x20;       // Load rating average

&#x20;       db.collection("reviews")

&#x20;           .whereEqualTo("vendorId", uid)

&#x20;           .get()

&#x20;           .addOnSuccessListener { documents ->

&#x20;               if (documents.isEmpty) {

&#x20;                   txtRating.text = "No reviews yet"

&#x20;               } else {

&#x20;                   val avg = documents.documents

&#x20;                       .mapNotNull { it.getDouble("rating") }

&#x20;                       .average()

&#x20;                   val count = documents.size()

&#x20;                   txtRating.text = "Average: %.1f ★ (%d reviews)".format(avg, count)

&#x20;               }

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   private fun openWhatsAppBusiness() {

&#x20;       val phoneNumber = vendorData?.get("phoneNumber") as? String ?: return

&#x20;       val intent = Intent(Intent.ACTION\_VIEW)

&#x20;       intent.data = Uri.parse("https://wa.me/$phoneNumber")

&#x20;       startActivity(intent)

&#x20;   }

}



SCREEN 5: UPLOAD PRODUCT (Vendor)

Purpose: Allow vendors to add new products



UI Elements:



┌──────────────────────────────────────┐

│  ← Back          Upload Product      │

│                                      │

│  ┌────────────────────────────────┐  │

│  │                                │  │

│  │    \[Camera Icon]               │  │

│  │    Tap to add photo            │  │

│  │                                │  │

│  └────────────────────────────────┘  │

│                                      │

│  Product Name                        │

│  ┌────────────────────────────────┐  │

│  │ \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_      │  │

│  └────────────────────────────────┘  │

│                                      │

│  Price (₹)                           │

│  ┌────────────────────────────────┐  │

│  │ \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_      │  │

│  └────────────────────────────────┘  │

│                                      │

│  Freshness                           │

│  ┌────────────────────────────────┐  │

│  │ ▼ Select freshness            │  │

│  └────────────────────────────────┘  │

│  (Fresh Today / 1-2 Days / 3-5 Days) │

│                                      │

│  Origin/Source                       │

│  ┌────────────────────────────────┐  │

│  │ \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_      │  │

│  └────────────────────────────────┘  │

│                                      │

│  Category                            │

│  ┌────────────────────────────────┐  │

│  │ ▼ Select category             │  │

│  └────────────────────────────────┘  │

│                                      │

│  Description                         │

│  ┌────────────────────────────────┐  │

│  │ \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_      │  │

│  │ \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_      │  │

│  └────────────────────────────────┘  │

│                                      │

│  ┌────────────────────────────────┐  │

│  │     UPLOAD PRODUCT             │  │

│  └────────────────────────────────┘  │

│                                      │

└──────────────────────────────────────┘



Validation Rules:



* Product name: Required, min 3 characters
* Price: Required, numeric, > 0
* Photo: Required
* Freshness: Required selection
* Origin: Required
* Category: Required selection
* Description: Optional, max 500 characters



Technical Implementation:

// UploadProductActivity.kt

class UploadProductActivity : AppCompatActivity() {

&#x20;   

&#x20;   private lateinit var auth: FirebaseAuth

&#x20;   private lateinit var db: FirebaseFirestore

&#x20;   private lateinit var storage: FirebaseStorage

&#x20;   private var selectedImageUri: Uri? = null

&#x20;   private val PICK\_IMAGE\_REQUEST = 1

&#x20;   

&#x20;   override fun onCreate(savedInstanceState: Bundle?) {

&#x20;       super.onCreate(savedInstanceState)

&#x20;       setContentView(R.layout.activity\_upload\_product)

&#x20;       

&#x20;       auth = FirebaseAuth.getInstance()

&#x20;       db = FirebaseFirestore.getInstance()

&#x20;       storage = FirebaseStorage.getInstance()

&#x20;       

&#x20;       imgProductPhoto.setOnClickListener {

&#x20;           openImagePicker()

&#x20;       }

&#x20;       

&#x20;       btnUpload.setOnClickListener {

&#x20;           validateAndUpload()

&#x20;       }

&#x20;       

&#x20;       setupSpinners()

&#x20;   }

&#x20;   

&#x20;   private fun setupSpinners() {

&#x20;       // Freshness spinner

&#x20;       val freshnessOptions = arrayOf("Fresh Today", "1-2 Days Old", "3-5 Days Old")

&#x20;       val freshnessAdapter = ArrayAdapter(this, 

&#x20;           android.R.layout.simple\_spinner\_dropdown\_item, 

&#x20;           freshnessOptions)

&#x20;       spinnerFreshness.adapter = freshnessAdapter

&#x20;       

&#x20;       // Category spinner

&#x20;       val categories = arrayOf(

&#x20;           "Vegetables", "Fruits", "Grains", "Dairy", 

&#x20;           "Meat \& Fish", "Bakery", "Handicrafts", "Other"

&#x20;       )

&#x20;       val categoryAdapter = ArrayAdapter(this,

&#x20;           android.R.layout.simple\_spinner\_dropdown\_item,

&#x20;           categories)

&#x20;       spinnerCategory.adapter = categoryAdapter

&#x20;   }

&#x20;   

&#x20;   private fun openImagePicker() {

&#x20;       val intent = Intent(Intent.ACTION\_GET\_CONTENT)

&#x20;       intent.type = "image/\*"

&#x20;       startActivityForResult(intent, PICK\_IMAGE\_REQUEST)

&#x20;   }

&#x20;   

&#x20;   override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

&#x20;       super.onActivityResult(requestCode, resultCode, data)

&#x20;       

&#x20;       if (requestCode == PICK\_IMAGE\_REQUEST \&\& resultCode == RESULT\_OK) {

&#x20;           selectedImageUri = data?.data

&#x20;           imgProductPhoto.setImageURI(selectedImageUri)

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun validateAndUpload() {

&#x20;       val name = etProductName.text.toString().trim()

&#x20;       val priceStr = etPrice.text.toString().trim()

&#x20;       val origin = etOrigin.text.toString().trim()

&#x20;       val description = etDescription.text.toString().trim()

&#x20;       val freshness = spinnerFreshness.selectedItem.toString()

&#x20;       val category = spinnerCategory.selectedItem.toString()

&#x20;       

&#x20;       // Validation

&#x20;       when {

&#x20;           name.length < 3 -> {

&#x20;               etProductName.error = "Name must be at least 3 characters"

&#x20;               return

&#x20;           }

&#x20;           priceStr.isEmpty() || priceStr.toDoubleOrNull() == null || priceStr.toDouble() <= 0 -> {

&#x20;               etPrice.error = "Enter valid price"

&#x20;               return

&#x20;           }

&#x20;           selectedImageUri == null -> {

&#x20;               Toast.makeText(this, "Please select a product photo", Toast.LENGTH\_SHORT).show()

&#x20;               return

&#x20;           }

&#x20;           origin.isEmpty() -> {

&#x20;               etOrigin.error = "Enter product origin/source"

&#x20;               return

&#x20;           }

&#x20;       }

&#x20;       

&#x20;       // Show loading

&#x20;       progressBar.visibility = View.VISIBLE

&#x20;       btnUpload.isEnabled = false

&#x20;       

&#x20;       // Upload image first, then save data

&#x20;       uploadImageAndSaveProduct(name, priceStr.toDouble(), freshness, origin, category, description)

&#x20;   }

&#x20;   

&#x20;   private fun uploadImageAndSaveProduct(

&#x20;       name: String,

&#x20;       price: Double,

&#x20;       freshness: String,

&#x20;       origin: String,

&#x20;       category: String,

&#x20;       description: String

&#x20;   ) {

&#x20;       val uid = auth.currentUser?.uid ?: return

&#x20;       val filename = "products/${uid}\_${System.currentTimeMillis()}.jpg"

&#x20;       val storageRef = storage.reference.child(filename)

&#x20;       

&#x20;       // Compress image

&#x20;       val compressedImage = compressImage(selectedImageUri!!)

&#x20;       

&#x20;       storageRef.putBytes(compressedImage)

&#x20;           .addOnSuccessListener { taskSnapshot ->

&#x20;               storageRef.downloadUrl.addOnSuccessListener { uri ->

&#x20;                   saveProductToFirestore(

&#x20;                       name, price, freshness, origin, category, description, uri.toString()

&#x20;                   )

&#x20;               }

&#x20;           }

&#x20;           .addOnFailureListener {

&#x20;               progressBar.visibility = View.GONE

&#x20;               btnUpload.isEnabled = true

&#x20;               Toast.makeText(this, "Upload failed: ${it.message}", Toast.LENGTH\_SHORT).show()

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   private fun compressImage(uri: Uri): ByteArray {

&#x20;       val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)

&#x20;       val outputStream = ByteArrayOutputStream()

&#x20;       

&#x20;       var quality = 80

&#x20;       bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

&#x20;       

&#x20;       // Ensure under 500KB

&#x20;       while (outputStream.size() > 500 \* 1024 \&\& quality > 10) {

&#x20;           outputStream.reset()

&#x20;           quality -= 10

&#x20;           bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

&#x20;       }

&#x20;       

&#x20;       return outputStream.toByteArray()

&#x20;   }

&#x20;   

&#x20;   private fun saveProductToFirestore(

&#x20;       name: String,

&#x20;       price: Double,

&#x20;       freshness: String,

&#x20;       origin: String,

&#x20;       category: String,

&#x20;       description: String,

&#x20;       imageUrl: String

&#x20;   ) {

&#x20;       val uid = auth.currentUser?.uid ?: return

&#x20;       

&#x20;       val productData = hashMapOf(

&#x20;           "name" to name,

&#x20;           "price" to price,

&#x20;           "freshness" to freshness,

&#x20;           "origin" to origin,

&#x20;           "category" to category,

&#x20;           "description" to description,

&#x20;           "imageUrl" to imageUrl,

&#x20;           "vendorId" to uid,

&#x20;           "vendorPhone" to auth.currentUser?.phoneNumber,

&#x20;           "createdAt" to FieldValue.serverTimestamp(),

&#x20;           "active" to true,

&#x20;           "viewCount" to 0

&#x20;       )

&#x20;       

&#x20;       db.collection("products").add(productData)

&#x20;           .addOnSuccessListener {

&#x20;               progressBar.visibility = View.GONE

&#x20;               Toast.makeText(this, "Product uploaded successfully!", Toast.LENGTH\_SHORT).show()

&#x20;               finish()

&#x20;           }

&#x20;           .addOnFailureListener {

&#x20;               progressBar.visibility = View.GONE

&#x20;               btnUpload.isEnabled = true

&#x20;               Toast.makeText(this, "Failed to save product", Toast.LENGTH\_SHORT).show()

&#x20;           }

&#x20;   }

}





SCREEN 6: VENDOR REVIEWS \& RATINGS

Purpose: Display all reviews received by vendor



UI Elements:



┌──────────────────────────────────────┐

│  ← Back          My Reviews          │

│                                      │

│  Overall Rating                      │

│  ┌────────────────────────────────┐  │

│  │         4.5 ★★★★★             │  │

│  │      Based on 23 reviews       │  │

│  └────────────────────────────────┘  │

│                                      │

│  ─────────────────────────────────   │

│                                      │

│  ┌────────────────────────────────┐  │

│  │ 👤 Customer Name               │  │

│  │ ★★★★★ 5.0                     │  │

│  │ "Excellent quality! Fresh..."  │  │

│  │ Product: Tomatoes              │  │

│  │ 2 days ago                     │  │

│  └────────────────────────────────┘  │

│                                      │

│  ┌────────────────────────────────┐  │

│  │ 👤 Another Customer            │  │

│  │ ★★★★☆ 4.0                     │  │

│  │ "Good product but..."          │  │

│  │ Product: Potatoes              │  │

│  │ 1 week ago                     │  │

│  └────────────────────────────────┘  │

│                                      │

└──────────────────────────────────────┘



Technical Implementation:



// VendorReviewsActivity.kt

class VendorReviewsActivity : AppCompatActivity() {

&#x20;   

&#x20;   private lateinit var db: FirebaseFirestore

&#x20;   private lateinit var auth: FirebaseAuth

&#x20;   private lateinit var adapter: ReviewAdapter

&#x20;   private val reviews = mutableListOf<Review>()

&#x20;   

&#x20;   override fun onCreate(savedInstanceState: Bundle?) {

&#x20;       super.onCreate(savedInstanceState)

&#x20;       setContentView(R.layout.activity\_vendor\_reviews)

&#x20;       

&#x20;       db = FirebaseFirestore.getInstance()

&#x20;       auth = FirebaseAuth.getInstance()

&#x20;       

&#x20;       setupRecyclerView()

&#x20;       loadReviews()

&#x20;   }

&#x20;   

&#x20;   private fun setupRecyclerView() {

&#x20;       adapter = ReviewAdapter(reviews)

&#x20;       rvReviews.layoutManager = LinearLayoutManager(this)

&#x20;       rvReviews.adapter = adapter

&#x20;   }

&#x20;   

&#x20;   private fun loadReviews() {

&#x20;       val uid = auth.currentUser?.uid ?: return

&#x20;       

&#x20;       db.collection("reviews")

&#x20;           .whereEqualTo("vendorId", uid)

&#x20;           .orderBy("timestamp", Query.Direction.DESCENDING)

&#x20;           .addSnapshotListener { snapshot, error ->

&#x20;               if (error != null) return@addSnapshotListener

&#x20;               

&#x20;               reviews.clear()

&#x20;               snapshot?.documents?.forEach { doc ->

&#x20;                   val review = doc.toObject(Review::class.java)

&#x20;                   review?.let { reviews.add(it) }

&#x20;               }

&#x20;               

&#x20;               adapter.notifyDataSetChanged()

&#x20;               calculateAverageRating()

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   private fun calculateAverageRating() {

&#x20;       if (reviews.isEmpty()) {

&#x20;           txtOverallRating.text = "No reviews yet"

&#x20;           txtReviewCount.text = ""

&#x20;           return

&#x20;       }

&#x20;       

&#x20;       val avg = reviews.map { it.rating }.average()

&#x20;       txtOverallRating.text = "%.1f".format(avg)

&#x20;       txtReviewCount.text = "Based on ${reviews.size} reviews"

&#x20;       ratingBar.rating = avg.toFloat()

&#x20;   }

}



data class Review(

&#x20;   val customerId: String = "",

&#x20;   val customerName: String = "",

&#x20;   val vendorId: String = "",

&#x20;   val productId: String = "",

&#x20;   val productName: String = "",

&#x20;   val rating: Double = 0.0,

&#x20;   val comment: String = "",

&#x20;   val timestamp: Timestamp? = null

)





SCREEN 4B: CUSTOMER DASHBOARD

Purpose: Browse products, search, and filter



UI Elements:



┌──────────────────────────────────────┐

│  ☰ Menu    \[Search Icon]  \[Profile]  │

│                                      │

│  🔍 Search products...               │

│  ┌────────────────────────────────┐  │

│  │ \_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_\_      │  │

│  └────────────────────────────────┘  │

│                                      │

│  ┌─┬─┬─┬─┬─┬─┬─┬─┐ (Chip Filters)    │

│  │All│Veg│Fruit│Grain│Dairy│...│    │

│  └─┴─┴─┴─┴─┴─┴─┴─┘                   │

│                                      │

│  ┌────────────┐  ┌────────────┐      │

│  │  \[Image]  │  │  \[Image]  │      │

│  │  Tomatoes │  │  Potatoes │      │

│  │  ₹40/kg   │  │  ₹30/kg   │      │

│  │  ★ 4.5    │  │  ★ 4.2    │      │

│  └────────────┘  └────────────┘      │

│                                      │

│  ┌────────────┐  ┌────────────┐      │

│  │  \[Image]  │  │  \[Image]  │      │

│  │  Onions   │  │  Carrots  │      │

│  │  ₹25/kg   │  │  ₹50/kg   │      │

│  │  ★ 4.8    │  │  ★ 4.0    │      │

│  └────────────┘  └────────────┘      │

│                                      │

└──────────────────────────────────────┘



Features:



* Real-time search by product name
* Category filter chips (multi-select)
* Pull-to-refresh
* Infinite scroll (pagination)
* Empty state when no products



Technical Implementation:



// CustomerDashboardActivity.kt

class CustomerDashboardActivity : AppCompatActivity() {

&#x20;   

&#x20;   private lateinit var db: FirebaseFirestore

&#x20;   private lateinit var adapter: ProductAdapter

&#x20;   private val products = mutableListOf<Product>()

&#x20;   private val filteredProducts = mutableListOf<Product>()

&#x20;   private var selectedCategories = mutableSetOf<String>()

&#x20;   

&#x20;   override fun onCreate(savedInstanceState: Bundle?) {

&#x20;       super.onCreate(savedInstanceState)

&#x20;       setContentView(R.layout.activity\_customer\_dashboard)

&#x20;       

&#x20;       db = FirebaseFirestore.getInstance()

&#x20;       

&#x20;       setupRecyclerView()

&#x20;       setupSearchView()

&#x20;       setupCategoryChips()

&#x20;       loadProducts()

&#x20;       

&#x20;       swipeRefresh.setOnRefreshListener {

&#x20;           loadProducts()

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun setupRecyclerView() {

&#x20;       adapter = ProductAdapter(filteredProducts) { product ->

&#x20;           openProductDetail(product)

&#x20;       }

&#x20;       

&#x20;       rvProducts.layoutManager = GridLayoutManager(this, 2)

&#x20;       rvProducts.adapter = adapter

&#x20;   }

&#x20;   

&#x20;   private fun setupSearchView() {

&#x20;       etSearch.addTextChangedListener(object : TextWatcher {

&#x20;           override fun afterTextChanged(s: Editable?) {

&#x20;               filterProducts(s.toString())

&#x20;           }

&#x20;           override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

&#x20;           override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

&#x20;       })

&#x20;   }

&#x20;   

&#x20;   private fun setupCategoryChips() {

&#x20;       val categories = listOf("All", "Vegetables", "Fruits", "Grains", "Dairy", "Meat \& Fish", "Bakery", "Other")

&#x20;       

&#x20;       categories.forEach { category ->

&#x20;           val chip = Chip(this)

&#x20;           chip.text = category

&#x20;           chip.isCheckable = true

&#x20;           chip.isChecked = category == "All"

&#x20;           

&#x20;           chip.setOnCheckedChangeListener { \_, isChecked ->

&#x20;               if (category == "All" \&\& isChecked) {

&#x20;                   selectedCategories.clear()

&#x20;                   chipGroup.children.forEach { (it as? Chip)?.isChecked = it == chip }

&#x20;               } else if (category != "All") {

&#x20;                   chipGroup.findViewById<Chip>(R.id.chipAll)?.isChecked = false

&#x20;                   if (isChecked) {

&#x20;                       selectedCategories.add(category)

&#x20;                   } else {

&#x20;                       selectedCategories.remove(category)

&#x20;                   }

&#x20;               }

&#x20;               applyFilters()

&#x20;           }

&#x20;           

&#x20;           chipGroup.addView(chip)

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun loadProducts() {

&#x20;       progressBar.visibility = View.VISIBLE

&#x20;       

&#x20;       db.collection("products")

&#x20;           .whereEqualTo("active", true)

&#x20;           .orderBy("createdAt", Query.Direction.DESCENDING)

&#x20;           .addSnapshotListener { snapshot, error ->

&#x20;               progressBar.visibility = View.GONE

&#x20;               swipeRefresh.isRefreshing = false

&#x20;               

&#x20;               if (error != null) {

&#x20;                   Toast.makeText(this, "Error loading products", Toast.LENGTH\_SHORT).show()

&#x20;                   return@addSnapshotListener

&#x20;               }

&#x20;               

&#x20;               products.clear()

&#x20;               snapshot?.documents?.forEach { doc ->

&#x20;                   val product = doc.toObject(Product::class.java)

&#x20;                   product?.let {

&#x20;                       it.id = doc.id

&#x20;                       products.add(it)

&#x20;                   }

&#x20;               }

&#x20;               

&#x20;               applyFilters()

&#x20;               showEmptyStateIfNeeded()

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   private fun filterProducts(query: String) {

&#x20;       val searchQuery = query.lowercase()

&#x20;       

&#x20;       filteredProducts.clear()

&#x20;       filteredProducts.addAll(

&#x20;           products.filter { product ->

&#x20;               val matchesSearch = product.name.lowercase().contains(searchQuery) ||

&#x20;                                 product.description.lowercase().contains(searchQuery)

&#x20;               

&#x20;               val matchesCategory = selectedCategories.isEmpty() || 

&#x20;                                    selectedCategories.contains(product.category)

&#x20;               

&#x20;               matchesSearch \&\& matchesCategory

&#x20;           }

&#x20;       )

&#x20;       

&#x20;       adapter.notifyDataSetChanged()

&#x20;       showEmptyStateIfNeeded()

&#x20;   }

&#x20;   

&#x20;   private fun applyFilters() {

&#x20;       filterProducts(etSearch.text.toString())

&#x20;   }

&#x20;   

&#x20;   private fun showEmptyStateIfNeeded() {

&#x20;       if (filteredProducts.isEmpty()) {

&#x20;           rvProducts.visibility = View.GONE

&#x20;           emptyState.visibility = View.VISIBLE

&#x20;       } else {

&#x20;           rvProducts.visibility = View.VISIBLE

&#x20;           emptyState.visibility = View.GONE

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun openProductDetail(product: Product) {

&#x20;       val intent = Intent(this, ProductDetailActivity::class.java)

&#x20;       intent.putExtra("PRODUCT\_ID", product.id)

&#x20;       startActivity(intent)

&#x20;   }

}



data class Product(

&#x20;   var id: String = "",

&#x20;   val name: String = "",

&#x20;   val price: Double = 0.0,

&#x20;   val freshness: String = "",

&#x20;   val origin: String = "",

&#x20;   val category: String = "",

&#x20;   val description: String = "",

&#x20;   val imageUrl: String = "",

&#x20;   val vendorId: String = "",

&#x20;   val vendorPhone: String = "",

&#x20;   val createdAt: Timestamp? = null,

&#x20;   val active: Boolean = true,

&#x20;   var averageRating: Double = 0.0,

&#x20;   var reviewCount: Int = 0

)



SCREEN 7: PRODUCT DETAIL (Customer)

Purpose: Show full product information with action buttons



UI Elements:



┌──────────────────────────────────────┐

│  ← Back                 \[Share Icon] │

│                                      │

│  ┌────────────────────────────────┐  │

│  │                                │  │

│  │      \[PRODUCT IMAGE]           │  │

│  │         Full Width             │  │

│  │                                │  │

│  └────────────────────────────────┘  │

│                                      │

│  Fresh Tomatoes                      │

│  ★★★★★ 4.5 (12 reviews)             │

│                                      │

│  ₹40 per kg                          │

│                                      │

│  ┌────────────────────────────────┐  │

│  │ 📦 Freshness: Fresh Today      │  │

│  │ 🌍 Origin: Local Farm, Pune    │  │

│  │ 📁 Category: Vegetables        │  │

│  │ 👤 Seller: Ramesh Kumar        │  │

│  └────────────────────────────────┘  │

│                                      │

│  Description                         │

│  Fresh organic tomatoes grown...    │

│  handpicked this morning...          │

│                                      │

│  ┌────────────────────────────────┐  │

│  │  💬 CHAT ON WHATSAPP          │  │

│  └────────────────────────────────┘  │

│                                      │

│  ┌────────────────────────────────┐  │

│  │  ⭐ WRITE A REVIEW             │  │

│  └────────────────────────────────┘  │

│                                      │

│  ──── Customer Reviews ────          │

│                                      │

│  ★★★★★ "Excellent quality!"          │

│  - Customer Name, 2 days ago         │

│                                      │

└──────────────────────────────────────┘



Behavior:



* Load product from Firestore by ID
* Load reviews for this product
* Calculate average rating in real-time
* WhatsApp button pre-fills message with product name
* Review button opens rating dialog



Technical Implementation:



// ProductDetailActivity.kt

class ProductDetailActivity : AppCompatActivity() {

&#x20;   

&#x20;   private lateinit var db: FirebaseFirestore

&#x20;   private lateinit var auth: FirebaseAuth

&#x20;   private var productId: String = ""

&#x20;   private var product: Product? = null

&#x20;   

&#x20;   override fun onCreate(savedInstanceState: Bundle?) {

&#x20;       super.onCreate(savedInstanceState)

&#x20;       setContentView(R.layout.activity\_product\_detail)

&#x20;       

&#x20;       db = FirebaseFirestore.getInstance()

&#x20;       auth = FirebaseAuth.getInstance()

&#x20;       

&#x20;       productId = intent.getStringExtra("PRODUCT\_ID") ?: return

&#x20;       

&#x20;       loadProductDetails()

&#x20;       loadReviews()

&#x20;       

&#x20;       btnWhatsApp.setOnClickListener {

&#x20;           openWhatsApp()

&#x20;       }

&#x20;       

&#x20;       btnWriteReview.setOnClickListener {

&#x20;           showReviewDialog()

&#x20;       }

&#x20;       

&#x20;       btnShare.setOnClickListener {

&#x20;           shareProduct()

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun loadProductDetails() {

&#x20;       db.collection("products").document(productId).get()

&#x20;           .addOnSuccessListener { document ->

&#x20;               product = document.toObject(Product::class.java)

&#x20;               product?.let { displayProduct(it) }

&#x20;               incrementViewCount()

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   private fun displayProduct(product: Product) {

&#x20;       txtProductName.text = product.name

&#x20;       txtPrice.text = "₹${product.price} per kg"

&#x20;       txtFreshness.text = "📦 Freshness: ${product.freshness}"

&#x20;       txtOrigin.text = "🌍 Origin: ${product.origin}"

&#x20;       txtCategory.text = "📁 Category: ${product.category}"

&#x20;       txtDescription.text = product.description

&#x20;       

&#x20;       // Load vendor name

&#x20;       db.collection("users").document(product.vendorId).get()

&#x20;           .addOnSuccessListener { vendor ->

&#x20;               val vendorName = vendor.getString("name") ?: "Vendor"

&#x20;               txtSeller.text = "👤 Seller: $vendorName"

&#x20;           }

&#x20;       

&#x20;       // Load image

&#x20;       Glide.with(this)

&#x20;           .load(product.imageUrl)

&#x20;           .placeholder(R.drawable.placeholder\_product)

&#x20;           .into(imgProduct)

&#x20;   }

&#x20;   

&#x20;   private fun loadReviews() {

&#x20;       db.collection("reviews")

&#x20;           .whereEqualTo("productId", productId)

&#x20;           .orderBy("timestamp", Query.Direction.DESCENDING)

&#x20;           .limit(5)

&#x20;           .addSnapshotListener { snapshot, error ->

&#x20;               if (error != null) return@addSnapshotListener

&#x20;               

&#x20;               val reviews = snapshot?.documents?.mapNotNull { 

&#x20;                   it.toObject(Review::class.java) 

&#x20;               } ?: emptyList()

&#x20;               

&#x20;               displayReviews(reviews)

&#x20;               calculateRating(reviews)

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   private fun displayReviews(reviews: List<Review>) {

&#x20;       layoutReviews.removeAllViews()

&#x20;       

&#x20;       reviews.forEach { review ->

&#x20;           val reviewView = layoutInflater.inflate(R.layout.item\_review, layoutReviews, false)

&#x20;           reviewView.findViewById<RatingBar>(R.id.ratingBar).rating = review.rating.toFloat()

&#x20;           reviewView.findViewById<TextView>(R.id.txtComment).text = "\\"${review.comment}\\""

&#x20;           reviewView.findViewById<TextView>(R.id.txtCustomer).text = 

&#x20;               "- ${review.customerName}, ${getTimeAgo(review.timestamp)}"

&#x20;           layoutReviews.addView(reviewView)

&#x20;       }

&#x20;       

&#x20;       if (reviews.isEmpty()) {

&#x20;           txtNoReviews.visibility = View.VISIBLE

&#x20;       } else {

&#x20;           txtNoReviews.visibility = View.GONE

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun calculateRating(reviews: List<Review>) {

&#x20;       if (reviews.isEmpty()) {

&#x20;           txtRating.text = "No reviews yet"

&#x20;           ratingBar.rating = 0f

&#x20;           return

&#x20;       }

&#x20;       

&#x20;       val avg = reviews.map { it.rating }.average()

&#x20;       txtRating.text = "★★★★★ %.1f (%d reviews)".format(avg, reviews.size)

&#x20;       ratingBar.rating = avg.toFloat()

&#x20;   }

&#x20;   

&#x20;   private fun openWhatsApp() {

&#x20;       val product = this.product ?: return

&#x20;       val message = "Hi, I'm interested in ${product.name}. Is it available?"

&#x20;       val phoneNumber = product.vendorPhone.replace("+", "").replace(" ", "")

&#x20;       

&#x20;       val intent = Intent(Intent.ACTION\_VIEW)

&#x20;       intent.data = Uri.parse("https://wa.me/$phoneNumber?text=${Uri.encode(message)}")

&#x20;       

&#x20;       try {

&#x20;           startActivity(intent)

&#x20;       } catch (e: Exception) {

&#x20;           Toast.makeText(this, "WhatsApp not installed", Toast.LENGTH\_SHORT).show()

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun showReviewDialog() {

&#x20;       val dialog = Dialog(this)

&#x20;       dialog.setContentView(R.layout.dialog\_write\_review)

&#x20;       

&#x20;       val ratingBar = dialog.findViewById<RatingBar>(R.id.ratingBar)

&#x20;       val etComment = dialog.findViewById<EditText>(R.id.etComment)

&#x20;       val btnSubmit = dialog.findViewById<Button>(R.id.btnSubmit)

&#x20;       

&#x20;       btnSubmit.setOnClickListener {

&#x20;           val rating = ratingBar.rating.toDouble()

&#x20;           val comment = etComment.text.toString().trim()

&#x20;           

&#x20;           if (rating == 0.0) {

&#x20;               Toast.makeText(this, "Please select a rating", Toast.LENGTH\_SHORT).show()

&#x20;               return@setOnClickListener

&#x20;           }

&#x20;           

&#x20;           submitReview(rating, comment)

&#x20;           dialog.dismiss()

&#x20;       }

&#x20;       

&#x20;       dialog.show()

&#x20;   }

&#x20;   

&#x20;   private fun submitReview(rating: Double, comment: String) {

&#x20;       val uid = auth.currentUser?.uid ?: return

&#x20;       val product = this.product ?: return

&#x20;       

&#x20;       db.collection("users").document(uid).get()

&#x20;           .addOnSuccessListener { userDoc ->

&#x20;               val customerName = userDoc.getString("name") ?: "Anonymous"

&#x20;               

&#x20;               val reviewData = hashMapOf(

&#x20;                   "customerId" to uid,

&#x20;                   "customerName" to customerName,

&#x20;                   "vendorId" to product.vendorId,

&#x20;                   "productId" to productId,

&#x20;                   "productName" to product.name,

&#x20;                   "rating" to rating,

&#x20;                   "comment" to comment,

&#x20;                   "timestamp" to FieldValue.serverTimestamp()

&#x20;               )

&#x20;               

&#x20;               db.collection("reviews").add(reviewData)

&#x20;                   .addOnSuccessListener {

&#x20;                       Toast.makeText(this, "Review submitted!", Toast.LENGTH\_SHORT).show()

&#x20;                   }

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   private fun incrementViewCount() {

&#x20;       db.collection("products").document(productId)

&#x20;           .update("viewCount", FieldValue.increment(1))

&#x20;   }

&#x20;   

&#x20;   private fun shareProduct() {

&#x20;       val product = this.product ?: return

&#x20;       val shareText = "Check out ${product.name} for ₹${product.price} on VendorConnect!"

&#x20;       

&#x20;       val intent = Intent(Intent.ACTION\_SEND)

&#x20;       intent.type = "text/plain"

&#x20;       intent.putExtra(Intent.EXTRA\_TEXT, shareText)

&#x20;       startActivity(Intent.createChooser(intent, "Share Product"))

&#x20;   }

&#x20;   

&#x20;   private fun getTimeAgo(timestamp: Timestamp?): String {

&#x20;       timestamp ?: return "Recently"

&#x20;       val now = System.currentTimeMillis()

&#x20;       val diff = now - timestamp.toDate().time

&#x20;       

&#x20;       return when {

&#x20;           diff < 3600000 -> "${diff / 60000} minutes ago"

&#x20;           diff < 86400000 -> "${diff / 3600000} hours ago"

&#x20;           diff < 604800000 -> "${diff / 86400000} days ago"

&#x20;           else -> "Over a week ago"

&#x20;       }

&#x20;   }

}



4\. BACKEND \& DATABASE STRUCTURE

Firebase Firestore Collections

Collection: users



{

&#x20; "uid": "firebase\_auth\_uid",

&#x20; "role": "VENDOR" | "CUSTOMER",

&#x20; "phoneNumber": "+919876543210",

&#x20; "name": "Ramesh Kumar",

&#x20; "email": "optional@email.com",

&#x20; "profileImage": "https://...",

&#x20; "createdAt": Timestamp,

&#x20; "profileComplete": true,

&#x20; "active": true,

&#x20; 

&#x20; // Vendor-specific fields

&#x20; "businessName": "Kumar Farms",

&#x20; "businessAddress": "Pune, Maharashtra",

&#x20; "gstNumber": "optional",

&#x20; 

&#x20; // Customer-specific fields

&#x20; "savedProducts": \["productId1", "productId2"]

}



Collection: products



{

&#x20; "id": "auto\_generated\_doc\_id",

&#x20; "name": "Fresh Tomatoes",

&#x20; "price": 40.0,

&#x20; "freshness": "Fresh Today",

&#x20; "origin": "Local Farm, Pune",

&#x20; "category": "Vegetables",

&#x20; "description": "Fresh organic tomatoes...",

&#x20; "imageUrl": "https://storage.googleapis.com/...",

&#x20; "vendorId": "vendor\_uid",

&#x20; "vendorPhone": "+919876543210",

&#x20; "createdAt": Timestamp,

&#x20; "updatedAt": Timestamp,

&#x20; "active": true,

&#x20; "viewCount": 234,

&#x20; "featured": false

}





Collection: reviews



{

&#x20; "customerId": "customer\_uid",

&#x20; "customerName": "Priya Sharma",

&#x20; "vendorId": "vendor\_uid",

&#x20; "productId": "product\_doc\_id",

&#x20; "productName": "Fresh Tomatoes",

&#x20; "rating": 4.5,

&#x20; "comment": "Excellent quality! Very fresh.",

&#x20; "timestamp": Timestamp,

&#x20; "helpful": 5,

&#x20; "verified": true

}



Collection: conversations (Optional for chat tracking) I WOULD LIKE TO HAVE CONNECTED TO THE WHATSAPP DIRECTLY 





{

&#x20; "customerId": "customer\_uid",

&#x20; "vendorId": "vendor\_uid",

&#x20; "productId": "product\_doc\_id",

&#x20; "lastMessage": "Is this available?",

&#x20; "lastMessageTime": Timestamp,

&#x20; "whatsappLink": "https://wa.me/..."

}





Firestore Security Rules



rules\_version = '2';

service cloud.firestore {

&#x20; match /databases/{database}/documents {

&#x20;   

&#x20;   // Users collection

&#x20;   match /users/{userId} {

&#x20;     allow read: if true; // Public profiles

&#x20;     allow write: if request.auth.uid == userId;

&#x20;   }

&#x20;   

&#x20;   // Products collection

&#x20;   match /products/{productId} {

&#x20;     allow read: if true; // Anyone can browse

&#x20;     allow create: if request.auth != null \&\& 

&#x20;                     get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == "VENDOR";

&#x20;     allow update, delete: if request.auth.uid == resource.data.vendorId;

&#x20;   }

&#x20;   

&#x20;   // Reviews collection

&#x20;   match /reviews/{reviewId} {

&#x20;     allow read: if true;

&#x20;     allow create: if request.auth != null \&\&

&#x20;                     get(/databases/$(database)/documents/users/$(request.auth.uid)).data.role == "CUSTOMER";

&#x20;     allow update, delete: if request.auth.uid == resource.data.customerId;

&#x20;   }

&#x20; }

}





Firebase Storage Structure



/products

&#x20; /{vendorId}\_{timestamp}.jpg

&#x20; /{vendorId}\_{timestamp}\_thumb.jpg (optional thumbnails)



/profiles

&#x20; /{userId}.jpg



/temp

&#x20; /{userId}\_{timestamp}.jpg (cleanup after 24h)





Storage Security Rules



rules\_version = '2';

service firebase.storage {

&#x20; match /b/{bucket}/o {

&#x20;   

&#x20;   match /products/{allPaths=\*\*} {

&#x20;     allow read: if true;

&#x20;     allow write: if request.auth != null \&\& 

&#x20;                    request.resource.size < 5 \* 1024 \* 1024 \&\&

&#x20;                    request.resource.contentType.matches('image/.\*');

&#x20;   }

&#x20;   

&#x20;   match /profiles/{userId}/{allPaths=\*\*} {

&#x20;     allow read: if true;

&#x20;     allow write: if request.auth.uid == userId \&\&

&#x20;                    request.resource.size < 2 \* 1024 \* 1024;

&#x20;   }

&#x20; }

}



5\. AUTHENTICATION SYSTEM

Phone Authentication Flow



1\. User enters phone number (+91XXXXXXXXXX)

2\. Firebase sends SMS with 6-digit OTP

3\. User enters OTP

4\. Firebase verifies OTP

5\. If valid:

&#x20;  a. Check if user exists in Firestore

&#x20;  b. If new: Create user document with role

&#x20;  c. If existing: Load user data

6\. Store auth state locally

7\. Navigate to appropriate dashboard





Session Management



// AuthManager.kt

class AuthManager(private val context: Context) {

&#x20;   

&#x20;   private val auth = FirebaseAuth.getInstance()

&#x20;   private val db = FirebaseFirestore.getInstance()

&#x20;   private val prefs = context.getSharedPreferences("app\_prefs", Context.MODE\_PRIVATE)

&#x20;   

&#x20;   fun isLoggedIn(): Boolean {

&#x20;       return auth.currentUser != null

&#x20;   }

&#x20;   

&#x20;   fun getUserRole(callback: (String?) -> Unit) {

&#x20;       val uid = auth.currentUser?.uid ?: return callback(null)

&#x20;       

&#x20;       db.collection("users").document(uid).get()

&#x20;           .addOnSuccessListener { doc ->

&#x20;               callback(doc.getString("role"))

&#x20;           }

&#x20;           .addOnFailureListener {

&#x20;               callback(null)

&#x20;           }

&#x20;   }

&#x20;   

&#x20;   fun logout() {

&#x20;       auth.signOut()

&#x20;       prefs.edit().clear().apply()

&#x20;   }

&#x20;   

&#x20;   fun saveUserRole(role: String) {

&#x20;       prefs.edit().putString("user\_role", role).apply()

&#x20;   }

&#x20;   

&#x20;   fun getCachedUserRole(): String? {

&#x20;       return prefs.getString("user\_role", null)

&#x20;   }

}





Auto-Login Implementation



// MainActivity.kt (Splash/Launch Activity)

class MainActivity : AppCompatActivity() {

&#x20;   

&#x20;   private lateinit var authManager: AuthManager

&#x20;   

&#x20;   override fun onCreate(savedInstanceState: Bundle?) {

&#x20;       super.onCreate(savedInstanceState)

&#x20;       setContentView(R.layout.activity\_main)

&#x20;       

&#x20;       authManager = AuthManager(this)

&#x20;       

&#x20;       Handler(Looper.getMainLooper()).postDelayed({

&#x20;           checkAuthStatus()

&#x20;       }, 2000) // 2-second splash

&#x20;   }

&#x20;   

&#x20;   private fun checkAuthStatus() {

&#x20;       if (authManager.isLoggedIn()) {

&#x20;           authManager.getUserRole { role ->

&#x20;               when (role) {

&#x20;                   "VENDOR" -> navigateTo(VendorDashboardActivity::class.java)

&#x20;                   "CUSTOMER" -> navigateTo(CustomerDashboardActivity::class.java)

&#x20;                   else -> navigateTo(RoleSelectionActivity::class.java)

&#x20;               }

&#x20;           }

&#x20;       } else {

&#x20;           navigateTo(RoleSelectionActivity::class.java)

&#x20;       }

&#x20;   }

&#x20;   

&#x20;   private fun navigateTo(activityClass: Class<\*>) {

&#x20;       val intent = Intent(this, activityClass)

&#x20;       intent.flags = Intent.FLAG\_ACTIVITY\_NEW\_TASK or Intent.FLAG\_ACTIVITY\_CLEAR\_TASK

&#x20;       startActivity(intent)

&#x20;       finish()

&#x20;   }

}





6\. FEATURE REQUIREMENTS

A. Functional Requirements



ID	Feature			Description				Priority

FR-01	Role Selection		User chooses Vendor or Customer		MUST HAVE

FR-02	Phone OTP Auth		Login via phone + 6-digit OTP		MUST HAVE

FR-03	Persistent Login	Auto-login on app restart		MUST HAVE

FR-04	Vendor Upload		Add product with photo, details		MUST HAVE

FR-05	Customer Browse		Grid view of all products		MUST HAVE

FR-06	Real-time Search	Filter products by name			MUST HAVE

FR-07	Category Filter		Chips to filter by category		MUST HAVE

FR-08	Product Detail		Full product info screen		MUST HAVE

FR-09	WhatsApp Chat		Direct messaging via WhatsApp		MUST HAVE

FR-10	Write Review		Customers rate products			MUST HAVE

FR-11	View Reviews		Display ratings on products		MUST HAVE

FR-12	Vendor Reviews		Vendors see their reviews		MUST HAVE

FR-13	Image Compression	Auto-compress before upload		MUST HAVE

FR-14	Empty States		Friendly UI when no data		GOOD TO HAVE

FR-15	Share Product		Share via social media			GOOD TO HAVE

FR-16	View Counter		Track product views			GOOD TO HAVE

FR-17	Profile Management	Edit user profile			FUTURE

FR-18	In-App Chat		Chat without WhatsApp			FUTURE

FR-19	Payment Gateway		Process payments in-app			FUTURE

FR-20	Order Tracking		Track delivery status			FUTURE





B. Non-Functional Requirements

ID	Requirement	Specification

NFR-01	Performance	Home screen loads in < 3 seconds on 4G

NFR-02	Scalability	Support 10,000+ products without lag

NFR-03	Compatibility	Android 7.0 (API 24) and above

NFR-04	Security	All data encrypted in transit (HTTPS)

NFR-05	Offline Mode	Show cached data when offline

NFR-06	Image Size	Compressed images < 500 KB each

NFR-07	Battery Usage	Efficient - no constant background sync

NFR-08	Accessibility	Support for TalkBack screen reader

NFR-09	Localization	Support English \& Hindi (future)

NFR-10	Analytics	Track user behavior with Firebase Analytics



7\. TECHNOLOGY STACK

Frontend (Android App)

Technology		Purpose				Version	

Kotlin	Primary 	programming language		Latest

Android Studio		IDE	Electric 		Eel+

XML Layouts		UI design			-

Material Design 3	UI components			Latest

RecyclerView		Product grid/lists		Built-in

Glide			Image loading \& caching		4.14.2

ViewBinding		Type-safe view access		Built-in



Backend \& Database

Technology			Purpose				Plan

Firebase Authentication		Phone OTP login			Free (10K/month)

Cloud Firestore			NoSQL database			Free (50K reads/day)

Firebase Storage		Image storage			Free (5 GB)

Cloud Functions			Server-side logic (optional)	Pay-as-you-go

Firebase Analytics		User tracking			Free



Third-Party Integrations

Service				Purpose			Cost

WhatsApp Business API		Direct messaging	Free (Intent)

Razorpay (Future)		Payment processing	2% per transaction

Google Maps API (Future)	Vendor location	Free 	(28K loads/month)



Development Tools

Tool			Purpose

Git \& GitHub		Version control

Figma/Google Stitch	UI/UX design

Postman			API testing

Firebase Emulator	Local testing

Crashlytics		Crash reporting



8\. API \& THIRD-PARTY INTEGRATIONS

A. Firebase Authentication API

Phone Number Verification:



val options = PhoneAuthOptions.newBuilder(auth)

&#x20;   .setPhoneNumber(phoneNumber)       // "+919876543210"

&#x20;   .setTimeout(60L, TimeUnit.SECONDS)

&#x20;   .setActivity(this)

&#x20;   .setCallbacks(callbacks)

&#x20;   .build()

PhoneAuthProvider.verifyPhoneNumber(options)



OTP Verification:



val credential = PhoneAuthProvider.getCredential(verificationId, code)

auth.signInWithCredential(credential)

&#x20;   .addOnCompleteListener { task ->

&#x20;       if (task.isSuccessful) {

&#x20;           val user = task.result?.user

&#x20;       }

&#x20;   }



B. Firestore CRUD Operations

Create Product:



db.collection("products").add(productData)

&#x20;   .addOnSuccessListener { documentReference ->

&#x20;       Log.d("Firestore", "DocumentSnapshot added with ID: ${documentReference.id}")

&#x20;   }



Read Products (Real-time):



db.collection("products")

&#x20;   .whereEqualTo("active", true)

&#x20;   .orderBy("createdAt", Query.Direction.DESCENDING)

&#x20;   .addSnapshotListener { snapshot, e ->

&#x20;       snapshot?.documents?.forEach { doc ->

&#x20;           val product = doc.toObject(Product::class.java)

&#x20;       }

&#x20;   }



Update Product:

db.collection("products").document(productId)

&#x20;   .update("price", newPrice)



Delete Product:

db.collection("products").document(productId)

&#x20;   .update("active", false) // Soft delete



C. Firebase Storage

Upload Image:



val storageRef = FirebaseStorage.getInstance().reference

val imageRef = storageRef.child("products/${System.currentTimeMillis()}.jpg")



imageRef.putBytes(compressedImageBytes)

&#x20;   .addOnSuccessListener { taskSnapshot ->

&#x20;       imageRef.downloadUrl.addOnSuccessListener { uri ->

&#x20;           val downloadUrl = uri.toString()

&#x20;       }

&#x20;   }



Download Image URL:

Glide.with(context)

&#x20;   .load(imageUrl)

&#x20;   .placeholder(R.drawable.placeholder)

&#x20;   .error(R.drawable.error\_image)

&#x20;   .into(imageView)



D. WhatsApp Intent

Open WhatsApp Chat:



fun openWhatsApp(phoneNumber: String, message: String) {

&#x20;   val url = "https://wa.me/$phoneNumber?text=${Uri.encode(message)}"

&#x20;   val intent = Intent(Intent.ACTION\_VIEW)

&#x20;   intent.data = Uri.parse(url)

&#x20;   

&#x20;   try {

&#x20;       context.startActivity(intent)

&#x20;   } catch (e: ActivityNotFoundException) {

&#x20;       Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH\_SHORT).show()

&#x20;   }

}





11\. DEPLOYMENT INSTRUCTIONS

Step 1: Generate Signed APK



1. In Android Studio: Build → Generate Signed Bundle/APK
2. Select APK
3. Create new keystore:

   * Key store path: keystore.jks
   * Password: (secure password)
   * Alias: release-key
4. Select release build variant
5. Click Finish



Step 2: Firebase Production Setup



\# Update Firestore indexes

firebase deploy --only firestore:indexes



\# Set production security rules

firebase deploy --only firestore:rules

firebase deploy --only storage:rules



\# Enable Firebase Analytics

\# (Automatically enabled in Firebase Console)



Step 3: Play Store Submission (Optional)



1. Create developer account ($25 one-time fee)
2. Prepare store listing:

   * App name: VendorConnect
   * Description: (from this SOP)
   * Screenshots: All screens
   * Feature graphic: 1024×500 px
   * App icon: 512×512 px
3. Upload signed APK
4. Set pricing: Free
5. Submit for review



Step 4: Distribution for Testing



\# Using Firebase App Distribution

firebase appdistribution:distribute app-release.apk \\

&#x20; --app YOUR\_APP\_ID \\

&#x20; --groups testers \\

&#x20; --release-notes "Initial beta release"



Step 5: Monitoring

* Firebase Crashlytics: Monitor crashes
* Firebase Analytics: Track user behavior
* Firestore Usage: Monitor read/write counts
* Storage Usage: Track bandwidth



APPENDIX A: COLOR PALETTE



<!-- colors.xml -->

<resources>

&#x20;   <!-- Primary Colors -->

&#x20;   <color name="primary">#FF6F00</color>          <!-- Saffron Orange -->

&#x20;   <color name="primary\_dark">#E65100</color>

&#x20;   <color name="primary\_light">#FFA726</color>

&#x20;   

&#x20;   <!-- Secondary Colors -->

&#x20;   <color name="secondary">#388E3C</color>        <!-- Earthy Green -->

&#x20;   <color name="secondary\_dark">#2E7D32</color>

&#x20;   <color name="secondary\_light">#66BB6A</color>

&#x20;   

&#x20;   <!-- Accent -->

&#x20;   <color name="accent">#FFC107</color>           <!-- Warm Yellow -->

&#x20;   

&#x20;   <!-- Backgrounds -->

&#x20;   <color name="background">#F5F5F5</color>

&#x20;   <color name="surface">#FFFFFF</color>

&#x20;   

&#x20;   <!-- Text -->

&#x20;   <color name="text\_primary">#212121</color>

&#x20;   <color name="text\_secondary">#757575</color>

&#x20;   <color name="text\_hint">#BDBDBD</color>

&#x20;   

&#x20;   <!-- Status -->

&#x20;   <color name="success">#4CAF50</color>

&#x20;   <color name="error">#F44336</color>

&#x20;   <color name="warning">#FF9800</color>

&#x20;   

&#x20;   <!-- Rating -->

&#x20;   <color name="rating\_star">#FFC107</color>

</resources>





APPENDIX B: FIRESTORE QUERIES REFERENCE

Get All Active Products

db.collection("products")

&#x20;   .whereEqualTo("active", true)

&#x20;   .orderBy("createdAt", Query.Direction.DESCENDING)



Get Products by Vendor

db.collection("products")

&#x20;   .whereEqualTo("active", true)

&#x20;   .orderBy("createdAt", Query.Direction.DESCENDING)



Get Products by Category

db.collection("products")

&#x20;   .whereEqualTo("category", "Vegetables")

&#x20;   .whereEqualTo("active", true)



Search Products by Name

// Note: Firestore doesn't support LIKE queries

// Implement client-side filtering:

products.filter { it.name.contains(query, ignoreCase = true) }



Get Reviews for Product

db.collection("reviews")

&#x20;   .whereEqualTo("productId", productId)

&#x20;   .orderBy("timestamp", Query.Direction.DESCENDING)



Get Reviews for Vendor

db.collection("reviews")

&#x20;   .whereEqualTo("vendorId", vendorId)

&#x20;   .orderBy("timestamp", Query.Direction.DESCENDING)



Pagination Example

var lastVisible: DocumentSnapshot? = null



fun loadMoreProducts() {

&#x20;   var query = db.collection("products")

&#x20;       .whereEqualTo("active", true)

&#x20;       .orderBy("createdAt", Query.Direction.DESCENDING)

&#x20;       .limit(20)

&#x20;   

&#x20;   lastVisible?.let {

&#x20;       query = query.startAfter(it)

&#x20;   }

&#x20;   

&#x20;   query.get().addOnSuccessListener { documents ->

&#x20;       lastVisible = documents.documents.lastOrNull()

&#x20;       // Process documents...

&#x20;   }

}



APPENDIX C: ERROR HANDLING

Common Errors \& Solutions

Error						Cause				Solution

FirebaseAuthInvalidCredentialsException		Wrong OTP			Show "Invalid OTP" toast

FirebaseNetworkException			No internet			Show "Check connection" dialog

StorageException: Quota exceeded		Storage limit reached		Implement better compression

FirestorePermissionDeniedException		Security rules block		Check Firestore rules

NullPointerException				Missing data field		Add null safety checks

OutOfMemoryError				Large images			Reduce image quality



Global Error Handler



class GlobalExceptionHandler : Thread.UncaughtExceptionHandler {

&#x20;   

&#x20;   private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

&#x20;   

&#x20;   override fun uncaughtException(thread: Thread, exception: Throwable) {

&#x20;       // Log to Crashlytics

&#x20;       FirebaseCrashlytics.getInstance().recordException(exception)

&#x20;       

&#x20;       // Show user-friendly message

&#x20;       // (Implement custom crash screen)

&#x20;       

&#x20;       defaultHandler?.uncaughtException(thread, exception)

&#x20;   }

}



// Set in Application class

Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler())



APPENDIX D: SCREEN NAVIGATION MAP

\[Splash Screen]

&#x20;    ↓

\[Role Selection] ←──────────────────┐

&#x20;    ↓                               │

\[Phone Auth]                         │

&#x20;    ↓                               │

\[OTP Verification]                   │

&#x20;    ↓                               │

\[Check User Role]                    │

&#x20;    ├──→ VENDOR                     │

&#x20;    │        ↓                      │

&#x20;    │   \[Vendor Dashboard]          │

&#x20;    │        ├──→ \[Upload Product]  │

&#x20;    │        ├──→ \[My Products]     │

&#x20;    │        ├──→ \[Reviews]         │

&#x20;    │        └──→ \[Profile] ────────┤

&#x20;    │                               │

&#x20;    └──→ CUSTOMER                   │

&#x20;             ↓                      │

&#x20;        \[Customer Dashboard]        │

&#x20;             ├──→ \[Product Detail]  │

&#x20;             │         ├──→ \[WhatsApp]

&#x20;             │         └──→ \[Write Review]

&#x20;             └──→ \[Profile] ────────┘



FINAL DELIVERY PACKAGE

What to Provide to Anti-Gravity Team:

1. This SOP Document (PDF)
2. Figma/Stitch Design Files (All screens)
3. Firebase Project Credentials (JSON file)
4. Assets Folder:

   * App icon (PNG, all densities)
   * Placeholder images
   * Empty state illustrations
   * Color palette file
5. Requirements Summary:

   * Feature list (prioritized)
   * Non-functional requirements
   * Success criteria



SUCCESS CRITERIA

The app is considered complete and successful when:



✅ Authentication:

* User can select role and authenticate via OTP
* Auto-login works on app restart
* Data persists across sessions



✅ Vendor Flow:



* Can upload products with all required fields
* Images compress to < 500 KB
* Products appear in customer dashboard
* Can view all reviews received



✅ Customer Flow:



* Can browse all products in grid view
* Search and filter work instantly
* Product detail shows all information
* WhatsApp button opens with pre-filled message
* Can submit reviews and ratings



✅ Performance:



* App loads in < 3 seconds
* Smooth scrolling with 50+ products
* No crashes under normal use
* Works offline (shows cached data)



✅ Polish:



* UI matches design system
* Empty states are friendly
* Loading indicators show progress
* Error messages are user-friendly













