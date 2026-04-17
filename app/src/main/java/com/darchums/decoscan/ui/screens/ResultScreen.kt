package com.darchums.decoscan.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.darchums.decoscan.ai.GeminiService
import com.darchums.decoscan.data.PreferenceManager
import com.darchums.decoscan.domain.model.MaterialType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    material: String,
    confidence: Float,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferenceManager = remember { PreferenceManager(context) }
    val geminiService = remember { GeminiService("YOUR_API_KEY") } // User can replace with real key
    
    val materialType = remember { MaterialType.fromString(material) }
    var aiInsight by remember { mutableStateOf<String?>(null) }
    var isLoadingAi by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val username = preferenceManager.loggedInUser.first() ?: return@LaunchedEffect
        
        // Update local stats
        preferenceManager.updateEcoScore(username, materialType.ecoPoints)
        preferenceManager.incrementMaterialCount(username, materialType.name)
        
        // Fetch AI Insight
        aiInsight = geminiService.getSustainabilityInsight(materialType, confidence)
        isLoadingAi = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        
        Icon(
            Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )
        
        Text(
            text = "Detection Complete",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Material Detected", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = materialType.displayName,
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Confidence: ${"%.1f".format(confidence * 100)}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        InfoSection(title = "Disposal Instructions", content = getDisposalAdvice(materialType))
        
        Spacer(modifier = Modifier.height(16.dp))
        
        InfoSection(
            title = "Environmental Impact",
            content = "Recycling this item saves approximately ${materialType.co2Saved}kg of CO2 and earns you ${materialType.ecoPoints} Eco Points!"
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Darchums AI Insight",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (isLoadingAi) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = aiInsight ?: getFallbackInsight(materialType),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Back to Home")
        }
    }
}

@Composable
fun InfoSection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text(text = content, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

fun getDisposalAdvice(material: MaterialType): String = when (material) {
    MaterialType.PLASTIC -> "Rinse thoroughly to remove food residue. Check for recycling symbols 1, 2, or 5. Remove caps and place in your blue bin."
    MaterialType.GLASS -> "Wash and dry. Do not break. Clear, brown, and green glass are usually accepted together in most municipal systems."
    MaterialType.PAPER -> "Keep dry and clean. Flatten boxes to save space. Do not recycle if contaminated with oil or grease (like pizza boxes)."
    MaterialType.METAL -> "Rinse cans and pull tabs. Aluminum and steel are highly recyclable. Crushing cans is optional but saves space."
    else -> "Consult your local waste management guide for specific instructions on this material."
}

fun getFallbackInsight(material: MaterialType): String = when (material) {
    MaterialType.PLASTIC -> "Plastic pollution is a major threat to marine life. Recycling one ton of plastic saves 5,774 kWh of energy."
    MaterialType.GLASS -> "Glass is 100% recyclable and can be recycled endlessly without loss in quality or purity."
    MaterialType.PAPER -> "Recycling paper saves trees and reduces landfill space. One ton of recycled paper saves 17 trees."
    MaterialType.METAL -> "Recycling aluminum uses 95% less energy than producing it from raw materials. It's one of the most sustainable materials."
    else -> "Every item recycled is a step towards a cleaner planet. Thank you for scanning!"
}
