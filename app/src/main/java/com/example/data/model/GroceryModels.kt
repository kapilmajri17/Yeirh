package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductVariant(
    val id: String,
    val name: String, // e.g. "500 g", "1 kg", "5 kg"
    val mrp: Double,
    val sellingPrice: Double,
    val stock: Int = 50,
    val discountPercent: Int = if (mrp > 0) (((mrp - sellingPrice) / mrp) * 100).toInt() else 0
)

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val hindiName: String = "",
    val brand: String = "",
    val categoryId: Long = 1,
    val subcategory: String = "",
    val description: String = "",
    val imagesJson: String = "[]", // List of image URIs/resource identifiers
    val mrp: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val discountPercent: Int = 0,
    val discountAmount: Double = 0.0,
    val unit: String = "1 kg",
    val stockQuantity: Int = 100,
    val minStockAlert: Int = 5,
    val sku: String = "",
    val barcode: String = "",
    val isActive: Boolean = true,
    val isFeatured: Boolean = false,
    val isBestSeller: Boolean = false,
    val isNewArrival: Boolean = false,
    val dealBadge: String = "", // e.g. "15% OFF", "BOGO", "Special Deal"
    val gstPercent: Double = 0.0,
    val variantsJson: String = "[]", // List<ProductVariant>
    val salesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val hindiName: String = "",
    val iconName: String = "storefront",
    val imageUri: String = "",
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "deals")
data class DealEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val hindiTitle: String = "",
    val description: String = "",
    val bannerUri: String = "",
    val dealType: String = "PERCENTAGE", // FLAT, PERCENTAGE, BOGO, COMBO, FESTIVAL
    val discountPercent: Int = 0,
    val discountAmount: Double = 0.0,
    val minOrderValue: Double = 0.0,
    val maxDiscount: Double = 0.0,
    val targetCategoryId: Long? = null,
    val startDate: String = "",
    val endDate: String = "",
    val isActive: Boolean = true
)

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subtitle: String = "",
    val buttonText: String = "Shop Now",
    val imageUri: String = "",
    val linkType: String = "CATEGORY", // CATEGORY, DEAL, PRODUCT
    val linkTarget: String = "1",
    val displayOrder: Int = 0,
    val isActive: Boolean = true
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val variantId: String = "",
    val variantName: String = "",
    val quantity: Int = 1,
    val unitPrice: Double,
    val mrp: Double,
    val unit: String = "",
    val isSavedForLater: Boolean = false
)

@Entity(tableName = "wishlist")
data class WishlistItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val addedAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class OrderItem(
    val productId: Long,
    val productName: String,
    val brand: String,
    val variantName: String,
    val quantity: Int,
    val unitPrice: Double,
    val mrp: Double,
    val imageUri: String = ""
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val landmark: String = "",
    val pincode: String = "",
    val subtotal: Double,
    val discount: Double = 0.0,
    val deliveryCharge: Double = 0.0,
    val finalAmount: Double,
    val paymentMethod: String = "Cash on Delivery", // Cash on Delivery, UPI, Online Payment
    val paymentStatus: String = "Pending", // Pending, Completed, Failed
    val orderStatus: String = "Order Placed", // Order Placed, Confirmed, Packed, Out for Delivery, Delivered, Cancelled
    val orderDate: Long = System.currentTimeMillis(),
    val notes: String = "",
    val itemsJson: String = "[]" // List<OrderItem>
)

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val description: String = "",
    val discountType: String = "PERCENTAGE", // PERCENTAGE or FLAT
    val discountValue: Double = 10.0,
    val minOrderValue: Double = 300.0,
    val maxDiscount: Double = 100.0,
    val expiryDate: String = "31 Dec 2026",
    val usageLimit: Int = 100,
    val isActive: Boolean = true
)

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: String = "PROMOTION", // PROMOTION, ORDER, DEAL
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@Entity(tableName = "store_settings")
data class StoreSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val storeName: String = "Majri Grocery Store",
    val hindiStoreName: String = "माजरी किराना स्टोर",
    val phone: String = "+91 98765 43210",
    val address: String = "Main Market, Majri, Sector 1, Pin 134109",
    val deliveryCharge: Double = 30.0,
    val freeDeliveryThreshold: Double = 499.0,
    val minOrderValue: Double = 99.0,
    val openingHours: String = "7:00 AM - 10:00 PM",
    val deliveryRadius: String = "5 km",
    val gstNumber: String = "06ABCDE1234F1Z5",
    val upiId: String = "majrigrocery@upi",
    val allowCod: Boolean = true,
    val allowUpi: Boolean = true,
    val allowOnline: Boolean = true
)

@Entity(tableName = "saved_addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val label: String = "Home",
    val recipientName: String,
    val phone: String,
    val streetAddress: String,
    val landmark: String = "",
    val pincode: String = "134109",
    val isDefault: Boolean = false
) {
    val name: String get() = recipientName
    val fullAddress: String get() = streetAddress
}
