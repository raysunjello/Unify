package com.cs407.unify.ui.screens.explore

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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

    val defaultHubsOrder = listOf("School", "Housing", "Transport", "City", "Social", "Misc")

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
                filteredHubs = defaultsThenOthers(hubs, defaultHubsOrder)
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    LaunchedEffect(prompt) {
        filteredHubs = if (prompt.isBlank()) {
            defaultsThenOthers(allHubs, defaultHubsOrder)
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

            OutlinedTextField(
                value = prompt,
                onValueChange = {prompt = it},
                placeholder = { Text("Search hubs...") },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp))
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
                            modifier = Modifier.padding(24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            contentPadding = PaddingValues(bottom = 80.dp), // Space for bottom nav bar
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(filteredHubs) { hubName ->
                                Button(
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth()
                                        .height(100.dp)
                                        .clickable { onClickHub(hubName) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.Transparent,
                                        contentColor = Color.White
                                    ),
                                    border = BorderStroke(2.dp, Color.White),
                                    onClick = { onClickHub(hubName) }
                                ) {
                                    Text(
                                        text = hubName.uppercase(),
                                        style = MaterialTheme.typography.headlineLarge,
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
/**
 * sorts hubs in alphabetical order but with the default hubs first in this order:
 * School, Housing, Transport, City, Social, Misc
 */
fun defaultsThenOthers(hubs: List<String>, defaultOrder: List<String>): List<String> {
    val defaultHubs = mutableListOf<String>()
    val otherHubs = mutableListOf<String>()

    // Separate hubs into defaults and others
    hubs.forEach { hub ->
        if (defaultOrder.any { it.equals(hub, ignoreCase = true) }) {
            defaultHubs.add(hub)
        } else {
            otherHubs.add(hub)
        }
    }
    // Sort defaults by the specified order
    val sortedDefaults = defaultOrder.mapNotNull { defaultName ->
        defaultHubs.find { it.equals(defaultName, ignoreCase = true) }
    }

    // Sort other hubs alphabetically
    val sortedOthers = otherHubs.sorted()

    // Combine: defaults first, then others
    return sortedDefaults + sortedOthers
}