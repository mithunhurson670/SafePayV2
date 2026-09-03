package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Contact
import com.example.data.model.FluencyComfortLevel
import com.example.data.model.NetworkState
import com.example.data.model.PaymentTransaction
import com.example.data.repository.PaysureRepository
import com.example.util.Formatters
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab {
    HOME,
    SEND_MONEY,
    HISTORY,
    HELP
}

enum class SendStep {
    SELECT_RECIPIENT,
    ENTER_AMOUNT,
    CONFIRM_REVIEW,
    DELAYED_SENDING_QUEUE,
    SUCCESS_RECEIPT
}

data class ExplainInfo(
    val title: String,
    val explanation: String
)

data class SendFlowUiState(
    val currentStep: SendStep = SendStep.SELECT_RECIPIENT,
    val selectedContact: Contact? = null,
    val amountInput: String = "",
    val amountInWords: String = "Zero rupees",
    val isExtraZeroWarning: Boolean = false,
    val isExtraZeroVerifiedByTap: Boolean = false,
    val spendPatternNotice: String? = null,
    val isDoublePaymentWarningOpen: Boolean = false,
    val duplicatePreviousTx: PaymentTransaction? = null,
    // Delayed Send Queue with STOP button
    val countdownRemainingSeconds: Int = 10,
    val countdownProgress: Float = 1.0f,
    val isStoppedByUser: Boolean = false,
    val stopFeedbackMessage: String? = null,
    val completedTransaction: PaymentTransaction? = null
)

