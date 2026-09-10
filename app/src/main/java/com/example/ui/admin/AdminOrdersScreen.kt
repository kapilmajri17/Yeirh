package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.GroceryViewModel
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier
) {
    val allOrders by viewModel.allOrders.collectAsState()
    val storeSettings by viewModel.storeSettings.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf("All") }
    var invoiceOrder by remember { mutableStateOf<OrderEntity?>(null) }

    val statusOptions = listOf("All", "Order Placed", "Confirmed", "Packed", "Out for Delivery", "Delivered", "Cancelled")

    val filteredOrders = remember(allOrders, searchQuery, selectedStatusFilter) {
        allOrders.filter { order ->
            val matchesStatus = selectedStatusFilter == "All" || order.orderStatus == selectedStatusFilter
            val matchesSearch = searchQuery.isBlank() ||
                    order.orderNumber.contains(searchQuery, ignoreCase = true) ||
                    order.customerName.contains(searchQuery, ignoreCase = true) ||
                    order.customerPhone.contains(searchQuery)
            matchesStatus && matchesSearch
        }.sortedByDescending { it.orderDate }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search by Order #, Customer, Phone...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        )

        // Status Horizontal Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(statusOptions) { status ->
                val count = if (status == "All") allOrders.size else allOrders.count { it.orderStatus == status }
                FilterChip(
                    selected = selectedStatusFilter == status,
                    onClick = { selectedStatusFilter = status },
                    label = { Text("$status ($count)") }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Orders List
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredOrders, key = { it.id }) { order ->
                AdminOrderCard(
                    order = order,
                    onUpdateStatus = { newStatus -> viewModel.updateOrderStatus(order.id, newStatus) },
                    onTogglePayment = {
                        val newPaymentStatus = if (order.paymentStatus == "Completed") "Pending" else "Completed"
                        viewModel.updateOrderPaymentStatus(order.id, newPaymentStatus)
                    },
                    onViewInvoice = { invoiceOrder = order }
                )
            }
        }

        // Invoice Sheet
        if (invoiceOrder != null) {
            val order = invoiceOrder!!
            val items = viewModel.parseOrderItems(order.itemsJson)

            ModalBottomSheet(
                onDismissRequest = { invoiceOrder = null },
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Store Tax Invoice - #${order.orderNumber}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Customer: ${order.customerName} (${order.customerPhone})", fontWeight = FontWeight.SemiBold)
                    Text(text = "Address: ${order.deliveryAddress}, ${order.landmark}", fontSize = 12.sp)

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    items.forEach { itm ->
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                        ) {
                            Text(text = "${itm.quantity}x ${itm.productName}", fontSize = 12.sp)
                            Text(text = "₹${(itm.unitPrice * itm.quantity).toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 10.dp))

                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Total Collected:", fontWeight = FontWeight.Bold)
                        Text(text = "₹${order.finalAmount.toInt()}", fontWeight = FontWeight.Bold, color = GroceryGreenDark)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrderCard(
    order: OrderEntity,
    onUpdateStatus: (String) -> Unit,
    onTogglePayment: () -> Unit,
    onViewInvoice: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(order.orderDate))

    var statusDropdownExpanded by remember { mutableStateOf(false) }
    val statuses = listOf("Order Placed", "Confirmed", "Packed", "Out for Delivery", "Delivered", "Cancelled")

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row
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

                // Interactive Order Status Dropdown
                ExposedDropdownMenuBox(
                    expanded = statusDropdownExpanded,
                    onExpandedChange = { statusDropdownExpanded = !statusDropdownExpanded }
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GroceryGreenPrimary.copy(alpha = 0.15f),
                        modifier = Modifier.menuAnchor()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = order.orderStatus,
                                color = GroceryGreenDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusDropdownExpanded)
                        }
                    }

                    ExposedDropdownMenu(
                        expanded = statusDropdownExpanded,
                        onDismissRequest = { statusDropdownExpanded = false }
                    ) {
                        statuses.forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    onUpdateStatus(st)
                                    statusDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp))

            // Customer Details
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Call, contentDescription = null, tint = GroceryGreenPrimary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "${order.customerName} • ${order.customerPhone}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "${order.deliveryAddress}, ${order.landmark} - ${order.pincode}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            if (order.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Notes: ${order.notes}", fontSize = 11.sp, color = GroceryGoldAccent, fontWeight = FontWeight.Medium)
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp))

            // Amount and Payment Toggle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(text = "Final Amount", fontSize = 10.sp, color = Color.Gray)
                    Text(text = "₹${order.finalAmount.toInt()}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = GroceryGreenDark)
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Payment Status Pill (Clickable to toggle)
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (order.paymentStatus == "Completed") GroceryGreenPrimary.copy(alpha = 0.15f) else GroceryRedDiscount.copy(alpha = 0.15f),
                        modifier = Modifier.clickable { onTogglePayment() }
                    ) {
                        Text(
                            text = "${order.paymentMethod}: ${order.paymentStatus}",
                            color = if (order.paymentStatus == "Completed") GroceryGreenDark else GroceryRedDiscount,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    OutlinedButton(
                        onClick = onViewInvoice,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Bill", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}
