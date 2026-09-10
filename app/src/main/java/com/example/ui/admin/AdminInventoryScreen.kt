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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProductEntity
import com.example.ui.GroceryViewModel
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount

@Composable
fun AdminInventoryScreen(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var stockFilter by remember { mutableStateOf("All") } // "All", "Out of Stock", "Low Stock", "In Stock"

    val filteredProducts = remember(allProducts, searchQuery, stockFilter) {
        allProducts.filter { product ->
            val matchesSearch = searchQuery.isBlank() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (stockFilter) {
                "Out of Stock" -> product.stockQuantity <= 0
                "Low Stock" -> product.stockQuantity in 1..10
                "In Stock" -> product.stockQuantity > 10
                else -> true
            }

            matchesSearch && matchesFilter
        }.sortedBy { it.stockQuantity }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search products to update stock...") },
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

        // Stock Filter Chips
        val filterOptions = listOf("All", "Out of Stock", "Low Stock", "In Stock")
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterOptions) { opt ->
                val count = when (opt) {
                    "Out of Stock" -> allProducts.count { it.stockQuantity <= 0 }
                    "Low Stock" -> allProducts.count { it.stockQuantity in 1..10 }
                    "In Stock" -> allProducts.count { it.stockQuantity > 10 }
                    else -> allProducts.size
                }
                FilterChip(
                    selected = stockFilter == opt,
                    onClick = { stockFilter = opt },
                    label = { Text("$opt ($count)") }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Inventory Items List
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredProducts, key = { it.id }) { product ->
                InventoryCard(
                    product = product,
                    onAdjustStock = { delta ->
                        val newStock = (product.stockQuantity + delta).coerceAtLeast(0)
                        viewModel.updateStock(product.id, newStock)
                    }
                )
            }
        }
    }
}

@Composable
private fun InventoryCard(
    product: ProductEntity,
    onAdjustStock: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "${product.brand} • Unit: ${product.unit}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                // Current Stock Badge
                val badgeColor = when {
                    product.stockQuantity <= 0 -> GroceryRedDiscount
                    product.stockQuantity <= 10 -> GroceryGoldAccent
                    else -> GroceryGreenPrimary
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${product.stockQuantity} in stock",
                        color = badgeColor,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Restock Buttons (+1, +5, +10, +25, -1)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Quick Adjust:",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Medium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuickStockPill("-1", onClick = { onAdjustStock(-1) }, isNegative = true)
                    QuickStockPill("+1", onClick = { onAdjustStock(1) })
                    QuickStockPill("+5", onClick = { onAdjustStock(5) })
                    QuickStockPill("+10", onClick = { onAdjustStock(10) })
                    QuickStockPill("+25", onClick = { onAdjustStock(25) })
                }
            }
        }
    }
}

@Composable
private fun QuickStockPill(
    label: String,
    onClick: () -> Unit,
    isNegative: Boolean = false
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isNegative) Color.LightGray.copy(alpha = 0.4f) else GroceryGreenPrimary.copy(alpha = 0.15f),
        modifier = Modifier
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = if (isNegative) Color.DarkGray else GroceryGreenDark,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}
