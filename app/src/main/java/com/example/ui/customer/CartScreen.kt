package com.example.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.CartItemEntity
import com.example.ui.GroceryViewModel
import com.example.ui.common.EmptyStateView
import com.example.ui.common.LocalAppLanguage
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount
import com.example.ui.theme.GroceryTextPrimary
import com.example.ui.theme.GroceryTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: GroceryViewModel,
    onNavigateToCheckout: () -> Unit,
    onNavigateToHome: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = LocalAppLanguage.current
    val isHindi = lang.isHindi()

    val cartItems by viewModel.cartItems.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val storeSettings by viewModel.storeSettings.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val appliedDiscount by viewModel.appliedCouponDiscount.collectAsState()

    val activeItems = cartItems.filter { !it.isSavedForLater }
    val savedItems = cartItems.filter { it.isSavedForLater }

    val subtotal = activeItems.sumOf { it.unitPrice * it.quantity }
    val mrpTotal = activeItems.sumOf { (if (it.mrp > 0) it.mrp else it.unitPrice) * it.quantity }
    val totalProductDiscount = (mrpTotal - subtotal).coerceAtLeast(0.0)

    val freeDeliveryThreshold = storeSettings?.freeDeliveryThreshold ?: 499.0
    val deliveryFee = if (subtotal >= freeDeliveryThreshold || subtotal == 0.0) 0.0 else (storeSettings?.deliveryCharge ?: 30.0)
    val finalTotal = (subtotal - appliedDiscount + deliveryFee).coerceAtLeast(0.0)

    var couponInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "शॉपिंग कार्ट (${activeItems.size})" else "Shopping Cart (${activeItems.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (activeItems.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    shadowElevation = 10.dp,
                    color = Color.White,
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = if (isHindi) "कुल देय राशि" else "Total Payable",
                                fontSize = 10.sp,
                                color = GroceryTextSecondary
                            )
                            Text(
                                text = "₹${finalTotal.toInt()}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = GroceryGreenPrimary
                            )
                            if (totalProductDiscount + appliedDiscount > 0) {
                                Text(
                                    text = if (isHindi) "आपने ₹${(totalProductDiscount + appliedDiscount).toInt()} बचाए!" else "You save ₹${(totalProductDiscount + appliedDiscount).toInt()}!",
                                    fontSize = 10.sp,
                                    color = GroceryGreenPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToCheckout,
                            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                            shape = RoundedCornerShape(50),
                            modifier = Modifier
                                .height(44.dp)
                                .testTag("proceed_to_checkout_btn")
                        ) {
                            Text(
                                text = if (isHindi) "चेकआउट करें" else "Proceed to Checkout",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (activeItems.isEmpty() && savedItems.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.ShoppingCart,
                title = if (isHindi) "आपकी कार्ट खाली है" else "Your Cart is Empty",
                description = if (isHindi) "अपनी दैनिक किराने की चीजें चुनें और ऑर्डर करें!" else "Add fresh vegetables, atta, pulses and groceries to start shopping!",
                actionText = if (isHindi) "किराना खरीदें" else "Shop Groceries",
                onActionClick = onNavigateToHome,
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Free Delivery Progress Banner
                if (activeItems.isNotEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingBag,
                                    contentDescription = null,
                                    tint = if (deliveryFee == 0.0) GroceryGreenPrimary else GroceryGoldAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (deliveryFee == 0.0) {
                                        if (isHindi) "बधाई हो! आपका ऑर्डर मुफ़्त डिलीवरी के लिए योग्य है।" else "Congratulations! Your order qualifies for Free Delivery!"
                                    } else {
                                        val remaining = (freeDeliveryThreshold - subtotal).toInt()
                                        if (isHindi) "मुफ़्त डिलीवरी के लिए ₹$remaining का सामान और जोड़ें!" else "Add items worth ₹$remaining more to get Free Delivery!"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GroceryTextPrimary
                                )
                            }
                        }
                    }
                }

                // Cart Items List
                items(activeItems, key = { it.id }) { cartItem ->
                    val product = allProducts.find { it.id == cartItem.productId }
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            // Image
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF8FAFC))
                            ) {
                                val imageRes = if (product != null) {
                                    val json = product.imagesJson
                                    if (json.contains("prod_wheat_atta")) context.resources.getIdentifier("prod_wheat_atta", "drawable", context.packageName)
                                    else if (json.contains("prod_basmati_rice")) context.resources.getIdentifier("prod_basmati_rice", "drawable", context.packageName)
                                    else 0
                                } else 0

                                if (imageRes != 0) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context).data(imageRes).build(),
                                        contentDescription = product?.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = GroceryGreenPrimary, modifier = Modifier.size(22.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Details
                            Column(modifier = Modifier.weight(1f)) {
                                val displayName = if (isHindi && product?.hindiName?.isNotBlank() == true) product.hindiName else (product?.name ?: "Grocery Product")
                                Text(
                                    text = displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 2,
                                    color = GroceryTextPrimary
                                )
                                Text(
                                    text = cartItem.variantName.ifBlank { cartItem.unit },
                                    fontSize = 10.sp,
                                    color = GroceryTextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "₹${cartItem.unitPrice.toInt()}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 13.sp,
                                        color = GroceryGreenPrimary
                                    )
                                    if (cartItem.mrp > cartItem.unitPrice) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "₹${cartItem.mrp.toInt()}",
                                            fontSize = 11.sp,
                                            textDecoration = TextDecoration.LineThrough,
                                            color = GroceryTextSecondary
                                        )
                                    }
                                }
                            }

                            // Stepper and Actions
                            Column(horizontalAlignment = Alignment.End) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50))
                                        .background(GroceryGreenPrimary)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1)
                                            }
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                    Text(
                                        text = "${cartItem.quantity}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp)
                                    )
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clickable {
                                                viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1)
                                            }
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", tint = Color.White, modifier = Modifier.size(14.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                Row {
                                    Text(
                                        text = if (isHindi) "बाद में खरीदें" else "Save for later",
                                        fontSize = 9.sp,
                                        color = GroceryGreenPrimary,
                                        modifier = Modifier
                                            .clickable { viewModel.toggleSaveForLater(cartItem.id) }
                                            .padding(2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Coupon Code Section
                if (activeItems.isNotEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocalOffer, contentDescription = null, tint = GroceryGoldAccent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "कूपन कोड लागू करें" else "Apply Coupon Code",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = GroceryTextPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))

                                if (appliedCoupon != null) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(GroceryGreenPrimary.copy(alpha = 0.1f))
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "${appliedCoupon?.code} Applied!",
                                                fontWeight = FontWeight.Bold,
                                                color = GroceryGreenPrimary,
                                                fontSize = 12.sp
                                            )
                                            Text(
                                                text = "Saved ₹${appliedDiscount.toInt()}",
                                                color = GroceryGreenPrimary,
                                                fontSize = 10.sp
                                            )
                                        }
                                        TextButton(onClick = { viewModel.removeCoupon() }) {
                                            Text(if (isHindi) "हटाएं" else "Remove", color = GroceryRedDiscount, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            value = couponInput,
                                            onValueChange = { couponInput = it.uppercase() },
                                            placeholder = { Text("e.g. MAJRI50", fontSize = 11.sp) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(8.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = GroceryGreenPrimary
                                            ),
                                            modifier = Modifier.weight(1f).height(44.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Button(
                                            onClick = {
                                                if (couponInput.isNotBlank()) {
                                                    viewModel.applyCoupon(couponInput, subtotal)
                                                }
                                            },
                                            shape = RoundedCornerShape(50),
                                            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                                            modifier = Modifier.height(44.dp)
                                        ) {
                                            Text(if (isHindi) "लागू करें" else "Apply", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Bill Details Summary Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = if (isHindi) "बिल विवरण (Bill Details)" else "Bill Details",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = GroceryTextPrimary
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                BillRow(label = if (isHindi) "कुल एमआरपी" else "Total MRP", value = "₹${mrpTotal.toInt()}")
                                if (totalProductDiscount > 0) {
                                    BillRow(
                                        label = if (isHindi) "उत्पाद छूट" else "Product Discount",
                                        value = "-₹${totalProductDiscount.toInt()}",
                                        valueColor = GroceryGreenPrimary
                                    )
                                }
                                BillRow(label = if (isHindi) "सामान का उप-कुल" else "Item Subtotal", value = "₹${subtotal.toInt()}")

                                if (appliedDiscount > 0) {
                                    BillRow(
                                        label = if (isHindi) "कूपन छूट (${appliedCoupon?.code})" else "Coupon Discount (${appliedCoupon?.code})",
                                        value = "-₹${appliedDiscount.toInt()}",
                                        valueColor = GroceryGreenPrimary
                                    )
                                }

                                BillRow(
                                    label = if (isHindi) "डिलीवरी शुल्क" else "Delivery Charge",
                                    value = if (deliveryFee == 0.0) (if (isHindi) "मुफ़्त" else "FREE") else "₹${deliveryFee.toInt()}",
                                    valueColor = if (deliveryFee == 0.0) GroceryGreenPrimary else GroceryTextPrimary
                                )

                                Divider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = if (isHindi) "अंतिम कुल राशि" else "Grand Total",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = GroceryTextPrimary
                                    )
                                    Text(
                                        text = "₹${finalTotal.toInt()}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp,
                                        color = GroceryGreenPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                // Saved For Later Items if any
                if (savedItems.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isHindi) "बाद के लिए सहेजे गए (${savedItems.size})" else "Saved For Later (${savedItems.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = GroceryTextSecondary
                        )
                    }

                    items(savedItems, key = { "saved_${it.id}" }) { savedItem ->
                        val product = allProducts.find { it.id == savedItem.productId }
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = product?.name ?: "Product",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 12.sp,
                                        color = GroceryTextPrimary
                                    )
                                    Text(text = "₹${savedItem.unitPrice.toInt()}", fontWeight = FontWeight.Bold, color = GroceryGreenPrimary, fontSize = 12.sp)
                                }
                                Row {
                                    TextButton(onClick = { viewModel.toggleSaveForLater(savedItem.id) }) {
                                        Text(if (isHindi) "कार्ट में भेजें" else "Move to Cart", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = GroceryGreenPrimary)
                                    }
                                    IconButton(onClick = { viewModel.removeCartItem(savedItem.id) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BillRow(label: String, value: String, valueColor: Color = Color.Unspecified) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = GroceryTextSecondary)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = if (valueColor != Color.Unspecified) valueColor else GroceryTextPrimary)
    }
}
