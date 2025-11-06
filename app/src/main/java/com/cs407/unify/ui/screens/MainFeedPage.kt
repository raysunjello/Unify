package com.cs407.unify.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cs407.unify.R
import com.cs407.unify.ui.components.UnifyBottomBar
import com.cs407.unify.ui.components.BottomTab



@Preview(showBackground = true)
@Composable
fun PreviewMainFeedPage() {
    MainFeedPage(
        onNavigateToPostPage = {},
        onNavigateToMarketPage = {},
        onNavigateToProfilePage = {},
        onNavigateToSearchPage = {},
    )
}
@Composable
fun MainFeedPage(
    onNavigateToPostPage: () -> Unit,
    onNavigateToMarketPage: () -> Unit,
    onNavigateToProfilePage: () -> Unit,
    onNavigateToSearchPage: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Image(
                painter = painterResource(id = R.drawable.unify_logo),
                contentDescription = "Unify Logo",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillBounds,
                colorFilter = null
            )

            Spacer(modifier = Modifier.height(40.dp))

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.placeholder_image),
                        contentDescription = "Placeholder Image",
                        modifier = Modifier
                            .size(100.dp),
                        contentScale = ContentScale.FillBounds,
                        colorFilter = null
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "Popular Threads",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black
                        )

                        Text(
                            text = "Trending / popular threads that have a lot of recent activity. Or suggested threads that user may be interested in.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.DarkGray

                        )
                    }
                }
            }

            // quick posts card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Quick Posts",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Similar to tweets, kind of like PSA announcements or random thoughts / ideas that people post.",

                        )
                }
            }

            //items for sale card
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.placeholder_image),
                        contentDescription = "Placeholder Image",
                        modifier = Modifier
                            .size(100.dp),
                        contentScale = ContentScale.FillBounds,
                        colorFilter = null
                    )

                    Column(
                        modifier = Modifier
                            .padding(start = 16.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        Text(
                            text = "Items For Sale",
                            style = MaterialTheme.typography.headlineMedium,
                        )

                        Text(
                            text = "Recently listed items or suggested items on sale."

                        )
                    }
                }
            }

        }

        UnifyBottomBar(
            current = BottomTab.Feed,
            onHome = {/* currently on home*/},
            onSearch = { onNavigateToSearchPage() },
            onPost = { onNavigateToPostPage() },
            onMarket = { onNavigateToMarketPage() },
            onProfile = { onNavigateToProfilePage()},
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }
}
