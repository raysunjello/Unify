package com.cs407.unify.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.unify.R
import com.cs407.unify.data.Post
import com.cs407.unify.data.UserState
import com.cs407.unify.ui.components.UnifyBottomBar
import com.cs407.unify.ui.components.BottomTab
import com.cs407.unify.ui.components.threads.Thread
import com.cs407.unify.ui.components.threads.ThreadStore
import java.util.UUID
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


@Composable
fun PostPage(
    userState: UserState,
    onNavigateToMainFeedPage: () -> Unit,
    onNavigateToMarketPage: () -> Unit,
    onNavigateToProfilePage: () -> Unit,
    onNavigateToSearchPage: () -> Unit
) {
    var postTitle by remember { mutableStateOf("") }
    var hub by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    var addImage by remember { mutableStateOf(false) }
    var postAnon by remember { mutableStateOf(false) }
    var context = LocalContext.current

    // wrapped in box in order for current bottomnavbar implementation to work
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            //logo
            Image(
                painter = painterResource(id = R.drawable.unify_logo),
                contentDescription = "Unify Logo",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 16.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.FillBounds,
                colorFilter = null
            )

            //post title field
            TextField(
                value = postTitle,
                onValueChange = { postTitle = it },
                placeholder = {
                    Text(
                        text = "Post Title...",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE8E8E8),
                    unfocusedContainerColor = Color(0xFFE8E8E8),
                    disabledContainerColor = Color(0xFFE8E8E8),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            //hub field
            TextField(
                value = hub,
                onValueChange = { hub = it },
                placeholder = {
                    Text(
                        text = "Hub...",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE8E8E8),
                    unfocusedContainerColor = Color(0xFFE8E8E8),
                    disabledContainerColor = Color(0xFFE8E8E8),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            //body field
            TextField(
                value = body,
                onValueChange = { body = it },
                placeholder = {
                    Text(
                        text = "Body...",
                        color = Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE8E8E8),
                    unfocusedContainerColor = Color(0xFFE8E8E8),
                    disabledContainerColor = Color(0xFFE8E8E8),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            //TODO : add image toggle + camera feature
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add Image?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                IconButton(
                    onClick = {
                        // TODO: Implement image picker/camera
                        Toast.makeText(
                            context,
                            "TODO",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = Color.LightGray,
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Image",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            //post anonymous toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Post Anon?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Switch(
                    checked = postAnon,
                    onCheckedChange = { postAnon = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color.Gray,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            //POST button
            Button(
                onClick = {
                    if (postTitle.isBlank() || body.isBlank() || hub.isBlank()) {
                        Toast.makeText(
                            context,
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        if (!userState.isLoggedIn || userState.uid.isBlank()) {
                            Toast.makeText(
                                context,
                                "You must be logged in to post.",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }
                    }
                    val db = FirebaseFirestore.getInstance()
                    val postsCollection = db.collection("posts")

                    val docRef = postsCollection.document()

                    val post = Post(
                        id = docRef.id,
                        title = postTitle,
                        body = body,
                        hub = hub,
                        isAnonymous = postAnon,
                        authorUid = userState.uid,
                        authorUsername = if (postAnon) null else userState.username,
                        authorUniversity = if (postAnon) null else userState.university,
                        createdAt = System.currentTimeMillis()
                    )

                    docRef.set(post)
                        .addOnSuccessListener {
                            postTitle = ""
                            body = ""
                            hub = ""

                            Toast.makeText(context, "Post uploaded!", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Upload failed: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                },
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = "POST",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 2.sp
                )
            }
        }
        UnifyBottomBar(
            current = BottomTab.Post,
            onHome = { onNavigateToMainFeedPage() },
            onSearch = { onNavigateToSearchPage()},
            onPost = { /* currently on post page */ },
            onMarket = { onNavigateToMarketPage() },
            onProfile = {onNavigateToProfilePage()},
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }
}
