package com.example.budgeting_client.manga

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
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

    Card(
        onClick = { startActivity(context, browserIntent, null) },
        modifier = modifier
    ) {
        Box(Modifier.fillMaxSize().padding(12.dp)) {
            Column(Modifier.align(Alignment.TopStart)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
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
        }
    }
}

@Preview
@Composable
fun Test() {
    BudgetingclientTheme {
        MangaCard(title = "Test", chapter = 10, lastUpdated = Date(), urlString = "https://google.com")
    }
}