@file:OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)

package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Contact
import com.example.data.model.FluencyComfortLevel
import com.example.data.model.NetworkState
import com.example.ui.components.AmountInWordsCard
import com.example.ui.components.DoublePaymentDialog
import com.example.ui.components.NetworkProtectionBanner
import com.example.ui.components.SpendPatternBanner
import com.example.ui.components.TrustRingAvatar
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CoralStop
import com.example.ui.theme.CoralStopLight
import com.example.ui.theme.EmeraldTrust
import com.example.ui.theme.EmeraldTrustLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySecondary
import com.example.ui.viewmodel.SendFlowUiState
import com.example.ui.viewmodel.SendStep
import com.example.util.Formatters

@Composable
fun SendMoneyScreen(
    state: SendFlowUiState,
    contacts: List<Contact>,
    walletBalance: Double,
    networkState: NetworkState,
    comfortLevel: FluencyComfortLevel,
    onBackClicked: () -> Unit,
    onContactSelected: (Contact) -> Unit,
    onAmountKeyTap: (String) -> Unit,
    onConfirmExtraZeroTap: () -> Unit,
    onProceedToReview: () -> Unit,
    onStartDelayedQueue: () -> Unit,
    onStopSendQueue: () -> Unit,
    onResetSendFlow: () -> Unit,
    onWhyLimitClicked: () -> Unit,
    onExplainRequested: (String, String) -> Unit,
    onRecheckNetwork: () -> Unit,
    onDismissDoublePaymentMistake: () -> Unit,
    onConfirmSendAnywayAfterDuplicate: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Check Double Payment Dialog
    if (state.isDoublePaymentWarningOpen && state.duplicatePreviousTx != null && state.selectedContact != null) {
        val amount = state.amountInput.toDoubleOrNull() ?: 0.0
        DoublePaymentDialog(
            previousTransaction = state.duplicatePreviousTx,
            currentAmount = amount,
            recipientName = state.selectedContact.name,
            onConfirmSendAgain = onConfirmSendAnywayAfterDuplicate,
            onCancelMistake = onDismissDoublePaymentMistake
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Top Navigation Header with Step Progress Indicator & Explicit Back Button
        if (state.currentStep != SendStep.SUCCESS_RECEIPT && state.currentStep != SendStep.DELAYED_SENDING_QUEUE) {
            StepFlowHeader(
                currentStep = state.currentStep,
                onBack = onBackClicked,
                onExplainHeader = {
                    onExplainRequested(
                        "Guided 3-Step Flow",
                        "Paysure walks you through 3 calm steps: first pick who to pay, next enter the amount safely with extra-zero checks, and finally review everything before sending."
                    )
                }
            )
        }

        // Body Content by Step
        when (state.currentStep) {
            SendStep.SELECT_RECIPIENT -> {
                Step1SelectRecipient(
                    contacts = contacts,
                    onContactSelected = onContactSelected,
                    onExplainContact = { c ->
                        onExplainRequested(
                            "${c.name} (${c.trustLevel.label})",
                            "${c.trustLevel.description} You have made ${c.pastTransactionCount} past payments."
                        )
                    }
                )
            }

            SendStep.ENTER_AMOUNT -> {
                state.selectedContact?.let { contact ->
                    Step2EnterAmount(
                        contact = contact,
                        amountInput = state.amountInput,
                        amountInWords = state.amountInWords,
                        isExtraZeroWarning = state.isExtraZeroWarning,
                        isExtraZeroVerified = state.isExtraZeroVerifiedByTap,
                        walletBalance = walletBalance,
                        networkState = networkState,
                        comfortLevel = comfortLevel,
                        onKeyTap = onAmountKeyTap,
                        onConfirmExtraZeroTap = onConfirmExtraZeroTap,
                        onProceed = onProceedToReview,
                        onWhyLimitClicked = onWhyLimitClicked,
                        onRecheckNetwork = onRecheckNetwork,
                        onExplainAmount = {
                            onExplainRequested(
                                "Amount Input & Extra-Zero Protection",
                                "Type the amount using the keypad. Paysure writes out the amount in words directly underneath so you never accidentally add an extra zero."
                            )
                        }
                    )
                }
            }

            SendStep.CONFIRM_REVIEW -> {
                state.selectedContact?.let { contact ->
                    Step3ReviewAndConfirm(
                        contact = contact,
                        amountInput = state.amountInput,
                        amountInWords = state.amountInWords,
                        spendPatternNotice = state.spendPatternNotice,
                        stopFeedbackMessage = state.stopFeedbackMessage,
                        networkState = networkState,
                        onConfirmSend = onStartDelayedQueue,
                        onBack = onBackClicked,
                        onRecheckNetwork = onRecheckNetwork,
                        onExplainReview = {
                            onExplainRequested(
                                "Review Step & Safety Queue",
                                "Double check all details. When you tap 'Confirm & Send', the money is NOT sent immediately—you get a 10-second countdown with a STOP button in case you change your mind."
                            )
                        }
                    )
                }
            }

            SendStep.DELAYED_SENDING_QUEUE -> {
                state.selectedContact?.let { contact ->
                    StepDelayedQueue(
                        contact = contact,
                        amountInput = state.amountInput,
                        remainingSeconds = state.countdownRemainingSeconds,
                        progress = state.countdownProgress,
                        onStop = onStopSendQueue
                    )
                }
            }

            SendStep.SUCCESS_RECEIPT -> {
                state.selectedContact?.let { contact ->
                    StepSuccessReceipt(
                        contact = contact,
                        amountInput = state.amountInput,
                        transaction = state.completedTransaction,
                        walletBalance = walletBalance,
                        onDone = onResetSendFlow
                    )
                }
            }
        }
    }
}

/**
 * Clean Top Step Progress Indicator with Explicit "← Back" button
 */
@Composable
private fun StepFlowHeader(
    currentStep: SendStep,
    onBack: () -> Unit,
    onExplainHeader: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Explicit, unambiguous Back button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onBack() }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
                    .testTag("send_step_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = NavyPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Back",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            }

            // Step Label
            Text(
                text = when (currentStep) {
                    SendStep.SELECT_RECIPIENT -> "Step 1 of 3: Choose Recipient"
                    SendStep.ENTER_AMOUNT -> "Step 2 of 3: Enter Amount"
                    SendStep.CONFIRM_REVIEW -> "Step 3 of 3: Review Details"
                    else -> ""
                },
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = EmeraldTrust
            )

            IconButton(
                onClick = onExplainHeader,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.HelpOutline,
                    contentDescription = "Explain Steps",
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Visual Progress Bars for 3 steps
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val currentStepIdx = when (currentStep) {
                SendStep.SELECT_RECIPIENT -> 1
                SendStep.ENTER_AMOUNT -> 2
                SendStep.CONFIRM_REVIEW -> 3
                else -> 3
            }

            for (i in 1..3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(
                            if (i <= currentStepIdx) EmeraldTrust else Color(0xFFCBD5E1)
                        )
                )
            }
        }
    }
}

