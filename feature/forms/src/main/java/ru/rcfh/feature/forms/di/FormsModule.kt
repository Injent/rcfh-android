package ru.rcfh.feature.forms.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.rcfh.feature.forms.presentation.form.FormsViewModel
import ru.rcfh.feature.forms.presentation.handbooksearch.HandbookDialogViewModel
import ru.rcfh.feature.forms.presentation.roweditor.RowEditorViewModel
import ru.rcfh.feature.forms.presentation.sectioneditor.SectionEditorViewModel

val FormsModule = module {
    viewModelOf(::FormsViewModel)
    viewModelOf(::RowEditorViewModel)
    viewModelOf(::HandbookDialogViewModel)
    viewModelOf(::SectionEditorViewModel)
}