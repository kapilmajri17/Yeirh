package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.GroceryViewModel
import com.example.ui.common.EmptyStateView
import com.example.ui.common.LocalAppLanguage
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderScreen(
    viewModel: GroceryViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val isHindi = lang.isHindi()

    val orders by viewModel.allOrders.collectAsState()
    val storeSettings by viewModel.storeSettings.collectAsState()

    var selectedOrderForInvoice by remember { mutableStateOf<OrderEntity?>(null) }
    var selectedOrderForTracking by remember { mutableStateOf<OrderEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "मेरे ऑर्डर्स (My Orders)" else "My Orders",
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
        modifier = modifier
    ) { innerPadding ->
        if (orders.isEmpty()) {
            EmptyStateView(
                icon = Icons.Default.Receipt,
                title = if (isHindi) "कोई पिछला ऑर्डर नहीं मिला" else "No Orders Found",
                description = if (isHindi) "किराने की खरीदारी शुरू करें और ऑर्डर विवरण यहां देखें।" else "Your past orders and delivery status will appear right here.",
                modifier = Modifier.padding(innerPadding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(orders.sortedByDescending { it.orderDate }, key = { it.id }) { order ->
                    val orderItems = remember(order.itemsJson) { viewModel.parseOrderItems(order.itemsJson) }
                    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                    val formattedDate = dateFormat.format(Date(order.orderDate))

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Order number and Status Badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        text = "#${order.orderNumber}",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = GroceryGreenDark
                                    )
                                    Text(
                                        text = formattedDate,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                val statusColor = when (order.orderStatus) {
                                    "Delivered" -> GroceryGreenPrimary
                                    "Out for Delivery" -> GroceryGoldAccent
                                    "Cancelled" -> GroceryRedDiscount
                                    else -> GroceryGreenDark
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = statusColor.copy(alpha = 0.15f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.5f))
                                ) {
                                    Text(
                                        text = order.orderStatus,
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            // Order Items Summary
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                orderItems.take(3).forEach { item ->
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "${item.quantity}x ${item.productName} (${item.variantName})",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "₹${(item.unitPrice * item.quantity).toInt()}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                if (orderItems.size > 3) {
                                    Text(
                                        text = "+${orderItems.size - 3} more items",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 12.dp))

                            // Total & Actions
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(
                                        text = if (isHindi) "कुल राशि" else "Total Amount",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "₹${order.finalAmount.toInt()}",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        color = GroceryGreenDark
                                    )
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(
                                        onClick = { selectedOrderForTracking = order },
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = if (isHindi) "ट्रैक करें" else "Track", fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = { selectedOrderForInvoice = order },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary)
                                    ) {
                                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(text = if (isHindi) "रसीद" else "Invoice", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Order Tracking Bottom Sheet
        if (selectedOrderForTracking != null) {
            val order = selectedOrderForTracking!!
            ModalBottomSheet(
                onDismissRequest = { selectedOrderForTracking = null },
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Order #${order.orderNumber} Tracking",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Delivery to: ${order.customerName}, ${order.deliveryAddress}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val stages = listOf("Order Placed", "Confirmed", "Packed", "Out for Delivery", "Delivered")
                    val currentStageIndex = stages.indexOf(order.orderStatus).let { if (it == -1) 1 else it }

                    stages.forEachIndexed { index, stage ->
                        val isDone = index <= currentStageIndex
                        val isCurrent = index == currentStageIndex

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) GroceryGreenPrimary else Color.LightGray)
                            ) {
                                if (isDone) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                } else {
                                    Box(modifier = Modifier.size(10.dp).background(Color.White, CircleShape))
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = stage,
                                    fontWeight = if (isCurrent) FontWeight.Black else if (isDone) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isDone) GroceryGreenDark else Color.Gray,
                                    fontSize = 14.sp
                                )
                                if (isCurrent) {
                                    Text(
                                        text = "Current Status",
                                        fontSize = 11.sp,
                                        color = GroceryGreenPrimary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Digital Invoice Bottom Sheet / Preview
        if (selectedOrderForInvoice != null) {
            val order = selectedOrderForInvoice!!
            val items = viewModel.parseOrderItems(order.itemsJson)

            ModalBottomSheet(
                onDismissRequest = { selectedOrderForInvoice = null },
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Invoice Header
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = storeSettings?.storeName ?: "Majri Grocery Store",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = GroceryGreenDark
                        )
                        Text(
                            text = storeSettings?.address ?: "Main Market, Majri",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Phone: ${storeSettings?.phone ?: "9876543210"} • GST: ${storeSettings?.gstNumber ?: "06AABCM1234F1Z5"}",
                            fontSize = 10.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(text = "Invoice #: INV-${order.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = "Payment: ${order.paymentMethod} (${order.paymentStatus})", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Customer: ${order.customerName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(text = order.customerPhone, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    // Items Table
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                    ) {
                        Text(text = "Item", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(2f))
                        Text(text = "Qty", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                        Text(text = "Price", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                        Text(text = "Total", fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    }

                    items.forEach { itm ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                        ) {
                            Text(text = "${itm.productName} (${itm.variantName})", fontSize = 11.sp, modifier = Modifier.weight(2f))
                            Text(text = "${itm.quantity}", fontSize = 11.sp, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                            Text(text = "₹${itm.unitPrice.toInt()}", fontSize = 11.sp, modifier = Modifier.weight(0.9f), textAlign = TextAlign.End)
                            Text(text = "₹${(itm.unitPrice * itm.quantity).toInt()}", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    // Totals
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Subtotal:", fontSize = 12.sp)
                        Text(text = "₹${order.subtotal.toInt()}", fontSize = 12.sp)
                    }
                    if (order.discount > 0) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Discount:", fontSize = 12.sp, color = GroceryGreenPrimary)
                            Text(text = "-₹${order.discount.toInt()}", fontSize = 12.sp, color = GroceryGreenPrimary)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Delivery Fee:", fontSize = 12.sp)
                        Text(text = if (order.deliveryCharge == 0.0) "FREE" else "₹${order.deliveryCharge.toInt()}", fontSize = 12.sp)
                    }

                    Divider(modifier = Modifier.padding(vertical = 6.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "GRAND TOTAL:", fontWeight = FontWeight.Black, fontSize = 14.sp)
                        Text(text = "₹${order.finalAmount.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = GroceryGreenDark)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Thank you for shopping with Majri Grocery Store! For customer support call +91 ${storeSettings?.phone ?: "9876543210"}",
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
