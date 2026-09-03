package com.example.data.repository

import com.example.data.model.Contact
import com.example.data.model.FluencyComfortLevel
import com.example.data.model.NetworkState
import com.example.data.model.PaymentTransaction
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.util.Formatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.math.abs

class PaysureRepository {

    private val _walletBalance = MutableStateFlow(24850.0)
    val walletBalance: StateFlow<Double> = _walletBalance.asStateFlow()

    private val _contacts = MutableStateFlow(createInitialContacts())
    val contacts: StateFlow<List<Contact>> = _contacts.asStateFlow()

    private val _transactions = MutableStateFlow(createInitialTransactions())
    val transactions: StateFlow<List<PaymentTransaction>> = _transactions.asStateFlow()

    private val _networkState = MutableStateFlow(NetworkState.GOOD)
    val networkState: StateFlow<NetworkState> = _networkState.asStateFlow()

    // Fluency tracking in-session
    private val _backspaceCount = MutableStateFlow(0)
    private val _explainUsageCount = MutableStateFlow(0)
    private val _sessionFlowStartTime = MutableStateFlow(System.currentTimeMillis())

    // Override comfort level if user wants to test specific mode in safety settings
    private val _forcedComfortLevel = MutableStateFlow<FluencyComfortLevel?>(null)

    val currentComfortLevel: FluencyComfortLevel
        get() {
            _forcedComfortLevel.value?.let { return it }
            val backspaces = _backspaceCount.value
            val explains = _explainUsageCount.value
            val timeTaken = (System.currentTimeMillis() - _sessionFlowStartTime.value) / 1000

            return when {
                backspaces >= 4 || explains >= 2 || timeTaken > 60 -> FluencyComfortLevel.CAUTIOUS
                backspaces in 2..3 || explains == 1 || timeTaken > 30 -> FluencyComfortLevel.MODERATE
                else -> FluencyComfortLevel.HIGH
            }
        }

    fun recordBackspace() {
        _backspaceCount.update { it + 1 }
    }

    fun recordExplainUsed() {
        _explainUsageCount.update { it + 1 }
    }

    fun resetFlowTiming() {
        _sessionFlowStartTime.value = System.currentTimeMillis()
        _backspaceCount.value = 0
    }

    fun setForcedComfortLevel(level: FluencyComfortLevel?) {
        _forcedComfortLevel.value = level
    }

    fun setNetworkState(state: NetworkState) {
        _networkState.value = state
    }

    fun addFunds(amount: Double) {
        _walletBalance.update { it + amount }
        val newTx = PaymentTransaction(
            id = "tx_${System.currentTimeMillis()}",
            recipientId = "self",
            recipientName = "Bank Deposit",
            roleOrRelation = "Safe Top-Up",
            amount = amount,
            type = TransactionType.RECEIVED,
            timestamp = System.currentTimeMillis(),
            plainDescription = "Added ${Formatters.formatCurrency(amount)} to your wallet",
            status = TransactionStatus.SUCCESS
        )
        _transactions.update { listOf(newTx) + it }
    }

    fun checkDoublePayment(recipientId: String, amount: Double): PaymentTransaction? {
        val twoMinutesAgo = System.currentTimeMillis() - (2 * 60 * 1000L)
        return _transactions.value.firstOrNull { tx ->
            tx.type == TransactionType.SENT &&
                    tx.status == TransactionStatus.SUCCESS &&
                    tx.recipientId == recipientId &&
                    tx.timestamp >= twoMinutesAgo &&
                    abs(tx.amount - amount) <= (tx.amount * 0.05) // within 5%
        }
    }

    fun evaluateSpendPattern(contact: Contact, amount: Double): String? {
        val typical = contact.typicalAmount
        if (typical <= 0) return null
        val multiplier = (amount / typical).toInt()

        return when {
            multiplier >= 10 -> "This is ${multiplier}x your usual payment to ${contact.name} (usually ~${Formatters.formatCurrency(typical)}). Please double-check for extra zeros."
            multiplier >= 3 -> "This is ${multiplier}x your usual payment to ${contact.name} (usually ~${Formatters.formatCurrency(typical)})."
            amount >= 10000.0 && typical < 1000.0 -> "You don't usually send this much at once."
            else -> null
        }
    }

