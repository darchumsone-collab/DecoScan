package com.darchums.decoscan.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.darchums.decoscan.ai.GeminiService
import com.darchums.decoscan.core.EcoUtils
import com.darchums.decoscan.domain.model.EcoTipsProvider
import com.darchums.decoscan.domain.model.MaterialType
import com.darchums.decoscan.viewmodel.EcoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    material: String,
    confidence: Float,
    onClose: () -> Unit,
    ecoViewModel: EcoViewModel = viewModel()
) {
    val context = LocalContext.current
    val geminiService = remember { GeminiService("YOUR_API_KEY") }
    val materialType = remember { MaterialType.fromString(material) }
    
    var aiInsight by remember { mutableStateOf<String?>(null) }
    var isLoadingAi by remember { mutableStateOf(true) }
    val ecoStats by ecoViewModel.ecoStats.collectAsState()

    // Animated Score State
    var animatedScore by remember { mutableFloatStateOf(0f) }
    val scoreTransition = animateFloatAsState(
        targetValue = animatedScore,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "ScoreAnimation"
    )

    val pointsEarned = remember(material, confidence) {
        val weight = when (material.lowercase()) {
            "plastic" -> 1.0f
            "paper" -> 1.5f
            "glass" -> 2.0f
            "metal" -> 2.5f
            else -> 0.0f
        }
        weight * confidence
    }

    LaunchedEffect(material, confidence) {
        // Safe update: trigger only once
        ecoViewModel.updateEcoStats(material, confidence)
        delay(300)
        animatedScore = pointsEarned
        
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
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Material Detected", style = MaterialTheme.typography.labelLarge)
                Text(
                    text = materialType.displayName,
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Confidence: ${"%.1f".format(confidence * 100)}%",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Animated Impact Rewards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImpactRewardItem(
                    label = "EcoScore Earned", 
                    value = "+${"%.1f".format(scoreTransition.value)} 🌱"
                )
                VerticalDivider(modifier = Modifier.height(40.dp), color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.2f))
                ImpactRewardItem(
                    label = "Current Level", 
                    value = ecoStats.getLevel()
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        
        // Dynamic Tip based on material
        val contextTip = remember(material) {
            EcoTipsProvider.getAllTips().find { it.category.equals(material, ignoreCase = true) } 
                ?: EcoTipsProvider.getRandomTip()
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Pro Eco Tip", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(contextTip.description, style = MaterialTheme.typography.bodyMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        InfoSection(title = "Disposal Instructions", content = getDisposalAdvice(materialType))
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Darchums AI Sustainability Insight",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (isLoadingAi) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.primary)
                } else {
                    Text(
                        text = aiInsight ?: getFallbackInsight(materialType),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Complete Mission", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ImpactRewardItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.7f))
    }
}

@Composable
fun InfoSection(title: String, content: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
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
