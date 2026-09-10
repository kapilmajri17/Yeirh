package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AddressEntity
import com.example.data.model.BannerEntity
import com.example.data.model.CartItemEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.DealEntity
import com.example.data.model.NotificationEntity
import com.example.data.model.OrderEntity
import com.example.data.model.ProductEntity
import com.example.data.model.StoreSettingsEntity
import com.example.data.model.WishlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GroceryDao {

    // --- Products ---
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    fun getProductById(id: Long): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductByIdDirect(id: Long): ProductEntity?

    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND isActive = 1")
    fun getProductsByCategory(categoryId: Long): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)

    @Query("UPDATE products SET stockQuantity = :newStock WHERE id = :id")
    suspend fun updateProductStock(id: Long, newStock: Int)

    @Query("UPDATE products SET salesCount = salesCount + :count, stockQuantity = MAX(0, stockQuantity - :count) WHERE id = :id")
    suspend fun recordProductSale(id: Long, count: Int)

    // --- Categories ---
    @Query("SELECT * FROM categories ORDER BY displayOrder ASC, id ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isActive = 1 ORDER BY displayOrder ASC, id ASC")
    fun getActiveCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteCategoryById(id: Long)

    // --- Deals ---
    @Query("SELECT * FROM deals ORDER BY id DESC")
    fun getAllDeals(): Flow<List<DealEntity>>

    @Query("SELECT * FROM deals WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveDeals(): Flow<List<DealEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeal(deal: DealEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeals(deals: List<DealEntity>)

    @Update
    suspend fun updateDeal(deal: DealEntity)

    @Query("DELETE FROM deals WHERE id = :id")
    suspend fun deleteDealById(id: Long)

    // --- Banners ---
    @Query("SELECT * FROM banners ORDER BY displayOrder ASC, id ASC")
    fun getAllBanners(): Flow<List<BannerEntity>>

    @Query("SELECT * FROM banners WHERE isActive = 1 ORDER BY displayOrder ASC, id ASC")
    fun getActiveBanners(): Flow<List<BannerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: BannerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanners(banners: List<BannerEntity>)

    @Update
    suspend fun updateBanner(banner: BannerEntity)

    @Query("DELETE FROM banners WHERE id = :id")
    suspend fun deleteBannerById(id: Long)

    // --- Cart Items ---
    @Query("SELECT * FROM cart_items ORDER BY id DESC")
    fun getCartItems(): Flow<List<CartItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(item: CartItemEntity): Long

    @Update
    suspend fun updateCartItem(item: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun deleteCartItemById(id: Long)

    @Query("DELETE FROM cart_items WHERE isSavedForLater = 0")
    suspend fun clearActiveCart()

    @Query("SELECT * FROM cart_items WHERE productId = :productId AND variantId = :variantId LIMIT 1")
    suspend fun findCartItem(productId: Long, variantId: String): CartItemEntity?

    // --- Wishlist ---
    @Query("SELECT * FROM wishlist ORDER BY addedAt DESC")
    fun getWishlist(): Flow<List<WishlistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWishlist(item: WishlistItemEntity): Long

    @Query("DELETE FROM wishlist WHERE productId = :productId")
    suspend fun deleteWishlistByProductId(productId: Long)

    @Query("SELECT COUNT(*) FROM wishlist WHERE productId = :productId")
    fun isInWishlist(productId: Long): Flow<Int>

    // --- Orders ---
    @Query("SELECT * FROM orders ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    fun getOrderById(id: Long): Flow<OrderEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)

    @Query("UPDATE orders SET orderStatus = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String)

    @Query("UPDATE orders SET paymentStatus = :status WHERE id = :orderId")
    suspend fun updatePaymentStatus(orderId: Long, status: String)

    // --- Coupons ---
    @Query("SELECT * FROM coupons ORDER BY id DESC")
    fun getAllCoupons(): Flow<List<CouponEntity>>

    @Query("SELECT * FROM coupons WHERE isActive = 1 ORDER BY id DESC")
    fun getActiveCoupons(): Flow<List<CouponEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: CouponEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupons(coupons: List<CouponEntity>)

    @Update
    suspend fun updateCoupon(coupon: CouponEntity)

    @Query("DELETE FROM coupons WHERE id = :id")
    suspend fun deleteCouponById(id: Long)

    @Query("SELECT * FROM coupons WHERE UPPER(code) = UPPER(:code) AND isActive = 1 LIMIT 1")
    suspend fun getCouponByCode(code: String): CouponEntity?

    // --- Notifications ---
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotifications(notifications: List<NotificationEntity>)

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationRead(id: Long)

    // --- Store Settings ---
    @Query("SELECT * FROM store_settings WHERE id = 1 LIMIT 1")
    fun getStoreSettings(): Flow<StoreSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setStoreSettings(settings: StoreSettingsEntity)

    // --- Saved Addresses ---
    @Query("SELECT * FROM saved_addresses ORDER BY isDefault DESC, id DESC")
    fun getAllAddresses(): Flow<List<AddressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddresses(addresses: List<AddressEntity>)

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Query("DELETE FROM saved_addresses WHERE id = :id")
    suspend fun deleteAddressById(id: Long)

    @Query("UPDATE saved_addresses SET isDefault = 0")
    suspend fun clearDefaultAddress()

    @Query("UPDATE saved_addresses SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultAddress(id: Long)
}
