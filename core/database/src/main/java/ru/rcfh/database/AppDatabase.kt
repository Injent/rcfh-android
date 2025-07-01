package ru.rcfh.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.rcfh.database.converter.JsonElementConverter
import ru.rcfh.database.converter.LocalDateTimeConverter
import ru.rcfh.database.dao.DocumentDao
import ru.rcfh.database.dao.FormDao
import ru.rcfh.database.dao.HandbookDao
import ru.rcfh.database.dao.ReferenceDao
import ru.rcfh.database.entity.DocumentEntity
import ru.rcfh.database.entity.FormEntity
import ru.rcfh.database.entity.HandbookEntity
import ru.rcfh.database.entity.ReferenceEntity

@Database(
    entities = [
        HandbookEntity::class,
        ReferenceEntity::class,
        DocumentEntity::class,
        FormEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(
    LocalDateTimeConverter::class,
    JsonElementConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun handbookDao(): HandbookDao
    abstract fun referenceDao(): ReferenceDao
    abstract fun formDao(): FormDao
    abstract fun documentDao(): DocumentDao
}