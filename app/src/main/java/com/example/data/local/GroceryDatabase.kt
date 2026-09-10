package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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

@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        DealEntity::class,
        BannerEntity::class,
        CartItemEntity::class,
        WishlistItemEntity::class,
        OrderEntity::class,
        CouponEntity::class,
        NotificationEntity::class,
        StoreSettingsEntity::class,
        AddressEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class GroceryDatabase : RoomDatabase() {
    abstract fun groceryDao(): GroceryDao

    companion object {
        @Volatile
        private var INSTANCE: GroceryDatabase? = null

        fun getDatabase(context: Context): GroceryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GroceryDatabase::class.java,
                    "majri_grocery_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
