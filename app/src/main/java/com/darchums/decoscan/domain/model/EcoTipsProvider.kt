package com.darchums.decoscan.domain.model

object EcoTipsProvider {
    private val tips = listOf(
        EcoTip("Home", "Switch to LED Bulbs", "LEDs use 75% less energy and last 25 times longer than incandescent lighting."),
        EcoTip("Home", "Lower Your Thermostat", "Lowering your heating by just 1-2 degrees can save a significant amount of energy."),
        EcoTip("Home", "Unplug Idle Electronics", "Devices consume 'vampire' energy even when turned off. Unplug them when not in use."),
        EcoTip("Travel", "Use Public Transit", "Taking a bus or train instead of driving can significantly reduce your carbon footprint."),
        EcoTip("Travel", "Maintain Your Vehicle", "Properly inflated tires and regular tune-ups improve fuel efficiency."),
        EcoTip("Travel", "Walk or Bike", "For short trips, walking or biking is zero-emission and great for your health."),
        EcoTip("Waste", "Compost Kitchen Scraps", "Reduce landfill waste by composting fruit and vegetable peels and coffee grounds."),
        EcoTip("Waste", "Avoid Single-Use Plastics", "Bring your own reusable bags, bottles, and straws to reduce plastic pollution."),
        EcoTip("Waste", "Donate Before Discarding", "If it's still usable, donate old clothes or electronics instead of throwing them away."),
        EcoTip("Waste", "Buy in Bulk", "Buying bulk items reduces packaging waste and often saves money.")
    )

    fun getRandomTip(): EcoTip {
        return tips.random()
    }

    fun getAllTips(): List<EcoTip> = tips
}
