package com.example.ui.customer

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.ui.GroceryViewModel
import com.example.ui.common.LocalAppLanguage
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary
import com.example.ui.theme.GroceryTextPrimary
import com.example.ui.theme.GroceryTextSecondary

data class CategoryVisual(val emoji: String, val bgColor: Color)

fun getCategoryVisual(categoryId: Long): CategoryVisual {
    return when (categoryId) {
        1L -> CategoryVisual("🌾", Color(0xFFFEF3C7))
        2L -> CategoryVisual("🛢️", Color(0xFFFEF9C3))
        3L -> CategoryVisual("🌶️", Color(0xFFFFE4E6))
        4L -> CategoryVisual("🥣", Color(0xFFFDE68A))
        5L -> CategoryVisual("🍯", Color(0xFFFED7AA))
        6L -> CategoryVisual("🍞", Color(0xFFFEE2E2))
        7L -> CategoryVisual("🍿", Color(0xFFFEF08A))
        8L -> CategoryVisual("☕", Color(0xFFE2E8F0))
        9L -> CategoryVisual("🥛", Color(0xFFE0F2FE))
        10L -> CategoryVisual("🥤", Color(0xFFDCFCE7))
        11L -> CategoryVisual("🧴", Color(0xFFF3E8FF))
        12L -> CategoryVisual("🧼", Color(0xFFCCFBF1))
        13L -> CategoryVisual("🥦", Color(0xFFDCFCE7))
        14L -> CategoryVisual("🪔", Color(0xFFFFEDD5))
        else -> CategoryVisual("🛒", Color(0xFFF1F5F9))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesTabScreen(
    viewModel: GroceryViewModel,
    onCategoryClick: (CategoryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val isHindi = lang.isHindi()

    val categories by viewModel.activeCategories.collectAsState()
    val allProducts by viewModel.activeProducts.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isHindi) "किराना श्रेणियां" else "All Categories",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        Text(
                            text = if (isHindi) "⚡ 10 मिनट में सब कुछ आपके दरवाज़े पर" else "⚡ Everything delivered in 10-12 mins",
                            color = GroceryGreenPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(categories, key = { it.id }) { category ->
                val count = allProducts.count { it.categoryId == category.id }
                val displayName = if (isHindi && category.hindiName.isNotBlank()) category.hindiName else category.name
                val visual = getCategoryVisual(category.id)

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFF1F5F9)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onCategoryClick(category) }
                        .testTag("category_grid_${category.id}")
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(visual.bgColor)
                        ) {
                            Text(
                                text = visual.emoji,
                                fontSize = 28.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = GroceryTextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            lineHeight = 15.sp
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = "$count " + (if (isHindi) "सामान" else "items"),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = GroceryTextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
