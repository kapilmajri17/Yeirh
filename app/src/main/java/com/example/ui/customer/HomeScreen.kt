package com.example.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.CategoryEntity
import com.example.data.model.ProductEntity
import com.example.ui.GroceryViewModel
import com.example.ui.common.LocalAppLanguage
import com.example.ui.common.ProductCard
import com.example.ui.common.SectionHeader
import com.example.ui.common.tr
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenLight
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount
import com.example.ui.theme.GroceryTextMuted
import com.example.ui.theme.GroceryTextPrimary
import com.example.ui.theme.GroceryTextSecondary

@Composable
fun HomeScreen(
    viewModel: GroceryViewModel,
    onNavigateToProductDetails: (ProductEntity) -> Unit,
    onNavigateToCategoryListing: (CategoryEntity) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToDeals: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = LocalAppLanguage.current
    val isHindi = lang.isHindi()

    val products by viewModel.activeProducts.collectAsState()
    val categories by viewModel.activeCategories.collectAsState()
    val banners by viewModel.activeBanners.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val wishlist by viewModel.wishlist.collectAsState()
    val storeSettings by viewModel.storeSettings.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotificationsCount = notifications.count { !it.isRead }

    val wishlistProductIds = wishlist.map { it.productId }.toSet()

    // Categorized product groups
    val todaysDeals = products.filter { it.discountPercent >= 15 || it.dealBadge.isNotEmpty() }.take(10)
    val bestSellers = products.filter { it.isBestSeller }.take(10)
    val dairyAndBread = products.filter { it.categoryId == 9L || it.categoryId == 6L }.take(10)
    val snacksAndDrinks = products.filter { it.categoryId == 7L || it.categoryId == 10L }.take(10)
    val cookingStaples = products.filter { it.categoryId == 1L || it.categoryId == 4L || it.categoryId == 5L }.take(10)
    val freshProduce = products.filter { it.categoryId == 13L }.take(10)
    val newArrivals = products.filter { it.isNewArrival }.take(10)
    val recommended = products.filter { it.isFeatured }.take(10)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Iconic Blinkit Header (Delivery in 10 mins, address pill, mic search bar)
        item {
            Surface(
                shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
                color = Color.White,
                shadowElevation = 4.dp,
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 14.dp)
                ) {
                    // Top Row: Blinkit 10 mins Branding + Location & Right Actions
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isHindi) "ब्लिंकिट • 10 मिनट" else "Blinkit in 10 minutes",
                                    color = GroceryTextPrimary,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFF22C55E),
                                    modifier = Modifier.size(8.dp)
                                ) {}
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = if (isHindi) "📍 माजरी गांव, मुख्य चौक, सेक्टर 4" else "📍 Majri Village, Main Chowk, Sector 4",
                                    color = GroceryTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = " ▾",
                                    color = GroceryTextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Right actions: Language pill & Notifications
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Language Switcher Pill
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .clickable { lang.toggleLanguage() }
                                    .testTag("language_toggle_btn")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = if (isHindi) "EN" else "हिन्दी",
                                        color = GroceryGreenPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Notification Bell (circular slate-100 button)
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFF1F5F9),
                                modifier = Modifier
                                    .size(38.dp)
                                    .clickable { onNavigateToNotifications() }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    BadgedBox(
                                        badge = {
                                            if (unreadNotificationsCount > 0) {
                                                Badge(
                                                    containerColor = GroceryRedDiscount,
                                                    modifier = Modifier.border(1.dp, Color.White, CircleShape)
                                                ) {
                                                    Text(
                                                        text = "$unreadNotificationsCount",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = "Notifications",
                                            tint = Color(0xFF334155),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Blinkit Search Input with Mic
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF1F5F9),
                        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToSearch() }
                            .testTag("home_search_bar")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = GroceryGreenPrimary,
                                    modifier = Modifier.size(19.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isHindi) "सामान खोजें 'दूध, ब्रेड, आटा, मैगी, चिप्स'..." else "Search 'milk, bread, atta, maggi, chips'...",
                                    color = GroceryTextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice Search",
                                tint = GroceryGreenPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Promotional Banners Carousel (High Density Compact Hero Banner)
        if (banners.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(14.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(banners) { banner ->
                        Card(
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier
                                .width(310.dp)
                                .height(140.dp)
                                .clickable {
                                    if (banner.linkType == "DEAL") {
                                        onNavigateToDeals()
                                    } else {
                                        val cat = categories.find { it.id.toString() == banner.linkTarget }
                                        if (cat != null) onNavigateToCategoryListing(cat) else onNavigateToDeals()
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.linearGradient(
                                            listOf(GroceryGreenDark, GroceryGreenLight)
                                        )
                                    )
                            ) {
                                // Image
                                val bannerResId = if (banner.imageUri.isNotEmpty()) {
                                    context.resources.getIdentifier(banner.imageUri, "drawable", context.packageName)
                                } else 0

                                if (bannerResId != 0) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context).data(bannerResId).build(),
                                        contentDescription = banner.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    // Gradient scrim
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Brush.verticalGradient(
                                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                                                    startY = 20f
                                                )
                                            )
                                    )
                                }

                                // Banner Text and CTA
                                Column(
                                    verticalArrangement = Arrangement.Bottom,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(14.dp)
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Color.White.copy(alpha = 0.2f),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    ) {
                                        Text(
                                            text = if (isHindi) "त्यौहार स्पेशल" else "Festival Special",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = banner.title,
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 16.sp
                                    )
                                    if (banner.subtitle.isNotBlank()) {
                                        Text(
                                            text = banner.subtitle,
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 11.sp,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = Color.White
                                    ) {
                                        Text(
                                            text = banner.buttonText,
                                            color = GroceryGreenDark,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Quick Category Dense Grid / Scroll
        item {
            Spacer(modifier = Modifier.height(14.dp))
            SectionHeader(
                title = if (isHindi) "श्रेणियां" else "Top Categories",
                subtitle = if (isHindi) "दुकान के मुख्य किराना विभाग" else "Explore grocery sections",
                actionText = if (isHindi) "सभी देखें" else "View all",
                onActionClick = {
                    if (categories.isNotEmpty()) onNavigateToCategoryListing(categories.first())
                }
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(categories) { category ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .width(70.dp)
                            .clickable { onNavigateToCategoryListing(category) }
                            .testTag("category_chip_${category.id}")
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp, horizontal = 4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(GroceryGreenPrimary.copy(alpha = 0.1f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Storefront,
                                    contentDescription = category.name,
                                    tint = GroceryGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            val catName = if (isHindi && category.hindiName.isNotBlank()) category.hindiName else category.name
                            Text(
                                text = catName,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = GroceryTextPrimary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // 4. Today's Deals Section (Dense Cards)
        if (todaysDeals.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = if (isHindi) "आज के धमाकेदार ऑफ़र्स" else "Today's Deals",
                    subtitle = if (isHindi) "प्रीमियम राशन पर भारी छूट" else "Special discounts & combo savings",
                    actionText = "SALE",
                    isSaleBadge = true,
                    onActionClick = onNavigateToDeals
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(todaysDeals) { product ->
                        val cartItem = cartItems.find { it.productId == product.id && !it.isSavedForLater }
                        ProductCard(
                            product = product,
                            cartItem = cartItem,
                            isInWishlist = wishlistProductIds.contains(product.id),
                            onProductClick = { onNavigateToProductDetails(product) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onIncreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1)
                            },
                            onDecreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1)
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(product.id) },
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }
        }

        // 5. Best Sellers
        if (bestSellers.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = if (isHindi) "सर्वाधिक बिकने वाले उत्पाद" else "Best Sellers",
                    subtitle = if (isHindi) "माजरी में ग्राहकों की पहली पसंद" else "Most loved grocery essentials"
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(bestSellers) { product ->
                        val cartItem = cartItems.find { it.productId == product.id && !it.isSavedForLater }
                        ProductCard(
                            product = product,
                            cartItem = cartItem,
                            isInWishlist = wishlistProductIds.contains(product.id),
                            onProductClick = { onNavigateToProductDetails(product) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onIncreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1)
                            },
                            onDecreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1)
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(product.id) },
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }
        }

        // 5a. Signature Blinkit Aisle: Dairy, Bread & Eggs
        if (dairyAndBread.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = if (isHindi) "🥛 दूध, ब्रेड व ताज़ा अंडे" else "🥛 Dairy, Bread & Eggs",
                    subtitle = if (isHindi) "अमूल दूध, मक्खन, ब्रेड व नाश्ता" else "Milk, paneer, butter & morning essentials"
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(dairyAndBread) { product ->
                        val cartItem = cartItems.find { it.productId == product.id && !it.isSavedForLater }
                        ProductCard(
                            product = product,
                            cartItem = cartItem,
                            isInWishlist = wishlistProductIds.contains(product.id),
                            onProductClick = { onNavigateToProductDetails(product) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onIncreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1)
                            },
                            onDecreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1)
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(product.id) },
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }
        }

        // 5b. Signature Blinkit Aisle: Snacks & Cold Drinks
        if (snacksAndDrinks.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = if (isHindi) "🍿 स्नैक्स, नमकीन व शीतल पेय" else "🍿 Snacks & Cold Drinks",
                    subtitle = if (isHindi) "मैगी, लेज़, कोल्ड ड्रिंक्स व जूस" else "Instant munchies, colas, juices & treats"
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(snacksAndDrinks) { product ->
                        val cartItem = cartItems.find { it.productId == product.id && !it.isSavedForLater }
                        ProductCard(
                            product = product,
                            cartItem = cartItem,
                            isInWishlist = wishlistProductIds.contains(product.id),
                            onProductClick = { onNavigateToProductDetails(product) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onIncreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1)
                            },
                            onDecreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1)
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(product.id) },
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }
        }

        // 5c. Signature Blinkit Aisle: Atta, Rice & Kitchen Staples
        if (cookingStaples.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = if (isHindi) "🌾 आटा, चावल, दालें व रसोई सामग्री" else "🌾 Atta, Rice, Dal & Cooking Staples",
                    subtitle = if (isHindi) "आशीर्वाद आटा, बासमती चावल, सरसों तेल व मसाले" else "Flour, basmati rice, mustard oil & spices"
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(cookingStaples) { product ->
                        val cartItem = cartItems.find { it.productId == product.id && !it.isSavedForLater }
                        ProductCard(
                            product = product,
                            cartItem = cartItem,
                            isInWishlist = wishlistProductIds.contains(product.id),
                            onProductClick = { onNavigateToProductDetails(product) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onIncreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1)
                            },
                            onDecreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1)
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(product.id) },
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }
        }

        // 5d. Farm Fresh Fruits & Veggies
        if (freshProduce.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = if (isHindi) "🥦 ताज़ी सब्ज़ियां व फल" else "🥦 Farm Fresh Fruits & Veggies",
                    subtitle = if (isHindi) "रोज़ाना ताज़ा व साफ़-सुथरा" else "Handpicked, farm fresh & hygienic"
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(freshProduce) { product ->
                        val cartItem = cartItems.find { it.productId == product.id && !it.isSavedForLater }
                        ProductCard(
                            product = product,
                            cartItem = cartItem,
                            isInWishlist = wishlistProductIds.contains(product.id),
                            onProductClick = { onNavigateToProductDetails(product) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onIncreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1)
                            },
                            onDecreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1)
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(product.id) },
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }
        }

        // 6. Promotional Banner Strip (Free Delivery Callout)
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(GroceryGreenPrimary.copy(alpha = 0.12f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = GroceryGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isHindi) "₹${storeSettings?.freeDeliveryThreshold?.toInt() ?: 499} से अधिक पर मुफ़्त डिलीवरी!" else "Free Delivery on orders above ₹${storeSettings?.freeDeliveryThreshold?.toInt() ?: 499}!",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = GroceryTextPrimary
                        )
                        Text(
                            text = if (isHindi) "सीधे आपके घर तक ताज़ा राशन" else "Fast delivery to your doorstep in Majri",
                            fontSize = 10.sp,
                            color = GroceryTextSecondary
                        )
                    }
                }
            }
        }

        // 7. New Arrivals
        if (newArrivals.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = if (isHindi) "नए किराना उत्पाद" else "New Arrivals",
                    subtitle = if (isHindi) "ताज़ा स्टॉक व नए ब्रांड्स" else "Recently added to our shelves"
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(newArrivals) { product ->
                        val cartItem = cartItems.find { it.productId == product.id && !it.isSavedForLater }
                        ProductCard(
                            product = product,
                            cartItem = cartItem,
                            isInWishlist = wishlistProductIds.contains(product.id),
                            onProductClick = { onNavigateToProductDetails(product) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onIncreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1)
                            },
                            onDecreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1)
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(product.id) },
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }
        }

        // 8. Recommended For You
        if (recommended.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(
                    title = if (isHindi) "आपके लिए अनुशंसित" else "Recommended For You",
                    subtitle = if (isHindi) "दैनिक घर की जरूरतें" else "Pantry staples tailored for you"
                )

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(recommended) { product ->
                        val cartItem = cartItems.find { it.productId == product.id && !it.isSavedForLater }
                        ProductCard(
                            product = product,
                            cartItem = cartItem,
                            isInWishlist = wishlistProductIds.contains(product.id),
                            onProductClick = { onNavigateToProductDetails(product) },
                            onAddToCart = { viewModel.addToCart(product) },
                            onIncreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1)
                            },
                            onDecreaseQty = {
                                if (cartItem != null) viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1)
                            },
                            onToggleWishlist = { viewModel.toggleWishlist(product.id) },
                            modifier = Modifier.width(150.dp)
                        )
                    }
                }
            }
        }
    }
}
