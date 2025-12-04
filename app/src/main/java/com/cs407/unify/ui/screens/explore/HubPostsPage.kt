package com.cs407.unify.ui.screens.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.unify.data.Post
import com.cs407.unify.ui.components.threads.Thread
import com.cs407.unify.ui.components.threads.ThreadCard
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HubPostsPage(
    hubName: String,
    onExit: () -> Unit,
    onClick: (Thread) -> Unit
) {
    var hubPosts by remember { mutableStateOf<List<Thread>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val db = FirebaseFirestore.getInstance()

    LaunchedEffect(hubName) {
        // Query posts with matching hub (case-insensitive)
        db.collection("posts")
            .get()
            .addOnSuccessListener { snapshot ->
                val posts = snapshot.documents.mapNotNull { doc ->
                    val post = doc.toObject(Post::class.java)
                    post?.let {
                        // Case-insensitive comparison
                        if (it.hub.equals(hubName, ignoreCase = true)) {
                            Thread(
                                id = it.id,
                                title = it.title,
                                body = it.body,
                                hub = it.hub,
                                imageBase64 = it.imageBase64
                            )
                        } else null
                    }
                }
                hubPosts = posts
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
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
                    text = hubName.uppercase(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    hubPosts.isEmpty() -> {
                        Text(
                            text = "No Posts in this hub yet",
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.padding(all = 20.dp)
                        ) {
                            items(hubPosts) { thread ->
                                ThreadCard(thread, onClick = { onClick(thread) })
                            }
                        }
                    }
                }
            }
        }
    }
}