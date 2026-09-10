package com.example.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.CartItemEntity
import com.example.data.model.ProductEntity
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount
import com.example.ui.theme.GroceryTextMuted
import com.example.ui.theme.GroceryTextPrimary
import com.example.ui.theme.GroceryTextSecondary

@Composable
fun ProductCard(
    product: ProductEntity,
    cartItem: CartItemEntity?,
    isInWishlist: Boolean,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    onIncreaseQty: () -> Unit,
    onDecreaseQty: () -> Unit,
    onToggleWishlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val isHindi = lang.isHindi()
    val isOutOfStock = product.stockQuantity <= 0

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick() }
            .testTag("product_card_${product.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            // Image & Badges Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.05f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF8FAFC))
            ) {
                // Product Image
                val context = LocalContext.current
                val resourceId = rememberImageRes(context, product)

                if (resourceId != 0) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(resourceId)
                            .crossfade(true)
                            .build(),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = GroceryGreenPrimary.copy(alpha = 0.35f),
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }

                // Blinkit 10 MINS Delivery Badge
                Surface(
                    shape = RoundedCornerShape(topStart = 14.dp, bottomEnd = 8.dp),
                    color = Color.White.copy(alpha = 0.92f),
                    border = BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "⚡ 10 MINS",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF15803D)
                        )
                    }
                }

                // Discount / Deal Badge (Bottom-start of image)
                if (product.discountPercent > 0 || product.dealBadge.isNotEmpty()) {
                    val badgeText = if (product.dealBadge.isNotEmpty()) product.dealBadge else "${product.discountPercent}% OFF"
                    Surface(
                        shape = RoundedCornerShape(topEnd = 6.dp, bottomStart = 14.dp),
                        color = Color(0xFF2563EB),
                        modifier = Modifier.align(Alignment.BottomStart)
                    ) {
                        Text(
                            text = badgeText,
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Wishlist Heart Button
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape)
                        .testTag("wishlist_btn_${product.id}")
                ) {
                    Icon(
                        imageVector = if (isInWishlist) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isInWishlist) GroceryRedDiscount else Color(0xFF94A3B8),
                        modifier = Modifier.size(15.dp)
                    )
                }

                // Out of stock overlay
                if (isOutOfStock) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.45f))
                    ) {
                        Surface(
                            color = GroceryRedDiscount,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (isHindi) "स्टॉक खत्म" else "Out of Stock",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Product Name (English or Hindi)
            val displayName = if (isHindi && product.hindiName.isNotBlank()) product.hindiName else product.name
            Text(
                text = displayName,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = GroceryTextPrimary
            )

            // Unit/Quantity
            Text(
                text = product.unit,
                fontSize = 10.sp,
                color = GroceryTextSecondary,
                modifier = Modifier.padding(vertical = 1.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Pricing Row & Add to Cart
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Price Details
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Text(
                        text = "₹${product.sellingPrice.toInt()}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = GroceryTextPrimary
                    )
                    if (product.mrp > product.sellingPrice) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "₹${product.mrp.toInt()}",
                            fontSize = 9.sp,
                            textDecoration = TextDecoration.LineThrough,
                            color = GroceryTextMuted
                        )
                    }
                }

                // Add to Cart Button / Quantity Stepper
                if (isOutOfStock) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFFF1F5F9),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text(
                            text = if (isHindi) "स्टॉक नहीं" else "Out",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = GroceryTextMuted,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                } else if (cartItem != null && cartItem.quantity > 0) {
                    // Stepper: - QTY + in compact pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(GroceryGreenPrimary)
                            .padding(horizontal = 2.dp, vertical = 2.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onDecreaseQty() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                        Text(
                            text = "${cartItem.quantity}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { onIncreaseQty() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase",
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                } else {
                    // Signature Blinkit ADD Button (White container, green border & text)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF7FFF9),
                        border = BorderStroke(1.dp, GroceryGreenPrimary),
                        modifier = Modifier
                            .clickable { onAddToCart() }
                            .testTag("add_btn_${product.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "ADD",
                                color = GroceryGreenPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add",
                                tint = GroceryGreenPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    isSaleBadge: Boolean = false,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = GroceryTextPrimary
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = GroceryTextSecondary
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            if (isSaleBadge || actionText.equals("SALE", ignoreCase = true)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFEE2E2),
                    modifier = Modifier.clickable { onActionClick() }
                ) {
                    Text(
                        text = actionText,
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            } else {
                Text(
                    text = actionText,
                    fontWeight = FontWeight.SemiBold,
                    color = GroceryGreenPrimary,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable { onActionClick() }
                )
            }
        }
    }
}

@Composable
fun EmptyStateView(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GroceryGreenPrimary,
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(16.dp))
            androidx.compose.material3.Button(
                onClick = onActionClick,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(text = actionText, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Helper to resolve product image or drawable
fun rememberImageRes(context: android.content.Context, product: ProductEntity): Int {
    return try {
        val json = product.imagesJson
        if (json.contains("prod_wheat_atta")) {
            val resId = context.resources.getIdentifier("prod_wheat_atta", "drawable", context.packageName)
            if (resId != 0) resId else 0
        } else if (json.contains("prod_basmati_rice")) {
            val resId = context.resources.getIdentifier("prod_basmati_rice", "drawable", context.packageName)
            if (resId != 0) resId else 0
        } else {
            0
        }
    } catch (e: Exception) {
        0
    }
}