/**
 * Step 1: Recipient Selection with Visual Trust Rings and search
 */
@Composable
private fun Step1SelectRecipient(
    contacts: List<Contact>,
    onContactSelected: (Contact) -> Unit,
    onExplainContact: (Contact) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery, contacts) {
        if (searchQuery.isBlank()) contacts
        else contacts.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.roleOrRelation.contains(searchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "Who would you like to pay?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
            Text(
                text = "Look for the green trust ring to confirm past safe recipients.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search name or role (e.g. Ramesh, Milk)...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF64748B))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", tint = Color(0xFF64748B))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyPrimary,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recipient_search_input")
            )
        }

        // List of Contacts with Trust Rings
        items(filtered.size) { idx ->
            val contact = filtered[idx]
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { onContactSelected(contact) },
                        onLongClick = { onExplainContact(contact) }
                    )
                    .testTag("recipient_card_${contact.id}")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    TrustRingAvatar(
                        contact = contact,
                        sizeDp = 60.dp
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = contact.roleOrRelation + " • " + contact.phoneMasked,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF475569)
                        )
                        // Trust Level explanation label
                        Text(
                            text = "🛡️ ${contact.trustLevel.label}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(contact.trustLevel.ringColorHex)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Pay",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Step 2: Enter Amount with extra-zero prevention, fluency limit, and keypad
 */
@Composable
private fun Step2EnterAmount(
    contact: Contact,
    amountInput: String,
    amountInWords: String,
    isExtraZeroWarning: Boolean,
    isExtraZeroVerified: Boolean,
    walletBalance: Double,
    networkState: NetworkState,
    comfortLevel: FluencyComfortLevel,
    onKeyTap: (String) -> Unit,
    onConfirmExtraZeroTap: () -> Unit,
    onProceed: () -> Unit,
    onWhyLimitClicked: () -> Unit,
    onRecheckNetwork: () -> Unit,
    onExplainAmount: () -> Unit
) {
    val amountNum = amountInput.toDoubleOrNull() ?: 0.0
    val isAmountValid = amountNum > 0
    val isOverBalance = amountNum > walletBalance
    val isOverFluencyLimit = amountNum > comfortLevel.transferLimit

    // If extra-zero check is active, user MUST tap words card first
    val isExtraZeroSatisfied = !isExtraZeroWarning || isExtraZeroVerified

    val canProceed = isAmountValid &&
            !isOverBalance &&
            !isOverFluencyLimit &&
            isExtraZeroSatisfied &&
            networkState.isSendAllowed

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Network Protection Warning if network dropped
        if (!networkState.isSendAllowed) {
            item {
                NetworkProtectionBanner(
                    networkState = networkState,
                    onTryAgain = onRecheckNetwork
                )
            }
        }

        // Recipient Summary Pill
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TrustRingAvatar(
                        contact = contact,
                        sizeDp = 44.dp
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Paying ${contact.name}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "${contact.roleOrRelation} • ${contact.trustLevel.label}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        // Large Numeric Amount Display
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, if (isOverBalance || isOverFluencyLimit) CoralStop else Color(0xFFCBD5E1)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("amount_display_card")
                    .combinedClickable(
                        onClick = {},
                        onLongClick = onExplainAmount
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ENTER AMOUNT",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        letterSpacing = 1.sp
                    )

                    Text(
                        text = if (amountInput.isEmpty()) "₹0" else "₹$amountInput",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = when {
                            isOverBalance || isOverFluencyLimit -> CoralStop
                            else -> NavyPrimary
                        }
                    )

                    // Balance reminder
                    Text(
                        text = "Available safe balance: ${Formatters.formatCurrency(walletBalance)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isOverBalance) CoralStop else Color(0xFF64748B),
                        fontWeight = if (isOverBalance) FontWeight.Bold else FontWeight.Normal
                    )

                    // Fluency limit warning banner if over limit
                    if (isOverFluencyLimit) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFFFEBEE))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "⚠️ Amount exceeds your current safety limit of ${Formatters.formatCurrency(comfortLevel.transferLimit)}.",
                                style = MaterialTheme.typography.labelSmall,
                                color = CoralStop,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // Feature 6: Dynamic Amount in Words with Extra-Zero Protection
        item {
            AmountInWordsCard(
                amountInWords = amountInWords,
                isHighAmountWarning = isExtraZeroWarning,
                isConfirmedByTap = isExtraZeroVerified,
                onConfirmTap = onConfirmExtraZeroTap
            )
        }

        // Fluency Cap Info Link (Feature 4)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF1F5F9))
                    .clickable { onWhyLimitClicked() }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🛡️ Safe limit for this session: ${Formatters.formatCurrency(comfortLevel.transferLimit)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Why this limit?",
                    style = MaterialTheme.typography.labelSmall,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Custom Numeric Touch Keypad for effortless thumb typing
        item {
            NumericKeypad(onKeyTap = onKeyTap)
        }

        // Sticky Thumb-zone Continue Button
        item {
            Button(
                onClick = onProceed,
                enabled = canProceed,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EmeraldTrust,
                    disabledContainerColor = Color(0xFFCBD5E1)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("amount_continue_button")
            ) {
                Text(
                    text = when {
                        !networkState.isSendAllowed -> "Network Weak (Paused)"
                        isOverBalance -> "Insufficient Balance"
                        isOverFluencyLimit -> "Exceeds Session Limit"
                        isExtraZeroWarning && !isExtraZeroVerified -> "Tap Words Above to Confirm Amount"
                        else -> "Continue to Review"
                    },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Clean Numeric Keypad for accessible typing
 */
@Composable
private fun NumericKeypad(onKeyTap: (String) -> Unit) {
    val keys = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("00", "0", "BACKSPACE")
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("numeric_keypad"),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        keys.forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { key ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (key == "BACKSPACE") Color(0xFFF1F5F9) else Color.White)
                            .clickable { onKeyTap(key) }
                            .testTag("key_$key")
                    ) {
                        if (key == "BACKSPACE") {
                            Icon(
                                imageVector = Icons.Default.Backspace,
                                contentDescription = "Delete last digit",
                                tint = Color(0xFF475569)
                            )
                        } else {
                            Text(
                                text = key,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Step 3: Confirmation & Review Card
 */
@Composable
private fun Step3ReviewAndConfirm(
    contact: Contact,
    amountInput: String,
    amountInWords: String,
    spendPatternNotice: String?,
    stopFeedbackMessage: String?,
    networkState: NetworkState,
    onConfirmSend: () -> Unit,
    onBack: () -> Unit,
    onRecheckNetwork: () -> Unit,
    onExplainReview: () -> Unit
) {
    val amountNum = amountInput.toDoubleOrNull() ?: 0.0

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Feedback if stopped earlier
        if (stopFeedbackMessage != null) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CoralStopLight),
                    border = BorderStroke(1.dp, CoralStop.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = CoralStop,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = stopFeedbackMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = CoralStop
                        )
                    }
                }
            }
        }

        // Network Alert if offline
        if (!networkState.isSendAllowed) {
            item {
                NetworkProtectionBanner(
                    networkState = networkState,
                    onTryAgain = onRecheckNetwork
                )
            }
        }

        item {
            Text(
                text = "Review Before Sending",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
            Text(
                text = "Take a moment to verify recipient and amount. You will still have a 10-second STOP window after tapping send.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // Feature 3: Spend-Pattern Mirror banner if applicable
        if (spendPatternNotice != null) {
            item {
                SpendPatternBanner(message = spendPatternNotice)
            }
        }

        // Review Details Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("review_details_card")
                    .combinedClickable(
                        onClick = {},
                        onLongClick = onExplainReview
                    )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Recipient Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        TrustRingAvatar(
                            contact = contact,
                            sizeDp = 64.dp
                        )
                        Column {
                            Text(
                                text = "Recipient",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = contact.name,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = NavyPrimary
                            )
                            Text(
                                text = "${contact.roleOrRelation} • ${contact.trustLevel.label}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(contact.trustLevel.ringColorHex),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFFE2E8F0))
                    )

                    // Amount Section
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Transfer Amount",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = Formatters.formatCurrency(amountNum),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "($amountInWords)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF475569)
                        )
                    }

                    // Zero Jargon Reassurance
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = EmeraldTrust,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Protected by Paysure Instant Recall & Mistake Shield",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF334155),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Sticky Thumb-zone Action Button ("Confirm & Send")
        item {
            Button(
                onClick = onConfirmSend,
                enabled = networkState.isSendAllowed,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldTrust),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .testTag("confirm_and_send_button")
            ) {
                Text(
                    text = "Confirm & Send",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        item {
            OutlinedButton(
                onClick = onBack,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("review_change_amount_button")
            ) {
                Text(
                    text = "Change Amount or Recipient",
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Feature 2: Delayed Send Queue with large animated STOP button
 * Shows a 10-second countdown with visual progress.
 * If user taps STOP, cancels immediately with zero deduction.
 */
@Composable
private fun StepDelayedQueue(
    contact: Contact,
    amountInput: String,
    remainingSeconds: Int,
    progress: Float,
    onStop: () -> Unit
) {
    val amountNum = amountInput.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Sending Payment...",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = NavyPrimary
        )

        Text(
            text = "Sending ${Formatters.formatCurrency(amountNum)} to ${contact.name}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF475569),
            modifier = Modifier.padding(top = 6.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Circular Countdown Progress Box
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(200.dp)
        ) {
            // Animated Circular Progress
            CircularProgressIndicator(
                progress = { progress },
                strokeWidth = 12.dp,
                color = EmeraldTrust,
                trackColor = Color(0xFFE2E8F0),
                modifier = Modifier.fillMaxSize()
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$remainingSeconds",
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyPrimary
                )
                Text(
                    text = "seconds remaining",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Changed your mind or made a mistake?\nTap STOP below to cancel safely.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = Color(0xFF475569)
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Large, clearly visible STOP button (Feature 2)
        Button(
            onClick = onStop,
            colors = ButtonDefaults.buttonColors(containerColor = CoralStop),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .testTag("delayed_queue_stop_button")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.StopCircle,
                    contentDescription = "Stop Payment",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "STOP PAYMENT",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Step 5: Payment Successful Screen
 */
@Composable
private fun StepSuccessReceipt(
    contact: Contact,
    amountInput: String,
    transaction: com.example.data.model.PaymentTransaction?,
    walletBalance: Double,
    onDone: () -> Unit
) {
    val amountNum = amountInput.toDoubleOrNull() ?: 0.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success Badge
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Payment Successful",
                tint = Color(0xFF2E7D32),
                modifier = Modifier.size(64.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Payment Successful!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = NavyPrimary
        )

        Text(
            text = "Money safely transferred to ${contact.name}",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Receipt Summary Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Amount Sent", color = Color(0xFF64748B), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        Formatters.formatCurrency(amountNum),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge,
                        color = NavyPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Paid to", color = Color(0xFF64748B), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        contact.name,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyLarge,
                        color = NavyPrimary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Trust Circle", color = Color(0xFF64748B), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "🛡️ ${contact.trustLevel.label}",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = EmeraldTrust
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Remaining Balance", color = Color(0xFF64748B), style = MaterialTheme.typography.bodyMedium)
                    Text(
                        Formatters.formatCurrency(walletBalance),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF334155)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onDone,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .testTag("success_done_button")
        ) {
            Text(
                text = "Back to Home",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
