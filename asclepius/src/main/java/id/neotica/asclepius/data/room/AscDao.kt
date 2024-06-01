package id.neotica.asclepius.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AscDao {
    @Query("select * from asc_entity order by asc_id desc")
    fun getAscList(): List<AscEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addHistory(item: AscEntity)
}