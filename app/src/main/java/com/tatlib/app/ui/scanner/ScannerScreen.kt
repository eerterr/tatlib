package com.tatlib.app.ui.scanner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.data.MockData
import com.tatlib.app.ui.components.RoundBackButton
import com.tatlib.app.ui.navigation.Routes
import kotlinx.coroutines.delay

/**
 * Mock camera / OCR screen. There is no real CameraX or ML text-recognition wired up —
 * tapping the shutter just simulates "we recognised a page" and drops the user into a
 * book detail screen after a short delay, exactly where a real scan-and-match flow would
 * hand off once OCR + matching is implemented.
 */
@Composable
fun ScannerScreen(navController: NavHostController) {
    var scanning by remember { mutableStateOf(false) }

    LaunchedEffect(scanning) {
        if (scanning) {
            delay(1200)
            navController.navigate(Routes.bookDetail(MockData.shurale.id)) {
                popUpTo(Routes.SCANNER) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1E1A))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScannerTopBar(navController)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(30.dp)
            ) {
                ScannerFrame(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.9f)
                        .align(Alignment.Center)
                )

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Камераны китапка яки биткә юнәлтегез",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Без текстны табып, аны уку өчен әзерләячәкбез",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 48.dp, top = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                ShutterButton(scanning = scanning, onClick = { if (!scanning) scanning = true })
            }
        }
    }
}

@Composable
private fun ScannerTopBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundBackButton(onClick = { navController.popBackStack() })
        IconButton(onClick = { navController.navigate(Routes.LIBRARY) }) {
            Icon(
                Icons.Filled.MenuBook,
                contentDescription = "Китапханәгә кайту",
                tint = Color.White
            )
        }
    }
}

@Composable
private fun ScannerFrame(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val cornerLen = size.minDimension * 0.12f
        val strokeWidth = 6f
        val c = Color.White.copy(alpha = 0.9f)

        // top-left
        drawLine(c, Offset(0f, cornerLen), Offset(0f, 0f), strokeWidth)
        drawLine(c, Offset(0f, 0f), Offset(cornerLen, 0f), strokeWidth)
        // top-right
        drawLine(c, Offset(size.width - cornerLen, 0f), Offset(size.width, 0f), strokeWidth)
        drawLine(c, Offset(size.width, 0f), Offset(size.width, cornerLen), strokeWidth)
        // bottom-left
        drawLine(c, Offset(0f, size.height - cornerLen), Offset(0f, size.height), strokeWidth)
        drawLine(c, Offset(0f, size.height), Offset(cornerLen, size.height), strokeWidth)
        // bottom-right
        drawLine(c, Offset(size.width - cornerLen, size.height), Offset(size.width, size.height), strokeWidth)
        drawLine(c, Offset(size.width, size.height), Offset(size.width, size.height - cornerLen), strokeWidth)
    }
}

@Composable
private fun ShutterButton(scanning: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(85.dp)
            .clickable(enabled = !scanning, onClick = onClick)
            .background(Color.White.copy(alpha = 0.15f), CircleShape)
            .padding(8.dp)
            .background(Color.White, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (scanning) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, strokeWidth = 3.dp)
        } else {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}
