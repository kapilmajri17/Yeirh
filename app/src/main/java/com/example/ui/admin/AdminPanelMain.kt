package com.example.ui.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.GroceryViewModel
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary

enum class AdminTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    PRODUCTS("Products", Icons.Default.Inventory2),
    ORDERS("Orders", Icons.Default.ReceiptLong),
    INVENTORY("Inventory", Icons.Default.Warehouse),
    MORE("More", Icons.Default.Tune)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelMain(
    viewModel: GroceryViewModel,
    onSwitchToCustomerApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTab by remember { mutableStateOf(AdminTab.DASHBOARD) }

    val allOrders by viewModel.allOrders.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()

    val pendingOrdersCount = allOrders.count { it.orderStatus in listOf("Order Placed", "Confirmed", "Packed") }
    val lowStockCount = allProducts.count { it.stockQuantity <= 10 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Majri Store Owner Panel",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp
                    )
                },
                actions = {
                    TextButton(
                        onClick = onSwitchToCustomerApp,
                        modifier = Modifier.testTag("switch_to_customer_app_btn")
                    ) {
                        Icon(Icons.Default.Store, contentDescription = null, tint = GroceryGreenPrimary)
                        Text(
                            text = " Customer View",
                            color = GroceryGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = GroceryGreenDark
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                AdminTab.entries.forEach { tab ->
                    val isSelected = currentTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentTab = tab },
                        icon = {
                            if (tab == AdminTab.ORDERS && pendingOrdersCount > 0) {
                                BadgedBox(badge = { Badge { Text("$pendingOrdersCount") } }) {
                                    Icon(tab.icon, contentDescription = tab.title)
                                }
                            } else if (tab == AdminTab.INVENTORY && lowStockCount > 0) {
                                BadgedBox(badge = { Badge(containerColor = Color(0xFFF59E0B)) { Text("$lowStockCount") } }) {
                                    Icon(tab.icon, contentDescription = tab.title)
                                }
                            } else {
                                Icon(tab.icon, contentDescription = tab.title)
                            }
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = GroceryGreenDark,
                            selectedTextColor = GroceryGreenDark,
                            indicatorColor = GroceryGreenPrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("admin_tab_${tab.name.lowercase()}")
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                AdminTab.DASHBOARD -> AdminDashboardScreen(
                    viewModel = viewModel,
                    onNavigateToProducts = { currentTab = AdminTab.PRODUCTS },
                    onNavigateToOrders = { currentTab = AdminTab.ORDERS },
                    onNavigateToInventory = { currentTab = AdminTab.INVENTORY },
                    onNavigateToAddProduct = { currentTab = AdminTab.PRODUCTS }
                )
                AdminTab.PRODUCTS -> AdminProductsScreen(
                    viewModel = viewModel
                )
                AdminTab.ORDERS -> AdminOrdersScreen(
                    viewModel = viewModel
                )
                AdminTab.INVENTORY -> AdminInventoryScreen(
                    viewModel = viewModel
                )
                AdminTab.MORE -> AdminMoreScreen(
                    viewModel = viewModel,
                    onLogout = onSwitchToCustomerApp
                )
            }
        }
    }
}
