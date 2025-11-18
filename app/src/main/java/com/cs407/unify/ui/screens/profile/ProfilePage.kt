package com.cs407.unify.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cs407.unify.R
import com.cs407.unify.ui.components.BottomTab
import com.cs407.unify.ui.components.UnifyBottomBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.cs407.unify.data.UserProfile

@Preview(showBackground = true)
@Composable
fun PreviewProfilePage() {
    ProfilePage(
        onNavigateToPostPage = {},
        onNavigateToMarketPage = {},
        onNavigateToMainFeedPage = {},
        onNavigateToSearchPage = {},
        onNavigateToMyPosts = {}
    )
}
@Composable
fun ProfilePage(
    onNavigateToPostPage: () -> Unit,
    onNavigateToMarketPage: () -> Unit,
    onNavigateToMainFeedPage: () -> Unit,
    onNavigateToSearchPage: () -> Unit,
    onNavigateToMyPosts: () -> Unit,
) {
//    val drawerState = rememberDrawerState(DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
    val auth = FirebaseAuth.getInstance()
    val uid = auth.currentUser?.uid

    var username by remember { mutableStateOf("Loading...") }
    var university by remember { mutableStateOf("Loading...") }

    LaunchedEffect(uid) {
        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val profile = snapshot.toObject(UserProfile::class.java)

                    username = profile?.username ?: "Error loading user"
                    university = profile?.university ?: "Error loading user"

                }
                .addOnFailureListener {
                    username = "Error loading user"
                }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.TopCenter)
        ) {

            Spacer(Modifier.height(24.dp))

            Image(
                painter = painterResource(id = R.drawable.unify_logo),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )

            Text(
                text = username, // TODO replace w string
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 16.dp),
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                text = university, // TODO replace w string
                modifier = Modifier.padding(vertical = 8.dp),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(24.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(12.dp)
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = "MY POSTS", // TODO replace w string
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(25.dp).clickable(onClick = onNavigateToMyPosts)
                    )
                }
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = "SAVED STUFF", // TODO replace w string
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(25.dp)
                    )
                }
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
                ) {
                    Text(
                        text = "RECENT ACTIVITY", // TODO replace w string
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(25.dp)
                    )
                    Spacer(Modifier.height(100.dp))
                }

            }

        }

        UnifyBottomBar(
            current = BottomTab.Profile,
            onHome = {onNavigateToMainFeedPage()},
            onSearch = { onNavigateToSearchPage() },
            onPost = { onNavigateToPostPage() },
            onMarket = { onNavigateToMarketPage() },
            onProfile = { /* currently on profile page */},
            modifier = Modifier.align(Alignment.BottomCenter)
        )

    }

}
