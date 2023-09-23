package com.example.budgeting_client.ui.manga

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Favorite
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.signature.ObjectKey
import com.example.budgeting_client.R
import com.example.budgeting_client.SmithersApplication
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

@SuppressLint("SimpleDateFormat")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun MangaCard(
    modifier: Modifier = Modifier,
    title: String, // The name of the crawler
    imageUrl: String,
    imageSignature: String?,
    chapter: Float?,
    chapterName: String?,
    lastUpdated: Date?,  // The date this chapter was catalogued
    latestCrawlSuccess: Boolean?, // The latest date the crawler successfully retrieved data from remote
    isRead: Boolean,
    isFavourite: Boolean,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onSyncClick: () -> Unit,
    onReadClick: () -> Unit,
    onFavouriteClick: () -> Unit
) {
    val expanded = remember { mutableStateOf(false) }

    Card(
        onClick = { onCardClick() },
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Column(Modifier.fillMaxWidth(0.2f)) {
                Surface(Modifier.fillMaxSize()) {
                    // TODO: Check why image is not re-rendered when uiState is changed
                    // TODO: Handle the case where the request for the image fails
                    GlideImage(
                        modifier = Modifier.fillMaxSize(),
                        model = imageUrl,
                        contentDescription = "Manga Cover",
                    ) {
                        if (imageSignature != null) {
                            it.signature(ObjectKey(imageSignature))
                        }

                        // TODO: Use app avatar for image placeholder
                        it.placeholder(R.drawable.webtoon)
                    }
                    Row {
                        if (isRead) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                        if (latestCrawlSuccess != null && !latestCrawlSuccess) {
                            // Last attempt at crawling ended in failure
                            Icon(ImageVector.vectorResource(id = R.drawable.exclamation), contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
            Column(
                Modifier
                    .fillMaxWidth(0.8f)
                    .padding(horizontal = 16.dp)) {
                Text(
                    text = title,
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 2
                )

                if (chapterName != null) {
                    Text(
                        text = chapterName,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (chapter != null) {
                    Text(
                        text = stringResource(id = R.string.chapter) + " $chapter",
                        style = MaterialTheme.typography.bodySmall,
                    )
                } else {
                    Text(
                        text = stringResource(id = R.string.no_recorded_chapters),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                if (lastUpdated != null) {
                    val dateFormatter = SimpleDateFormat("MMM dd, yyyy hh:mm a z")
                    dateFormatter.timeZone = TimeZone.getDefault()
                    Text(
                        text = dateFormatter.format(lastUpdated),
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            Column(modifier = Modifier
                .fillMaxWidth(0.5f)
                .wrapContentSize(Alignment.TopEnd)
            ) {
                IconButton(onClick = onFavouriteClick) {
                    Icon(
                        imageVector = if (isFavourite)
                            Icons.Default.Favorite
                        else
                            ImageVector.vectorResource(id = R.drawable.empty_favourite),
                        contentDescription = if (isFavourite)
                            stringResource(id = R.string.unfavourite)
                        else
                            stringResource(id = R.string.favourite)
                    )
                }
            }
            Column(modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize(Alignment.TopEnd)) {
                IconButton(onClick = { expanded.value = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More menu")
                }
                DropdownMenu(
                    expanded = expanded.value,
                    onDismissRequest = { expanded.value = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.edit)) },
                        onClick = { onEditClick() },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(id = R.string.sync)) },
                        onClick = {
                            expanded.value = false
                            onSyncClick()
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = null
                            )
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(if (isRead)
                            stringResource(id = R.string.markUnread)
                        else
                            stringResource(id = R.string.markRead)
                        )},
                        onClick = {
                            expanded.value = false
                            onReadClick()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isRead)
                                    ImageVector.vectorResource(id = R.drawable.visibility_off)
                                else
                                    ImageVector.vectorResource(id = R.drawable.visibility),
                                contentDescription = null
                            )
                        }
                    )
//                    DropdownMenuItem(
//                        text = { Text(if (isFavourite)
//                            stringResource(id = R.string.unfavourite)
//                        else
//                            stringResource(id = R.string.favourite)
//                        )},
//                        onClick = {
//                            expanded.value = false
//                            onFavouriteClick()
//                        },
//                        leadingIcon = {
//                            Icon(
//                                imageVector = if (isFavourite)
//                                    Icons.Outlined.Favorite
//                                else
//                                    Icons.Default.Favorite,
//                                contentDescription = null
//                            )
//                        }
//                    )
                }
            }
        }
    }
}