package ru.rcfh.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.rcfh.database.converter.ListInListConverter
import ru.rcfh.database.dao.FormDao
import ru.rcfh.database.dao.HandbookDao
import ru.rcfh.database.dao.ReferenceDao
import ru.rcfh.database.dao.StateCacheDao
import ru.rcfh.database.entity.FormEntity
import ru.rcfh.database.entity.HandbookEntity
import ru.rcfh.database.entity.ReferenceEntity
import ru.rcfh.database.entity.StateCacheEntity

@Database(
    entities = [
        HandbookEntity::class,
        ReferenceEntity::class,
        FormEntity::class,
        StateCacheEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    ListInListConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun handbookDao(): HandbookDao
    abstract fun referenceDao(): ReferenceDao
    abstract fun stateCacheDao(): StateCacheDao
    abstract fun formDao(): FormDao
}