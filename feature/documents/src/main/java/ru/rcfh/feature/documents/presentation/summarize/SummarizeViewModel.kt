package ru.rcfh.feature.documents.presentation.summarize

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.rcfh.core.sdui.common.DetectedError
import ru.rcfh.core.sdui.common.ErrorReport
import ru.rcfh.core.sdui.common.ProblemAddress
import ru.rcfh.core.sdui.data.DocumentStateManager
import ru.rcfh.navigation.Navigator
import ru.rcfh.navigation.Screen

data class SummarizeUiState(
    val report: ErrorReport = ErrorReport(),
    val hasProblems: Boolean = true
)

class SummarizeViewModel(
    private val documentStateManager: DocumentStateManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val documentId = savedStateHandle.toRoute<Screen.Summarize>().documentId

    private val _uiState = MutableStateFlow(SummarizeUiState())
    val uiState = _uiState
        .onStart {
            detectErrors()
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SummarizeUiState()
        )

    fun onNavigateToProblem(problem: DetectedError) {
        val (formId, type) = documentStateManager.getFormIdByProblem(problem)
        viewModelScope.launch {
            Navigator.navigate(
                when (type) {
                    ProblemAddress.COMPARISON_TABLE -> Screen.ComparisonTable(
                        documentId = documentId,
                        formId = formId,
                        templateId = problem.address?.parentId ?: problem.templateId,
                        initialRowIdx = problem.address?.rowIndex ?: 0
                    )
                    ProblemAddress.TABLE -> Screen.TableRecord(
                        documentId = documentId,
                        formId = formId,
                        templateId = problem.address?.parentId ?: problem.templateId,
                        rowIdx = problem.address?.rowIndex ?: 0
                    )
                    ProblemAddress.FORM -> Screen.FormNavigator(
                        documentId = documentId,
                        formId = formId
                    )
                }
            )
        }
    }

    fun detectErrors() {
        viewModelScope.launch {
            val problems = documentStateManager.detectErrors() ?: ErrorReport()
            _uiState.value = SummarizeUiState(
                report = problems,
                hasProblems = problems.severe.isNotEmpty() || problems.warnings.isNotEmpty()
            )
        }
    }

    fun onBack() {
        viewModelScope.launch {
            Navigator.navigateUp()
        }
    }
}