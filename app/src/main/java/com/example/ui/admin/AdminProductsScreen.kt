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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.GroceryViewModel
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductsScreen(
    viewModel: GroceryViewModel,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val categories by viewModel.allCategories.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Long?>(null) }
    var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }
    var isAddingNewProduct by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    val filteredProducts = remember(allProducts, searchQuery, selectedCategoryId) {
        allProducts.filter { product ->
            val matchesQuery = searchQuery.isBlank() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.hindiName.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategoryId == null || product.categoryId == selectedCategoryId
            matchesQuery && matchesCategory
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { isAddingNewProduct = true },
                containerColor = GroceryGreenPrimary,
                contentColor = Color.White,
                modifier = Modifier.testTag("admin_add_product_fab")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search inventory products...") },
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

            // Category Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { selectedCategoryId = null },
                        label = { Text("All Categories (${allProducts.size})") }
                    )
                }
                items(categories) { cat ->
                    val count = allProducts.count { it.categoryId == cat.id }
                    FilterChip(
                        selected = selectedCategoryId == cat.id,
                        onClick = { selectedCategoryId = cat.id },
                        label = { Text("${cat.name} ($count)") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Products List
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    AdminProductRow(
                        product = product,
                        onEdit = { productToEdit = product },
                        onDuplicate = { viewModel.duplicateProduct(product.id) },
                        onDelete = { productToDelete = product },
                        onToggleActive = { viewModel.toggleProductActive(product.id) },
                        onUpdateStock = { newStock -> viewModel.updateStock(product.id, newStock) }
                    )
                }
            }
        }

        // Add/Edit Product Bottom Sheet
        if (isAddingNewProduct || productToEdit != null) {
            ProductEditBottomSheet(
                product = productToEdit,
                categories = categories,
                onDismiss = {
                    isAddingNewProduct = false
                    productToEdit = null
                },
                onSave = { savedProduct ->
                    viewModel.saveProduct(savedProduct) {
                        isAddingNewProduct = false
                        productToEdit = null
                    }
                }
            )
        }

        // Delete Confirmation Dialog
        if (productToDelete != null) {
            val p = productToDelete!!
            AlertDialog(
                onDismissRequest = { productToDelete = null },
                title = { Text("Delete Product") },
                text = { Text("Are you sure you want to remove '${p.name}' from the store? This will also remove it from customer views.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteProduct(p)
                            productToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GroceryRedDiscount)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { productToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun AdminProductRow(
    product: ProductEntity,
    onEdit: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onToggleActive: () -> Unit,
    onUpdateStock: (Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (product.hindiName.isNotBlank()) {
                        Text(
                            text = product.hindiName,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Brand: ${product.brand.ifBlank { "Store" }} • Unit: ${product.unit}",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Active Switch
                Column(horizontalAlignment = Alignment.End) {
                    Switch(
                        checked = product.isActive,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(checkedThumbColor = GroceryGreenPrimary)
                    )
                    Text(
                        text = if (product.isActive) "Active" else "Hidden",
                        fontSize = 10.sp,
                        color = if (product.isActive) GroceryGreenPrimary else Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing & Stock row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${product.sellingPrice.toInt()}",
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp,
                        color = GroceryGreenDark
                    )
                    if (product.mrp > product.sellingPrice) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "MRP ₹${product.mrp.toInt()}",
                            fontSize = 12.sp,
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = GroceryRedDiscount.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${product.discountPercent}% OFF",
                                color = GroceryRedDiscount,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                // Quick Stock Adjustment
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = if (product.stockQuantity <= 0) GroceryRedDiscount else if (product.stockQuantity <= 10) Color(0xFFF59E0B) else GroceryGreenPrimary.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Stock: ${product.stockQuantity}",
                            color = if (product.stockQuantity <= 10) Color.White else GroceryGreenDark,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "+5",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = GroceryGreenPrimary,
                        modifier = Modifier
                            .clickable { onUpdateStock(product.stockQuantity + 5) }
                            .padding(4.dp)
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 10.dp))

            // Action Buttons (Edit, Duplicate, Delete)
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = GroceryGreenPrimary)
                }
                IconButton(onClick = onDuplicate) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = GroceryRedDiscount)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditBottomSheet(
    product: ProductEntity?,
    categories: List<CategoryEntity>,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var hindiName by remember { mutableStateOf(product?.hindiName ?: "") }
    var brand by remember { mutableStateOf(product?.brand ?: "") }
    var unit by remember { mutableStateOf(product?.unit ?: "1 kg") }
    var description by remember { mutableStateOf(product?.description ?: "") }

    // Dynamic Price Calculator fields
    var mrpText by remember { mutableStateOf(product?.mrp?.toInt()?.toString() ?: "100") }
    var sellingPriceText by remember { mutableStateOf(product?.sellingPrice?.toInt()?.toString() ?: "90") }
    var discountPercentText by remember { mutableStateOf(product?.discountPercent?.toString() ?: "10") }
    var stockText by remember { mutableStateOf(product?.stockQuantity?.toString() ?: "50") }
    var dealBadge by remember { mutableStateOf(product?.dealBadge ?: "") }

    var isFeatured by remember { mutableStateOf(product?.isFeatured ?: false) }
    var isBestSeller by remember { mutableStateOf(product?.isBestSeller ?: false) }
    var isNewArrival by remember { mutableStateOf(product?.isNewArrival ?: false) }
    var isActive by remember { mutableStateOf(product?.isActive ?: true) }

    var selectedCat by remember { mutableStateOf(categories.find { it.id == product?.categoryId } ?: categories.firstOrNull()) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = if (product == null) "Add New Grocery Product" else "Edit Product Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            // English & Hindi Names
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name (English)*") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("edit_product_name")
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = hindiName,
                onValueChange = { hindiName = it },
                label = { Text("Hindi Product Name (हिंदी नाम)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("edit_product_hindi_name")
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit (1kg, 500g)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Category Selector
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCat?.name ?: "Select Category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                selectedCat = cat
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // DYNAMIC PRICE CALCULATOR BOX
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Dynamic Pricing & Discounts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = GroceryGreenDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = mrpText,
                            onValueChange = {
                                mrpText = it
                                val mrpVal = it.toDoubleOrNull() ?: 0.0
                                val sellingVal = sellingPriceText.toDoubleOrNull() ?: 0.0
                                if (mrpVal > 0 && sellingVal > 0) {
                                    val disc = (((mrpVal - sellingVal) / mrpVal) * 100).toInt().coerceAtLeast(0)
                                    discountPercentText = disc.toString()
                                }
                            },
                            label = { Text("MRP (₹)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("edit_mrp_input")
                        )

                        OutlinedTextField(
                            value = sellingPriceText,
                            onValueChange = {
                                sellingPriceText = it
                                val mrpVal = mrpText.toDoubleOrNull() ?: 0.0
                                val sellingVal = it.toDoubleOrNull() ?: 0.0
                                if (mrpVal > 0 && sellingVal > 0) {
                                    val disc = (((mrpVal - sellingVal) / mrpVal) * 100).toInt().coerceAtLeast(0)
                                    discountPercentText = disc.toString()
                                }
                            },
                            label = { Text("Selling Price (₹)*") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("edit_selling_price_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = discountPercentText,
                            onValueChange = {
                                discountPercentText = it
                                val discVal = it.toDoubleOrNull() ?: 0.0
                                val mrpVal = mrpText.toDoubleOrNull() ?: 0.0
                                if (mrpVal > 0) {
                                    val newSelling = mrpVal - (mrpVal * (discVal / 100.0))
                                    sellingPriceText = newSelling.toInt().toString()
                                }
                            },
                            label = { Text("Discount (%)") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )

                        OutlinedTextField(
                            value = stockText,
                            onValueChange = { stockText = it },
                            label = { Text("Stock Quantity*") },
                            singleLine = true,
                            modifier = Modifier.weight(1f).testTag("edit_stock_input")
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = dealBadge,
                onValueChange = { dealBadge = it },
                label = { Text("Deal Badge (e.g. 15% OFF, BUY 1 GET 1)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Checkboxes
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isFeatured, onCheckedChange = { isFeatured = it }, colors = CheckboxDefaults.colors(checkedColor = GroceryGreenPrimary))
                Text("Featured on Home Screen", fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isBestSeller, onCheckedChange = { isBestSeller = it }, colors = CheckboxDefaults.colors(checkedColor = GroceryGreenPrimary))
                Text("Mark as Best Seller", fontSize = 12.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = isNewArrival, onCheckedChange = { isNewArrival = it }, colors = CheckboxDefaults.colors(checkedColor = GroceryGreenPrimary))
                Text("Mark as New Arrival", fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val mrp = mrpText.toDoubleOrNull() ?: 100.0
                        val sellingPrice = sellingPriceText.toDoubleOrNull() ?: 90.0
                        val discount = discountPercentText.toIntOrNull() ?: 0
                        val stock = stockText.toIntOrNull() ?: 50

                        val updated = product?.copy(
                            name = name,
                            hindiName = hindiName,
                            brand = brand,
                            unit = unit,
                            categoryId = selectedCat?.id ?: 1L,
                            mrp = mrp,
                            sellingPrice = sellingPrice,
                            discountPercent = discount,
                            stockQuantity = stock,
                            dealBadge = dealBadge,
                            description = description,
                            isFeatured = isFeatured,
                            isBestSeller = isBestSeller,
                            isNewArrival = isNewArrival,
                            isActive = isActive
                        ) ?: ProductEntity(
                            name = name,
                            hindiName = hindiName,
                            brand = brand,
                            unit = unit,
                            categoryId = selectedCat?.id ?: 1L,
                            mrp = mrp,
                            sellingPrice = sellingPrice,
                            discountPercent = discount,
                            stockQuantity = stock,
                            dealBadge = dealBadge,
                            description = description,
                            isFeatured = isFeatured,
                            isBestSeller = isBestSeller,
                            isNewArrival = isNewArrival,
                            isActive = isActive
                        )
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_product_btn")
            ) {
                Text("Save Product", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
