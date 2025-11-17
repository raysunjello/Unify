package com.cs407.unify.ui.components

import android.os.Build.VERSION_CODES.R
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .combinedClickable(onClick = { onClick() }),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Unspecified, // TODO
        )
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = thread.title, fontWeight = FontWeight.Bold
            ) // TITLE
            Text(
                text = thread.body, fontWeight = FontWeight.Thin
            ) // ABSTRACT / BODY TODO()
        }
    }
}
