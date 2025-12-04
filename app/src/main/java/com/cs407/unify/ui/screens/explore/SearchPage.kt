package com.cs407.unify.ui.screens.HomePage

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cs407.unify.ui.components.BottomTab
import com.cs407.unify.ui.components.UnifyBottomBar

@Preview(showBackground = true)
@Composable
fun PreviewSearchPage() {
    SearchPage(
        onNavigateToPostPage = {},
        onNavigateToMarketPage = {},
        onNavigateToMainFeedPage = {},
        onNavigateToProfilePage = {},
        onClickSchool = {},
        onClickHousing = {},
        onClickTransport = {},
        onClickCity = {},
        onClickSocial = {},
        onClickMisc = {}
    )
}
@Composable
fun SearchPage(
    onNavigateToPostPage: () -> Unit,
    onNavigateToMarketPage: () -> Unit,
    onNavigateToMainFeedPage: () -> Unit,
    onNavigateToProfilePage: () -> Unit,
    onClickSchool: () -> Unit,
    onClickHousing: () -> Unit,
    onClickTransport: () -> Unit,
    onClickCity: () -> Unit,
    onClickSocial: () -> Unit,
    onClickMisc: () -> Unit,
) {

    var prompt by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 48.dp)
        ) {

            OutlinedTextField(
                value = prompt,
                onValueChange = {prompt = it},
                placeholder = { Text("Search hubs...") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFD3D3D3))
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) { // SCHOOL
                    Text(
                        text = "SCHOOL", // TODO replace w string
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(25.dp)
                            .clickable(onClick = onClickSchool)
                    )
                }
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) { // HOUSING
                    Text(
                        text = "HOUSING", // TODO replace w string
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(25.dp)
                        .clickable(onClick = onClickHousing)
                    )
                }
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) { // TRANSPORT
                    Text(
                        text = "TRANSPORT", // TODO replace w string
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(25.dp)
                        .clickable(onClick = onClickTransport)
                    )
                }
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) { // CITY LIFE
                    Text(
                        text = "CITY LIFE", // TODO replace w string
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(25.dp)
                        .clickable(onClick = onClickCity)
                    )
                }

                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) { // SOCIAL
                    Text(
                        text = "SOCIAL", // TODO replace w string
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(25.dp)
                        .clickable(onClick = onClickSocial)
                    )
                }
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) { // MISC
                    Text(
                        text = "MISC", // TODO replace w string
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(25.dp)
                        .clickable(onClick = onClickMisc)
                    )
                }
            }


        }
        UnifyBottomBar(
            current = BottomTab.Search,
            onHome = { onNavigateToMainFeedPage() },
            onSearch = {/* currently on search page*/},
            onPost = { onNavigateToPostPage() },
            onMarket = { onNavigateToMarketPage() },
            onProfile = { onNavigateToProfilePage() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}