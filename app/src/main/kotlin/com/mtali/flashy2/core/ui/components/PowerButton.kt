package com.mtali.flashy2.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mtali.flashy2.R
import com.mtali.flashy2.core.ui.theme.FlashyAmber
import com.mtali.flashy2.core.ui.theme.FlashyTheme
import com.mtali.flashy2.core.ui.theme.InactiveGray

/**
 * The central power toggle that lives inside the [CircularSlider]. Renders as a tinted circle whose
 * fill animates between [onColor] and [offColor].
 */
@Composable
fun PowerButton(
  isOn: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  size: Dp = 120.dp,
  onColor: Color = FlashyAmber.copy(alpha = 0.18f),
  offColor: Color = Color.Transparent,
  iconTint: Color = FlashyAmber,
) {
  val background by animateColorAsState(if (isOn) onColor else offColor, label = "powerBg")
  Surface(
    onClick = onClick,
    shape = CircleShape,
    color = background,
    modifier = modifier.size(size),
  ) {
    Box(contentAlignment = Alignment.Center) {
      Icon(
        painter = painterResource(R.drawable.power),
        contentDescription = stringResource(R.string.cd_power),
        tint = if (isOn) iconTint else InactiveGray,
        modifier = Modifier.size(size / 2),
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun PowerButtonPreview() {
  FlashyTheme {
    PowerButton(isOn = true, onClick = {})
  }
}
