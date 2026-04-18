package com.darchums.decoscan.core

object EcoUtils {
    /**
     * Converts CO2 grams into meaningful real-world impact messages.
     */
    fun getCo2ImpactMessage(grams: Float): String {
        return when {
            grams <= 0 -> "Start scanning to see your impact!"
            grams < 10 -> "That's enough CO2 saved to power a LED bulb for 30 minutes!"
            grams < 50 -> "You've saved enough CO2 to charge a smartphone twice!"
            grams < 100 -> "This impact is equivalent to preventing 5 miles of car emissions!"
            grams < 500 -> "You've saved as much CO2 as a young tree absorbs in a week!"
            else -> "Your effort is equivalent to powering a laptop for a full workday!"
        }
    }
}
