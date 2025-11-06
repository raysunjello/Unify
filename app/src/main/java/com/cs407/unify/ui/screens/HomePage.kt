package com.cs407.unify.ui.screens.HomePage

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HomePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 48.dp)
    ) {

        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Search hubs...") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50.dp))
                .background(Color(0xFFD3D3D3))
        )

        Spacer(modifier = Modifier.height(32.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            items(3) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFBDBDBD)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFDCDCDC))
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Suggested Post Title",
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                                maxLines = 3
                            )
                        }
                    }
                }
            }
        }
    }
}