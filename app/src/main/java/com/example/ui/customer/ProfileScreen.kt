package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AddressEntity
import com.example.data.model.ProductEntity
import com.example.ui.GroceryViewModel
import com.example.ui.common.LocalAppLanguage
import com.example.ui.common.ProductCard
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: GroceryViewModel,
    onNavigateToOrders: () -> Unit,
    onNavigateToAdminPanel: () -> Unit,
    onNavigateToProductDetails: (ProductEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val isHindi = lang.isHindi()

    val storeSettings by viewModel.storeSettings.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()

    var showAdminPinDialog by remember { mutableStateOf(false) }
    var adminPinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf(false) }

    var showWishlistSheet by remember { mutableStateOf(false) }
    var showAddressSheet by remember { mutableStateOf(false) }
    var newAddressName by remember { mutableStateOf("") }
    var newAddressPhone by remember { mutableStateOf("") }
    var newAddressText by remember { mutableStateOf("") }
    var newAddressLandmark by remember { mutableStateOf("") }
    var newAddressPincode by remember { mutableStateOf("") }

    val wishlistProducts = remember(wishlist, allProducts) {
        val ids = wishlist.map { it.productId }.toSet()
        allProducts.filter { ids.contains(it.id) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "मेरा खाता (My Account)" else "My Account",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Profile Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(GroceryGreenPrimary.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = GroceryGreenPrimary, modifier = Modifier.size(32.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Praveen Sharma",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "+91 9876543210 • Majri",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Quick Menu Actions
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        // My Orders
                        ProfileMenuRow(
                            icon = Icons.Default.Receipt,
                            title = if (isHindi) "मेरे ऑर्डर्स और रसीदें" else "My Orders & Receipts",
                            subtitle = if (isHindi) "ऑर्डर स्थिति और इनवॉइस डाउनलोड करें" else "Check tracking and digital bills",
                            onClick = onNavigateToOrders
                        )

                        Divider(modifier = Modifier.padding(horizontal = 16.dp))

                        // Wishlist
                        ProfileMenuRow(
                            icon = Icons.Default.Favorite,
                            title = if (isHindi) "पसंदीदा उत्पाद (${wishlistProducts.size})" else "Wishlist (${wishlistProducts.size})",
                            subtitle = if (isHindi) "सहेजे गए किराना सामान" else "Saved products for later",
                            onClick = { showWishlistSheet = true }
                        )

                        Divider(modifier = Modifier.padding(horizontal = 16.dp))

                        // Saved Delivery Addresses
                        ProfileMenuRow(
                            icon = Icons.Default.LocationOn,
                            title = if (isHindi) "डिलीवरी पते (${savedAddresses.size})" else "Saved Delivery Addresses (${savedAddresses.size})",
                            subtitle = if (isHindi) "घर, ऑफिस या दुकान का पता" else "Manage home & work addresses",
                            onClick = { showAddressSheet = true }
                        )

                        Divider(modifier = Modifier.padding(horizontal = 16.dp))

                        // Language Toggle
                        ProfileMenuRow(
                            icon = Icons.Default.Translate,
                            title = if (isHindi) "भाषा बदलें (Language)" else "App Language",
                            subtitle = if (isHindi) "वर्तमान भाषा: हिन्दी" else "Current Language: English",
                            trailingText = if (isHindi) "हिन्दी" else "English",
                            onClick = { lang.toggleLanguage() }
                        )
                    }
                }
            }

            // Owner / Admin Portal Callout
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GroceryGreenDark),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showAdminPinDialog = true }
                        .testTag("owner_admin_panel_entry")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(18.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f))
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GroceryGoldAccent)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isHindi) "मालिक / एडमिन पैनल" else "Owner / Admin Management Panel",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = if (isHindi) "सामान, स्टॉक, ऑर्डर्स और रेट बदलें (PIN: 1234)" else "Manage stock, products, orders & rates (PIN: 1234)",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.White)
                    }
                }
            }

            // Store Information & Support Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isHindi) "स्टोर संपर्क एवं सहायता" else "Store Help & Contact",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = GroceryGreenPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "+91 ${storeSettings?.phone ?: "9876543210"}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = GroceryGreenPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = storeSettings?.address ?: "Main Market, Majri", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = GroceryGreenPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Hours: ${storeSettings?.openingHours ?: "7:00 AM - 10:00 PM (Daily)"}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Admin PIN Verification Dialog
        if (showAdminPinDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAdminPinDialog = false
                    adminPinInput = ""
                    pinError = false
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = GroceryGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = if (isHindi) "एडमिन प्रमाणीकरण" else "Owner Verification")
                    }
                },
                text = {
                    Column {
                        Text(
                            text = if (isHindi) "स्टोर प्रबंधन में प्रवेश करने के लिए 4-अंकों का गुप्त पिन दर्ज करें (डिफ़ॉल्ट पिन: 1234):" else "Enter the 4-digit store owner security PIN (Default PIN: 1234):",
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = adminPinInput,
                            onValueChange = {
                                if (it.length <= 4) adminPinInput = it
                                pinError = false
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            label = { Text("PIN") },
                            isError = pinError,
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("admin_pin_input")
                        )
                        if (pinError) {
                            Text(
                                text = "Incorrect PIN! Please enter 1234.",
                                color = GroceryRedDiscount,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (viewModel.verifyAdminPin(adminPinInput)) {
                                showAdminPinDialog = false
                                adminPinInput = ""
                                onNavigateToAdminPanel()
                            } else {
                                pinError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
                    ) {
                        Text(if (isHindi) "प्रवेश करें" else "Login")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAdminPinDialog = false }) {
                        Text(if (isHindi) "रद्द करें" else "Cancel")
                    }
                }
            )
        }

        // Wishlist Bottom Sheet
        if (showWishlistSheet) {
            ModalBottomSheet(
                onDismissRequest = { showWishlistSheet = false },
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isHindi) "मेरी पसंदीदा सूची (${wishlistProducts.size})" else "My Wishlist (${wishlistProducts.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (wishlistProducts.isEmpty()) {
                        Text(
                            text = if (isHindi) "आपकी पसंदीदा सूची खाली है।" else "No items in your wishlist yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(wishlistProducts) { product ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.padding(12.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text(text = "₹${product.sellingPrice.toInt()}", color = GroceryGreenDark, fontWeight = FontWeight.Bold)
                                        }
                                        Row {
                                            Button(
                                                onClick = {
                                                    viewModel.addToCart(product)
                                                    showWishlistSheet = false
                                                },
                                                colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                                                shape = RoundedCornerShape(6.dp)
                                            ) {
                                                Text(if (isHindi) "कार्ट में जोड़ें" else "Add to Cart", fontSize = 11.sp)
                                            }
                                            IconButton(onClick = { viewModel.toggleWishlist(product.id) }) {
                                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Gray)
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

        // Saved Addresses Bottom Sheet
        if (showAddressSheet) {
            ModalBottomSheet(
                onDismissRequest = { showAddressSheet = false },
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isHindi) "सहेजे गए पते" else "Saved Delivery Addresses",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    savedAddresses.forEach { addr ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(12.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = addr.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        if (addr.isDefault) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(4.dp),
                                                color = GroceryGreenPrimary.copy(alpha = 0.15f)
                                            ) {
                                                Text("Default", color = GroceryGreenDark, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                    Text(text = "${addr.fullAddress}, ${addr.landmark} - ${addr.pincode}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "Phone: ${addr.phone}", fontSize = 11.sp, color = Color.Gray)
                                }
                                IconButton(onClick = { viewModel.deleteAddress(addr.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    trailingText: String? = null,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(icon, contentDescription = null, tint = GroceryGreenPrimary, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        if (trailingText != null) {
            Text(text = trailingText, fontWeight = FontWeight.Bold, color = GroceryGreenPrimary, fontSize = 12.sp)
        } else {
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
        }
    }
}
