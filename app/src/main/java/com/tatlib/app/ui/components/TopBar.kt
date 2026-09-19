package com.tatlib.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.tatlib.app.ui.AppLanguage

/** TAT / RU switch that appears in the top-right corner of almost every screen. */
@Composable
fun LanguageToggle(
    language: AppLanguage,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onToggle
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "TAT",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (language == AppLanguage.TAT) FontWeight.SemiBold else FontWeight.Normal,
            textDecoration = if (language == AppLanguage.TAT) TextDecoration.Underline else null
        )
        Text(" / ", style = MaterialTheme.typography.bodyLarge)
        Text(
            "RU",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (language == AppLanguage.RU) FontWeight.SemiBold else FontWeight.Normal,
            textDecoration = if (language == AppLanguage.RU) TextDecoration.Underline else null
        )
    }
}

/** Row with an optional back chevron on the left and the language toggle / overflow
 *  on the right — used at the top of most screens instead of a heavy Material AppBar,
 *  matching the light-touch headers in the Figma file. */
@Composable
fun ScreenTopBar(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    showLanguageToggle: Boolean = true,
    language: AppLanguage = AppLanguage.TAT,
    onToggleLanguage: () -> Unit = {},
    showOverflow: Boolean = false,
    onOverflowClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            RoundBackButton(onClick = onBack)
        }
        Spacer(Modifier.weight(1f))
        if (showLanguageToggle) {
            LanguageToggle(language = language, onToggle = onToggleLanguage)
        }
        if (showOverflow) {
            IconButton(onClick = onOverflowClick) {
                Icon(Icons.Default.MoreVert, contentDescription = "Күбрәк")
            }
        }
    }
}
