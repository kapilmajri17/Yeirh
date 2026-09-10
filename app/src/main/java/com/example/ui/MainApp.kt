package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.admin.AdminPanelMain
import com.example.ui.common.LanguageState
import com.example.ui.common.LocalAppLanguage
import com.example.ui.customer.CartScreen
import com.example.ui.customer.CategoriesTabScreen
import com.example.ui.customer.CheckoutScreen
import com.example.ui.customer.DealsScreen
import com.example.ui.customer.HomeScreen
import com.example.ui.customer.NotificationScreen
import com.example.ui.customer.OrderScreen
import com.example.ui.customer.OrderSuccessScreen
import com.example.ui.customer.ProductDetailsScreen
import com.example.ui.customer.ProductListingScreen
import com.example.ui.customer.ProfileScreen
import com.example.ui.customer.SearchScreen
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount
import com.example.ui.theme.GroceryTextSecondary

enum class CustomerTab(val labelEn: String, val labelHi: String, val icon: ImageVector) {
    HOME("Home", "होम", Icons.Default.Home),
    CATEGORIES("Categories", "श्रेणियां", Icons.Default.Category),
    DEALS("Deals", "ऑफ़र्स", Icons.Default.LocalOffer),
    CART("Cart", "कार्ट", Icons.Default.ShoppingCart),
    PROFILE("Account", "खाता", Icons.Default.Person)
}

sealed class AppScreen {
    object CustomerTabs : AppScreen()
    data class ProductDetails(val product: ProductEntity) : AppScreen()
    object Search : AppScreen()
    data class ProductListing(val category: CategoryEntity) : AppScreen()
    object Checkout : AppScreen()
    object Orders : AppScreen()
    object Notifications : AppScreen()
    data class OrderSuccess(val orderId: Long) : AppScreen()
}

