package ru.rcfh.data.model.mapper

import ru.rcfh.data.model.Handbook
import ru.rcfh.data.model.HandbookCollection
import ru.rcfh.data.model.Reference
import ru.rcfh.database.entity.HandbookEntity
import ru.rcfh.database.entity.ReferenceEntity
import ru.rcfh.network.model.NetworkHandbookCollection

fun NetworkHandbookCollection.toExternalModel() = HandbookCollection(
    version = version,
    handbooks = handbooks.map(NetworkHandbookCollection.Handbook::toExternalModel)
)

fun NetworkHandbookCollection.Handbook.toExternalModel() = Handbook(
    id = id,
    name = name,
    references = references.map(NetworkHandbookCollection.Handbook.Reference::toExternalModel)
)

fun NetworkHandbookCollection.Handbook.Reference.toExternalModel() = Reference(
    id = id,
    name = name,
    description = description,
    signCodes = signCodes,
    code = code
)

fun NetworkHandbookCollection.Handbook.toEntity() = HandbookEntity(
    id = id,
    name = name,
)

fun NetworkHandbookCollection.Handbook.Reference.toEntity(handbookId: Int) = ReferenceEntity(
    id = id,
    name = name,
    handbookId = handbookId,
    description = description,
    code = code,
    signCodes = signCodes
)

fun ReferenceEntity.toExternalModel() = Reference(
    id = id,
    name = name,
    description = description,
    signCodes = signCodes,
    code = code
)