package com.darchums.decoscan.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(onFinished: () -> Unit) {
    var currentPage by remember { mutableStateOf(0) }
    
    val pages = listOf(
        OnboardingPage(
            "AI Waste Detection",
            "Instantly identify plastic, glass, paper, and metal using our advanced offline AI engine.",
            "Scan Smart"
        ),
        OnboardingPage(
            "Offline Intelligence",
            "DecoScan works fully offline, protecting your privacy and saving data while you save the planet.",
            "Stay Private"
        ),
        OnboardingPage(
            "Environmental Impact",
            "Track your eco-score and see the real-world CO2 savings from your recycling efforts.",
            "Start Impacting"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = pages[currentPage].title,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = pages[currentPage].description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(48.dp))
        Button(
            onClick = {
                if (currentPage < pages.size - 1) {
                    currentPage++
                } else {
                    onFinished()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (currentPage < pages.size - 1) "Next" else "Get Started")
        }
    }
}

data class OnboardingPage(val title: String, val description: String, val buttonText: String)
