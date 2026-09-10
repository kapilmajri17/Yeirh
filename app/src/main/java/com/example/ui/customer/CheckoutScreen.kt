package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.GroceryViewModel
import com.example.ui.common.LocalAppLanguage
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: GroceryViewModel,
    onOrderPlaced: (Long) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val isHindi = lang.isHindi()

    val cartItems by viewModel.cartItems.collectAsState()
    val storeSettings by viewModel.storeSettings.collectAsState()
    val appliedCoupon by viewModel.appliedCoupon.collectAsState()
    val appliedDiscount by viewModel.appliedCouponDiscount.collectAsState()
    val savedAddresses by viewModel.savedAddresses.collectAsState()

    val activeItems = cartItems.filter { !it.isSavedForLater }
    val subtotal = activeItems.sumOf { it.unitPrice * it.quantity }
    val freeThreshold = storeSettings?.freeDeliveryThreshold ?: 499.0
    val deliveryFee = if (subtotal >= freeThreshold) 0.0 else (storeSettings?.deliveryCharge ?: 30.0)
    val finalTotal = (subtotal - appliedDiscount + deliveryFee).coerceAtLeast(0.0)

    val defaultAddr = savedAddresses.firstOrNull { it.isDefault } ?: savedAddresses.firstOrNull()

    var customerName by remember { mutableStateOf(defaultAddr?.name ?: "Praveen Sharma") }
    var customerPhone by remember { mutableStateOf(defaultAddr?.phone ?: "9876543210") }
    var address by remember { mutableStateOf(defaultAddr?.fullAddress ?: "House No. 142, Street 3, Majri") }
    var landmark by remember { mutableStateOf(defaultAddr?.landmark ?: "Near Primary School") }
    var pincode by remember { mutableStateOf(defaultAddr?.pincode ?: "134109") }
    var selectedPaymentMethod by remember { mutableStateOf("Cash on Delivery") }
    var selectedDeliverySlot by remember { mutableStateOf("Express Delivery (15-30 Mins)") }
    var orderNotes by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "ऑर्डर चेकआउट" else "Checkout",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 10.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = if (isHindi) "कुल भुगतान" else "Amount to Pay",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "₹${finalTotal.toInt()}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = GroceryGreenDark
                        )
                    }

                    Button(
                        onClick = {
                            if (customerName.isNotBlank() && customerPhone.isNotBlank() && address.isNotBlank()) {
                                isSubmitting = true
                                viewModel.placeOrder(
                                    customerName = customerName,
                                    customerPhone = customerPhone,
                                    address = address,
                                    landmark = landmark,
                                    pincode = pincode,
                                    paymentMethod = selectedPaymentMethod,
                                    notes = "$selectedDeliverySlot. $orderNotes",
                                    onSuccess = { orderId ->
                                        isSubmitting = false
                                        onOrderPlaced(orderId)
                                    }
                                )
                            } else {
                                viewModel.showMessage("Please fill in delivery details")
                            }
                        },
                        enabled = !isSubmitting && activeItems.isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("confirm_place_order_btn")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isHindi) "ऑर्डर बुक करें" else "Place Order",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Delivery Address Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = GroceryGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "1. डिलीवरी पता (Delivery Address)" else "1. Delivery Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text(if (isHindi) "पूरा नाम" else "Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = customerPhone,
                        onValueChange = { customerPhone = it },
                        label = { Text(if (isHindi) "मोबाइल नंबर" else "Phone Number") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_phone_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(if (isHindi) "मकान नंबर, गली / मोहल्ला" else "House / Flat No., Street, Area") },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("checkout_address_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = landmark,
                            onValueChange = { landmark = it },
                            label = { Text(if (isHindi) "लैंडमार्क (पहचान)" else "Landmark") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.3f)
                        )
                        OutlinedTextField(
                            value = pincode,
                            onValueChange = { pincode = it },
                            label = { Text("Pincode") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(0.9f)
                        )
                    }
                }
            }

            // 2. Delivery Time Slot Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = GroceryGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "2. डिलीवरी समय (Delivery Slot)" else "2. Delivery Slot",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val slots = listOf(
                        "Express Delivery (15-30 Mins)" to "Instant dispatch from Majri store",
                        "Today Evening (6:00 PM - 8:00 PM)" to "Convenient evening grocery drop",
                        "Tomorrow Morning (8:00 AM - 10:00 AM)" to "Fresh morning delivery"
                    )

                    slots.forEach { (slotTitle, slotSubtitle) ->
                        val isSelected = selectedDeliverySlot == slotTitle
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GroceryGreenPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GroceryGreenPrimary else Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedDeliverySlot = slotTitle }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedDeliverySlot = slotTitle },
                                    colors = RadioButtonDefaults.colors(selectedColor = GroceryGreenPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(text = slotTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = slotSubtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // 3. Payment Method Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, tint = GroceryGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isHindi) "3. भुगतान विधि (Payment Method)" else "3. Payment Method",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val paymentOptions = listOf(
                        Triple("Cash on Delivery", "Pay cash or scan QR upon delivery", Icons.Default.Money),
                        Triple("UPI / Google Pay / PhonePe", "Pay securely via any UPI App", Icons.Default.QrCode),
                        Triple("Store Pickup", "Pick up packed order directly from Majri Store", Icons.Default.Store)
                    )

                    paymentOptions.forEach { (name, desc, icon) ->
                        val isSelected = selectedPaymentMethod == name
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) GroceryGreenPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) GroceryGreenPrimary else Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedPaymentMethod = name }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(10.dp)
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedPaymentMethod = name },
                                    colors = RadioButtonDefaults.colors(selectedColor = GroceryGreenPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(icon, contentDescription = null, tint = GroceryGreenPrimary, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }

            // 4. Order Items Preview & Summary
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isHindi) "ऑर्डर सारांश (${activeItems.size} आइटम)" else "Order Summary (${activeItems.size} items)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    activeItems.forEach { item ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                        ) {
                            Text(
                                text = "${item.quantity}x ${item.variantName.ifBlank { item.unit }}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "₹${(item.unitPrice * item.quantity).toInt()}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Subtotal", fontSize = 12.sp)
                        Text(text = "₹${subtotal.toInt()}", fontSize = 12.sp)
                    }
                    if (appliedDiscount > 0) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Coupon (${appliedCoupon?.code})", fontSize = 12.sp, color = GroceryGreenPrimary)
                            Text(text = "-₹${appliedDiscount.toInt()}", fontSize = 12.sp, color = GroceryGreenPrimary)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Delivery Fee", fontSize = 12.sp)
                        Text(text = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Total Payable", fontWeight = FontWeight.Black, fontSize = 15.sp)
                        Text(text = "₹${finalTotal.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = GroceryGreenDark)
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
