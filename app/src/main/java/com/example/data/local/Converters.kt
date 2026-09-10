package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.OrderItem
import com.example.data.model.ProductVariant
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi: Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val stringListType = Types.newParameterizedType(List::class.java, String::class.java)
    private val stringListAdapter = moshi.adapter<List<String>>(stringListType)

    private val variantListType = Types.newParameterizedType(List::class.java, ProductVariant::class.java)
    private val variantListAdapter = moshi.adapter<List<ProductVariant>>(variantListType)

    private val orderItemListType = Types.newParameterizedType(List::class.java, OrderItem::class.java)
    private val orderItemListAdapter = moshi.adapter<List<OrderItem>>(orderItemListType)

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        return if (list == null) "[]" else stringListAdapter.toJson(list)
    }

    @TypeConverter
    fun toStringList(json: String?): List<String> {
        if (json.isNullOrEmpty()) return emptyList()
        return try {
            stringListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromVariantList(list: List<ProductVariant>?): String {
        return if (list == null) "[]" else variantListAdapter.toJson(list)
    }

    @TypeConverter
    fun toVariantList(json: String?): List<ProductVariant> {
        if (json.isNullOrEmpty()) return emptyList()
        return try {
            variantListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromOrderItemList(list: List<OrderItem>?): String {
        return if (list == null) "[]" else orderItemListAdapter.toJson(list)
    }

    @TypeConverter
    fun toOrderItemList(json: String?): List<OrderItem> {
        if (json.isNullOrEmpty()) return emptyList()
        return try {
            orderItemListAdapter.fromJson(json) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}
