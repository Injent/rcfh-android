package ru.rcfh.feature.forms.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.rcfh.feature.forms.presentation.form.FormViewModel
import ru.rcfh.feature.forms.presentation.handbooksearch.HandbookSearchViewModel
import ru.rcfh.feature.forms.presentation.sectionrecord.SectionRecordViewModel
import ru.rcfh.feature.forms.presentation.sheet.SheetsViewModel
import ru.rcfh.feature.forms.presentation.sheetrecord.SheetRecordViewModel

val FormsModule = module {
    viewModelOf(::HandbookSearchViewModel)
    viewModelOf(::FormViewModel)
    viewModelOf(::SheetsViewModel)
    viewModelOf(::SheetRecordViewModel)
    viewModelOf(::SectionRecordViewModel)
}