package com.example.judotournamenttracker.ui

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.judotournamenttracker.data.Tournament
import com.example.judotournamenttracker.viewmodel.TournamentViewModel
import com.google.gson.Gson
import androidx.compose.runtime.collectAsState
import java.io.OutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentListScreen(
    navController: NavController,
    viewModel: TournamentViewModel
) {
    val context = LocalContext.current
    val searchText by viewModel.searchText.collectAsState()
    val tournaments by viewModel.filteredTournaments.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var expandedCategoryFilter by remember { mutableStateOf(false) }

    val createDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri: Uri? ->
        if (uri != null) {
            exportTournamentsToPdf(context, tournaments, uri)
            Toast.makeText(context, "PDF bylo exportováno", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Export zrušen", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchText,
                        onValueChange = { viewModel.setSearchText(it) },
                        singleLine = true,
                        label = { Text("Hledat turnaj...") },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSortOrder() }) {
                        Icon(Icons.Default.Sort, contentDescription = "Seřadit podle data")
                    }
                    IconButton(onClick = {
                        createDocumentLauncher.launch("turnaje_export.pdf")
                    }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Exportovat do PDF")
                    }
                    IconButton(onClick = {
                        navController.navigate("category_list")
                    }) {
                        Icon(Icons.Default.List, contentDescription = "Seznam kategorií")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_tournament") }) {
                Icon(Icons.Default.Add, contentDescription = "Přidat turnaj")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(12.dp)
                .fillMaxSize()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { expandedCategoryFilter = true }) {
                    Text("Filtrovat dle kategorie")
                }
                Button(onClick = { viewModel.setSelectedCategory(null) }) {
                    Text("Zrušit filtr")
                }
            }
            DropdownMenu(
                expanded = expandedCategoryFilter,
                onDismissRequest = { expandedCategoryFilter = false }
            ) {
                categories.forEach { cat ->
                    DropdownMenuItem(
                        text = { Text(cat.name) },
                        onClick = {
                            viewModel.setSelectedCategory(cat.id)
                            expandedCategoryFilter = false
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(tournaments) { tournament ->
                    TournamentCard(tournament) {
                        val json = Gson().toJson(tournament)
                        navController.navigate("tournament_detail/$json")
                    }
                }
            }
        }
    }
}

@Composable
fun TournamentCard(tournament: Tournament, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = tournament.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Datum: ${tournament.date}")
            Text(text = "Místo: ${tournament.location}")
        }
    }
}

fun exportTournamentsToPdf(context: Context, tournaments: List<Tournament>, outputUri: Uri) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdfDocument.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint().apply { textSize = 14f }

    var yPosition = 50f
    val startX = 50f

    canvas.drawText("Seznam turnajů", startX, yPosition, paint)
    yPosition += 40

    for (t in tournaments) {
        canvas.drawText("Název: ${t.name}", startX, yPosition, paint)
        yPosition += 20
        canvas.drawText("Místo: ${t.location}", startX, yPosition, paint)
        yPosition += 20
        canvas.drawText("Datum: ${t.date}", startX, yPosition, paint)
        yPosition += 20
        canvas.drawText("Popis: ${t.description}", startX, yPosition, paint)
        yPosition += 40
    }

    pdfDocument.finishPage(page)

    try {
        context.contentResolver.openOutputStream(outputUri)?.use { outputStream: OutputStream ->
            pdfDocument.writeTo(outputStream)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        pdfDocument.close()
    }
}