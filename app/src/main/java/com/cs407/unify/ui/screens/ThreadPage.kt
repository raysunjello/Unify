package com.cs407.unify.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cs407.unify.ui.components.Thread

@Composable
fun ThreadPage(onExit: () -> Unit, modifier: Modifier = Modifier) {

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
        bottomBar = { // TODO: replace with COMMENT text field??
//            Button(
//                onClick = { navBack() },
//                modifier = Modifier
//                    .height(80.dp)
//                    .fillMaxWidth()
//                    .padding(bottom = 35.dp),
//                shape = RoundedCornerShape(50)
//            ) {
//                Text(stringResource(R.string.save_button))
//            }
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // NOTE TITLE
            Text(
                text = "thread.title",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(16.dp)
            )
            HorizontalDivider(thickness = 1.dp, modifier = Modifier.padding(10.dp))
            // NOTE BODY
            Text(
                text = "thread.body",
                fontSize = 20.sp,
                color = Color.Unspecified,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}