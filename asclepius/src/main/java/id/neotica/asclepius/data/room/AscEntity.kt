package id.neotica.asclepius.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity("asc_entity")
class AscEntity (
    @field:ColumnInfo("asc_id")
    @field:PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    @field:ColumnInfo("percentage")
    val percentage: String,
    @field:ColumnInfo("category")
    val category: String,
    @field:ColumnInfo("image_uri")
    val imageUri: String,
    @field:ColumnInfo("date_added")
    val dateAdded: String = getCurrentDateTime(),
)

fun getCurrentDateTime(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    return current.format(formatter)
}