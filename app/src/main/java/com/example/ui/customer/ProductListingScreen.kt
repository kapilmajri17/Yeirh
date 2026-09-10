package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.GroceryViewModel
import com.example.ui.common.EmptyStateView
import com.example.ui.common.LocalAppLanguage
import com.example.ui.common.ProductCard
import com.example.ui.theme.GroceryGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListingScreen(
    category: CategoryEntity,
    viewModel: GroceryViewModel,
    onNavigateToProductDetails: (ProductEntity) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToCart: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val isHindi = lang.isHindi()

    val allProducts by viewModel.activeProducts.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val wishlistIds = wishlist.map { it.productId }.toSet()

    val categoryProducts = remember(allProducts, category.id) {
        allProducts.filter { it.categoryId == category.id }
    }

    // Subcategories list
    val subcategories = remember(categoryProducts) {
        categoryProducts.map { it.subcategory }.filter { it.isNotBlank() }.distinct()
    }

    var selectedSubcategory by remember { mutableStateOf<String?>(null) }

    val displayedProducts = remember(categoryProducts, selectedSubcategory) {
        if (selectedSubcategory == null) categoryProducts else categoryProducts.filter { it.subcategory == selectedSubcategory }
    }

    val catName = if (isHindi && category.hindiName.isNotBlank()) category.hindiName else category.name

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = catName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = onNavigateToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = GroceryGreenPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Subcategories horizontal chips if any
            if (subcategories.size > 1) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedSubcategory == null,
                            onClick = { selectedSubcategory = null },
                            label = { Text(if (isHindi) "सभी" else "All", fontSize = 12.sp) }
                        )
                    }
                    items(subcategories) { sub ->
                        FilterChip(
                            selected = selectedSubcategory == sub,
                            onClick = { selectedSubcategory = sub },
                            label = { Text(sub, fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Products count
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${displayedProducts.size} " + (if (isHindi) "उत्पाद" else "items"),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            if (displayedProducts.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.Storefront,
                    title = if (isHindi) "इस श्रेणी में कोई उत्पाद नहीं है" else "No Products In This Category",
                    description = if (isHindi) "कृपया अन्य श्रेणियों के उत्पाद देखें" else "Check out other fresh grocery sections!"
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(displayedProducts, key = { it.id }) { product ->
                        val cartItem = cartItems.find { it.productId == product.id && !it.isSavedForLater }
                        ProductCard(
                            product = product,
                            cartItem = cartItem,
                            isInWishlist = wishlistIds.contains(product.id),
                            onProductClick = { onNavigateToProductDetails(product) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onIncreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1)
                            },
                            onDecreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1)
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(product.id) }
                        )
                    }
                }
            }
        }
    }
}
