package com.cs407.unify.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.unify.data.Post
import com.cs407.unify.data.UserState
import com.cs407.unify.ui.components.threads.Thread
import com.cs407.unify.ui.components.threads.ThreadCard
import com.cs407.unify.ui.components.threads.ThreadStore
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CartPage(
    userState: UserState,
    onExit: () -> Unit,
    onClick: (Thread) -> Unit
) {
    var cartThreads by remember { mutableStateOf<List<Thread>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var refreshTrigger by remember { mutableStateOf(0) }
    val context = LocalContext.current

    LaunchedEffect(userState.uid, refreshTrigger) {
        if (userState.uid.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }

        val db = FirebaseFirestore.getInstance()

        // First, get all cart post IDs
        db.collection("users")
            .document(userState.uid)
            .collection("cart")
            .get()
            .addOnSuccessListener { cartSnapshot ->
                val cartIds = cartSnapshot.documents.map { it.id }

                if (cartIds.isEmpty()) {
                    cartThreads = emptyList()
                    isLoading = false
                    return@addOnSuccessListener
                }

                // Update ThreadStore
                ThreadStore.cartThreadIds.clear()
                ThreadStore.cartThreadIds.addAll(cartIds)

                // Then fetch the actual posts (max 10 at a time due to Firebase limit)
                db.collection("posts")
                    .whereIn("id", cartIds.take(10))
                    .get()
                    .addOnSuccessListener { postsSnapshot ->
                        val loadedThreads = postsSnapshot.documents.mapNotNull { doc ->
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
                        cartThreads = loadedThreads
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

    fun removeFromCart(threadId: String) {
        val db = FirebaseFirestore.getInstance()

        // Remove from Firebase
        db.collection("users")
            .document(userState.uid)
            .collection("cart")
            .document(threadId)
            .delete()
            .addOnSuccessListener {
                // Remove from ThreadStore
                ThreadStore.cartThreadIds.remove(threadId)

                // Show toast
                Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show()

                // Trigger refresh
                refreshTrigger++
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to remove from cart", Toast.LENGTH_SHORT).show()
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
                    text = "MY CART",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    cartThreads.isEmpty() -> {
                        Text(
                            text = "No items in cart yet",
                            modifier = Modifier.padding(24.dp),
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.padding(all = 20.dp)
                        ) {
                            items(cartThreads) { thread ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Thread Card takes most of the space
                                    Box(modifier = Modifier.weight(1f)) {
                                        ThreadCard(thread, onClick = { onClick(thread) })
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    // Delete button
                                    IconButton(
                                        onClick = { removeFromCart(thread.id) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove from cart",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}