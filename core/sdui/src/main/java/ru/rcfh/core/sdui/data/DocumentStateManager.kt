package ru.rcfh.core.sdui.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import ru.rcfh.core.sdui.common.DetectedError
import ru.rcfh.core.sdui.common.ErrorReport
import ru.rcfh.core.sdui.common.ErrorType
import ru.rcfh.core.sdui.common.ProblemAddress
import ru.rcfh.core.sdui.state.ComparisonTableState
import ru.rcfh.core.sdui.state.DocumentState
import ru.rcfh.core.sdui.state.TableState
import ru.rcfh.core.sdui.template.FormOptions
import ru.rcfh.core.sdui.template.FormTab
import ru.rcfh.database.dao.FormDao
import ru.rcfh.database.entity.FormEntity
import kotlin.time.Duration.Companion.seconds

class DocumentStateManager(
    private val formRepo: FormRepo,
    private val documentRepository: DocumentRepository,
    private val formDao: FormDao,
) {
    private var document: DocumentState? = null
    private var saveJob: Job? = null

    suspend fun loadDocument(documentId: Int): DocumentState {
        val scope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())
        if (documentId != document?.documentId) {
            document = DocumentState(
                documentId = documentId,
                documentScope = scope,
                formTemplates = formRepo.getForms(),
                formsContent = formDao.getForms(documentId).associate {
                    it.formId to it.state.jsonObject
                }
            )
            document!!.observeEvent(Any::class) {
                saveJob?.cancel()
                saveJob = scope.launch {
                    delay(1.seconds)
                    save()
                }
            }
            return document!!
        } else {
            return document!!
        }
    }

    suspend fun detectErrors(): ErrorReport = withContext(Dispatchers.Default) {
        val docId = document?.documentId ?: return@withContext ErrorReport()
        val formElements = formRepo.getDocumentTemplate().forms

        val errors = formElements.flatMap { element ->
            val formId = when (element) {
                is FormOptions -> {
                    documentRepository.getDocumentOptions(docId).formOptions[element.optionId]
                }
                is FormTab -> element.formId
            }
            document?.forms?.get(formId)?.fields?.flatMap { it.detectErrors() } ?: emptyList()
        }
        return@withContext ErrorReport(
            severe = errors.filter { it.type == ErrorType.SEVERE },
            warnings = errors.filter { it.type == ErrorType.WARNING }
        )
    }

    fun getFormIdByProblem(problem: DetectedError): Pair<Int, ProblemAddress> {
        var address: ProblemAddress? = null
        val formId = document?.forms?.values?.find {
            val index = it.fields.map { f -> f.id }.indexOf(
                problem.address?.parentId ?: problem.templateId
            )
            if (index == -1) return@find false

            address = when (it.fields[index]) {
                is ComparisonTableState -> ProblemAddress.COMPARISON_TABLE
                is TableState -> ProblemAddress.TABLE
                else -> ProblemAddress.FORM
            }
            true
        }!!.id
        return formId to address!!
    }

    suspend fun save() = withContext(Dispatchers.Default) {
        val documentId = document?.documentId ?: return@withContext

        for (formState in document?.forms?.values ?: emptyList()) {
            val jsonData = buildJsonObject {
                formState.fields.forEach { field ->
                    put(field.id, field.save())
                }
            }

            formDao.upsert(
                FormEntity(
                    documentId = documentId,
                    formId = formState.id,
                    isValid = formState.isValid(),
                    state = jsonData
                )
            )
        }
        println("SAVED")
    }

    fun closeUnsaved() {
        document = null
    }
}