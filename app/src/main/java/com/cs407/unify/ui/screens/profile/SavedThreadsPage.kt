package com.cs407.unify.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.cs407.unify.data.UserState
import com.cs407.unify.ui.components.threads.Thread
import com.cs407.unify.ui.components.threads.ThreadCard
import com.cs407.unify.ui.components.threads.ThreadStore
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SavedThreadsPage(
    userState: UserState,
    onExit: () -> Unit,
    onClick: (Thread) -> Unit
) {
    var savedThreads by remember { mutableStateOf<List<Thread>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userState.uid) {
        if (userState.uid.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }

        val db = FirebaseFirestore.getInstance()

        // First, get all saved post IDs
        db.collection("users")
            .document(userState.uid)
            .collection("savedPosts")
            .get()
            .addOnSuccessListener { savedSnapshot ->
                val savedIds = savedSnapshot.documents.map { it.id }

                if (savedIds.isEmpty()) {
                    savedThreads = emptyList()
                    isLoading = false
                    return@addOnSuccessListener
                }

                // Update ThreadStore
                ThreadStore.savedThreadIds.clear()
                ThreadStore.savedThreadIds.addAll(savedIds)

                // Then fetch the actual posts (max 10 at a time due to Firebase limit)
                db.collection("posts")
                    .whereIn("id", savedIds.take(10))
                    .get()
                    .addOnSuccessListener { postsSnapshot ->
                        val loadedThreads = postsSnapshot.documents.mapNotNull { doc ->
                            val post = doc.toObject(Post::class.java)
                            post?.let {
                                Thread(
                                    id = it.id,
                                    title = it.title,
                                    body = it.body,
                                    hub = it.hub
                                )
                            }
                        }
                        savedThreads = loadedThreads
                        isLoading = false
                    }
                    .addOnFailureListener {
                        isLoading = false
                    }
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
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
                Icon(Icons.Default.ArrowBack, contentDescription = "Exit")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp)
            ) {
                Text(
                    text = "SAVED STUFF",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    savedThreads.isEmpty() -> {
                        Text(
                            text = "No saved posts yet",
                            modifier = Modifier.padding(24.dp),
                            color = Color.Gray
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.padding(all = 20.dp)
                        ) {
                            items(savedThreads) { thread ->
                                ThreadCard(thread, onClick = { onClick(thread) })
                            }
                        }
                    }
                }
            }
        }
    }
}