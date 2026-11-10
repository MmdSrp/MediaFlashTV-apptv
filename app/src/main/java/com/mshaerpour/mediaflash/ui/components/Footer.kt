package com.mshaerpour.mediaflash.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mshaerpour.mediaflash.R
import com.mshaerpour.mediaflash.ui.theme.Accent
import com.mshaerpour.mediaflash.ui.theme.MicaDeep
import com.mshaerpour.mediaflash.ui.theme.StrokeSubtle

/**
 * Windows 11-style status bar / footer.
 *
 * A thin, 32dp-high strip at the very bottom of the screen that mimics the
 * Windows 11 shell bottom bar: small accent indicator dot + author credit.
 * The background is a subtle gradient that fades into the main Mica surface.
 *
 * Text: "Created By Mohammad Shaerpour"
 */
@Composable
fun AppFooter(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, MicaDeep.copy(alpha = 0.95f)),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth(),
        ) {
            // Left accent dot (Windows 11-style status indicator)
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Accent),
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.footer_credit),
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.4.sp,
            )
            Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Accent.copy(alpha = 0.6f)),
            )
        }
    }
}

private val TextSecondary = com.mshaerpour.mediaflash.ui.theme.TextSecondary
