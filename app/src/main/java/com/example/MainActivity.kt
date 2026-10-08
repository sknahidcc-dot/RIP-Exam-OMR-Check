package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AppTab
import com.example.ui.MainViewModel
import com.example.ui.components.DeveloperDialog
import com.example.ui.components.SplashScreen
import com.example.ui.components.TopNavBar
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.ExamSetupScreen
import com.example.ui.screens.FlutterExportScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.StudentsScreen
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val isSplashVisible by viewModel.isSplashVisible.collectAsState()
                val currentTab by viewModel.currentTab.collectAsState()
                val showDeveloperDialog by viewModel.showDeveloperDialog.collectAsState()

                if (isSplashVisible) {
                    SplashScreen(
                        onContinue = { viewModel.dismissSplash() }
                    )
                } else {
                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AmoledBlack),
                        containerColor = AmoledBlack,
                        contentColor = TextWhite,
                        topBar = {
                            TopNavBar(
                                onDeveloperClick = { viewModel.setDeveloperDialog(true) }
                            )
                        },
                        bottomBar = {
                            AppBottomNavigationBar(
                                currentTab = currentTab,
                                onTabSelected = { viewModel.setTab(it) }
                            )
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .background(AmoledBlack)
                        ) {
                            AnimatedContent(
                                targetState = currentTab,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "tab_transition"
                            ) { tab ->
                                when (tab) {
                                    AppTab.SCANNER -> ScannerScreen(viewModel = viewModel)
                                    AppTab.EXAM_SETUP -> ExamSetupScreen(viewModel = viewModel)
                                    AppTab.STUDENTS -> StudentsScreen(viewModel = viewModel)
                                    AppTab.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                                    AppTab.FLUTTER_EXPORT -> FlutterExportScreen()
                                }
                            }
                        }
                    }

                    if (showDeveloperDialog) {
                        DeveloperDialog(
                            onDismiss = { viewModel.setDeveloperDialog(false) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppBottomNavigationBar(
    currentTab: AppTab,
    onTabSelected: (AppTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .drawBehind {
                drawLine(
                    color = DarkBorder,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bottom_nav_bar"),
        containerColor = DarkSurface,
        tonalElevation = 0.dp
    ) {
        AppTab.entries.forEach { tab ->
            val isSelected = currentTab == tab
            val icon = when (tab) {
                AppTab.SCANNER -> Icons.Default.QrCodeScanner
                AppTab.EXAM_SETUP -> Icons.Default.Tune
                AppTab.STUDENTS -> Icons.Default.People
                AppTab.ANALYTICS -> Icons.Default.Insights
                AppTab.FLUTTER_EXPORT -> Icons.Default.Code
            }

            NavigationBarItem(
                selected = isSelected,
                onClick = { onTabSelected(tab) },
                icon = {
                    Icon(
                        imageVector = icon,
                        contentDescription = tab.title,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = tab.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AmoledBlack,
                    selectedTextColor = CyanNeon,
                    indicatorColor = CyanNeon,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                ),
                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
        }
    }
}
