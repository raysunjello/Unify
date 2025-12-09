package com.cs407.unify.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.unify.data.Hub
import com.cs407.unify.data.UserState
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun MyHubsPage(
    userState: UserState,
    onExit: () -> Unit,
    onHubClick: (String) -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var myHubs by remember { mutableStateOf<List<Hub>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var hubToDelete by remember { mutableStateOf<Hub?>(null) }
    var isDeleting by remember { mutableStateOf(false) }

    // Load user's hubs
    LaunchedEffect(userState.uid) {
        if (userState.uid.isBlank()) {
            isLoading = false
            return@LaunchedEffect
        }

        db.collection("hubs")
            .whereEqualTo("creatorUid", userState.uid)
            .get()
            .addOnSuccessListener { snapshot ->
                myHubs = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Hub::class.java)
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
                Toast.makeText(context, "Failed to load hubs", Toast.LENGTH_SHORT).show()
            }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && hubToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    showDeleteDialog = false
                    hubToDelete = null
                }
            },
            title = { Text("Delete Hub?") },
            text = {
                Text(
                    "Are you sure you want to delete \"${hubToDelete?.name}\"? " +
                            "This will permanently delete the hub and ALL posts and comments in it. " +
                            "This action cannot be undone."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        isDeleting = true
                        val hub = hubToDelete ?: return@Button

                        // Step 1: Get all posts in this hub
                        db.collection("posts")
                            .whereEqualTo("hub", hub.name)
                            .get()
                            .addOnSuccessListener { postsSnapshot ->
                                val postIds = postsSnapshot.documents.map { it.id }

                                if (postIds.isEmpty()) {
                                    // No posts, just delete the hub
                                    db.collection("hubs").document(hub.id)
                                        .delete()
                                        .addOnSuccessListener {
                                            myHubs = myHubs.filter { it.id != hub.id }
                                            isDeleting = false
                                            showDeleteDialog = false
                                            hubToDelete = null
                                            Toast.makeText(
                                                context,
                                                "Hub deleted successfully",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .addOnFailureListener { e ->
                                            isDeleting = false
                                            Toast.makeText(
                                                context,
                                                "Failed to delete hub: ${e.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                } else {
                                    // Delete all posts and their comments, then delete the hub
                                    var deletedPosts = 0
                                    val totalPosts = postIds.size

                                    postIds.forEach { postId ->
                                        val postRef = db.collection("posts").document(postId)

                                        // Delete all comments for this post
                                        postRef.collection("comments")
                                            .get()
                                            .addOnSuccessListener { commentsSnapshot ->
                                                val batch = db.batch()

                                                // Add all comment deletions to batch
                                                commentsSnapshot.documents.forEach { commentDoc ->
                                                    batch.delete(commentDoc.reference)
                                                }

                                                // Add post deletion to batch
                                                batch.delete(postRef)

                                                // Commit batch
                                                batch.commit()
                                                    .addOnSuccessListener {
                                                        deletedPosts++

                                                        // If all posts deleted, delete the hub
                                                        if (deletedPosts == totalPosts) {
                                                            db.collection("hubs").document(hub.id)
                                                                .delete()
                                                                .addOnSuccessListener {
                                                                    myHubs = myHubs.filter { it.id != hub.id }
                                                                    isDeleting = false
                                                                    showDeleteDialog = false
                                                                    hubToDelete = null
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Hub and all posts deleted successfully",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                                .addOnFailureListener { e ->
                                                                    isDeleting = false
                                                                    Toast.makeText(
                                                                        context,
                                                                        "Posts deleted but failed to delete hub: ${e.message}",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                }
                                                        }
                                                    }
                                                    .addOnFailureListener { e ->
                                                        isDeleting = false
                                                        Toast.makeText(
                                                            context,
                                                            "Failed to delete post: ${e.message}",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                            }
                                            .addOnFailureListener { e ->
                                                isDeleting = false
                                                Toast.makeText(
                                                    context,
                                                    "Failed to load comments: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                }
                            }
                            .addOnFailureListener { e ->
                                isDeleting = false
                                Toast.makeText(
                                    context,
                                    "Failed to load posts: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    },
                    enabled = !isDeleting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    if (isDeleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Delete")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        hubToDelete = null
                    },
                    enabled = !isDeleting
                ) {
                    Text("Cancel")
                }
            }
        )
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
                    text = "MY HUBS",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                    myHubs.isEmpty() -> {
                        Text(
                            text = "You haven't created any hubs yet",
                            modifier = Modifier.padding(24.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(myHubs) { hub ->
                                HubCard(
                                    hub = hub,
                                    onClick = { onHubClick(hub.name) },
                                    onDelete = {
                                        hubToDelete = hub
                                        showDeleteDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HubCard(
    hub: Hub,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = hub.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Created ${formatDate(hub.createdAt)}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete Hub",
                    tint = Color.Red,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

fun formatDate(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        days > 30 -> {
            val months = days / 30
            "${months}mo ago"
        }
        days > 0 -> "${days}d ago"
        hours > 0 -> "${hours}h ago"
        minutes > 0 -> "${minutes}m ago"
        else -> "just now"
    }
}