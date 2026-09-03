package com.example.data.model

enum class TrustLevel(
    val label: String,
    val description: String,
    val ringStrokeWidthDp: Float,
    val ringColorHex: Long,
    val isVerified: Boolean
) {
    NEW(
        label = "New Contact",
        description = "First-time recipient. Double check their details carefully.",
        ringStrokeWidthDp = 2f,
        ringColorHex = 0xFF9E9E9E,
        isVerified = false
    ),
    FAMILIAR(
        label = "Familiar (1-2 payments)",
        description = "You have safely sent money to them once or twice.",
        ringStrokeWidthDp = 3.5f,
        ringColorHex = 0xFF26A69A,
        isVerified = false
    ),
    FREQUENT(
        label = "Frequent (3-5 payments)",
        description = "Regular trusted recipient in your frequent circle.",
        ringStrokeWidthDp = 5f,
        ringColorHex = 0xFF00C853,
        isVerified = true
    ),
    HIGHLY_TRUSTED(
        label = "Highly Trusted (6+ payments)",
        description = "Long-standing trusted contact. Verified by your past transactions.",
        ringStrokeWidthDp = 6.5f,
        ringColorHex = 0xFF00E676,
        isVerified = true
    );

    companion object {
        fun fromCount(count: Int): TrustLevel = when {
            count <= 0 -> NEW
            count in 1..2 -> FAMILIAR
            count in 3..5 -> FREQUENT
            else -> HIGHLY_TRUSTED
        }
    }
}

data class Contact(
    val id: String,
    val name: String,
    val roleOrRelation: String,
    val phoneMasked: String,
    val avatarBgColorHex: Long,
    val initials: String,
    val pastTransactionCount: Int,
    val typicalAmount: Double
) {
    val trustLevel: TrustLevel get() = TrustLevel.fromCount(pastTransactionCount)
}

enum class TransactionType {
    SENT,
    RECEIVED
}

enum class TransactionStatus {
    SUCCESS,
    CANCELLED_BY_USER,
    STOPPED_BY_QUEUE
}

data class PaymentTransaction(
    val id: String,
    val recipientId: String,
    val recipientName: String,
    val roleOrRelation: String,
    val amount: Double,
    val type: TransactionType,
    val timestamp: Long,
    val plainDescription: String,
    val status: TransactionStatus = TransactionStatus.SUCCESS
)

enum class NetworkState(val label: String, val isSendAllowed: Boolean, val description: String) {
    GOOD("Good Connection", true, "Fast & reliable network. Safe to send money."),
    POOR("Poor Connection", false, "Your internet looks weak right now. Transfers are paused to prevent stuck or duplicate charges."),
    OFFLINE("No Internet (Offline)", false, "You are currently offline. Please reconnect to mobile data or Wi-Fi.")
}

enum class FluencyComfortLevel(
    val title: String,
    val transferLimit: Double,
    val badgeLabel: String,
    val plainReason: String
) {
    CAUTIOUS(
        title = "Cautious Guardrail",
        transferLimit = 5000.0,
        badgeLabel = "Limit: ₹5,000",
        plainReason = "Your current transfer limit is ₹5,000 because we detected frequent corrections, extra time, or help inquiries. This automatically protects you from unintended large transfers."
    ),
    MODERATE(
        title = "Moderate Guardrail",
        transferLimit = 15000.0,
        badgeLabel = "Limit: ₹15,000",
        plainReason = "Your transfer limit is ₹15,000. Your navigation is steady with standard safety checkpoints."
    ),
    HIGH(
        title = "Full Limit",
        transferLimit = 50000.0,
        badgeLabel = "Limit: ₹50,000",
        plainReason = "Your transfer limit is ₹50,000 based on fluent, confident interactions during your session."
    )
}
