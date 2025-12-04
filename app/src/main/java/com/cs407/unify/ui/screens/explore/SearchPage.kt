package com.cs407.unify.ui.screens.explore

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
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
import com.google.firebase.firestore.FirebaseFirestore

@Preview(showBackground = true)
@Composable
fun PreviewSearchPage() {
    SearchPage(
        onNavigateToPostPage = {},
        onNavigateToMarketPage = {},
        onNavigateToMainFeedPage = {},
        onNavigateToProfilePage = {},
    )
}
@Composable
fun SearchPage(
    onNavigateToPostPage: () -> Unit,
    onNavigateToMarketPage: () -> Unit,
    onNavigateToMainFeedPage: () -> Unit,
    onNavigateToProfilePage: () -> Unit,
    onClickHub: (String) -> Unit = {}
) {

    var prompt by remember { mutableStateOf("") }
    var allHubs by remember { mutableStateOf<List<String>>(emptyList()) }
    var filteredHubs by remember { mutableStateOf<List<String>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val db = FirebaseFirestore.getInstance()

    // Load all hubs from database
    LaunchedEffect(Unit) {
        db.collection("hubs")
            .orderBy("name")
            .get()
            .addOnSuccessListener { snapshot ->
                val hubs = snapshot.documents.mapNotNull { doc ->
                    doc.getString("name")
                }
                allHubs = hubs
                filteredHubs = hubs
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    // Filter hubs based on search input
    LaunchedEffect(prompt) {
        filteredHubs = if (prompt.isBlank()) {
            allHubs
        } else {
            allHubs.filter { hub ->
                hub.lowercase().startsWith(prompt.lowercase())
            }.sorted()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 48.dp)
        ) {

            TextField(
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
                when {
                    isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
                    }
                    filteredHubs.isEmpty() && prompt.isNotBlank() -> {
                        Text(
                            text = "This hub does not currently exist, make a post to this hub to create it!",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            modifier = Modifier.padding(24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    else -> {
                        filteredHubs.forEach { hubName ->
                            ElevatedCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(16.dp)
                                    .clickable { onClickHub(hubName) },
                                shape = MaterialTheme.shapes.extraLarge,
                                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                            ) {
                                Text(
                                    text = hubName.uppercase(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .padding(25.dp)

                                )
                            }
                        }
                    }
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