package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AmoledBlack
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun FlutterExportScreen() {
    val context = LocalContext.current
    var selectedFileIndex by remember { mutableIntStateOf(0) }

    val files = listOf(
        Pair("build_apk.yml", ".github/workflows/build_apk.yml"),
        Pair("pubspec.yaml", "pubspec.yaml"),
        Pair("main.dart", "lib/main.dart")
    )

    val currentContent = when (selectedFileIndex) {
        0 -> GITHUB_WORKFLOW_CONTENT
        1 -> PUBSPEC_YAML_CONTENT
        else -> MAIN_DART_PREVIEW_CONTENT
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AmoledBlack)
            .padding(horizontal = 16.dp)
            .testTag("flutter_export_screen_list"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, CyanNeon.copy(alpha = 0.5f), RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "GitHub Mobile CI/CD & Flutter Code",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextWhite
                            )
                            Text(
                                text = "Ready for GitHub Mobile / Web APK Generation",
                                fontSize = 12.sp,
                                color = CyanNeon
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CyanNeon.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(18.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Since you do not have a PC, create your GitHub repository on mobile, copy these 3 files, and GitHub Actions will automatically compile your APK and create a downloadable GitHub Release!",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Tab Row to select between the 3 files
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkBorder)
            ) {
                TabRow(
                    selectedTabIndex = selectedFileIndex,
                    containerColor = DarkSurface,
                    contentColor = CyanNeon,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedFileIndex]),
                            color = CyanNeon
                        )
                    }
                ) {
                    files.forEachIndexed { index, pair ->
                        Tab(
                            selected = selectedFileIndex == index,
                            onClick = { selectedFileIndex = index },
                            text = { Text(pair.first, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }
            }
        }

        // Action header with Copy button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "File: ${files[selectedFileIndex].second}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                    Text(
                        text = "${currentContent.lines().size} lines of code",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText(files[selectedFileIndex].first, currentContent))
                        Toast.makeText(context, "Copied ${files[selectedFileIndex].first} to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.height(40.dp).testTag("copy_file_content_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyanNeon,
                        contentColor = AmoledBlack
                    )
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy File", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Code Viewer Block
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, DarkBorder, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkCardBg)
            ) {
                Box(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = currentContent,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextWhite,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

private val GITHUB_WORKFLOW_CONTENT = """
name: Build & Release OMR APK

on:
  push:
    branches: [ main, master ]
    tags: [ 'v*' ]
  workflow_dispatch:

jobs:
  build:
    name: Build Flutter APK
    runs-on: ubuntu-latest

    steps:
      - name: Checkout Code
        uses: actions/checkout@v4

      - name: Setup Java JDK
        uses: actions/setup-java@v4
        with:
          distribution: 'zulu'
          java-version: '17'

      - name: Setup Flutter
        uses: subosito/flutter-action@v2
        with:
          channel: 'stable'
          cache: true

      - name: Get Dependencies
        run: flutter pub get

      - name: Build Release APK
        run: flutter build apk --release

      - name: Upload APK Artifact
        uses: actions/upload-artifact@v4
        with:
          name: app-release-apk
          path: build/app/outputs/flutter-apk/app-release.apk

      - name: Create GitHub Release
        if: startsWith(github.ref, 'refs/tags/') || github.event_name == 'workflow_dispatch'
        uses: softprops/action-gh-release@v2
        with:
          files: build/app/outputs/flutter-apk/app-release.apk
          tag_name: release-@@RUN_NUM@@
          name: "RIP Exam OMR Release #@@RUN_NUM@@"
          body: |
            Automated Release Build for RIP Exam OMR Check.
            Developer: Nahid Hasan (University of Barishal)
            Download the attached app-release.apk below.
        env:
          GITHUB_TOKEN: @@TOKEN@@
""".trimIndent()
    .replace("@@RUN_NUM@@", "$" + "{{ github.run_number }}")
    .replace("@@TOKEN@@", "$" + "{{ secrets.GITHUB_TOKEN }}")

private const val PUBSPEC_YAML_CONTENT = """name: rip_exam_omr
description: "AMOLED OMR sheet scanner and evaluator with Computer Vision by Nahid Hasan"
publish_to: "none"
version: 1.0.0+1

environment:
  sdk: ">=3.0.0 <4.0.0"

dependencies:
  flutter:
    sdk: flutter
  cupertino_icons: ^1.0.8
  image: ^4.2.0
  camera: ^0.10.6
  image_picker: ^1.1.2
  path_provider: ^2.1.3
  hive: ^2.2.3
  hive_flutter: ^1.1.0
  fl_chart: ^0.68.0
  intl: ^0.19.0
  url_launcher: ^6.3.0

dev_dependencies:
  flutter_test:
    sdk: flutter
  flutter_lints: ^3.0.0

flutter:
  uses-material-design: true
"""

private const val MAIN_DART_PREVIEW_CONTENT = """// See full lib/main.dart in project directory
// Pure Dark / AMOLED Black theme (#000000)
// Complete Flutter application featuring:
// - iOS Motion Splash Screen ("Nahid Bro", "RIP Exam OMR Check")
// - 1723x2448 Coordinate OMR Engine
// - 4-Digit Roll Number extraction
// - 100 Questions evaluation with negative marking
// - Student Profile & Hive persistence
// - Interactive charts & Developer details modal (Nahid Hasan)
"""
