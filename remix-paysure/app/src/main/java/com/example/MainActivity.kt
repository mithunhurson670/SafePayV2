package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.NetworkState
import com.example.ui.components.AddFundsDialog
import com.example.ui.components.BottomNavBar
import com.example.ui.components.ExplainTooltipDialog
import com.example.ui.components.FluencyExplainerDialog
import com.example.ui.components.PaysureTopBar
import com.example.ui.screens.HelpSafetyScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SendMoneyScreen
import com.example.ui.theme.PaysureTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.PaysureViewModel
import com.example.ui.viewmodel.SendStep

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PaysureTheme {
                PaysureApp()
            }
        }
    }
}

@Composable
fun PaysureApp(viewModel: PaysureViewModel = viewModel()) {
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val walletBalance by viewModel.walletBalance.collectAsStateWithLifecycle()
    val isBalanceHidden by viewModel.isBalanceHidden.collectAsStateWithLifecycle()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val networkState by viewModel.networkState.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val sendState by viewModel.sendState.collectAsStateWithLifecycle()
    val activeExplain by viewModel.activeExplain.collectAsStateWithLifecycle()
    val isFluencyExplainerOpen by viewModel.isFluencyExplainerOpen.collectAsStateWithLifecycle()

    var isAddFundsDialogOpen by remember { mutableStateOf(false) }

    // System Back Handler
    BackHandler(enabled = currentTab != AppTab.HOME || sendState.currentStep != SendStep.SELECT_RECIPIENT) {
        if (currentTab == AppTab.SEND_MONEY) {
            if (sendState.currentStep == SendStep.SELECT_RECIPIENT) {
                viewModel.selectTab(AppTab.HOME)
            } else {
                viewModel.stepBack()
            }
        } else {
            viewModel.selectTab(AppTab.HOME)
        }
    }

    // Modal Dialogs
    activeExplain?.let { info ->
        ExplainTooltipDialog(
            title = info.title,
            explanation = info.explanation,
            onDismiss = { viewModel.dismissExplain() }
        )
    }

    if (isFluencyExplainerOpen) {
        FluencyExplainerDialog(
            comfortLevel = viewModel.comfortLevel,
            onDismiss = { viewModel.dismissFluencyExplainer() }
        )
    }

    if (isAddFundsDialogOpen) {
        AddFundsDialog(
            onAdd = { amount -> viewModel.addDemoFunds(amount) },
            onDismiss = { isAddFundsDialogOpen = false }
        )
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            PaysureTopBar(
                networkState = networkState,
                currentLanguage = currentLanguage,
                onToggleNetwork = {
                    val next = when (networkState) {
                        NetworkState.GOOD -> NetworkState.POOR
                        NetworkState.POOR -> NetworkState.OFFLINE
                        NetworkState.OFFLINE -> NetworkState.GOOD
                    }
                    viewModel.setNetworkStatus(next)
                },
                onToggleLanguage = { viewModel.toggleLanguage() },
                onExplainNetwork = {
                    viewModel.triggerExplain(
                        "Network Protection",
                        "Paysure checks your internet signal in real time. If the connection is weak or drops offline, money transfers are safely paused to avoid stuck or duplicate charges."
                    )
                },
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars)
            )
        },
        bottomBar = {
            // Hide bottom bar during active sending countdown queue to prevent accidental tab clicks
            if (sendState.currentStep != SendStep.DELAYED_SENDING_QUEUE) {
                BottomNavBar(
                    selectedTab = currentTab,
                    onTabSelected = { tab ->
                        if (tab == AppTab.SEND_MONEY && currentTab != AppTab.SEND_MONEY) {
                            viewModel.startSendFlow()
                        } else {
                            viewModel.selectTab(tab)
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF7F9FC))
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "tab_transition"
            ) { tab ->
                when (tab) {
                    AppTab.HOME -> {
                        HomeScreen(
                            walletBalance = walletBalance,
                            isBalanceHidden = isBalanceHidden,
                            onToggleBalance = { viewModel.toggleBalanceVisibility() },
                            contacts = contacts,
                            recentTransactions = transactions,
                            networkState = networkState,
                            comfortLevel = viewModel.comfortLevel,
                            onSendMoneyClicked = { viewModel.startSendFlow() },
                            onContactClicked = { contact -> viewModel.startSendFlow(preselectedContact = contact) },
                            onViewHistoryClicked = { viewModel.selectTab(AppTab.HISTORY) },
                            onAddFundsClicked = { isAddFundsDialogOpen = true },
                            onWhyLimitClicked = { viewModel.openFluencyExplainer() },
                            onExplainRequested = { title, desc -> viewModel.triggerExplain(title, desc) },
                            onRecheckNetwork = { viewModel.setNetworkStatus(NetworkState.GOOD) }
                        )
                    }

                    AppTab.SEND_MONEY -> {
                        SendMoneyScreen(
                            state = sendState,
                            contacts = contacts,
                            walletBalance = walletBalance,
                            networkState = networkState,
                            comfortLevel = viewModel.comfortLevel,
                            onBackClicked = { viewModel.stepBack() },
                            onContactSelected = { contact -> viewModel.onSelectContact(contact) },
                            onAmountKeyTap = { key -> viewModel.onAmountKeyTap(key) },
                            onConfirmExtraZeroTap = { viewModel.onConfirmExtraZeroTap() },
                            onProceedToReview = { viewModel.proceedToConfirmationReview() },
                            onStartDelayedQueue = { viewModel.startDelayedSendQueue() },
                            onStopSendQueue = { viewModel.stopSendQueue() },
                            onResetSendFlow = { viewModel.resetSendFlow() },
                            onWhyLimitClicked = { viewModel.openFluencyExplainer() },
                            onExplainRequested = { title, desc -> viewModel.triggerExplain(title, desc) },
                            onRecheckNetwork = { viewModel.setNetworkStatus(NetworkState.GOOD) },
                            onDismissDoublePaymentMistake = { viewModel.onDismissDoublePaymentMistake() },
                            onConfirmSendAnywayAfterDuplicate = { viewModel.onConfirmSendAnywayAfterDuplicate() }
                        )
                    }

                    AppTab.HISTORY -> {
                        HistoryScreen(
                            transactions = transactions,
                            onExplainRequested = { title, desc -> viewModel.triggerExplain(title, desc) }
                        )
                    }

                    AppTab.HELP -> {
                        HelpSafetyScreen(
                            networkState = networkState,
                            comfortLevel = viewModel.comfortLevel,
                            onSetNetwork = { s -> viewModel.setNetworkStatus(s) },
                            onSetFluencyComfort = { l -> viewModel.setForcedFluencyComfort(l) },
                            onAddFunds = { amt -> viewModel.addDemoFunds(amt) },
                            onWhyLimitClicked = { viewModel.openFluencyExplainer() },
                            onExplainRequested = { title, desc -> viewModel.triggerExplain(title, desc) }
                        )
                    }
                }
            }
        }
    }
}
