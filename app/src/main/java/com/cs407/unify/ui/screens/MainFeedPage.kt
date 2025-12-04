package com.cs407.unify.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cs407.unify.R
import com.cs407.unify.data.Post
import com.cs407.unify.ui.components.UnifyBottomBar
import com.cs407.unify.ui.components.BottomTab
import com.cs407.unify.ui.components.threads.Thread
import com.cs407.unify.ui.components.threads.ThreadCard
import com.cs407.unify.ui.components.threads.ThreadStore
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


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
    onNavigateToSearchPage: () -> Unit,
    onNavigateToThreadPage: () -> Unit = {}
) {
    var threadState by remember { mutableStateOf(ThreadStore.threads.toMutableMap()) }

    val db = remember { FirebaseFirestore.getInstance() }

    var threads by remember { mutableStateOf<List<Thread>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        db.collection("posts")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(50)
            .get()
            .addOnSuccessListener { snapshot ->
                val loadedThreads = snapshot.documents.mapNotNull { doc ->
                    val post = doc.toObject(Post::class.java)
                    post?.let {
                        val thread =
                            Thread(
                                id = it.id.ifBlank { doc.id },
                                title = it.title,
                                body = it.body,
                                hub = it.hub,
                                imageBase64 = it.imageBase64
                            )
                        ThreadStore.threads[thread.id] = thread
                        thread
                    }
                }
                threads = loadedThreads
                isLoading = false
                errorMessage = null
            }
            .addOnFailureListener { e ->
                isLoading = false
                errorMessage = e.message ?: "Failed to load posts."
            }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top)
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

//            LazyColumn(
//                modifier = Modifier.padding(all = 20.dp)
//            ) {
//                items(threadState.entries.toList()) { thread ->
//                    ThreadCard(
//                        thread.value,
//                        onNavigateToPostPage
//                    ) // TODO : implement click -> ThreadPage
//                }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) { item {
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
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Text(
                                text = "Trending / popular threads that have a lot of recent activity. Or suggested threads that user may be interested in.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)

                            )
                        }
                    }
                }
            }

            //quick posts card
                item {
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
                }
                item {
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

                items(threads) { thread ->
                    ThreadCard(
                        thread = thread,
                        onClick = {
                            ThreadStore.selectedThread = thread
                            onNavigateToThreadPage()
                        }
                    )
                }
            }
        }

        UnifyBottomBar(
            current = BottomTab.Feed,
            onHome = {/* currently on home*/ },
            onSearch = { onNavigateToSearchPage() },
            onPost = { onNavigateToPostPage() },
            onMarket = { onNavigateToMarketPage() },
            onProfile = { onNavigateToProfilePage() },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }
}