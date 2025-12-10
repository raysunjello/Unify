package com.cs407.unify.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.sp
import com.cs407.unify.data.Comment
import com.cs407.unify.data.Post
import com.cs407.unify.data.UserState
import com.cs407.unify.ui.components.threads.Thread
import com.cs407.unify.ui.components.threads.ThreadCard
import com.cs407.unify.ui.components.threads.ThreadStore
import com.google.firebase.firestore.FirebaseFirestore

data class CommentedPost(
    val thread: Thread,
    val lastCommentedAt: Long,
    val commentCount: Int
)

data class RepliedPost(
    val thread: Thread,
    val lastReplyAt: Long,
    val replyCount: Int
)

@Composable
fun RecentActivityPage(
    userState: UserState,
    onExit: () -> Unit,
    onClick: (Thread) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    var commentedPosts by remember { mutableStateOf<List<CommentedPost>>(emptyList()) }
    var repliedPosts by remember { mutableStateOf<List<RepliedPost>>(emptyList()) }
    var isLoadingComments by remember { mutableStateOf(true) }
    var isLoadingReplies by remember { mutableStateOf(true) }

    var commentsExpanded by remember { mutableStateOf(true) }
    var repliesExpanded by remember { mutableStateOf(true) }

    // Load posts where user has commented
    LaunchedEffect(userState.uid) {
        if (userState.uid.isBlank()) {
            isLoadingComments = false
            return@LaunchedEffect
        }

        // Get all posts
        db.collection("posts")
            .get()
            .addOnSuccessListener { postsSnapshot ->
                val postsList = mutableListOf<CommentedPost>()
                var processedPosts = 0
                val totalPosts = postsSnapshot.documents.size

                if (totalPosts == 0) {
                    isLoadingComments = false
                    return@addOnSuccessListener
                }

                postsSnapshot.documents.forEach { postDoc ->
                    val post = postDoc.toObject(Post::class.java)

                    if (post != null) {
                        // Check if user has commented on this post
                        db.collection("posts")
                            .document(postDoc.id)
                            .collection("comments")
                            .whereEqualTo("authorUid", userState.uid)
                            .get()
                            .addOnSuccessListener { commentsSnapshot ->
                                val userComments = commentsSnapshot.documents.mapNotNull {
                                    it.toObject(Comment::class.java)
                                }

                                if (userComments.isNotEmpty()) {
                                    val lastComment = userComments.maxByOrNull { it.createdAt }

                                    postsList.add(
                                        CommentedPost(
                                            thread = Thread(
                                                id = post.id.ifBlank { postDoc.id },
                                                title = post.title,
                                                body = post.body,
                                                hub = post.hub,
                                                imageBase64 = post.imageBase64,
                                                isMarketPost = post.isMarketPost,
                                                price = post.price,
                                                contactInfo = post.contactInfo
                                            ),
                                            lastCommentedAt = lastComment?.createdAt ?: 0L,
                                            commentCount = userComments.size
                                        )
                                    )
                                }

                                processedPosts++
                                if (processedPosts == totalPosts) {
                                    // Sort by most recent comment
                                    commentedPosts = postsList.sortedByDescending { it.lastCommentedAt }
                                    isLoadingComments = false
                                }
                            }
                            .addOnFailureListener {
                                processedPosts++
                                if (processedPosts == totalPosts) {
                                    commentedPosts = postsList.sortedByDescending { it.lastCommentedAt }
                                    isLoadingComments = false
                                }
                            }
                    } else {
                        processedPosts++
                        if (processedPosts == totalPosts) {
                            commentedPosts = postsList.sortedByDescending { it.lastCommentedAt }
                            isLoadingComments = false
                        }
                    }
                }
            }
            .addOnFailureListener {
                isLoadingComments = false
            }
    }

    // Load posts where others have replied to user's comments
    LaunchedEffect(userState.uid) {
        if (userState.uid.isBlank()) {
            isLoadingReplies = false
            return@LaunchedEffect
        }

        // Get all posts
        db.collection("posts")
            .get()
            .addOnSuccessListener { postsSnapshot ->
                val postsList = mutableListOf<RepliedPost>()
                var processedPosts = 0
                val totalPosts = postsSnapshot.documents.size

                if (totalPosts == 0) {
                    isLoadingReplies = false
                    return@addOnSuccessListener
                }

                postsSnapshot.documents.forEach { postDoc ->
                    val post = postDoc.toObject(Post::class.java)

                    if (post != null) {
                        // Get all comments on this post
                        db.collection("posts")
                            .document(postDoc.id)
                            .collection("comments")
                            .get()
                            .addOnSuccessListener { commentsSnapshot ->
                                val allComments = commentsSnapshot.documents.mapNotNull {
                                    it.toObject(Comment::class.java)?.copy(id = it.id)
                                }

                                // Find user's comment IDs (both top-level and replies)
                                val userCommentIds = allComments
                                    .filter { it.authorUid == userState.uid }
                                    .map { it.id }
                                    .toSet()

                                if (userCommentIds.isNotEmpty()) {
                                    // Find ONLY direct replies to user's specific comments
                                    // (not all replies on the post, only replies WHERE parentCommentId is one of user's comment IDs)
                                    val repliesToUser = allComments.filter { comment ->
                                        // Must have a parentCommentId
                                        !comment.parentCommentId.isNullOrBlank() &&
                                                // The parent must be one of the user's comments
                                                comment.parentCommentId in userCommentIds &&
                                                // Must not be the user replying to themselves
                                                comment.authorUid != userState.uid
                                    }

                                    if (repliesToUser.isNotEmpty()) {
                                        val lastReply = repliesToUser.maxByOrNull { it.createdAt }

                                        postsList.add(
                                            RepliedPost(
                                                thread = Thread(
                                                    id = post.id.ifBlank { postDoc.id },
                                                    title = post.title,
                                                    body = post.body,
                                                    hub = post.hub,
                                                    imageBase64 = post.imageBase64,
                                                    isMarketPost = post.isMarketPost,
                                                    price = post.price,
                                                    contactInfo = post.contactInfo
                                                ),
                                                lastReplyAt = lastReply?.createdAt ?: 0L,
                                                replyCount = repliesToUser.size
                                            )
                                        )
                                    }
                                }

                                processedPosts++
                                if (processedPosts == totalPosts) {
                                    // Sort by most recent reply
                                    repliedPosts = postsList.sortedByDescending { it.lastReplyAt }
                                    isLoadingReplies = false
                                }
                            }
                            .addOnFailureListener {
                                processedPosts++
                                if (processedPosts == totalPosts) {
                                    repliedPosts = postsList.sortedByDescending { it.lastReplyAt }
                                    isLoadingReplies = false
                                }
                            }
                    } else {
                        processedPosts++
                        if (processedPosts == totalPosts) {
                            repliedPosts = postsList.sortedByDescending { it.lastReplyAt }
                            isLoadingReplies = false
                        }
                    }
                }
            }
            .addOnFailureListener {
                isLoadingReplies = false
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

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Text(
                        text = "ACTIVITY",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Recent Comments Section
                item {
                    ExpandableSection(
                        title = "Recent Comments From You",
                        count = commentedPosts.size,
                        expanded = commentsExpanded,
                        onToggle = { commentsExpanded = !commentsExpanded },
                        isLoading = isLoadingComments
                    )
                }

                if (commentsExpanded) {
                    if (isLoadingComments) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    } else if (commentedPosts.isEmpty()) {
                        item {
                            Text(
                                text = "No recent comments",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Gray
                            )
                        }
                    } else {
                        items(commentedPosts) { commentedPost ->
                            CommentedPostCard(
                                commentedPost = commentedPost,
                                onClick = {
                                    ThreadStore.selectedThread = commentedPost.thread
                                    onClick(commentedPost.thread)
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // New Replies Section
                item {
                    ExpandableSection(
                        title = "New Replies To You",
                        count = repliedPosts.size,
                        expanded = repliesExpanded,
                        onToggle = { repliesExpanded = !repliesExpanded },
                        isLoading = isLoadingReplies
                    )
                }

                if (repliesExpanded) {
                    if (isLoadingReplies) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    } else if (repliedPosts.isEmpty()) {
                        item {
                            Text(
                                text = "No new replies",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Gray
                            )
                        }
                    } else {
                        items(repliedPosts) { repliedPost ->
                            RepliedPostCard(
                                repliedPost = repliedPost,
                                onClick = {
                                    ThreadStore.selectedThread = repliedPost.thread
                                    onClick(repliedPost.thread)
                                }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun ExpandableSection(
    title: String,
    count: Int,
    expanded: Boolean,
    onToggle: () -> Unit,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onToggle() },
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
            Column {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (!isLoading) {
                    Text(
                        text = "$count post${if (count != 1) "s" else ""}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = if (expanded) "Collapse" else "Expand",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun CommentedPostCard(
    commentedPost: CommentedPost,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = commentedPost.thread.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = commentedPost.thread.hub,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "You commented ${commentedPost.commentCount} time${if (commentedPost.commentCount != 1) "s" else ""}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = formatTimeAgo(commentedPost.lastCommentedAt),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun RepliedPostCard(
    repliedPost: RepliedPost,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = repliedPost.thread.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = repliedPost.thread.hub,
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${repliedPost.replyCount} new repl${if (repliedPost.replyCount != 1) "ies" else "y"}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Last reply ${formatTimeAgo(repliedPost.lastReplyAt)}",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

fun formatTimeAgo(timestamp: Long): String {
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