@Composable
fun MainApp(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier
) {
    val languageState = remember { LanguageState() }
    val snackbarHostState = remember { SnackbarHostState() }

    var isAdminMode by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(CustomerTab.HOME) }
    var currentScreen by remember { mutableStateOf<AppScreen>(AppScreen.CustomerTabs) }

    val userMessage by viewModel.userMessage.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val activeCartItems = cartItems.filter { !it.isSavedForLater }
    val activeCartCount = activeCartItems.sumOf { it.quantity }
    val activeCartTotal = activeCartItems.sumOf { it.unitPrice * it.quantity }

    // User Message Toast / SnackBar
    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearMessage()
        }
    }

    CompositionLocalProvider(LocalAppLanguage provides languageState) {
        if (isAdminMode) {
            AdminPanelMain(
                viewModel = viewModel,
                onSwitchToCustomerApp = { isAdminMode = false },
                modifier = modifier
            )
        } else {
            // Customer App with Bottom Bar
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    val showBottomBar = currentScreen is AppScreen.CustomerTabs
                    AnimatedVisibility(visible = showBottomBar, enter = fadeIn(), exit = fadeOut()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Transparent)
                        ) {
                            // Signature Blinkit Sticky Floating Cart Strip
                            AnimatedVisibility(
                                visible = activeCartCount > 0 && currentTab != CustomerTab.CART,
                                enter = slideInVertically { it } + fadeIn(),
                                exit = slideOutVertically { it } + fadeOut()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = GroceryGreenPrimary,
                                    shadowElevation = 8.dp,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 6.dp)
                                        .clickable { currentTab = CustomerTab.CART }
                                        .testTag("floating_cart_bar")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color.White.copy(alpha = 0.22f),
                                                modifier = Modifier.size(34.dp)
                                            ) {
                                                Box(contentAlignment = Alignment.Center) {
                                                    Text("🛍️", fontSize = 15.sp)
                                                }
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Text(
                                                    text = "$activeCartCount ${if (activeCartCount == 1) "ITEM" else "ITEMS"} • ₹${activeCartTotal.toInt()}",
                                                    color = Color.White,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                                Text(
                                                    text = if (languageState.isHindi()) "⚡ 10 मिनट में डिलीवरी" else "⚡ Delivery in 10-12 mins",
                                                    color = Color.White.copy(alpha = 0.85f),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color.White.copy(alpha = 0.18f))
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = if (languageState.isHindi()) "कार्ट देखें" else "View Cart",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.ChevronRight,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            // Bottom Tab Bar
                            Surface(
                                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                                color = Color.White,
                                shadowElevation = 8.dp,
                                border = BorderStroke(1.dp, Color(0xFFF1F5F9))
                            ) {
                            NavigationBar(
                                containerColor = Color.Transparent,
                                tonalElevation = 0.dp
                            ) {
                                CustomerTab.entries.forEach { tab ->
                                    val isSelected = currentTab == tab
                                    val label = if (languageState.isHindi()) tab.labelHi else tab.labelEn

                                    NavigationBarItem(
                                        selected = isSelected,
                                        onClick = { currentTab = tab },
                                        icon = {
                                            if (tab == CustomerTab.CART && activeCartCount > 0) {
                                                BadgedBox(
                                                    badge = {
                                                        Badge(
                                                            containerColor = GroceryRedDiscount,
                                                            modifier = Modifier.border(1.dp, Color.White, CircleShape)
                                                        ) {
                                                            Text(
                                                                text = "$activeCartCount",
                                                                color = Color.White,
                                                                fontSize = 9.sp,
                                                                fontWeight = FontWeight.Bold
                                                            )
                                                        }
                                                    }
                                                ) {
                                                    Icon(tab.icon, contentDescription = label, modifier = Modifier.size(22.dp))
                                                }
                                            } else {
                                                Icon(tab.icon, contentDescription = label, modifier = Modifier.size(22.dp))
                                            }
                                        },
                                        label = {
                                            Text(
                                                text = label,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 9.sp
                                            )
                                        },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = GroceryGreenPrimary,
                                            selectedTextColor = GroceryGreenPrimary,
                                            unselectedIconColor = GroceryTextSecondary.copy(alpha = 0.5f),
                                            unselectedTextColor = GroceryTextSecondary.copy(alpha = 0.5f),
                                            indicatorColor = GroceryGreenPrimary.copy(alpha = 0.12f)
                                        ),
                                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                                    )
                                }
                            }
                        }
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
                    when (val screen = currentScreen) {
                        is AppScreen.CustomerTabs -> {
                            when (currentTab) {
                                CustomerTab.HOME -> HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToProductDetails = { currentScreen = AppScreen.ProductDetails(it) },
                                    onNavigateToCategoryListing = { currentScreen = AppScreen.ProductListing(it) },
                                    onNavigateToSearch = { currentScreen = AppScreen.Search },
                                    onNavigateToDeals = { currentTab = CustomerTab.DEALS },
                                    onNavigateToNotifications = { currentScreen = AppScreen.Notifications }
                                )
                                CustomerTab.CATEGORIES -> CategoriesTabScreen(
                                    viewModel = viewModel,
                                    onCategoryClick = { currentScreen = AppScreen.ProductListing(it) }
                                )
                                CustomerTab.DEALS -> DealsScreen(
                                    viewModel = viewModel,
                                    onNavigateToCart = { currentTab = CustomerTab.CART }
                                )
                                CustomerTab.CART -> CartScreen(
                                    viewModel = viewModel,
                                    onNavigateToCheckout = { currentScreen = AppScreen.Checkout },
                                    onNavigateToHome = { currentTab = CustomerTab.HOME },
                                    onBack = { currentTab = CustomerTab.HOME }
                                )
                                CustomerTab.PROFILE -> ProfileScreen(
                                    viewModel = viewModel,
                                    onNavigateToOrders = { currentScreen = AppScreen.Orders },
                                    onNavigateToAdminPanel = { isAdminMode = true },
                                    onNavigateToProductDetails = { currentScreen = AppScreen.ProductDetails(it) }
                                )
                            }
                        }

                        is AppScreen.ProductDetails -> {
                            BackHandler { currentScreen = AppScreen.CustomerTabs }
                            ProductDetailsScreen(
                                product = screen.product,
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.CustomerTabs },
                                onNavigateToCart = {
                                    currentTab = CustomerTab.CART
                                    currentScreen = AppScreen.CustomerTabs
                                }
                            )
                        }

                        is AppScreen.Search -> {
                            BackHandler { currentScreen = AppScreen.CustomerTabs }
                            SearchScreen(
                                viewModel = viewModel,
                                onNavigateToProductDetails = { currentScreen = AppScreen.ProductDetails(it) },
                                onBack = { currentScreen = AppScreen.CustomerTabs }
                            )
                        }

                        is AppScreen.ProductListing -> {
                            BackHandler { currentScreen = AppScreen.CustomerTabs }
                            ProductListingScreen(
                                category = screen.category,
                                viewModel = viewModel,
                                onNavigateToProductDetails = { currentScreen = AppScreen.ProductDetails(it) },
                                onNavigateToSearch = { currentScreen = AppScreen.Search },
                                onNavigateToCart = {
                                    currentTab = CustomerTab.CART
                                    currentScreen = AppScreen.CustomerTabs
                                },
                                onBack = { currentScreen = AppScreen.CustomerTabs }
                            )
                        }

                        is AppScreen.Checkout -> {
                            BackHandler { currentScreen = AppScreen.CustomerTabs }
                            CheckoutScreen(
                                viewModel = viewModel,
                                onOrderPlaced = { orderId -> currentScreen = AppScreen.OrderSuccess(orderId) },
                                onBack = { currentScreen = AppScreen.CustomerTabs }
                            )
                        }

                        is AppScreen.Orders -> {
                            BackHandler { currentScreen = AppScreen.CustomerTabs }
                            OrderScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.CustomerTabs }
                            )
                        }

                        is AppScreen.Notifications -> {
                            BackHandler { currentScreen = AppScreen.CustomerTabs }
                            NotificationScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = AppScreen.CustomerTabs }
                            )
                        }

                        is AppScreen.OrderSuccess -> {
                            BackHandler {
                                currentTab = CustomerTab.HOME
                                currentScreen = AppScreen.CustomerTabs
                            }
                            OrderSuccessScreen(
                                orderId = screen.orderId,
                                onViewOrders = { currentScreen = AppScreen.Orders },
                                onContinueShopping = {
                                    currentTab = CustomerTab.HOME
                                    currentScreen = AppScreen.CustomerTabs
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
