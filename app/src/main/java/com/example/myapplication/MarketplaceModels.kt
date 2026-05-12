package com.example.myapplication

import java.util.Date

enum class UserRole(val displayName: String) {
    VENDOR("Vendor/Seller"),
    CUSTOMER("Customer")
}

data class MarketplaceUser(
    val uid: String,
    val role: UserRole,
    val phoneNumber: String,
    val name: String,
    val businessName: String = "",
    val businessAddress: String = "",
    val profileComplete: Boolean = false,
    val active: Boolean = true
)

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val freshness: String,
    val origin: String,
    val category: String,
    val description: String,
    val imageUrl: String,
    val vendorId: String,
    val vendorPhone: String,
    val vendorName: String,
    val createdAt: Date,
    val active: Boolean = true,
    val averageRating: Double = 0.0,
    val reviewCount: Int = 0,
    val viewCount: Int = 0,
    val soldOut: Boolean = false
)

data class Review(
    val id: String,
    val customerId: String,
    val customerName: String,
    val vendorId: String,
    val productId: String,
    val productName: String,
    val rating: Double,
    val comment: String,
    val timestamp: Date,
    val verified: Boolean = true
)

data class OtpSession(
    val phoneNumber: String,
    val role: UserRole,
    val code: String,
    val createdAt: Date = Date()
)

