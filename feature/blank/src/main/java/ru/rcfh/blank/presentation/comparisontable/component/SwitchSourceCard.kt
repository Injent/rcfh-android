package ru.rcfh.blank.presentation.comparisontable.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.rcfh.designsystem.component.AppTextButton
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.glpm.feature.blank.R

@Composable
internal fun SwitchSourceCard(
    name: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .height(48.dp)
            .background(AppTheme.colorScheme.background3, CircleShape)
            .padding(start = AppTheme.spacing.l)
    ) {
        Text(
            text = name,
            style = AppTheme.typography.callout,
            color = AppTheme.colorScheme.foreground1
        )

        AppTextButton(
            text = stringResource(R.string.feature_blank_button_yes),
            onClick = onClick,
            shape = CircleShape
        )
    }
}