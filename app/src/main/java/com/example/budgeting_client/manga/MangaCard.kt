package com.example.budgeting_client.manga

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.core.content.ContextCompat.startActivity
import com.example.budgeting_client.R
import com.example.budgeting_client.ui.theme.BudgetingclientTheme
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaCard(
    modifier: Modifier = Modifier,
    title: String,
    chapter: Short,
    lastUpdated: Date,
    urlString: String,
) {
    val context = LocalContext.current
    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(urlString))
    val expanded = remember { mutableStateOf(false) }

    Card(
        onClick = { startActivity(context, browserIntent, null) },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(8.dp)
        ) {
            Column(Modifier.fillMaxWidth(0.2f)) {
                Surface(Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.webtoon),
                        contentDescription = "Manga Cover",
                    )
                }
            }
            Column(Modifier.fillMaxWidth(0.8f).padding(horizontal = 16.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    lineHeight = 0.em
                )
                Text(
                    text = "Chapter $chapter",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = SimpleDateFormat.getDateTimeInstance().format(lastUpdated),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Column(modifier = Modifier.fillMaxWidth().wrapContentSize(Alignment.TopEnd)) {
                IconButton(onClick = { expanded.value = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More menu")
                }
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Edit") },
                        onClick = { /* Handle edit! */ },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Sync") },
                        onClick = { /* Handle sync! */ },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun Test() {
    BudgetingclientTheme {
        MangaCard(
            title = "Test",
            chapter = 10,
            lastUpdated = Date(),
            urlString = "https://google.com"
        )
    }
}