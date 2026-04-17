package com.darchums.decoscan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class EcoTip(val category: String, val title: String, val description: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcoTipsScreen(onBack: () -> Unit) {
    val tips = listOf(
        EcoTip("Home", "Switch to LED Bulbs", "LEDs use 75% less energy and last 25 times longer than incandescent lighting."),
        EcoTip("Waste", "Compost Kitchen Scraps", "Reduce landfill waste by composting fruit and vegetable peels, coffee grounds, and eggshells."),
        EcoTip("Travel", "Use Public Transit", "Taking a bus or train instead of driving alone can significantly reduce your carbon footprint."),
        EcoTip("Waste", "Avoid Single-Use Plastics", "Bring your own reusable bags, bottles, and straws to reduce plastic pollution."),
        EcoTip("Home", "Unplug Idle Electronics", "Devices consume 'vampire' energy even when turned off. Unplug them when not in use."),
        EcoTip("Waste", "Donate Before Discarding", "If it's still usable, donate old clothes, furniture, or electronics instead of throwing them away."),
        EcoTip("Travel", "Maintain Your Vehicle", "Properly inflated tires and regular tune-ups improve fuel efficiency and reduce emissions."),
        EcoTip("Home", "Lower Your Thermostat", "Lowering your heating by just 1-2 degrees can save a significant amount of energy over time.")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Eco Tips") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(tips) { tip ->
                TipCard(tip)
            }
        }
    }
}

@Composable
fun TipCard(tip: EcoTip) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = tip.category.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = tip.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tip.description,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
