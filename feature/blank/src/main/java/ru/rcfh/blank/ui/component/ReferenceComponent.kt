package ru.rcfh.blank.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import ru.rcfh.blank.presentation.referencesearch.ReferenceSearchScreen
import ru.rcfh.blank.state.ViewerNavigator
import ru.rcfh.blank.ui.queryapi.queryOrDefault
import ru.rcfh.blank.ui.queryapi.update
import ru.rcfh.blank.ui.search.FilterLogic
import ru.rcfh.blank.ui.search.FilterLogicPreset
import ru.rcfh.blank.ui.search.SearchLogic
import ru.rcfh.blank.ui.state.DocumentScope
import ru.rcfh.blank.ui.state.Element
import ru.rcfh.designsystem.component.ReferenceTextField
import ru.rcfh.glpm.feature.blank.R

abstract class ReferenceComponent(
    target: String,
    private val handbookId: Int,
    private val searchLogic: SearchLogic = SearchLogic.BASIC,
    filterLogic: FilterLogic? = null,
    private val onCard: Boolean = false,
) : Component("${target}.name") {
    val rootTarget = target

    abstract val label: String

    init {
        if (filterLogic != null) {
            FilterLogicPreset[rootTarget] = filterLogic
        }
    }

    @Composable
    override fun Content(state: Element, navigator: ViewerNavigator, documentScope: DocumentScope) {
        ReferenceTextField(
            value = state.queryOrDefault(target, ""),
            onClear = {
                state.update(rootTarget, null)
            },
            onClick = {
                navigator.goTo(
                    ReferenceSearchScreen(
                        title = label,
                        handbookId = handbookId,
                        resultPath = rootTarget,
                        searchLogic = searchLogic
                    )
                )
            },
            enabled = enabled,
            error = errorMsg,
            label = label,
            onCard = onCard,
            placeholder = stringResource(R.string.feature_blank_placeholder_reference)
        )
    }
}