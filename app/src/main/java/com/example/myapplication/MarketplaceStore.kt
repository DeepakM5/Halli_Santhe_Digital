package com.example.myapplication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random

object MarketplaceStore {
    val categories = listOf(
        "Vegetables",
        "Fruits",
        "Grains",
        "Dairy",
        "Meat & Fish",
        "Bakery",
        "Sarees",
        "Bangles",
        "Spices",
        "Handicrafts",
        "Other"
    )

    val freshnessCategories = setOf(
        "Vegetables",
        "Fruits",
        "Grains",
        "Dairy",
        "Meat & Fish",
        "Bakery",
        "Spices"
    )

    val users = mutableStateListOf<MarketplaceUser>()
    val products = mutableStateListOf<Product>()
    val reviews = mutableStateListOf<Review>()

    var currentUser by mutableStateOf<MarketplaceUser?>(null)
        private set

    var pendingSession by mutableStateOf<OtpSession?>(null)
        private set

    init {
        seedDemoData()
        recalculateProductStats()
    }

    fun logout() {
        currentUser = null
        pendingSession = null
    }

    fun startOtpSession(role: UserRole, phoneNumber: String): OtpSession {
        val normalizedPhone = normalizePhone(phoneNumber)
        val code = generateCode()
        val session = OtpSession(
            phoneNumber = normalizedPhone,
            role = role,
            code = code
        )
        pendingSession = session
        return session
    }

    fun verifyOtp(code: String): Boolean {
        val session = pendingSession ?: return false
        if (session.code != code) return false

        val existingUser = users.firstOrNull { it.phoneNumber == session.phoneNumber }
        currentUser = existingUser ?: createUser(session.role, session.phoneNumber)
        pendingSession = null
        return true
    }

    fun createUser(role: UserRole, phoneNumber: String): MarketplaceUser {
        val cleanedPhone = normalizePhone(phoneNumber)
        val user = MarketplaceUser(
            uid = nextId("user"),
            role = role,
            phoneNumber = cleanedPhone,
            name = defaultNameFor(role, cleanedPhone),
            businessName = if (role == UserRole.VENDOR) "My Local Store" else "",
            businessAddress = if (role == UserRole.VENDOR) "" else ""
        )
        users.add(0, user)
        return user
    }

    fun ensureLoggedInUser(): MarketplaceUser? = currentUser

    fun signInWithPhone(role: UserRole, phoneNumber: String) {
        val normalizedPhone = normalizePhone(phoneNumber)
        val user = users.firstOrNull { it.phoneNumber == normalizedPhone }
        currentUser = user ?: createUser(role, normalizedPhone)
    }

    fun getUserById(uid: String): MarketplaceUser? = users.firstOrNull { it.uid == uid }

    fun getVendorName(uid: String): String {
        return getUserById(uid)?.name ?: "Vendor"
    }

    fun getVendorPhone(uid: String): String {
        return getUserById(uid)?.phoneNumber ?: ""
    }

    fun getProductById(productId: String): Product? = products.firstOrNull { it.id == productId }

    fun getVendorProducts(vendorId: String): List<Product> =
        products.filter { it.vendorId == vendorId && it.active }

    fun getVendorReviews(vendorId: String): List<Review> =
        reviews.filter { it.vendorId == vendorId }.sortedByDescending { it.timestamp }

    fun getProductReviews(productId: String): List<Review> =
        reviews.filter { it.productId == productId }.sortedByDescending { it.timestamp }

    fun addProduct(
        name: String,
        price: Double,
        freshness: String,
        origin: String,
        category: String,
        description: String,
        imageUrl: String
    ) {
        val vendor = currentUser ?: return
        val product = Product(
            id = nextId("product"),
            name = name,
            price = price,
            freshness = freshness,
            origin = origin,
            category = category,
            description = description,
            imageUrl = imageUrl,
            vendorId = vendor.uid,
            vendorPhone = vendor.phoneNumber,
            vendorName = vendor.name,
            createdAt = java.util.Date(),
            active = true
        )
        products.add(0, product)
        recalculateProductStats()
    }

    fun addReview(productId: String, rating: Double, comment: String) {
        val user = currentUser ?: return
        val product = getProductById(productId) ?: return
        val review = Review(
            id = nextId("review"),
            customerId = user.uid,
            customerName = user.name,
            vendorId = product.vendorId,
            productId = product.id,
            productName = product.name,
            rating = rating,
            comment = comment,
            timestamp = java.util.Date(),
            verified = true
        )
        reviews.add(0, review)
        recalculateProductStats()
    }

    fun markViewed(productId: String) {
        val index = products.indexOfFirst { it.id == productId }
        if (index >= 0) {
            val product = products[index]
            products[index] = product.copy(viewCount = product.viewCount + 1)
        }
    }

