package com.example.budgeting_client.ui.manga

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.example.budgeting_client.R
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MangaCard(
    modifier: Modifier = Modifier,
    title: String, // The name of the crawler
    chapter: Short?,
    lastUpdated: Date?,  // The date this chapter was catalogued
    lastRemoteSync: Date?, // The latest date the crawler successfully retrieved data from remote
    urlString: String, // The remote source
    isRead: Boolean,
    onEditClick: () -> Unit
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
                    // TODO: Add param for image based on LatestMangaUpdate otherwise use the Crawler.adapter
                    Image(
                        modifier = Modifier.fillMaxSize(),
                        painter = painterResource(id = R.drawable.webtoon),
                        contentDescription = "Manga Cover",
                    )
                    Row {
                        if (isRead) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        if (lastRemoteSync != null && (System.currentTimeMillis() - lastRemoteSync.time) > (1000 * 60 * 60 * 24 * 3)) {
                            // Last remote sync was more than 3 days ago
                            Icon(ImageVector.vectorResource(id = R.drawable.exclamation), contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            Column(Modifier.fillMaxWidth(0.8f).padding(horizontal = 16.dp)) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )

                if (chapter != null) {
                    Text(
                        text = "Chapter $chapter",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    Text(
                        text = "No chapters recorded",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                if (lastUpdated != null) {
                    Text(
                        text = SimpleDateFormat.getDateTimeInstance().format(lastUpdated),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
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
                        onClick = { onEditClick() },
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