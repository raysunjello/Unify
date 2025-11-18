package com.cs407.unify.ui.components.threads

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
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
