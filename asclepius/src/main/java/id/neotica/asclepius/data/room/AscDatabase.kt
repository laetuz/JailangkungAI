package id.neotica.asclepius.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [AscEntity::class], version = 1, exportSchema = false)
abstract class AscDatabase: RoomDatabase() {
    abstract fun dao(): AscDao
}