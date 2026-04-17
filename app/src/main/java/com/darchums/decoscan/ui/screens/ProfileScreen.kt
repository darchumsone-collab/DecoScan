package com.darchums.decoscan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.darchums.decoscan.data.PreferenceManager
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val preferenceManager = remember { PreferenceManager(context) }
    var username by remember { mutableStateOf("") }
    var ecoScore by remember { mutableIntStateOf(0) }
    var totalScans by remember { mutableIntStateOf(0) }
    var materialCounts by remember { mutableStateOf(mapOf<String, Int>()) }

    LaunchedEffect(Unit) {
        val user = preferenceManager.loggedInUser.first() ?: ""
        username = user
        ecoScore = preferenceManager.getEcoScore(user).first()
        totalScans = preferenceManager.getTotalScans(user).first()
        
        val counts = mutableMapOf<String, Int>()
        listOf("Plastic", "Glass", "Paper", "Metal").forEach { material ->
            counts[material] = preferenceManager.getMaterialCount(user, material).first()
        }
        materialCounts = counts
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = username, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(text = "Eco Warrior Level ${ (ecoScore / 20) + 1 }", color = MaterialTheme.colorScheme.secondary)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatItem(label = "Total Scans", value = totalScans.toString())
                StatItem(label = "Eco Score", value = ecoScore.toString())
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Material Breakdown",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            materialCounts.forEach { (material, count) ->
                MaterialRow(material, count, totalScans)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.weight(1f))
            Text(text = "DecoScan v1.0.0", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
        Text(text = label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun MaterialRow(material: String, count: Int, total: Int) {
    val progress = if (total > 0) count.toFloat() / total else 0f
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = material, style = MaterialTheme.typography.bodyMedium)
            Text(text = count.toString(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}
