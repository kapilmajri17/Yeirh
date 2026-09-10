package com.example.ui.admin

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BannerEntity
import com.example.data.model.CategoryEntity
import com.example.data.model.CouponEntity
import com.example.data.model.DealEntity
import com.example.data.model.StoreSettingsEntity
import com.example.ui.GroceryViewModel
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMoreScreen(
    viewModel: GroceryViewModel,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories by viewModel.allCategories.collectAsState()
    val deals by viewModel.allDeals.collectAsState()
    val coupons by viewModel.allCoupons.collectAsState()
    val banners by viewModel.allBanners.collectAsState()
    val storeSettings by viewModel.storeSettings.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()

    var activeSheet by remember { mutableStateOf<String?>(null) }

    // Dialog state for Category Add
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var catName by remember { mutableStateOf("") }
    var catHindiName by remember { mutableStateOf("") }

    // Dialog state for Coupon Add
    var showAddCouponDialog by remember { mutableStateOf(false) }
    var couponCode by remember { mutableStateOf("") }
    var couponDiscount by remember { mutableStateOf("50") }
    var couponMinOrder by remember { mutableStateOf("500") }
    var couponDesc by remember { mutableStateOf("") }

    // Dialog state for Broadcast Announcement
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Management Modules Header
        item {
            Text(
                text = "Store Administration Hub",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Configure store operations, marketing, and rates",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Section 1: Core Configurations
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    AdminNavigationRow(
                        icon = Icons.Default.Category,
                        title = "Categories Management (${categories.size})",
                        subtitle = "Add, edit, or reorder grocery categories",
                        onClick = { activeSheet = "CATEGORIES" }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))

                    AdminNavigationRow(
                        icon = Icons.Default.LocalOffer,
                        title = "Coupons & Promo Codes (${coupons.size})",
                        subtitle = "Manage discount vouchers and promo codes",
                        onClick = { activeSheet = "COUPONS" }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))

                    AdminNavigationRow(
                        icon = Icons.Default.LocalOffer,
                        title = "Deals & Promotions (${deals.size})",
                        subtitle = "Create flash sales, combo packs, and festive deals",
                        onClick = { activeSheet = "DEALS" }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))

                    AdminNavigationRow(
                        icon = Icons.Default.Image,
                        title = "Banners & Carousel (${banners.size})",
                        subtitle = "Manage home screen marketing slides",
                        onClick = { activeSheet = "BANNERS" }
                    )
                }
            }
        }

        // Section 2: Store Operations & Broadcast
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    AdminNavigationRow(
                        icon = Icons.Default.Settings,
                        title = "Store Settings & Delivery Rates",
                        subtitle = "Store name, delivery fee, threshold, address & UPI",
                        onClick = { activeSheet = "SETTINGS" }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))

                    AdminNavigationRow(
                        icon = Icons.Default.People,
                        title = "Customers (${allOrders.map { it.customerPhone }.distinct().size})",
                        subtitle = "View registered customers and purchase histories",
                        onClick = { activeSheet = "CUSTOMERS" }
                    )
                    Divider(modifier = Modifier.padding(horizontal = 16.dp))

                    AdminNavigationRow(
                        icon = Icons.Default.Campaign,
                        title = "Broadcast Notification",
                        subtitle = "Send push announcements directly to all customer apps",
                        onClick = { showBroadcastDialog = true }
                    )
                }
            }
        }

        // Exit Admin Panel Button
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF374151)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Exit Admin Panel & Return to Store", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Modal Sheet: Categories
    if (activeSheet == "CATEGORIES") {
        ModalBottomSheet(
            onDismissRequest = { activeSheet = null },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Categories (${categories.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Button(
                        onClick = { showAddCategoryDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categories) { cat ->
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
                                Column {
                                    Text(text = cat.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    if (cat.hindiName.isNotBlank()) Text(text = cat.hindiName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { viewModel.deleteCategory(cat.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GroceryRedDiscount)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet: Store Settings
    if (activeSheet == "SETTINGS" && storeSettings != null) {
        var sName by remember { mutableStateOf(storeSettings?.storeName ?: "") }
        var sHindiName by remember { mutableStateOf(storeSettings?.hindiStoreName ?: "") }
        var sAddress by remember { mutableStateOf(storeSettings?.address ?: "") }
        var sPhone by remember { mutableStateOf(storeSettings?.phone ?: "") }
        var sHours by remember { mutableStateOf(storeSettings?.openingHours ?: "") }
        var sDeliveryFee by remember { mutableStateOf(storeSettings?.deliveryCharge?.toInt()?.toString() ?: "30") }
        var sFreeThreshold by remember { mutableStateOf(storeSettings?.freeDeliveryThreshold?.toInt()?.toString() ?: "499") }
        var sGst by remember { mutableStateOf(storeSettings?.gstNumber ?: "") }
        var sUpi by remember { mutableStateOf(storeSettings?.upiId ?: "") }

        ModalBottomSheet(
            onDismissRequest = { activeSheet = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Store Settings & Rates", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(value = sName, onValueChange = { sName = it }, label = { Text("Store Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = sHindiName, onValueChange = { sHindiName = it }, label = { Text("Hindi Store Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = sAddress, onValueChange = { sAddress = it }, label = { Text("Store Address") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = sPhone, onValueChange = { sPhone = it }, label = { Text("Contact Phone") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = sHours, onValueChange = { sHours = it }, label = { Text("Store Opening Hours") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(value = sDeliveryFee, onValueChange = { sDeliveryFee = it }, label = { Text("Delivery Fee (₹)") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(value = sFreeThreshold, onValueChange = { sFreeThreshold = it }, label = { Text("Free Delivery Min (₹)") }, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = sUpi, onValueChange = { sUpi = it }, label = { Text("UPI ID for Payments") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = sGst, onValueChange = { sGst = it }, label = { Text("GST Number") }, modifier = Modifier.fillMaxWidth())

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val updated = storeSettings?.copy(
                            storeName = sName,
                            hindiStoreName = sHindiName,
                            address = sAddress,
                            phone = sPhone,
                            openingHours = sHours,
                            deliveryCharge = sDeliveryFee.toDoubleOrNull() ?: 30.0,
                            freeDeliveryThreshold = sFreeThreshold.toDoubleOrNull() ?: 499.0,
                            upiId = sUpi,
                            gstNumber = sGst
                        )
                        if (updated != null) {
                            viewModel.updateStoreSettings(updated)
                            activeSheet = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save Settings", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Modal Sheet: Coupons
    if (activeSheet == "COUPONS") {
        ModalBottomSheet(
            onDismissRequest = { activeSheet = null },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Store Coupons (${coupons.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Button(
                        onClick = { showAddCouponDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add")
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(coupons) { c ->
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
                                Column {
                                    Text(text = c.code, fontWeight = FontWeight.Black, fontSize = 14.sp, color = GroceryGreenDark)
                                    Text(text = "Discount: ₹${c.discountValue.toInt()} • Min Order: ₹${c.minOrderValue.toInt()}", fontSize = 11.sp)
                                }
                                IconButton(onClick = { viewModel.deleteCoupon(c.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GroceryRedDiscount)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet: Customers List
    if (activeSheet == "CUSTOMERS") {
        val customerMap = remember(allOrders) {
            allOrders.groupBy { it.customerPhone }
        }

        ModalBottomSheet(
            onDismissRequest = { activeSheet = null },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Registered Customers (${customerMap.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(customerMap.entries.toList()) { entry ->
                        val orders = entry.value
                        val name = orders.firstOrNull()?.customerName ?: "Customer"
                        val phone = entry.key
                        val totalSpent = orders.sumOf { it.finalAmount }

                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Total Spent: ₹${totalSpent.toInt()}", fontWeight = FontWeight.Bold, color = GroceryGreenDark)
                                }
                                Text(text = "Phone: $phone • Orders: ${orders.size}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet: Deals
    if (activeSheet == "DEALS") {
        ModalBottomSheet(
            onDismissRequest = { activeSheet = null },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Deals & Promotions (${deals.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(deals) { deal ->
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
                                    Text(text = deal.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = deal.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { viewModel.deleteDeal(deal.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GroceryRedDiscount)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet: Banners
    if (activeSheet == "BANNERS") {
        ModalBottomSheet(
            onDismissRequest = { activeSheet = null },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                Text("Marketing Banners (${banners.size})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(banners) { b ->
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
                                    Text(text = b.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = b.subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = { viewModel.deleteBanner(b.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GroceryRedDiscount)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Category Dialog
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showAddCategoryDialog = false },
            title = { Text("Add Category") },
            text = {
                Column {
                    OutlinedTextField(value = catName, onValueChange = { catName = it }, label = { Text("Category Name (English)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = catHindiName, onValueChange = { catHindiName = it }, label = { Text("Hindi Category Name") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (catName.isNotBlank()) {
                            viewModel.saveCategory(CategoryEntity(name = catName, hindiName = catHindiName))
                            showAddCategoryDialog = false
                            catName = ""
                            catHindiName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCategoryDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Add Coupon Dialog
    if (showAddCouponDialog) {
        AlertDialog(
            onDismissRequest = { showAddCouponDialog = false },
            title = { Text("Create Coupon Code") },
            text = {
                Column {
                    OutlinedTextField(value = couponCode, onValueChange = { couponCode = it.uppercase() }, label = { Text("Coupon Code (e.g. FESTIVE100)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = couponDiscount, onValueChange = { couponDiscount = it }, label = { Text("Discount Amount (₹)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = couponMinOrder, onValueChange = { couponMinOrder = it }, label = { Text("Minimum Order Value (₹)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (couponCode.isNotBlank()) {
                            viewModel.saveCoupon(
                                CouponEntity(
                                    code = couponCode,
                                    discountType = "FLAT",
                                    discountValue = couponDiscount.toDoubleOrNull() ?: 50.0,
                                    minOrderValue = couponMinOrder.toDoubleOrNull() ?: 500.0,
                                    description = "Flat ₹${couponDiscount} OFF on orders above ₹${couponMinOrder}",
                                    expiryDate = "31 Dec 2026"
                                )
                            )
                            showAddCouponDialog = false
                            couponCode = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
                ) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddCouponDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Broadcast Dialog
    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Broadcast Notification") },
            text = {
                Column {
                    Text("Send live grocery promotion or store announcement to all customer apps:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(value = broadcastTitle, onValueChange = { broadcastTitle = it }, label = { Text("Announcement Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = broadcastMessage, onValueChange = { broadcastMessage = it }, label = { Text("Message Body") }, maxLines = 3, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (broadcastTitle.isNotBlank()) {
                            viewModel.broadcastNotification(broadcastTitle, broadcastMessage)
                            showBroadcastDialog = false
                            broadcastTitle = ""
                            broadcastMessage = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
                ) {
                    Text("Broadcast Now")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun AdminNavigationRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(GroceryGreenPrimary.copy(alpha = 0.12f))
            ) {
                Icon(icon, contentDescription = null, tint = GroceryGreenDark, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.Gray)
    }
}
