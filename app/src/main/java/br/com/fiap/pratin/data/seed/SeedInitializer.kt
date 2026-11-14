package br.com.fiap.pratin.data.seed

import androidx.room.withTransaction
import br.com.fiap.pratin.data.local.db.PratinDatabase
import br.com.fiap.pratin.domain.rules.agora
import java.time.Clock

/** Grava os dados de exemplo só quando o banco está vazio (RN16). */
class SeedInitializer(
    private val database: PratinDatabase,
    private val clock: Clock,
) {
    /** Devolve `true` se gravou o seed nesta chamada. */
    suspend fun executarSeNecessario(): Boolean = database.withTransaction {
        val alimentoDao = database.alimentoDao()
        val reservaDao = database.reservaDao()
        if (alimentoDao.contar() > 0) {
            false
        } else {
            val agora = clock.agora()
            alimentoDao.inserirTodos(SeedData.alimentosDisponiveis(agora))

            val marmitaId = alimentoDao.inserir(SeedData.marmitaReservada(agora))
            reservaDao.inserir(SeedData.reservaDaMarmita(marmitaId, agora))

            val paoId = alimentoDao.inserir(SeedData.paoFrancesRetirado(agora))
            reservaDao.inserir(SeedData.reservaDoPaoFrances(paoId, agora))
            true
        }
    }
}
