package com.cs407.unify.ui.components.threads

import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.unify.data.UserState
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.cs407.unify.data.Comment as PostComment
import com.cs407.unify.data.Post
import androidx.compose.material.icons.filled.Delete

@Composable
fun ThreadPage(
    thread: Thread,
    userState: UserState,
    onExit: () -> Unit,
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var comment by remember { mutableStateOf("") }
    var isSaved by remember { mutableStateOf(ThreadStore.isThreadSaved(thread.id)) }
    var isInCart by remember { mutableStateOf(ThreadStore.isThreadInCart(thread.id)) }
    var commentsList by remember { mutableStateOf<List<PostComment>>(emptyList()) }

    var postAuthorName by remember { mutableStateOf<String?>(thread.authorUsername) }
    var postAuthorUniversity by remember { mutableStateOf<String?>(thread.authorUniversity) }
    var postIsAnonymous by remember { mutableStateOf(thread.isAnonymous) }
    var postAuthorUid by remember { mutableStateOf<String?>(thread.authorUid) }
    val isOwner = postAuthorUid == userState.uid

    LaunchedEffect(thread.id) {
        // Load all comments, then filter for top-level ones in code
        // This handles both old comments (missing parentCommentId field) and new ones
        db.collection("posts")
            .document(thread.id)
            .collection("comments")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { snapshot ->
                // Filter to show only top-level comments (those with null, empty, or blank parentCommentId)
                commentsList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(PostComment::class.java)?.copy(id = doc.id)
                }.filter { comment ->
                    // Top-level comment if parentCommentId is null, empty, or only whitespace
                    val parentId = comment.parentCommentId
                    parentId.isNullOrBlank()
                }
            }

        db.collection("posts")
            .document(thread.id)
            .get()
            .addOnSuccessListener { doc ->
                val post = doc.toObject(Post::class.java)
                if (post != null) {
                    postIsAnonymous = post.isAnonymous
                    postAuthorName = post.authorUsername
                    postAuthorUniversity = post.authorUniversity
                    postAuthorUid = post.authorUid
                }
            }

    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onExit) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "exit"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Show cart button for market posts, save button for regular posts
                if (thread.isMarketPost) {
                    // Cart button for market posts
                    IconButton(
                        onClick = {
                            val userCartRef = db.collection("users")
                                .document(userState.uid)
                                .collection("cart")
                                .document(thread.id)

                            if (isInCart) {
                                // Remove from cart
                                userCartRef.delete()
                                    .addOnSuccessListener {
                                        ThreadStore.cartThreadIds.remove(thread.id)
                                        isInCart = false
                                        Toast.makeText(context, "Removed from cart", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Failed to remove: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                // Add to cart
                                userCartRef.set(mapOf(
                                    "postId" to thread.id,
                                    "addedAt" to System.currentTimeMillis()
                                ))
                                    .addOnSuccessListener {
                                        ThreadStore.cartThreadIds.add(thread.id)
                                        isInCart = true
                                        Toast.makeText(context, "Added to cart!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Failed to add: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = if (isInCart) "Remove from cart" else "Add to cart",
                            tint = if (isInCart) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    // Save button for regular posts
                    IconButton(
                        onClick = {
                            val userSavedRef = db.collection("users")
                                .document(userState.uid)
                                .collection("savedPosts")
                                .document(thread.id)

                            if (isSaved) {
                                // Unsave
                                userSavedRef.delete()
                                    .addOnSuccessListener {
                                        ThreadStore.savedThreadIds.remove(thread.id)
                                        isSaved = false
                                        Toast.makeText(context, "Post unsaved", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Failed to unsave: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                // Save
                                userSavedRef.set(mapOf(
                                    "postId" to thread.id,
                                    "savedAt" to System.currentTimeMillis()
                                ))
                                    .addOnSuccessListener {
                                        ThreadStore.savedThreadIds.add(thread.id)
                                        isSaved = true
                                        Toast.makeText(context, "Post saved!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(context, "Failed to save: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isSaved) "Unsave" else "Save",
                            tint = if (isSaved) Color.Red else Color.Gray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                if (isOwner) {
                    IconButton(
                        onClick = {
                            db.collection("posts")
                                .document(thread.id)
                                .delete()
                                .addOnSuccessListener {
                                    ThreadStore.threads.remove(thread.id)
                                    ThreadStore.savedThreadIds.remove(thread.id)
                                    ThreadStore.cartThreadIds.remove(thread.id)

                                    Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                                    onExit()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        context,
                                        "Failed to delete: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete post",
                            tint = Color.Red,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp),
                    value = comment,
                    onValueChange = { comment = it },
                    placeholder = {
                        Text(
                            text = "Comment...",
                            color = Color.Gray
                        )
                    },
                    shape = RoundedCornerShape(50)
                )

                // Send button
                IconButton(
                    onClick = {
                        if (comment.isBlank()) return@IconButton

                        val newCommentRef = db.collection("posts")
                            .document(thread.id)
                            .collection("comments")
                            .document()

                        val newComment = PostComment(
                            id = newCommentRef.id,
                            postId = thread.id,
                            text = comment,
                            authorUid = userState.uid,
                            authorUsername = userState.username.ifBlank { null },
                            authorUniversity = userState.university.ifBlank { null },
                            createdAt = System.currentTimeMillis()
                        )

                        newCommentRef
                            .set(newComment)
                            .addOnSuccessListener {
                                commentsList = commentsList + newComment
                                comment = ""

                                Toast.makeText(
                                    context,
                                    "Comment posted!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(
                                    context,
                                    "Failed to post comment: ${e.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    },
                    enabled = comment.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send comment",
                        tint = if (comment.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    ) { innerPadding ->
        // Convert Base64 to Bitmap if image exists
        val bitmap = remember(thread.imageBase64) {
            if (thread.imageBase64 != null) {
                try {
                    val bytes = Base64.decode(thread.imageBase64, Base64.DEFAULT)
                    BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Thread content
            Text(
                text = thread.title,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Hub: ${thread.hub}",
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            Text(
                text = buildString {
                    append(
                        when {
                            postIsAnonymous -> "Anonymous"
                            !postAuthorName.isNullOrBlank() -> postAuthorName
                            else -> "Unknown user"
                        }
                    )
                    postAuthorUniversity?.takeIf { it.isNotBlank() }?.let {
                        append(" • ")
                        append(it)
                    }
                },
                fontSize = 16.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )


            // Display market-specific info if it's a market post
            if (thread.isMarketPost) {
                thread.price?.let { price ->
                    Text(
                        text = "Price: $price",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }

                thread.contactInfo?.let { contact ->
                    Text(
                        text = "Contact: $contact",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }

            // Display image if exists
            bitmap?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.FillWidth
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(10.dp))
            Text(
                text = thread.body,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(16.dp)
            )

            // Comments section
            if (commentsList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Comments (${commentsList.size})",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))

                commentsList.forEach { comment ->
                    CommentCard(
                        comment = comment,
                        userState = userState,
                        postId = thread.id
                    )
                }
            }
        }
    }
}

@Composable
fun CommentCard(
    comment: PostComment,
    userState: UserState,
    postId: String,
    isReply: Boolean = false
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var showReplyInput by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    var showReplies by remember { mutableStateOf(false) }
    var replies by remember { mutableStateOf<List<PostComment>>(emptyList()) }
    var repliesLoaded by remember { mutableStateOf(false) }
    var replyCount by remember { mutableStateOf(0) }

    // Check if this comment has replies (run once on mount)
    LaunchedEffect(comment.id) {
        db.collection("posts")
            .document(postId)
            .collection("comments")
            .get()
            .addOnSuccessListener { snapshot ->
                // Count replies for this comment
                val replyDocs = snapshot.documents.filter { doc ->
                    val parentId = doc.getString("parentCommentId")
                    parentId == comment.id
                }
                replyCount = replyDocs.size
            }
    }

    // Load replies for this comment when user expands them
    LaunchedEffect(comment.id, showReplies) {
        if (showReplies && !repliesLoaded) {
            db.collection("posts")
                .document(postId)
                .collection("comments")
                .orderBy("createdAt")
                .get()
                .addOnSuccessListener { snapshot ->
                    // Filter for replies to this specific comment
                    replies = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(PostComment::class.java)?.copy(id = doc.id)
                    }.filter { reply ->
                        reply.parentCommentId == comment.id
                    }
                    repliesLoaded = true
                }
        }
    }

    Column {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (isReply) 32.dp else 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 4.dp
                ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isReply) Color(0xFFE8E8E8) else Color(0xFFF5F5F5)
            )
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = comment.authorUsername ?: "Unknown user",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.text,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTimestamp(comment.createdAt),
                        fontSize = 12.sp,
                        color = Color.Gray
                    )

                    if (!isReply) {
                        androidx.compose.material3.TextButton(
                            onClick = { showReplyInput = !showReplyInput },
                            modifier = Modifier
                                .padding(0.dp)
                                .height(24.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = "Reply",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Reply input field
        if (showReplyInput && !isReply) {
            Column(
                modifier = Modifier.padding(start = 32.dp, end = 16.dp, top = 4.dp)
            ) {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    placeholder = { Text("Write a reply...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 8.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
                ) {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            showReplyInput = false
                            replyText = ""
                        }
                    ) {
                        Text("Cancel")
                    }

                    androidx.compose.material3.Button(
                        onClick = {
                            if (replyText.isBlank()) return@Button

                            val newReplyRef = db.collection("posts")
                                .document(postId)
                                .collection("comments")
                                .document()

                            val newReply = PostComment(
                                id = newReplyRef.id,
                                postId = postId,
                                text = replyText,
                                authorUid = userState.uid,
                                authorUsername = userState.username.ifBlank { null },
                                authorUniversity = userState.university.ifBlank { null },
                                createdAt = System.currentTimeMillis(),
                                parentCommentId = comment.id
                            )

                            newReplyRef.set(newReply)
                                .addOnSuccessListener {
                                    replies = replies + newReply
                                    replyCount = replies.size
                                    showReplies = true
                                    repliesLoaded = true
                                    replyText = ""
                                    showReplyInput = false
                                    Toast.makeText(context, "Reply posted!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        context,
                                        "Failed to post reply: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        },
                        enabled = replyText.isNotBlank(),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Post Reply")
                    }
                }
            }
        }

        // View Replies button and replies list
        if (!isReply && replyCount > 0) {
            androidx.compose.material3.TextButton(
                onClick = { showReplies = !showReplies },
                modifier = Modifier.padding(start = 32.dp, top = 4.dp)
            ) {
                Text(
                    text = if (showReplies) "Hide Replies ($replyCount)" else "View Replies ($replyCount)",
                    fontSize = 12.sp
                )
            }

            if (showReplies) {
                replies.forEach { reply ->
                    CommentCard(
                        comment = reply,
                        userState = userState,
                        postId = postId,
                        isReply = true
                    )
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}