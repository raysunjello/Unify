package com.cs407.unify.ui.screens.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.cs407.unify.data.UserState
import com.cs407.unify.data.Post
import com.cs407.unify.ui.components.threads.ThreadCard
import com.cs407.unify.ui.components.threads.Thread
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfilePagePosts(
    userState: UserState,
    onExit: () -> Unit,
    onClick: (Thread) -> Unit)
{
    val uid = userState.uid

    var threads by remember { mutableStateOf<List<Thread>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uid) {
        val db = FirebaseFirestore.getInstance()
        db.collection("posts")
            .whereEqualTo("authorUid", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                val loadedThreads = snapshot.documents.mapNotNull { doc ->
                    val post = doc.toObject(Post::class.java)
                    post?.let {
                        Thread(
                            id = it.id,
                            title = it.title,
                            body = it.body,
                            hub = it.hub,
                            imageBase64 = it.imageBase64,
                            isMarketPost = it.isMarketPost,
                            price = it.price,
                            contactInfo = it.contactInfo
                        )
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

    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            IconButton(
                onClick = { onExit() },
                modifier = Modifier.align(Alignment.TopStart).padding(horizontal = 8.dp)
                    .padding(top = 15.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Exit")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 20.dp)
            ) {
                Text(
                    text = "MY POSTS", // TODO replace w string
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                when {
                    threads.isEmpty() -> {
                        Text(
                            text = "No Posts",
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.padding(all = 20.dp)
                        ) {
                            items(threads) { thread ->
                                ThreadCard(thread, onClick = { onClick(thread) })
                            }
                        }
                    }
                }
            }
        }
    }
}