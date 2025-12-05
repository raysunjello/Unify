package com.cs407.unify.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.unify.data.Post
import com.cs407.unify.ui.components.threads.Thread
import com.cs407.unify.ui.components.threads.ThreadCard
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MarketCategoryPage(
    categoryName: String,
    onExit: () -> Unit,
    onClick: (Thread) -> Unit
) {
    var marketPosts by remember { mutableStateOf<List<Thread>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    var prompt by remember { mutableStateOf("") }
    var filteredPosts by remember { mutableStateOf<List<Thread>>(emptyList()) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(categoryName) {
        db.collection("posts")
            .get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.documents.mapNotNull { doc ->
                    val post = doc.toObject(Post::class.java)

                    post?.let {
                        // Filter for market posts with matching category
                        if (it.isMarketPost == true && it.hub.equals(categoryName, ignoreCase = true)) {
                            Thread(
                                id = it.id.ifBlank { doc.id },
                                title = it.title,
                                body = it.body,
                                hub = it.hub,
                                imageBase64 = it.imageBase64,
                                isMarketPost = it.isMarketPost,
                                price = it.price,
                                contactInfo = it.contactInfo
                            )
                        } else {
                            null
                        }
                    }
                }

                marketPosts = posts
                filteredPosts = posts
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    LaunchedEffect(prompt) {
        filteredPosts = if (prompt.isBlank()) {
            marketPosts
        } else {
            marketPosts.filter { post ->
                post.title.lowercase().contains(prompt.lowercase()) ||
                        post.body.lowercase().contains(prompt.lowercase())
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            IconButton(
                onClick = { onExit() },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(horizontal = 8.dp)
                    .padding(top = 15.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp)
            ) {
                Text(
                    text = categoryName.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    placeholder = { Text("Search $categoryName...") },
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
                        .width(300.dp)
                        .height(75.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .padding(8.dp),
                    shape = RoundedCornerShape(50)
                )

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    marketPosts.isEmpty() -> {
                        Text(
                            text = "No items in this category yet",
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    filteredPosts.isEmpty() && prompt.isNotBlank() -> {
                        Text(
                            text = "No matches found",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.padding(all = 20.dp)
                        ) {
                            items(filteredPosts) { thread ->
                                ThreadCard(thread, onClick = { onClick(thread) })
                            }
                        }
                    }
                }
            }
        }
    }
}