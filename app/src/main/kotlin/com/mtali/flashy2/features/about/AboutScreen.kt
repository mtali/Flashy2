package com.mtali.flashy2.features.about

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mtali.flashy2.BuildConfig
import com.mtali.flashy2.R
import com.mtali.flashy2.core.ui.theme.FlashyAmber
import com.mtali.flashy2.core.ui.theme.FlashyTheme

private const val SOURCE_URL = "https://github.com/mtali/flashy2"
private const val ISSUES_URL = "https://github.com/mtali/flashy2/issues"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutRoute(
  onBackClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val uriHandler = LocalUriHandler.current
  Scaffold(
    modifier = modifier.fillMaxSize(),
    topBar = {
      TopAppBar(
        title = { Text(stringResource(R.string.about)) },
        navigationIcon = {
          IconButton(onClick = onBackClick) {
            Icon(painterResource(R.drawable.baseline_arrow_back_24), stringResource(R.string.cd_back))
          }
        },
      )
    },
  ) { padding ->
    Column(
      modifier =
      Modifier
        .padding(padding)
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Icon(
        painter = painterResource(R.drawable.flash),
        contentDescription = null,
        tint = FlashyAmber,
        modifier = Modifier.size(72.dp).padding(top = 24.dp),
      )
      Text(stringResource(R.string.app_name), style = MaterialTheme.typography.titleLarge)
      Text(
        stringResource(R.string.version_text, BuildConfig.VERSION_NAME),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Text(
        stringResource(R.string.about_tagline),
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier.padding(top = 8.dp),
      )
      Text(stringResource(R.string.created_by), style = MaterialTheme.typography.bodyMedium)
      Text(
        stringResource(R.string.based_on),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )

      HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

      SectionTitle(stringResource(R.string.contribute))
      LinkRow(R.drawable.github, stringResource(R.string.source_code)) { uriHandler.openUri(SOURCE_URL) }
      LinkRow(R.drawable.code, stringResource(R.string.report_problem)) { uriHandler.openUri(ISSUES_URL) }

      HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

      SectionTitle(stringResource(R.string.oss_licenses))
      Text(
        "${stringResource(R.string.android_jetpack)} · ${stringResource(R.string.apache2)}",
        style = MaterialTheme.typography.bodyMedium,
      )
      Text(
        "${stringResource(R.string.kotlin)} · ${stringResource(R.string.apache2)}",
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(bottom = 24.dp),
      )
    }
  }
}

@Composable
private fun SectionTitle(title: String) {
  Text(
    text = title,
    style = MaterialTheme.typography.titleLarge,
    color = MaterialTheme.colorScheme.primary,
    fontWeight = FontWeight.SemiBold,
    modifier = Modifier.fillMaxWidth(),
  )
}

@Composable
private fun LinkRow(
  icon: Int,
  label: String,
  onClick: () -> Unit,
) {
  TextButton(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
    Icon(painterResource(icon), contentDescription = null, modifier = Modifier.size(20.dp))
    Text(label, modifier = Modifier.padding(start = 12.dp).weight(1f))
  }
}

@Preview(showBackground = true)
@Composable
private fun AboutPreview() {
  FlashyTheme {
    AboutRoute(onBackClick = {})
  }
}
