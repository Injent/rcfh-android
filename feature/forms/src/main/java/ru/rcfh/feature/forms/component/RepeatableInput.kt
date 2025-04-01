package ru.rcfh.feature.forms.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.compose.LocalStateCache
import ru.rcfh.core.sdui.model.Template
import ru.rcfh.designsystem.component.AppCard
import ru.rcfh.designsystem.component.AppTextButton
import ru.rcfh.designsystem.theme.AppTheme
import ru.rcfh.feature.forms.R

@Composable
fun RepeatableInput(
    template: Template.Repeatable,
    modifier: Modifier = Modifier,
    row: Int = 0,
) {
    val coroutineScope = rememberCoroutineScope()
    val cache = LocalStateCache.current
    var cardsCount by rememberSaveable { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        cache.observeIterationCount(
            templateId = template.id,
            row = row
        )
            .collectLatest {
                cardsCount = it
            }
    }

    AppCard(
        contentPadding = PaddingValues(
            top = AppTheme.spacing.l,
            start = AppTheme.spacing.l,
            end = AppTheme.spacing.l,
            bottom = AppTheme.spacing.sNudge
        ),
        modifier = modifier
            .animateContentSize()
    ) {
        Text(
            text = template.name,
            style = AppTheme.typography.headline1,
            color = AppTheme.colorScheme.foreground1,
            modifier = Modifier
        )

        key(cardsCount) {
            repeat(cardsCount) { index ->
                HorizontalDivider(
                    color = AppTheme.colorScheme.stroke2,
                    thickness = 1.2.dp,
                    modifier = Modifier
                        .padding(top = AppTheme.spacing.xl)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.s),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Запись ${index + 1}",
                        style = AppTheme.typography.subheadline,
                        color = AppTheme.colorScheme.foreground3,
                        modifier = Modifier
                            .padding(top = AppTheme.spacing.s)
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = "Удалить",
                        color = AppTheme.colorScheme.foregroundError,
                        style = AppTheme.typography.subheadlineButton,
                        modifier = Modifier
                            .clickable {
                                coroutineScope.launch(NonCancellable) {
                                    cache.removeRepeatable(
                                        parentTemplateId = template.id,
                                        row = row,
                                        iteration = index,
                                    )
                                }
                            }
                            .padding(top = AppTheme.spacing.s, bottom = AppTheme.spacing.l)
                    )
                }
                template.templates.forEach { temp ->
                    TextInput(
                        parentTemplateId = template.id,
                        template = temp,
                        row = row,
                        iteration = index,
                        onCard = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = AppTheme.spacing.l)
                    )
                }
            }
        }

        HorizontalDivider(
            color = AppTheme.colorScheme.stroke2,
            thickness = 1.2.dp,
            modifier = Modifier
                .padding(top = AppTheme.spacing.xs)
        )
        AppTextButton(
            text = stringResource(R.string.button_add),
            onClick = {
                coroutineScope.launch(NonCancellable) {
                    template.templates.forEach { temp ->
                        cache.putText(
                            parentTemplateId = template.id,
                            templateId = temp.id,
                            row = row,
                            iteration = cardsCount,
                            value = "",
                            instantWrite = true
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.spacing.sNudge)
        )
    }
}