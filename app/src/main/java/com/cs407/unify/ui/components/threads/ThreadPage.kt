package com.cs407.unify.ui.components.threads

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Send
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
import androidx.compose.ui.graphics.Color
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

@Composable
fun ThreadPage(
    thread: Thread,
    userState: UserState,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var comment by remember { mutableStateOf("") }
    var isSaved by remember { mutableStateOf(ThreadStore.isThreadSaved(thread.id)) }
    var commentsList by remember { mutableStateOf<List<PostComment>>(emptyList()) }

    LaunchedEffect(thread.id) {
        db.collection("posts")
            .document(thread.id)
            .collection("comments")
            .orderBy("createdAt")
            .get()
            .addOnSuccessListener { snapshot ->
                commentsList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(PostComment::class.java)?.copy(id = doc.id)
                }
            }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onExit) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "exit"
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Save/Unsave button
                IconButton(
                    onClick = {
                        ThreadStore.toggleSaved(thread.id)
                        isSaved = !isSaved

                        Toast.makeText(
                            context,
                            if (isSaved) "Post saved!" else "Post unsaved",
                            Toast.LENGTH_SHORT
                        ).show()
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
                        imageVector = Icons.Filled.Send,
                        contentDescription = "Send comment",
                        tint = if (comment.isNotBlank()) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                }
            }
        }
    ) { innerPadding ->
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
                    CommentCard(comment = comment)
                }
            }
        }
    }
}

@Composable
fun CommentCard(comment: PostComment) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = comment.text,
                fontSize = 16.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTimestamp(comment.createdAt),
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}