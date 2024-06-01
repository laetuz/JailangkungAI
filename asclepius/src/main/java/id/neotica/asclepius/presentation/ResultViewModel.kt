package id.neotica.asclepius.presentation

import androidx.lifecycle.ViewModel
import id.neotica.asclepius.data.room.AscEntity
import id.neotica.asclepius.domain.AscDaoInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResultViewModel(
    private val dao: AscDaoInteractor
): ViewModel() {

    suspend fun addHistory(item: AscEntity) {
        withContext(Dispatchers.IO) {
            dao.addHistory(item)
        }
    }
}