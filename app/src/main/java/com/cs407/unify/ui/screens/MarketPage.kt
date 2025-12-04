package com.cs407.unify.ui.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.unify.ui.components.UnifyBottomBar
import com.cs407.unify.ui.components.BottomTab

@Preview(showBackground = true)
@Composable
fun PreviewMarketPage() {
    MarketPage(
        onNavigateToPostPage = {},
        onNavigateToMainFeedPage = {},
        onNavigateToProfilePage = {},
        onNavigateToSearchPage = {}
    )
}
@Composable
fun MarketPage(
    onNavigateToPostPage: () -> Unit,
    onNavigateToMainFeedPage: () -> Unit,
    onNavigateToProfilePage: () -> Unit,
    onNavigateToSearchPage: () -> Unit
) {
    // wrapped in box in order for current bottomnavbar implementation to work
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            //top bar with search and cart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //search bar
                SearchBar(
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                //cart icon
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = "Shopping Cart",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable { /* TODO: Navigate to cart */ },
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            //category list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CategoryItem("Tickets")
                    CategoryItem("Furniture")
                    CategoryItem("Textbooks")
                    CategoryItem("Notes")
                    CategoryItem("Other Stuff")
                }
            }
        }

        UnifyBottomBar(
            current = BottomTab.Market,
            onHome = { onNavigateToMainFeedPage() },
            onSearch = { onNavigateToSearchPage() },
            onPost = { onNavigateToPostPage() },
            onMarket = {/* currently on market page*/ },
            onProfile = { onNavigateToProfilePage() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }

    TextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        modifier = modifier
            .height(50.dp),
        placeholder = {
            Text(
                text = "Search market...",
                color = Color.Gray
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
        },
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(25.dp),
        singleLine = true
    )
}

@Composable
fun CategoryItem(categoryName: String) {
    Text(
        text = categoryName,
        fontSize = 27.sp,
        fontWeight = FontWeight.Black,
        letterSpacing = 1.sp,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Navigate to category subpage */ }
            .padding(vertical = 8.dp)
    )
}
