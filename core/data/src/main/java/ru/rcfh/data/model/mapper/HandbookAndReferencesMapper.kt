package ru.rcfh.data.model.mapper

import ru.rcfh.data.model.Handbook
import ru.rcfh.data.model.HandbookCollection
import ru.rcfh.data.model.ReferenceObj
import ru.rcfh.network.model.NetworkHandbookCollection

fun NetworkHandbookCollection.toExternalModel() = HandbookCollection(
    version = version,
    handbooks = handbooks.map(NetworkHandbookCollection.Handbook::toExternalModel)
)

fun NetworkHandbookCollection.Handbook.toExternalModel() = Handbook(
    id = id,
    name = name,
    references = references.map { ReferenceObj(it) }
)