class PaysureViewModel(
    private val repository: PaysureRepository = PaysureRepository()
) : ViewModel() {

    // Global navigation & UI state
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    val walletBalance: StateFlow<Double> = repository.walletBalance
    val contacts: StateFlow<List<Contact>> = repository.contacts
    val transactions: StateFlow<List<PaymentTransaction>> = repository.transactions
    val networkState: StateFlow<NetworkState> = repository.networkState

    private val _isBalanceHidden = MutableStateFlow(false)
    val isBalanceHidden: StateFlow<Boolean> = _isBalanceHidden.asStateFlow()

    private val _currentLanguage = MutableStateFlow("English")
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    // Fluency limit
    val comfortLevel: FluencyComfortLevel
        get() = repository.currentComfortLevel

    private val _isFluencyExplainerOpen = MutableStateFlow(false)
    val isFluencyExplainerOpen: StateFlow<Boolean> = _isFluencyExplainerOpen.asStateFlow()

    // Explain this modal
    private val _activeExplain = MutableStateFlow<ExplainInfo?>(null)
    val activeExplain: StateFlow<ExplainInfo?> = _activeExplain.asStateFlow()

    // Send Money Flow State
    private val _sendState = MutableStateFlow(SendFlowUiState())
    val sendState: StateFlow<SendFlowUiState> = _sendState.asStateFlow()

    private var queueCountdownJob: Job? = null

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
        if (tab == AppTab.SEND_MONEY && _sendState.value.currentStep == SendStep.SUCCESS_RECEIPT) {
            // Reset for next send
            resetSendFlow()
        }
    }

    fun toggleBalanceVisibility() {
        _isBalanceHidden.update { !it }
    }

    fun toggleLanguage() {
        _currentLanguage.update { current ->
            when (current) {
                "English" -> "हिंदी"
                "हिंदी" -> "தமிழ்"
                else -> "English"
            }
        }
    }

    fun triggerExplain(title: String, explanation: String) {
        repository.recordExplainUsed()
        _activeExplain.value = ExplainInfo(title, explanation)
    }

    fun dismissExplain() {
        _activeExplain.value = null
    }

    fun openFluencyExplainer() {
        repository.recordExplainUsed()
        _isFluencyExplainerOpen.value = true
    }

    fun dismissFluencyExplainer() {
        _isFluencyExplainerOpen.value = false
    }

    fun setNetworkStatus(state: NetworkState) {
        repository.setNetworkState(state)
    }

    fun setForcedFluencyComfort(level: FluencyComfortLevel?) {
        repository.setForcedComfortLevel(level)
    }

    fun addDemoFunds(amount: Double) {
        repository.addFunds(amount)
    }

    // ==================== SEND MONEY FLOW ====================

    fun startSendFlow(preselectedContact: Contact? = null) {
        repository.resetFlowTiming()
        _sendState.value = SendFlowUiState(
            currentStep = if (preselectedContact != null) SendStep.ENTER_AMOUNT else SendStep.SELECT_RECIPIENT,
            selectedContact = preselectedContact
        )
        _currentTab.value = AppTab.SEND_MONEY
    }

    fun onSelectContact(contact: Contact) {
        _sendState.update {
            it.copy(
                selectedContact = contact,
                currentStep = SendStep.ENTER_AMOUNT
            )
        }
    }

    fun onAmountKeyTap(key: String) {
        val current = _sendState.value.amountInput
        val updated = when (key) {
            "BACKSPACE" -> {
                repository.recordBackspace()
                if (current.isNotEmpty()) current.dropLast(1) else ""
            }
            "CLEAR" -> {
                repository.recordBackspace()
                ""
            }
            else -> {
                // Prevent leading zero repetition
                if (current == "0" && key != ".") key
                else if (current.length >= 7) current // max ₹99,99,999
                else current + key
            }
        }

        updateAmountInternal(updated)
    }

    private fun updateAmountInternal(newAmountStr: String) {
        val amountVal = newAmountStr.toLongOrNull() ?: 0L
        val words = Formatters.amountInWords(amountVal)
        val contact = _sendState.value.selectedContact

        // Extra-zero threshold: > 10x typical amount OR > 5,000 rupees
        val isHighWarning = if (contact != null && amountVal > 0) {
            val ratio = amountVal.toDouble() / contact.typicalAmount
            ratio >= 8.0 || amountVal >= 5000L
        } else {
            amountVal >= 5000L
        }

        _sendState.update {
            it.copy(
                amountInput = newAmountStr,
                amountInWords = words,
                isExtraZeroWarning = isHighWarning,
                // Reset tap confirmation whenever number changes
                isExtraZeroVerifiedByTap = false
            )
        }
    }

    fun onConfirmExtraZeroTap() {
        _sendState.update { it.copy(isExtraZeroVerifiedByTap = true) }
    }

    fun proceedToConfirmationReview() {
        val contact = _sendState.value.selectedContact ?: return
        val amount = _sendState.value.amountInput.toDoubleOrNull() ?: return

        // 1. Check Network protection
        if (!repository.networkState.value.isSendAllowed) {
            // Blocked by network
            return
        }

        // 2. Check Fluency Cap
        if (amount > comfortLevel.transferLimit) {
            return
        }

        // 3. Check Spend Pattern Notice
        val spendNotice = repository.evaluateSpendPattern(contact, amount)

        // 4. Check Double Payment Protection
        val duplicate = repository.checkDoublePayment(contact.id, amount)
        if (duplicate != null) {
            _sendState.update {
                it.copy(
                    isDoublePaymentWarningOpen = true,
                    duplicatePreviousTx = duplicate,
                    spendPatternNotice = spendNotice
                )
            }
            return
        }

        // Proceed to Step 3: Review
        _sendState.update {
            it.copy(
                currentStep = SendStep.CONFIRM_REVIEW,
                spendPatternNotice = spendNotice
            )
        }
    }

    fun onDismissDoublePaymentMistake() {
        _sendState.update {
            it.copy(
                isDoublePaymentWarningOpen = false,
                duplicatePreviousTx = null
            )
        }
    }

    fun onConfirmSendAnywayAfterDuplicate() {
        _sendState.update {
            it.copy(
                isDoublePaymentWarningOpen = false,
                duplicatePreviousTx = null,
                currentStep = SendStep.CONFIRM_REVIEW
            )
        }
    }

    /**
     * Feature 2: Delayed Send Queue with STOP button.
     * Initiates 10-second countdown with visual progress and STOP affordance.
     */
    fun startDelayedSendQueue() {
        queueCountdownJob?.cancel()
        _sendState.update {
            it.copy(
                currentStep = SendStep.DELAYED_SENDING_QUEUE,
                countdownRemainingSeconds = 10,
                countdownProgress = 1.0f,
                isStoppedByUser = false,
                stopFeedbackMessage = null
            )
        }

        queueCountdownJob = viewModelScope.launch {
            val totalSeconds = 10
            for (sec in totalSeconds downTo 1) {
                _sendState.update {
                    it.copy(
                        countdownRemainingSeconds = sec,
                        countdownProgress = sec.toFloat() / totalSeconds.toFloat()
                    )
                }
                delay(1000L)
            }

            // If not cancelled/stopped, complete transaction!
            completeFinalTransaction()
        }
    }

    fun stopSendQueue() {
        queueCountdownJob?.cancel()
        _sendState.update {
            it.copy(
                currentStep = SendStep.CONFIRM_REVIEW,
                isStoppedByUser = true,
                stopFeedbackMessage = "Transaction cancelled safely. No money was transferred."
            )
        }
    }

    private fun completeFinalTransaction() {
        val contact = _sendState.value.selectedContact ?: return
        val amount = _sendState.value.amountInput.toDoubleOrNull() ?: return

        val completedTx = repository.executeTransaction(contact, amount)

        _sendState.update {
            it.copy(
                currentStep = SendStep.SUCCESS_RECEIPT,
                completedTransaction = completedTx,
                countdownProgress = 0f,
                countdownRemainingSeconds = 0
            )
        }
    }

    fun stepBack() {
        when (_sendState.value.currentStep) {
            SendStep.ENTER_AMOUNT -> {
                _sendState.update { it.copy(currentStep = SendStep.SELECT_RECIPIENT) }
            }
            SendStep.CONFIRM_REVIEW -> {
                _sendState.update { it.copy(currentStep = SendStep.ENTER_AMOUNT) }
            }
            SendStep.DELAYED_SENDING_QUEUE -> {
                // Pressing back in queue triggers immediate stop
                stopSendQueue()
            }
            SendStep.SUCCESS_RECEIPT -> {
                resetSendFlow()
                _currentTab.value = AppTab.HOME
            }
            SendStep.SELECT_RECIPIENT -> {
                _currentTab.value = AppTab.HOME
            }
        }
    }

    fun resetSendFlow() {
        queueCountdownJob?.cancel()
        _sendState.value = SendFlowUiState()
    }
}
