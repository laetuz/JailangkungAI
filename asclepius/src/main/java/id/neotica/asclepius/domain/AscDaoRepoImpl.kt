package id.neotica.asclepius.domain

import id.neotica.asclepius.data.room.AscDao
import id.neotica.asclepius.data.room.AscEntity

class AscDaoRepoImpl(private val dao: AscDao) {

    fun getHistory(): List<AscEntity> {
        return dao.getAscList()
    }

    fun addHistory(item: AscEntity) {
        return dao.addHistory(item)
    }
}