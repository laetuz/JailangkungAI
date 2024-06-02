package id.neotica.asclepius.presentation.history

import androidx.lifecycle.ViewModel
import id.neotica.asclepius.domain.AscDaoInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HistoryViewModel(
    private val dao: AscDaoInteractor
): ViewModel() {
    suspend fun getHistory() = withContext(Dispatchers.IO) { dao.getHistory() }
}