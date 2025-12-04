package com.cs407.unify.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import com.cs407.unify.R
import com.cs407.unify.data.Hub
import com.cs407.unify.data.Post
import com.cs407.unify.data.UserState
import com.cs407.unify.ui.components.UnifyBottomBar
import com.cs407.unify.ui.components.BottomTab
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.File


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
    var postAnon by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }
    var showHubDropdown by remember { mutableStateOf(false) }
    var hubSuggestions by remember { mutableStateOf<List<String>>(emptyList()) }

    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    // Fetch hub suggestions based on user input
    LaunchedEffect(hub) {
        if (hub.isNotEmpty()) {
            db.collection("hubs")
                .orderBy("name")
                .startAt(hub.uppercase())
                .endAt(hub.uppercase() + "\uf8ff")
                .get()
                .addOnSuccessListener { snapshot ->
                    hubSuggestions = snapshot.documents.mapNotNull { doc ->
                        doc.getString("name")
                    }
                    showHubDropdown = hubSuggestions.isNotEmpty()
                }
                .addOnFailureListener {
                    hubSuggestions = emptyList()
                    showHubDropdown = false
                }
        } else {
            hubSuggestions = emptyList()
            showHubDropdown = false
        }
    }

    // Create a temporary file for camera capture
    val tempImageFile = remember {
        File.createTempFile(
            "image_${System.currentTimeMillis()}",
            ".jpg",
            context.cacheDir
        ).apply {
            deleteOnExit()
        }
    }

    val tempImageUri = remember {
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempImageFile
        )
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = tempImageUri
        }
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
        }
    }

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(tempImageUri)
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Gallery permission launcher
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "Storage permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to convert URI to Base64
    fun uriToBase64(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Compress bitmap to reduce size
            val outputStream = ByteArrayOutputStream()
            var quality = 80
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

            // If still too large, reduce quality further
            while (outputStream.size() > 1024 * 1024 && quality > 20) {
                outputStream.reset()
                quality -= 10
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            }

            val byteArray = outputStream.toByteArray()
            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Image source selection dialog
    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Select Image Source") },
            text = { Text("Choose where to get your image from") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        // Check camera permission
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.CAMERA
                            ) -> {
                                cameraLauncher.launch(tempImageUri)
                            }
                            else -> {
                                cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        }
                    }
                ) {
                    Text("Camera")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showImageSourceDialog = false
                        when (PackageManager.PERMISSION_GRANTED) {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.READ_MEDIA_IMAGES
                            ) -> {
                                galleryLauncher.launch("image/*")
                            }
                            else -> {
                                galleryPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                            }
                        }
                    }
                ) {
                    Text("Library")
                }
            }
        )
    }

    // Main UI
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

            // Display selected image or default logo
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .size(300.dp)
                        .padding(bottom = 16.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
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
            }

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

            //hub field with dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
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

                // Dropdown menu for hub suggestions
                if (showHubDropdown && hubSuggestions.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 60.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column {
                            hubSuggestions.take(5).forEach { suggestion ->
                                Text(
                                    text = suggestion,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            hub = suggestion
                                            showHubDropdown = false
                                        }
                                        .padding(12.dp),
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                if (suggestion != hubSuggestions.take(5).last()) {
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }

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

            // Add image button
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
                        if (imageUri != null) {
                            Toast.makeText(
                                context,
                                "Image already added!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            showImageSourceDialog = true
                        }
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
                        return@Button
                    }

                    if (!userState.isLoggedIn || userState.uid.isBlank()) {
                        Toast.makeText(
                            context,
                            "You must be logged in to post.",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    isUploading = true

                    // Convert image to Base64 if present
                    val imageBase64 = imageUri?.let { uri ->
                        uriToBase64(uri)
                    }

                    if (imageUri != null && imageBase64 == null) {
                        isUploading = false
                        Toast.makeText(
                            context,
                            "Failed to process image",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    // Check if hub exists, if not create it
                    db.collection("hubs")
                        .whereEqualTo("name", hub.trim().uppercase())
                        .get()
                        .addOnSuccessListener { hubSnapshot ->
                            if (hubSnapshot.isEmpty) {
                                // Hub doesn't exist, create it
                                val newHub = Hub(
                                    name = hub.trim().uppercase(),
                                )
                                db.collection("hubs").add(newHub)
                            }

                            // Proceed with creating the post
                            val postsCollection = db.collection("posts")
                            val docRef = postsCollection.document()

                            val post = Post(
                                id = docRef.id,
                                title = postTitle,
                                body = body,
                                hub = hub.trim().uppercase(),
                                isAnonymous = postAnon,
                                authorUid = userState.uid,
                                authorUsername = if (postAnon) null else userState.username,
                                authorUniversity = if (postAnon) null else userState.university,
                                createdAt = System.currentTimeMillis(),
                                imageBase64 = imageBase64
                            )

                            docRef.set(post)
                                .addOnSuccessListener {
                                    isUploading = false
                                    postTitle = ""
                                    body = ""
                                    hub = ""
                                    imageUri = null
                                    Toast.makeText(context, "Post uploaded!", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener { e ->
                                    isUploading = false
                                    Toast.makeText(
                                        context,
                                        "Upload failed: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                        .addOnFailureListener { e ->
                            isUploading = false
                            Toast.makeText(
                                context,
                                "Failed to check hub: ${e.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                },
                enabled = !isUploading,
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = if (isUploading) "UPLOADING..." else "POST",
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