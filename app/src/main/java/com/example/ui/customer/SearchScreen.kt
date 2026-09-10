package com.example.ui.customer

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.GroceryViewModel
import com.example.ui.common.EmptyStateView
import com.example.ui.common.LocalAppLanguage
import com.example.ui.common.ProductCard
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary

enum class SortOption(val labelEn: String, val labelHi: String) {
    POPULARITY("Popularity", "लोकप्रिय"),
    PRICE_LOW_HIGH("Price: Low to High", "कीमत: कम से ज्यादा"),
    PRICE_HIGH_LOW("Price: High to Low", "कीमत: ज्यादा से कम"),
    DISCOUNT("Highest Discount", "अधिकतम छूट"),
    NEWEST("Newest", "नए उत्पाद")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: GroceryViewModel,
    onNavigateToProductDetails: (ProductEntity) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val isHindi = lang.isHindi()

    var searchQuery by remember { mutableStateOf("") }
    val recentSearches = remember { mutableStateListOf("Atta", "Rice", "Amul Milk", "Oil", "Dal") }

    val allProducts by viewModel.activeProducts.collectAsState()
    val categories by viewModel.activeCategories.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val wishlistIds = wishlist.map { it.productId }.toSet()

    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var inStockOnly by remember { mutableStateOf(false) }
    var selectedSort by remember { mutableStateOf(SortOption.POPULARITY) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // Filter & Sort Logic
    val filteredProducts = remember(searchQuery, selectedCategoryId, inStockOnly, selectedSort, allProducts) {
        var list = allProducts.filter { product ->
            val matchesQuery = searchQuery.isBlank() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.hindiName.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true) ||
                    product.subcategory.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedCategoryId == null || product.categoryId == selectedCategoryId
            val matchesStock = !inStockOnly || product.stockQuantity > 0

            matchesQuery && matchesCategory && matchesStock
        }

        list = when (selectedSort) {
            SortOption.POPULARITY -> list.sortedByDescending { it.salesCount }
            SortOption.PRICE_LOW_HIGH -> list.sortedBy { it.sellingPrice }
            SortOption.PRICE_HIGH_LOW -> list.sortedByDescending { it.sellingPrice }
            SortOption.DISCOUNT -> list.sortedByDescending { it.discountPercent }
            SortOption.NEWEST -> list.sortedByDescending { it.createdAt }
        }

        list
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            if (it.isNotBlank() && !recentSearches.contains(it.trim())) {
                                if (recentSearches.size >= 8) recentSearches.removeLast()
                                recentSearches.add(0, it.trim())
                            }
                        },
                        placeholder = {
                            Text(
                                text = if (isHindi) "किराना खोजें..." else "Search products…",
                                fontSize = 14.sp
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = GroceryGreenPrimary)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GroceryGreenPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("search_input_field")
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = if (selectedCategoryId != null || inStockOnly) GroceryGreenPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
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
            // Recent Searches Chips (when search is blank)
            if (searchQuery.isBlank() && recentSearches.isNotEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isHindi) "हाल की खोजें" else "Recent Searches",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        TextButton(onClick = { recentSearches.clear() }) {
                            Text(text = if (isHindi) "हटाएं" else "Clear", fontSize = 12.sp, color = Color.Gray)
                        }
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                    ) {
                        items(recentSearches) { term ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { searchQuery = term }
                            ) {
                                Text(
                                    text = term,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Active Filters & Sorting Bar
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Category Chip
                item {
                    FilterChip(
                        selected = selectedCategoryId != null,
                        onClick = { showFilterSheet = true },
                        label = {
                            val cat = categories.find { it.id == selectedCategoryId }
                            val catName = if (cat != null) {
                                if (isHindi && cat.hindiName.isNotBlank()) cat.hindiName else cat.name
                            } else (if (isHindi) "श्रेणी" else "Category")
                            Text(catName, fontSize = 12.sp)
                        }
                    )
                }

                // In Stock Chip
                item {
                    FilterChip(
                        selected = inStockOnly,
                        onClick = { inStockOnly = !inStockOnly },
                        label = {
                            Text(if (isHindi) "केवल स्टॉक में" else "In Stock Only", fontSize = 12.sp)
                        }
                    )
                }

                // Sort Chips
                SortOption.entries.forEach { option ->
                    item {
                        FilterChip(
                            selected = selectedSort == option,
                            onClick = { selectedSort = option },
                            label = {
                                Text(if (isHindi) option.labelHi else option.labelEn, fontSize = 12.sp)
                            }
                        )
                    }
                }
            }

            // Search Results Count Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${filteredProducts.size} " + (if (isHindi) "उत्पाद मिले" else "products found"),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Results Grid or Empty State
            if (filteredProducts.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Default.SearchOff,
                    title = if (isHindi) "कोई उत्पाद नहीं मिला" else "No Products Found",
                    description = if (isHindi) "कृपया दूसरे कीवर्ड्स या स्पेलिंग से खोजें" else "Try searching with different terms or check spelling."
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
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

        // Filter Bottom Sheet
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = rememberModalBottomSheetState()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = if (isHindi) "फ़िल्टर और वर्गीकरण" else "Filters & Sorting",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isHindi) "श्रेणी चुनें" else "Category",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategoryId == null,
                                onClick = { selectedCategoryId = null },
                                label = { Text(if (isHindi) "सभी" else "All") }
                            )
                        }
                        items(categories) { category ->
                            FilterChip(
                                selected = selectedCategoryId == category.id,
                                onClick = { selectedCategoryId = category.id },
                                label = {
                                    Text(if (isHindi && category.hindiName.isNotBlank()) category.hindiName else category.name)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isHindi) "क्रमबद्ध करें (Sort By)" else "Sort By",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SortOption.entries.forEach { opt ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedSort == opt) GroceryGreenPrimary.copy(alpha = 0.12f) else Color.Transparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedSort = opt
                                        showFilterSheet = false
                                    }
                                    .padding(vertical = 8.dp, horizontal = 10.dp)
                            ) {
                                Text(
                                    text = if (isHindi) opt.labelHi else opt.labelEn,
                                    fontWeight = if (selectedSort == opt) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedSort == opt) GroceryGreenDark else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