    fun deleteProduct(productId: String): Boolean {
        val index = products.indexOfFirst { it.id == productId }
        if (index >= 0) {
            products.removeAt(index)
            return true
        }
        return false
    }

    fun toggleSoldOut(productId: String): Boolean {
        val index = products.indexOfFirst { it.id == productId }
        if (index >= 0) {
            val product = products[index]
            products[index] = product.copy(soldOut = !product.soldOut)
            return true
        }
        return false
    }

    fun activeProducts(): List<Product> = products.filter { it.active }.sortedByDescending { it.createdAt }

    fun requiresFreshness(category: String): Boolean = freshnessCategories.contains(category)

    private fun recalculateProductStats() {
        products.forEachIndexed { index, product ->
            val productReviews = reviews.filter { it.productId == product.id }
            val average = if (productReviews.isEmpty()) 0.0 else productReviews.map { it.rating }.average()
            products[index] = product.copy(
                averageRating = average,
                reviewCount = productReviews.size
            )
        }
    }

    private fun seedDemoData() {
        if (users.isNotEmpty()) return

        val vendor = MarketplaceUser(
            uid = "vendor-1",
            role = UserRole.VENDOR,
            phoneNumber = "+919876543210",
            name = "Ramesh Kumar",
            businessName = "Kumar Farms",
            businessAddress = "Pune, Maharashtra",
            profileComplete = true
        )
        val customer = MarketplaceUser(
            uid = "customer-1",
            role = UserRole.CUSTOMER,
            phoneNumber = "+919812345678",
            name = "Priya Sharma",
            profileComplete = true
        )
        users.add(vendor)
        users.add(customer)

        val demoProducts = listOf(
            Product(
                id = "product-1",
                name = "Fresh Tomatoes",
                price = 40.0,
                freshness = "Fresh Today",
                origin = "Local Farm, Pune",
                category = "Vegetables",
                description = "Organic tomatoes harvested this morning from a nearby farm.",
                imageUrl = "android.resource://com.example.myapplication/drawable/product_tomatoes",
                vendorId = vendor.uid,
                vendorPhone = vendor.phoneNumber,
                vendorName = vendor.name,
                createdAt = java.util.Date(System.currentTimeMillis() - 3600000)
            ),
                Product(
                 id = "product-2",
                 name = "Seasonal Mangoes",
                 price = 120.0,
                 freshness = "Fresh Today",
                 origin = "Halli Santhe Hubli",
                 category = "Fruits",
                description = "Sweet seasonal mangoes handpicked and ready for delivery.",
                imageUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Mangoes_%28Magnifera_indica%29_from_India.jpg/1280px-Mangoes_%28Magnifera_indica%29_from_India.jpg",
                vendorId = vendor.uid,
                vendorPhone = vendor.phoneNumber,
                vendorName = vendor.name,
                createdAt = java.util.Date(System.currentTimeMillis() - 7200000)
            ),
            Product(
                id = "product-3",
                name = "Ragi Millet",
                price = 65.0,
                freshness = "3-5 Days Old",
                origin = "Mandya Farmers Market",
                category = "Grains",
                description = "Nutritious ragi cleaned and packed for daily meals.",
                imageUrl = "https://images.unsplash.com/photo-1574323347407-f5e1ad6d020b?auto=format&fit=crop&w=1200&q=80",
                vendorId = vendor.uid,
                vendorPhone = vendor.phoneNumber,
                vendorName = vendor.name,
                createdAt = java.util.Date(System.currentTimeMillis() - 10800000)
            ),
            Product(
                id = "product-4",
                name = "A2 Cow Milk",
                price = 70.0,
                freshness = "Fresh Today",
                origin = "Local Dairy, Bengaluru Rural",
                category = "Dairy",
                description = "Fresh cow milk supplied in the morning batch.",
                imageUrl = "https://images.unsplash.com/photo-1563636619-e9143da7973b?auto=format&fit=crop&w=1200&q=80",
                vendorId = vendor.uid,
                vendorPhone = vendor.phoneNumber,
                vendorName = vendor.name,
                createdAt = java.util.Date(System.currentTimeMillis() - 14400000)
            ),
            Product(
                id = "product-5",
                name = "Fresh Pomfret",
                price = 320.0,
                freshness = "Fresh Today",
                origin = "Mangaluru Coast",
                category = "Meat & Fish",
                description = "Cleaned fresh pomfret suitable for curry and fry.",
                imageUrl = "https://images.unsplash.com/photo-1615141982883-c7ad0e69fd62?auto=format&fit=crop&w=1200&q=80",
                vendorId = vendor.uid,
                vendorPhone = vendor.phoneNumber,
                vendorName = vendor.name,
                createdAt = java.util.Date(System.currentTimeMillis() - 18000000)
            ),
            Product(
                id = "product-6",
                name = "Iyengar Bakery Buns",
                price = 45.0,
                freshness = "Fresh Today",
                origin = "Local Bakery",
                category = "Bakery",
                description = "Soft sweet buns baked in small batches.",
                imageUrl = "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=1200&q=80",
                vendorId = vendor.uid,
                vendorPhone = vendor.phoneNumber,
                vendorName = vendor.name,
                createdAt = java.util.Date(System.currentTimeMillis() - 21600000)
            ),
            Product(
                id = "product-7",
                name = "Ilkal Silk Saree",
                price = 1850.0,
                freshness = "",
                origin = "Ilkal Weavers Cooperative",
                category = "Sarees",
                description = "Traditional handwoven saree with rich border work.",
                imageUrl = "android.resource://com.example.myapplication/drawable/product_ilkal_saree",
                vendorId = vendor.uid,
                vendorPhone = vendor.phoneNumber,
                vendorName = vendor.name,
                createdAt = java.util.Date(System.currentTimeMillis() - 25200000)
            ),
            Product(
                id = "product-8",
                name = "Glass Bangles Set",
                price = 180.0,
                freshness = "",
                origin = "Channapatna Bazaar",
                category = "Bangles",
                description = "Colorful traditional bangles for festive wear.",
                imageUrl = "https://images.unsplash.com/photo-1611652022419-a9419f74343d?auto=format&fit=crop&w=1200&q=80",
                vendorId = vendor.uid,
                vendorPhone = vendor.phoneNumber,
                vendorName = vendor.name,
                createdAt = java.util.Date(System.currentTimeMillis() - 28800000)
            ),
            Product(
                id = "product-9",
                name = "Byadgi Chilli Powder",
                price = 95.0,
                freshness = "3-5 Days Old",
                origin = "Byadgi, Karnataka",
                category = "Spices",
                description = "Aromatic chilli powder with deep color and mild heat.",
                imageUrl = "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?auto=format&fit=crop&w=1200&q=80",
                vendorId = vendor.uid,
                vendorPhone = vendor.phoneNumber,
                vendorName = vendor.name,
                createdAt = java.util.Date(System.currentTimeMillis() - 32400000),
                soldOut = true
            ),
            Product(
                id = "product-10",
                name = "Sandalwood Carving",
                price = 650.0,
                freshness = "",
                origin = "Mysuru Artisan Lane",
                category = "Handicrafts",
                description = "Hand-carved decorative piece made by local artisans.",
                imageUrl = "https://images.unsplash.com/photo-1620121692029-d088224ddc74?auto=format&fit=crop&w=1200&q=80",
                vendorId = vendor.uid,
                vendorPhone = vendor.phoneNumber,
                vendorName = vendor.name,
                createdAt = java.util.Date(System.currentTimeMillis() - 36000000)
            ),
            Product(
                id = "product-11",
                name = "Brass Pooja Diya",
                price = 220.0,
                freshness = "",
                origin = "Local Temple Street Market",
                category = "Other",
                description = "Polished brass diya for home pooja and gifting.",
                imageUrl = "android.resource://com.example.myapplication/drawable/product_brass_diya",
                vendorId = vendor.uid,
                vendorPhone = vendor.phoneNumber,
                vendorName = vendor.name,
                createdAt = java.util.Date(System.currentTimeMillis() - 39600000)
            )
        )
        products.addAll(demoProducts)

        reviews.addAll(
            listOf(
                Review(
                    id = "review-1",
                    customerId = customer.uid,
                    customerName = customer.name,
                    vendorId = vendor.uid,
                    productId = "product-1",
                    productName = "Fresh Tomatoes",
                    rating = 5.0,
                    comment = "Excellent quality and very fresh.",
                    timestamp = java.util.Date(System.currentTimeMillis() - 86400000)
                ),
                Review(
                    id = "review-2",
                    customerId = customer.uid,
                    customerName = customer.name,
                    vendorId = vendor.uid,
                    productId = "product-2",
                    productName = "Seasonal Mangoes",
                    rating = 4.2,
                    comment = "Good price and good quality.",
                    timestamp = java.util.Date(System.currentTimeMillis() - 172800000)
                )
            )
        )
    }

    private fun generateCode(): String = "%06d".format(Random.nextInt(0, 1_000_000))

    private fun nextId(prefix: String): String = "$prefix-${System.currentTimeMillis()}-${Random.nextInt(1000, 9999)}"

    private fun defaultNameFor(role: UserRole, phoneNumber: String): String {
        val suffix = phoneNumber.takeLast(4)
        return when (role) {
            UserRole.VENDOR -> "Vendor $suffix"
            UserRole.CUSTOMER -> "Customer $suffix"
        }
    }

    private fun normalizePhone(phoneNumber: String): String {
        val digits = phoneNumber.filter { it.isDigit() }
        return if (digits.length == 10) "+91$digits" else if (phoneNumber.startsWith("+")) phoneNumber else "+$digits"
    }
}

