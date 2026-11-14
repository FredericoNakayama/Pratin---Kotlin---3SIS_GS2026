package br.com.fiap.pratin.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.fiap.pratin.data.local.db.dao.AlimentoDao
import br.com.fiap.pratin.data.local.db.dao.ReservaDao
import br.com.fiap.pratin.data.local.db.entity.AlimentoEntity
import br.com.fiap.pratin.data.local.db.entity.ReservaEntity

/** Banco local do Pratin: única fonte de dados de anúncios e reservas (ADR-01). */
@Database(
    entities = [AlimentoEntity::class, ReservaEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class PratinDatabase : RoomDatabase() {
    abstract fun alimentoDao(): AlimentoDao
    abstract fun reservaDao(): ReservaDao

    companion object {
        const val NOME_ARQUIVO = "pratin.db"

        fun criar(context: Context): PratinDatabase =
            Room.databaseBuilder(context, PratinDatabase::class.java, NOME_ARQUIVO).build()
    }
}
