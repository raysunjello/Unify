package com.cs407.unify.ui.components.threads

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Locale

// thread : data object
// onClick : to Thread Page
// onExit : back to previous page

@Composable
fun ThreadCard(
    thread: Thread, onClick: () -> Unit
) {

    val pattern = "yyyy-MM-dd HH:mm"
    val dateFormatter = SimpleDateFormat(pattern, Locale.getDefault())

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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 12.dp)
            .combinedClickable(onClick = { onClick() }),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Unspecified, // TODO
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Display image at top if exists
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Post Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 10.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            // Row for title and hub
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = thread.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(5.dp)
                        .weight(1f)
                )

                Text(
                    text = thread.hub,
                    fontWeight = FontWeight.Normal,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    modifier = Modifier.padding(5.dp)
                )
            }

            // Abstract/Body - single line only
            Text(
                text = thread.body,
                fontWeight = FontWeight.Thin,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1, // Only show 1 line
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .padding(bottom = 10.dp)
            )
        }
    }
}