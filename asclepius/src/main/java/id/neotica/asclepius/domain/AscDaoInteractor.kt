package id.neotica.asclepius.domain

import id.neotica.asclepius.data.room.AscEntity

class AscDaoInteractor(private val ascDao: AscDaoRepoImpl) {
    fun addHistory(item: AscEntity) {
        ascDao.addHistory(item)
    }
}