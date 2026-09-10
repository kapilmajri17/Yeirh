package com.example.ui.customer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.ui.GroceryViewModel
import com.example.ui.common.LocalAppLanguage
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryRedDiscount
import com.example.ui.theme.GroceryTextPrimary
import com.example.ui.theme.GroceryTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealsScreen(
    viewModel: GroceryViewModel,
    onNavigateToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = LocalAppLanguage.current
    val isHindi = lang.isHindi()

    val deals by viewModel.activeDeals.collectAsState()
    val coupons by viewModel.activeCoupons.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isHindi) "ऑफ़र्स और डिस्काउंट्स" else "Deals & Offers",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Coupons Header
            item {
                Text(
                    text = if (isHindi) "उपलब्ध कूपन्स (Available Coupons)" else "Available Store Coupons",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GroceryTextSecondary
                )
            }

            // Coupon Cards
            items(coupons) { coupon ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GroceryGreenPrimary.copy(alpha = 0.12f),
                                    border = BorderStroke(1.dp, GroceryGreenPrimary.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = coupon.code,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = GroceryGreenPrimary,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = GroceryGoldAccent.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = if (coupon.discountType == "PERCENTAGE") "${coupon.discountValue.toInt()}% OFF" else "₹${coupon.discountValue.toInt()} OFF",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color(0xFF78350F),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = coupon.description,
                                fontSize = 11.sp,
                                color = GroceryTextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Min Order: ₹${coupon.minOrderValue.toInt()} • Valid till ${coupon.expiryDate}",
                                fontSize = 10.sp,
                                color = GroceryTextSecondary,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.applyCoupon(coupon.code, 1000.0)
                                onNavigateToCart()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
                            shape = RoundedCornerShape(50),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isHindi) "लागू करें" else "Apply",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Featured Store Deals Header
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isHindi) "धमाकेदार ऑफर्स और फेस्टिव सेल" else "Special Promotions & Deals",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = GroceryTextSecondary
                )
            }

            // Deals Cards
            items(deals) { deal ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        // Banner if present
                        val bannerResId = if (deal.bannerUri.isNotBlank()) {
                            context.resources.getIdentifier(deal.bannerUri, "drawable", context.packageName)
                        } else 0

                        if (bannerResId != 0) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(115.dp)
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context).data(bannerResId).build(),
                                    contentDescription = deal.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    shape = RoundedCornerShape(bottomEnd = 10.dp),
                                    color = GroceryRedDiscount,
                                    modifier = Modifier.align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = deal.dealType,
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Column(modifier = Modifier.padding(12.dp)) {
                            val dealTitle = if (isHindi && deal.hindiTitle.isNotBlank()) deal.hindiTitle else deal.title
                            Text(
                                text = dealTitle,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GroceryTextPrimary
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = deal.description,
                                fontSize = 11.sp,
                                color = GroceryTextSecondary,
                                lineHeight = 15.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Timer,
                                        contentDescription = null,
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (deal.endDate.isNotBlank()) "Valid till ${deal.endDate}" else "Limited Time Offer",
                                        fontSize = 10.sp,
                                        color = Color(0xFF94A3B8)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(50),
                                    color = GroceryGreenPrimary.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = if (deal.discountPercent > 0) "${deal.discountPercent}% OFF" else if (deal.discountAmount > 0) "Save ₹${deal.discountAmount.toInt()}" else "Special",
                                        color = GroceryGreenPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
