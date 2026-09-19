package com.tatlib.app.ui.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.tatlib.app.data.MockData
import com.tatlib.app.ui.AppViewModel
import com.tatlib.app.ui.components.FilterChip
import com.tatlib.app.ui.components.LevelBadge
import com.tatlib.app.ui.components.QuoteCard
import com.tatlib.app.ui.components.SimpleLineChart
import com.tatlib.app.ui.components.SoftCard
import com.tatlib.app.ui.components.StatTile

private val periods = listOf("Атна", "Ай", "Ел")
private val trend = listOf(0.15f, 0.28f, 0.22f, 0.4f, 0.55f, 0.48f, 0.7f)

@Composable
fun ProgressScreen(navController: NavHostController, viewModel: AppViewModel) {
    var selectedPeriod by remember { mutableStateOf(periods[1]) }
    val stats = MockData.progressStats
    val level = viewModel.userLevel ?: stats.currentLevel

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 30.dp)
    ) {
        Text(
            "Алгарышың",
            style = MaterialTheme.typography.displayLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            periods.forEach { period ->
                FilterChip(
                    text = period,
                    selected = selectedPeriod == period,
                    onClick = { selectedPeriod = period }
                )
            }
        }

        SoftCard(modifier = Modifier.padding(top = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Хәзерге дәрәҗәң", style = MaterialTheme.typography.bodyLarge)
                    Text(
                        level.label,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                LevelBadge(level)
            }

            Text(
                "Тагын бераз — һәм ${nextLevelLabel(level)} дәрәҗәсен сынап карарга була",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp, bottom = 20.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatTile(value = stats.textsRead.toString(), label = "укылган текст")
                StatTile(value = "+${stats.newWords}", label = "яңа сүз")
                StatTile(value = "${stats.originalTextPercent}%", label = "оригинал текстның")
            }

            SimpleLineChart(
                values = trend,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(top = 24.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                stats.monthLabels.forEach { month ->
                    Text(
                        month,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        QuoteCard(text = MockData.inspirationalQuotes[1])
        Spacer(Modifier.height(30.dp))
    }
}

private fun nextLevelLabel(current: com.tatlib.app.data.TatarLevel): String =
    when (current) {
        com.tatlib.app.data.TatarLevel.A1 -> "A2"
        com.tatlib.app.data.TatarLevel.A2 -> "B1"
        com.tatlib.app.data.TatarLevel.B1 -> "B2"
        com.tatlib.app.data.TatarLevel.B2 -> "C1"
        com.tatlib.app.data.TatarLevel.C1 -> "C1"
    }
