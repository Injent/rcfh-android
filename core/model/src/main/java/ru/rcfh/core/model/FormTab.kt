package ru.rcfh.core.model

sealed interface FormTab {
    val formId: Int

    @JvmInline
    value class Tab(override val formId: Int) : FormTab

    class ReplaceableTab(
        override val formId: Int,
        val tabs: List<Tab>
    ) : FormTab

    companion object {
        val preset = listOf(
            Tab(formId = 1),
            Tab(formId = 2),
            Tab(formId = 3),
            ReplaceableTab(
                formId = 100,
                tabs = listOf(
                    Tab(formId = 4),
                    Tab(formId = 5)
                )
            ),
            Tab(formId = 6)
        )

        fun getKnownTabs(): List<Tab> = preset.filterIsInstance<Tab>()

        fun isReplaceable(id: Int): Boolean {
            return preset.any { tab ->
                tab is ReplaceableTab && (id == tab.formId || id in tab.tabs.map(Tab::formId))
            }
        }

        fun findReplaceOptions(tab: Tab): List<Tab> {
            return preset
                .find { replaceable ->
                    replaceable is ReplaceableTab && tab in replaceable.tabs
                }
                .let { it as ReplaceableTab? }
                ?.tabs
                ?: emptyList()
        }

        /**
         * Corrects the order of items in the list of tabs.
         * If there is no [ReplaceableTab] in the list, it will add it to the desired position.
         */
        fun normalize(tabs: List<Tab>): List<FormTab> {
            return preset.map { formTab ->
                when (formTab) {
                    is ReplaceableTab -> {
                        formTab.tabs.find { it in tabs } ?: formTab
                    }
                    is Tab -> formTab
                }
            }
        }

        /**
         * Restores tab order from form IDs
         */
        fun fromIds(ids: List<Int>): List<FormTab> {
            return preset.mapNotNull { tab ->
                when (tab) {
                    is ReplaceableTab -> tab.tabs.find { it.formId in ids }
                    is Tab -> if (tab.formId in ids) tab else null
                }
            }.let { normalize(it) }
        }
    }
}
