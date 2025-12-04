package com.cs407.unify.ui.screens.explore

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cs407.unify.ui.components.threads.Thread
import com.cs407.unify.ui.components.threads.ThreadCard
import com.cs407.unify.ui.components.threads.ThreadStore

@Composable
fun HousingHubPage(onExit: () -> Unit, onClick: (Thread) -> Unit) {
    var housingHub by remember { mutableStateOf(ThreadStore.getSavedThreads()) } // TODO : change

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
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
                Icon(Icons.Default.ArrowBack, contentDescription = "Exit")
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 20.dp)
            ) {
                Text(
                    text = "HOUSING",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                when {
                    housingHub.isEmpty() -> {
                        Text(
                            text = "No Posts",
                            modifier = Modifier.padding(24.dp),
                            color = Color.Gray
                        )
                    }
                    else -> {
                        LazyColumn(
                            modifier = Modifier.padding(all = 20.dp)
                        ) {
                            items(housingHub) { thread ->
                                ThreadCard(thread, onClick = { onClick(thread) })
                            }
                        }
                    }
                }
            }
        }
    }
}