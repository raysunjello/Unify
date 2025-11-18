package com.cs407.unify.ui.components.threads

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ThreadPage(
    thread: Thread,
    onExit: () -> Unit,
    modifier: Modifier = Modifier
) {

    var comment by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            IconButton(onClick = onExit) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "exit",
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        },
        bottomBar = { // TODO : complete comment implementation
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp, horizontal = 16.dp),
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
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // TODO : replace with REAL thread title (thread.title)
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
            // TODO : replace with REAL thread body (thread.body)
            Text(
                text = thread.body,
                fontSize = 20.sp,
                color = Color.Unspecified,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}