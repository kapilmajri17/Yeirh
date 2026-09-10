package com.example.ui.customer

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.ProductEntity
import com.example.data.model.ProductVariant
import com.example.ui.GroceryViewModel
import com.example.ui.common.LocalAppLanguage
import com.example.ui.common.rememberImageRes
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailsScreen(
    product: ProductEntity,
    viewModel: GroceryViewModel,
    onBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = LocalAppLanguage.current
    val isHindi = lang.isHindi()

    val wishlist by viewModel.wishlist.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    val isInWishlist = wishlist.any { it.productId == product.id }
    val variants = remember(product.variantsJson) { viewModel.parseVariants(product.variantsJson) }

    var selectedVariant by remember { mutableStateOf<ProductVariant?>(variants.firstOrNull()) }

    // Pricing depends on chosen variant or base product
    val currentSellingPrice = selectedVariant?.sellingPrice ?: product.sellingPrice
    val currentMrp = selectedVariant?.mrp ?: product.mrp
    val currentStock = selectedVariant?.stock ?: product.stockQuantity
    val discountPercent = if (currentMrp > currentSellingPrice) {
        (((currentMrp - currentSellingPrice) / currentMrp) * 100).toInt()
    } else product.discountPercent

    val cartItem = cartItems.find {
        it.productId == product.id && (selectedVariant == null || it.variantId == selectedVariant?.id) && !it.isSavedForLater
    }

    val isOutOfStock = currentStock <= 0

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi && product.hindiName.isNotBlank()) product.hindiName else product.name,
                        maxLines = 1,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleWishlist(product.id) }) {
                        Icon(
                            imageVector = if (isInWishlist) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Wishlist",
                            tint = if (isInWishlist) GroceryRedDiscount else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onNavigateToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = GroceryGreenPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (isOutOfStock) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.LightGray.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = if (isHindi) "वर्तमान में स्टॉक में नहीं है" else "Out of Stock",
                                    color = Color.DarkGray,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else if (cartItem != null && cartItem.quantity > 0) {
                        // Cart Stepper
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(GroceryGreenPrimary)
                                .padding(horizontal = 16.dp)
                        ) {
                            IconButton(onClick = { viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1) }) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White)
                            }
                            Text(
                                text = "${cartItem.quantity} in Cart",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            IconButton(onClick = { viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1) }) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White)
                            }
                        }
                        Button(
                            onClick = onNavigateToCart,
                            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenDark),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(text = if (isHindi) "कार्ट देखें" else "View Cart", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Add to Cart and Buy Now
                        OutlinedButton(
                            onClick = {
                                viewModel.addToCart(product, selectedVariant)
                            },
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, GroceryGreenPrimary),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("details_add_to_cart_btn")
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = GroceryGreenPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "कार्ट में जोड़ें" else "Add to Cart",
                                color = GroceryGreenPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.addToCart(product, selectedVariant)
                                onNavigateToCart()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("details_buy_now_btn")
                        ) {
                            Text(
                                text = if (isHindi) "अभी खरीदें" else "Buy Now",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Large Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.2f)
                    .background(Color.White)
            ) {
                val imageRes = rememberImageRes(context, product)
                if (imageRes != 0) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(imageRes).build(),
                        contentDescription = product.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = GroceryGreenPrimary.copy(alpha = 0.4f),
                            modifier = Modifier.size(90.dp)
                        )
                    }
                }

                // Discount Badge on top left
                if (discountPercent > 0) {
                    Surface(
                        shape = RoundedCornerShape(topStart = 0.dp, bottomEnd = 12.dp),
                        color = GroceryRedDiscount,
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "$discountPercent% OFF",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Product Details Content Card
            Card(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Brand
                    if (product.brand.isNotBlank()) {
                        Text(
                            text = product.brand.uppercase(),
                            color = GroceryGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Product Name (English & Hindi)
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (product.hindiName.isNotBlank()) {
                        Text(
                            text = product.hindiName,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Price and Discount row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "₹${currentSellingPrice.toInt()}",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black,
                            color = GroceryGreenDark
                        )
                        if (currentMrp > currentSellingPrice) {
                            Text(
                                text = "MRP ₹${currentMrp.toInt()}",
                                fontSize = 16.sp,
                                textDecoration = TextDecoration.LineThrough,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GroceryRedDiscount.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "Save ₹${(currentMrp - currentSellingPrice).toInt()}",
                                    color = GroceryRedDiscount,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "(Inclusive of all taxes)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Variants selector (e.g. 500g, 1kg, 2kg, 5kg)
                    if (variants.isNotEmpty()) {
                        Text(
                            text = if (isHindi) "मात्रा / साइज़ चुनें:" else "Select Quantity / Pack Size:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            variants.forEach { variant ->
                                val isSelected = selectedVariant?.id == variant.id
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) GroceryGreenPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.5.dp,
                                        if (isSelected) GroceryGreenPrimary else Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .clickable { selectedVariant = variant }
                                        .weight(1f)
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
                                    ) {
                                        Text(
                                            text = variant.name,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = if (isSelected) GroceryGreenDark else MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "₹${variant.sellingPrice.toInt()}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = if (isSelected) GroceryGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // Stock status
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (!isOutOfStock) Icons.Default.CheckCircle else Icons.Default.Remove,
                            contentDescription = null,
                            tint = if (!isOutOfStock) GroceryGreenPrimary else GroceryRedDiscount,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (!isOutOfStock) {
                                if (isHindi) "स्टॉक में उपलब्ध ($currentStock यूनिट्स)" else "In Stock ($currentStock packs available)"
                            } else {
                                if (isHindi) "स्टॉक खत्म" else "Currently Out of Stock"
                            },
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = if (!isOutOfStock) GroceryGreenPrimary else GroceryRedDiscount
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Offers & Deals Box
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = GroceryGoldAccent.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                tint = GroceryGoldAccent,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "दुकान का स्पेशल ऑफर" else "Store Offer Available",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF78350F)
                                )
                                Text(
                                    text = if (isHindi) "कूपन 'MAJRI50' का उपयोग करके ₹50 की अतिरिक्त छूट प्राप्त करें।" else "Use coupon code MAJRI50 for flat ₹50 OFF on orders above ₹600.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Description
                    Text(
                        text = if (isHindi) "उत्पाद विवरण" else "Product Description",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = product.description.ifBlank { "Guaranteed fresh and genuine grocery product sourced directly from verified authorized local suppliers for Majri Grocery Store customers." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Product Specifications table
                    Text(
                        text = if (isHindi) "अतिरिक्त जानकारी" else "Product Information",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        InfoRow(label = if (isHindi) "ब्रांड" else "Brand", value = product.brand.ifBlank { "Majri Grocery Store" })
                        InfoRow(label = if (isHindi) "पैकेज साइज" else "Unit Size", value = product.unit)
                        if (product.sku.isNotBlank()) {
                            InfoRow(label = "SKU", value = product.sku)
                        }
                        if (product.barcode.isNotBlank()) {
                            InfoRow(label = if (isHindi) "बारकोड" else "Barcode", value = product.barcode)
                        }
                        InfoRow(label = if (isHindi) "विक्रेता" else "Seller", value = "Majri Grocery Store, Majri")
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}