    fun executeTransaction(contact: Contact, amount: Double): PaymentTransaction {
        // Deduct balance
        _walletBalance.update { (it - amount).coerceAtLeast(0.0) }

        // Update contact's past transaction count (+1 builds trust ring)
        _contacts.update { list ->
            list.map {
                if (it.id == contact.id) {
                    it.copy(pastTransactionCount = it.pastTransactionCount + 1)
                } else {
                    it
                }
            }
        }

        val tx = PaymentTransaction(
            id = "tx_${System.currentTimeMillis()}",
            recipientId = contact.id,
            recipientName = contact.name,
            roleOrRelation = contact.roleOrRelation,
            amount = amount,
            type = TransactionType.SENT,
            timestamp = System.currentTimeMillis(),
            plainDescription = "You paid ${contact.name} (${contact.roleOrRelation}) ${Formatters.formatCurrency(amount)}",
            status = TransactionStatus.SUCCESS
        )

        _transactions.update { listOf(tx) + it }
        return tx
    }

    private fun createInitialContacts(): List<Contact> = listOf(
        Contact(
            id = "c_1",
            name = "Ramesh Kumar",
            roleOrRelation = "Vegetable Seller",
            phoneMasked = "•••• 4210",
            avatarBgColorHex = 0xFF2E7D32,
            initials = "RK",
            pastTransactionCount = 8,
            typicalAmount = 180.0
        ),
        Contact(
            id = "c_2",
            name = "Sunita Devi",
            roleOrRelation = "Milk Delivery",
            phoneMasked = "•••• 8933",
            avatarBgColorHex = 0xFF00838F,
            initials = "SD",
            pastTransactionCount = 4,
            typicalAmount = 450.0
        ),
        Contact(
            id = "c_3",
            name = "Priya Sharma",
            roleOrRelation = "Sister",
            phoneMasked = "•••• 1120",
            avatarBgColorHex = 0xFF6A1B9A,
            initials = "PS",
            pastTransactionCount = 14,
            typicalAmount = 1500.0
        ),
        Contact(
            id = "c_4",
            name = "Vikram Singh",
            roleOrRelation = "Tea & Snacks",
            phoneMasked = "•••• 7701",
            avatarBgColorHex = 0xFFD84315,
            initials = "VS",
            pastTransactionCount = 3,
            typicalAmount = 50.0
        ),
        Contact(
            id = "c_5",
            name = "Dr. V. Sharma",
            roleOrRelation = "Neighborhood Clinic",
            phoneMasked = "•••• 9002",
            avatarBgColorHex = 0xFF1565C0,
            initials = "VS",
            pastTransactionCount = 2,
            typicalAmount = 500.0
        ),
        Contact(
            id = "c_6",
            name = "Deepak Sharma",
            roleOrRelation = "New Electrician",
            phoneMasked = "•••• 3341",
            avatarBgColorHex = 0xFF455A64,
            initials = "DS",
            pastTransactionCount = 0, // NEW contact -> grey ring
            typicalAmount = 600.0
        ),
        Contact(
            id = "c_7",
            name = "Asha Medicals",
            roleOrRelation = "Pharmacy",
            phoneMasked = "•••• 6542",
            avatarBgColorHex = 0xFFC2185B,
            initials = "AM",
            pastTransactionCount = 6,
            typicalAmount = 350.0
        )
    )

    private fun createInitialTransactions(): List<PaymentTransaction> {
        val now = System.currentTimeMillis()
        val oneHour = 3600 * 1000L
        val oneDay = 24 * 3600 * 1000L

        return listOf(
            PaymentTransaction(
                id = "tx_01",
                recipientId = "c_1",
                recipientName = "Ramesh Kumar",
                roleOrRelation = "Vegetable Seller",
                amount = 150.0,
                type = TransactionType.SENT,
                timestamp = now - (3 * oneHour),
                plainDescription = "You paid Ramesh Kumar (Vegetable Seller) ₹150 today"
            ),
            PaymentTransaction(
                id = "tx_02",
                recipientId = "c_3",
                recipientName = "Priya Sharma",
                roleOrRelation = "Sister",
                amount = 2500.0,
                type = TransactionType.RECEIVED,
                timestamp = now - (18 * oneHour),
                plainDescription = "Priya Sharma sent ₹2,500 to your wallet"
            ),
            PaymentTransaction(
                id = "tx_03",
                recipientId = "c_2",
                recipientName = "Sunita Devi",
                roleOrRelation = "Milk Delivery",
                amount = 450.0,
                type = TransactionType.SENT,
                timestamp = now - (1 * oneDay + 2 * oneHour),
                plainDescription = "You paid Sunita Devi (Milk Delivery) ₹450 yesterday"
            ),
            PaymentTransaction(
                id = "tx_04",
                recipientId = "c_4",
                recipientName = "Vikram Singh",
                roleOrRelation = "Tea & Snacks",
                amount = 60.0,
                type = TransactionType.SENT,
                timestamp = now - (2 * oneDay + 4 * oneHour),
                plainDescription = "You paid Vikram Singh (Tea & Snacks) ₹60"
            )
        )
    }
}
