package com.cs407.unify.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cs407.unify.ui.components.UnifyBottomBar
import androidx.compose.ui.unit.sp
import com.cs407.unify.ui.components.BottomTab

@Preview(showBackground = true)
@Composable
fun PreviewMarketPage() {
    MarketPage(
        onNavigateToPostPage = {},
        onNavigateToMainFeedPage = {},
        onNavigateToProfilePage = {},
        onNavigateToSearchPage = {},
        onNavigateToMarketCategory = {},
        onNavigateToCart = {}
    )
}

@Composable
fun MarketPage(
    onNavigateToPostPage: () -> Unit,
    onNavigateToMainFeedPage: () -> Unit,
    onNavigateToProfilePage: () -> Unit,
    onNavigateToSearchPage: () -> Unit,
    onNavigateToMarketCategory: (String) -> Unit = {},
    onNavigateToCart: () -> Unit = {}
) {
    val categories = listOf("TICKETS", "FURNITURE", "TEXTBOOKS", "NOTES", "OTHER STUFF")

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
                    .padding(bottom = 24.dp, top = 48.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //search bar
                SearchBar(
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(16.dp))

                //cart icon
                IconButton(
                    onClick = { onNavigateToCart() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Shopping Cart",
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            //category list
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp), // Space for bottom nav bar
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(categories) { category ->
                        Button(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .height(100.dp)
                                .clickable { onNavigateToMarketCategory(category) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = Color.White
                            ),
                            border = BorderStroke(2.dp, Color.White),
                            onClick = { onNavigateToMarketCategory(category) }
                        ) {
                            Text(
                                text = category.uppercase(),
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(25.dp)
                            )
                        }
                    }
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