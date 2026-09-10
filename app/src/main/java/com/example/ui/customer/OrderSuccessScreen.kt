package com.example.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.common.LocalAppLanguage
import com.example.ui.theme.GroceryGoldAccent
import com.example.ui.theme.GroceryGreenDark
import com.example.ui.theme.GroceryGreenPrimary

@Composable
fun OrderSuccessScreen(
    orderId: Long,
    onViewOrders: () -> Unit,
    onContinueShopping: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = LocalAppLanguage.current
    val isHindi = lang.isHindi()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        // Success Checkmark Badge
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(GroceryGreenPrimary)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isHindi) "ऑर्डर सफलतापूर्वक दर्ज हुआ!" else "Order Placed Successfully!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Black,
            color = GroceryGreenDark,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isHindi) "माजरी किराना स्टोर से खरीदारी करने के लिए धन्यवाद।" else "Thank you for shopping with Majri Grocery Store!",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(18.dp)
            ) {
                Text(
                    text = "Order Reference ID",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = "#MGS-${orderId}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp,
                    color = GroceryGreenDark
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GroceryGreenPrimary.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isHindi) "अनुमानित डिलीवरी: 15-30 मिनट" else "Estimated Delivery: 15-30 mins",
                        color = GroceryGreenDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Button(
            onClick = onViewOrders,
            colors = ButtonDefaults.buttonColors(containerColor = GroceryGreenPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("view_order_receipt_btn")
        ) {
            Icon(Icons.Default.Receipt, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "ऑर्डर व रसीद देखें" else "View Order & Invoice",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onContinueShopping,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, GroceryGreenPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = GroceryGreenPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isHindi) "और किराना खरीदें" else "Continue Shopping",
                fontWeight = FontWeight.Bold,
                color = GroceryGreenPrimary
            )
        }
    }
}
