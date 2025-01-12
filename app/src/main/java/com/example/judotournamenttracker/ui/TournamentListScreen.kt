package com.example.judotournamenttracker.ui

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.CreateDocument
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
import com.example.judotournamenttracker.data.TournamentWithCategories
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
    val twcList by viewModel.filteredTournamentsWithCats.collectAsState()
    val categories by viewModel.categories.collectAsState()

    var expandedCategoryFilter by remember { mutableStateOf(false) }

    val createDocLauncher = rememberLauncherForActivityResult(CreateDocument("application/pdf")) { uri ->
        if (uri != null) {
            val justTournaments = twcList.map { it.tournament }
            exportTournamentsToPdf(context, justTournaments, uri)
            Toast.makeText(context, "PDF exportováno", Toast.LENGTH_SHORT).show()
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
                        Icon(Icons.Default.Sort, contentDescription = "Seřadit")
                    }
                    IconButton(onClick = {
                        createDocLauncher.launch("turnaje_export.pdf")
                    }) {
                        Icon(Icons.Default.FileDownload, contentDescription = "Export do PDF")
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
            FloatingActionButton(onClick = {
                navController.navigate("add_tournament")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Přidat turnaj")
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
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
                items(twcList) { twc ->
                    TournamentCard(twc) {
                        val json = Gson().toJson(twc.tournament)
                        navController.navigate("tournament_detail/$json")
                    }
                }
            }
        }
    }
}

@Composable
fun TournamentCard(twc: TournamentWithCategories, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(twc.tournament.name, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Datum: ${twc.tournament.date}")
            Text("Místo: ${twc.tournament.location}")
            if (twc.categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                val catStr = twc.categories.joinToString { it.name }
                Text("Kategorie: $catStr")
            }
        }
    }
}

fun exportTournamentsToPdf(context: Context, tournaments: List<Tournament>, uri: Uri) {
    val pdf = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
    val page = pdf.startPage(pageInfo)
    val canvas = page.canvas
    val paint = Paint().apply { textSize = 14f }

    var yPos = 50f
    val startX = 50f
    canvas.drawText("Seznam turnajů", startX, yPos, paint)
    yPos += 40

    for (t in tournaments) {
        canvas.drawText("Název: ${t.name}", startX, yPos, paint)
        yPos += 20
        canvas.drawText("Místo: ${t.location}", startX, yPos, paint)
        yPos += 20
        canvas.drawText("Datum: ${t.date}", startX, yPos, paint)
        yPos += 20
        canvas.drawText("Popis: ${t.description}", startX, yPos, paint)
        yPos += 40
    }
    pdf.finishPage(page)

    context.contentResolver.openOutputStream(uri)?.use { output ->
        pdf.writeTo(output)
    }
    pdf.close()
}