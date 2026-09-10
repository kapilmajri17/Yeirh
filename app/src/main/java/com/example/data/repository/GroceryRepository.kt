package com.example.data.repository

import com.example.data.local.GroceryDao
import com.example.data.local.SampleDataProvider
import com.example.data.model.AddressEntity
import com.example.data.model.BannerEntity
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.DealEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItem
import com.example.data.model.ProductEntity
import com.example.data.model.ProductVariant
import com.example.data.model.StoreSettingsEntity
import com.example.data.model.WishlistItemEntity
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GroceryRepository(private val dao: GroceryDao) {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val orderItemListType = Types.newParameterizedType(List::class.java, OrderItem::class.java)
    private val orderItemAdapter = moshi.adapter<List<OrderItem>>(orderItemListType)
    private val variantListType = Types.newParameterizedType(List::class.java, ProductVariant::class.java)
    private val variantAdapter = moshi.adapter<List<ProductVariant>>(variantListType)

    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val activeProducts: Flow<List<ProductEntity>> = dao.getActiveProducts()
    val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()
    val activeCategories: Flow<List<CategoryEntity>> = dao.getActiveCategories()
    val allDeals: Flow<List<DealEntity>> = dao.getAllDeals()
    val activeDeals: Flow<List<DealEntity>> = dao.getActiveDeals()
    val allBanners: Flow<List<BannerEntity>> = dao.getAllBanners()
    val activeBanners: Flow<List<BannerEntity>> = dao.getActiveBanners()
    val cartItems: Flow<List<CartItemEntity>> = dao.getCartItems()
    val wishlist: Flow<List<WishlistItemEntity>> = dao.getWishlist()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val allCoupons: Flow<List<CouponEntity>> = dao.getAllCoupons()
    val activeCoupons: Flow<List<CouponEntity>> = dao.getActiveCoupons()
    val notifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()
    val storeSettings: Flow<StoreSettingsEntity?> = dao.getStoreSettings()
    val savedAddresses: Flow<List<AddressEntity>> = dao.getAllAddresses()

    init {
        // Pre-populate sample data asynchronously if database is empty
        CoroutineScope(Dispatchers.IO).launch {
            checkAndSeedInitialData()
        }
    }

    private suspend fun checkAndSeedInitialData() {
        val currentCategories = dao.getAllCategories().first()
        val currentProducts = dao.getAllProducts().first()
        if (currentCategories.isEmpty()) {
            dao.insertCategories(SampleDataProvider.getInitialCategories())
            dao.insertProducts(SampleDataProvider.getInitialProducts())
            dao.insertBanners(SampleDataProvider.getInitialBanners())
            dao.insertDeals(SampleDataProvider.getInitialDeals())
            dao.insertCoupons(SampleDataProvider.getInitialCoupons())
            dao.insertNotifications(SampleDataProvider.getInitialNotifications())
            dao.insertAddresses(SampleDataProvider.getInitialAddresses())
            SampleDataProvider.getInitialOrders().forEach { dao.insertOrder(it) }
            dao.setStoreSettings(SampleDataProvider.initialStoreSettings)
        } else if (currentProducts.size < 30) {
            dao.insertProducts(SampleDataProvider.getInitialProducts())
        }
    }

    fun getProductById(id: Long): Flow<ProductEntity?> = dao.getProductById(id)

    fun getProductsByCategory(categoryId: Long): Flow<List<ProductEntity>> =
        dao.getProductsByCategory(categoryId)

    fun isInWishlist(productId: Long): Flow<Int> = dao.isInWishlist(productId)

    // --- Product Admin Operations ---
    suspend fun saveProduct(product: ProductEntity): Long = withContext(Dispatchers.IO) {
        if (product.id == 0L) {
            dao.insertProduct(product)
        } else {
            dao.updateProduct(product)
            product.id
        }
    }

    suspend fun deleteProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        dao.deleteProduct(product)
    }

    suspend fun duplicateProduct(productId: Long) = withContext(Dispatchers.IO) {
        val original = dao.getProductByIdDirect(productId) ?: return@withContext
        val duplicate = original.copy(
            id = 0,
            name = "${original.name} (Copy)",
            hindiName = if (original.hindiName.isNotEmpty()) "${original.hindiName} (प्रति)" else "",
            createdAt = System.currentTimeMillis()
        )
        dao.insertProduct(duplicate)
    }

    suspend fun toggleProductActive(productId: Long) = withContext(Dispatchers.IO) {
        val p = dao.getProductByIdDirect(productId) ?: return@withContext
        dao.updateProduct(p.copy(isActive = !p.isActive))
    }

    suspend fun updateStock(productId: Long, newStock: Int) = withContext(Dispatchers.IO) {
        dao.updateProductStock(productId, newStock.coerceAtLeast(0))
    }

    // --- Category Admin Operations ---
    suspend fun saveCategory(category: CategoryEntity): Long = withContext(Dispatchers.IO) {
        if (category.id == 0L) dao.insertCategory(category) else { dao.updateCategory(category); category.id }
    }

    suspend fun deleteCategory(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteCategoryById(id)
    }

    // --- Deal Admin Operations ---
    suspend fun saveDeal(deal: DealEntity): Long = withContext(Dispatchers.IO) {
        if (deal.id == 0L) dao.insertDeal(deal) else { dao.updateDeal(deal); deal.id }
    }

    suspend fun deleteDeal(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteDealById(id)
    }

    // --- Banner Admin Operations ---
    suspend fun saveBanner(banner: BannerEntity): Long = withContext(Dispatchers.IO) {
        if (banner.id == 0L) dao.insertBanner(banner) else { dao.updateBanner(banner); banner.id }
    }

    suspend fun deleteBanner(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteBannerById(id)
    }

    // --- Cart Operations ---
    suspend fun addToCart(
        product: ProductEntity,
        variant: ProductVariant? = null,
        qty: Int = 1
    ) = withContext(Dispatchers.IO) {
        val variantId = variant?.id ?: ""
        val variantName = variant?.name ?: product.unit
        val price = variant?.sellingPrice ?: product.sellingPrice
        val mrp = variant?.mrp ?: product.mrp
        val unit = variant?.name ?: product.unit

        val existing = dao.findCartItem(product.id, variantId)
        if (existing != null) {
            dao.updateCartItem(existing.copy(quantity = existing.quantity + qty))
        } else {
            val item = CartItemEntity(
                productId = product.id,
                variantId = variantId,
                variantName = variantName,
                quantity = qty,
                unitPrice = price,
                mrp = mrp,
                unit = unit
            )
            dao.insertCartItem(item)
        }
    }

    suspend fun updateCartQuantity(cartItemId: Long, newQuantity: Int) = withContext(Dispatchers.IO) {
        if (newQuantity <= 0) {
            dao.deleteCartItemById(cartItemId)
        } else {
            val currentItems = dao.getCartItems().first()
            val item = currentItems.find { it.id == cartItemId }
            if (item != null) {
                dao.updateCartItem(item.copy(quantity = newQuantity))
            }
        }
    }

    suspend fun removeCartItem(cartItemId: Long) = withContext(Dispatchers.IO) {
        dao.deleteCartItemById(cartItemId)
    }

    suspend fun toggleSaveForLater(cartItemId: Long) = withContext(Dispatchers.IO) {
        val currentItems = dao.getCartItems().first()
        val item = currentItems.find { it.id == cartItemId }
        if (item != null) {
            dao.updateCartItem(item.copy(isSavedForLater = !item.isSavedForLater))
        }
    }

    // --- Wishlist Operations ---
    suspend fun toggleWishlist(productId: Long) = withContext(Dispatchers.IO) {
        val inWishlist = dao.isInWishlist(productId).first() > 0
        if (inWishlist) {
            dao.deleteWishlistByProductId(productId)
        } else {
            dao.insertWishlist(WishlistItemEntity(productId = productId))
        }
    }

    // --- Order Operations ---
    suspend fun placeOrder(
        customerName: String,
        customerPhone: String,
        address: String,
        landmark: String,
        pincode: String,
        subtotal: Double,
        discount: Double,
        deliveryCharge: Double,
        finalAmount: Double,
        paymentMethod: String,
        orderItems: List<OrderItem>,
        notes: String = ""
    ): Long = withContext(Dispatchers.IO) {
        val orderNum = "MGS-${System.currentTimeMillis().toString().takeLast(6)}"
        val order = OrderEntity(
            orderNumber = orderNum,
            customerName = customerName,
            customerPhone = customerPhone,
            deliveryAddress = address,
            landmark = landmark,
            pincode = pincode,
            subtotal = subtotal,
            discount = discount,
            deliveryCharge = deliveryCharge,
            finalAmount = finalAmount,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == "Cash on Delivery") "Pending" else "Completed",
            orderStatus = "Order Placed",
            orderDate = System.currentTimeMillis(),
            notes = notes,
            itemsJson = orderItemAdapter.toJson(orderItems)
        )
        val orderId = dao.insertOrder(order)

        // Automatically reduce stock for each product in the order
        for (item in orderItems) {
            dao.recordProductSale(item.productId, item.quantity)
        }

        // Clear active cart items
        dao.clearActiveCart()

        // Create notification
        dao.insertNotification(
            NotificationEntity(
                title = "Order Placed Successfully!",
                message = "Your order #$orderNum of ₹${String.format("%.0f", finalAmount)} has been placed. We're packing your fresh groceries!",
                type = "ORDER"
            )
        )

        orderId
    }

    suspend fun updateOrderStatus(orderId: Long, status: String) = withContext(Dispatchers.IO) {
        dao.updateOrderStatus(orderId, status)
        dao.insertNotification(
            NotificationEntity(
                title = "Order Update: $status",
                message = "Your order status is now '$status'. Thank you for shopping with Majri Grocery Store!",
                type = "ORDER"
            )
        )
    }

    suspend fun updateOrderPaymentStatus(orderId: Long, status: String) = withContext(Dispatchers.IO) {
        dao.updatePaymentStatus(orderId, status)
    }

    // --- Coupon Operations ---
    suspend fun validateCoupon(code: String, orderAmount: Double): Result<Double> = withContext(Dispatchers.IO) {
        val coupon = dao.getCouponByCode(code.trim()) ?: return@withContext Result.failure(Exception("Invalid coupon code"))
        if (orderAmount < coupon.minOrderValue) {
            return@withContext Result.failure(Exception("Minimum order value for ${coupon.code} is ₹${coupon.minOrderValue.toInt()}"))
        }
        val discount = if (coupon.discountType == "PERCENTAGE") {
            (orderAmount * (coupon.discountValue / 100.0)).coerceAtMost(coupon.maxDiscount)
        } else {
            coupon.discountValue.coerceAtMost(orderAmount)
        }
        Result.success(discount)
    }

    suspend fun saveCoupon(coupon: CouponEntity) = withContext(Dispatchers.IO) {
        if (coupon.id == 0L) dao.insertCoupon(coupon) else dao.updateCoupon(coupon)
    }

    suspend fun deleteCoupon(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteCouponById(id)
    }

    // --- Store Settings Operations ---
    suspend fun updateStoreSettings(settings: StoreSettingsEntity) = withContext(Dispatchers.IO) {
        dao.setStoreSettings(settings)
    }

    // --- Notifications ---
    suspend fun sendNotification(title: String, message: String, type: String = "PROMOTION") = withContext(Dispatchers.IO) {
        dao.insertNotification(
            NotificationEntity(
                title = title,
                message = message,
                type = type
            )
        )
    }

    suspend fun markNotificationRead(id: Long) = withContext(Dispatchers.IO) {
        dao.markNotificationRead(id)
    }

    // --- Saved Addresses ---
    suspend fun saveAddress(address: AddressEntity) = withContext(Dispatchers.IO) {
        if (address.isDefault) {
            dao.clearDefaultAddress()
        }
        if (address.id == 0L) dao.insertAddress(address) else dao.updateAddress(address)
    }

    suspend fun setDefaultAddress(id: Long) = withContext(Dispatchers.IO) {
        dao.clearDefaultAddress()
        dao.setDefaultAddress(id)
    }

    suspend fun deleteAddress(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteAddressById(id)
    }
}
