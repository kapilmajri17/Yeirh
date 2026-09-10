package com.example.ui.admin

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderEntity
import com.example.ui.GroceryViewModel
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount

@Composable
fun AdminDashboardScreen(
    viewModel: GroceryViewModel,
    onNavigateToProducts: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToAddProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()

    // Calculated stats
    val totalRevenue = allOrders.filter { it.orderStatus != "Cancelled" }.sumOf { it.finalAmount }
    val pendingOrders = allOrders.filter { it.orderStatus in listOf("Order Placed", "Confirmed", "Packed") }
    val lowStockProducts = allProducts.filter { it.stockQuantity in 1..10 }
    val outOfStockProducts = allProducts.filter { it.stockQuantity <= 0 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Action Bar
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onNavigateToAddProduct,
                    colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add Product", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onNavigateToOrders,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Manage Orders", fontSize = 12.sp)
                }
            }
        }

        // Summary Metric Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricStatCard(
                        title = "Total Revenue",
                        value = "₹${totalRevenue.toInt()}",
                        subtitle = "From ${allOrders.size} orders",
                        icon = Icons.Default.TrendingUp,
                        iconBg = GroceryGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Pending Orders",
                        value = "${pendingOrders.size}",
                        subtitle = "Needs fulfillment",
                        icon = Icons.Default.PendingActions,
                        iconBg = GroceryGoldAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    MetricStatCard(
                        title = "Total Products",
                        value = "${allProducts.size}",
                        subtitle = "${allProducts.count { it.isActive }} Active in Store",
                        icon = Icons.Default.Inventory,
                        iconBg = GroceryGreenDark,
                        modifier = Modifier.weight(1f)
                    )
                    MetricStatCard(
                        title = "Stock Alerts",
                        value = "${lowStockProducts.size + outOfStockProducts.size}",
                        subtitle = "${outOfStockProducts.size} Out / ${lowStockProducts.size} Low",
                        icon = Icons.Default.Warning,
                        iconBg = GroceryRedDiscount,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Weekly Sales Bar Visualizer
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Weekly Sales Trend (Majri Store)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val days = listOf("Mon" to 0.4f, "Tue" to 0.65f, "Wed" to 0.5f, "Thu" to 0.85f, "Fri" to 0.75f, "Sat" to 1.0f, "Sun" to 0.95f)
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        days.forEach { (day, fraction) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(22.dp)
                                        .height((85 * fraction).dp)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(if (fraction >= 0.85f) GroceryGreenPrimary else GroceryGreenPrimary.copy(alpha = 0.5f))
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = day, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Low Stock Urgent Alert Card
        if (lowStockProducts.isNotEmpty() || outOfStockProducts.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = GroceryRedDiscount.copy(alpha = 0.08f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, GroceryRedDiscount.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = GroceryRedDiscount, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Low / Out of Stock Alert",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = GroceryRedDiscount
                                )
                            }
                            TextButton(onClick = onNavigateToInventory) {
                                Text("Restock All", color = GroceryRedDiscount, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        val alertItems = (outOfStockProducts + lowStockProducts).take(4)
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                            alertItems.forEach { product ->
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "${product.name} (${product.unit})",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (product.stockQuantity <= 0) GroceryRedDiscount else GroceryGoldAccent
                                    ) {
                                        Text(
                                            text = if (product.stockQuantity <= 0) "OUT OF STOCK" else "Only ${product.stockQuantity} left",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Orders Section
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Recent Customer Orders",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToOrders) {
                    Text("View All (${allOrders.size})", color = GroceryGreenPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(allOrders.take(5), key = { it.id }) { order ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(text = "#${order.orderNumber} • ${order.customerName}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "₹${order.finalAmount.toInt()} • ${order.paymentMethod}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GroceryGreenPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = order.orderStatus,
                                color = GroceryGreenDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun MetricStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconBg.copy(alpha = 0.15f))
                ) {
                    Icon(icon, contentDescription = null, tint = iconBg, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
