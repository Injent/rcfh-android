package ru.rcfh.glpm.feature.form.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import ru.rcfh.glpm.feature.form.presentation.comparisontable.ComparisonTableViewModel
import ru.rcfh.glpm.feature.form.presentation.form.FormViewModel
import ru.rcfh.glpm.feature.form.presentation.formnavigator.FormNavigatorViewModel
import ru.rcfh.glpm.feature.form.presentation.handbooksearch.HandbookSearchViewModel
import ru.rcfh.glpm.feature.form.presentation.table.TableViewModel
import ru.rcfh.glpm.feature.form.presentation.tablerecord.TableRecordViewModel

val FormModule = module {
    viewModelOf(::FormViewModel)
    viewModelOf(::HandbookSearchViewModel)
    viewModelOf(::TableViewModel)
    viewModelOf(::TableRecordViewModel)
    viewModelOf(::ComparisonTableViewModel)
    viewModelOf(::FormNavigatorViewModel)
}