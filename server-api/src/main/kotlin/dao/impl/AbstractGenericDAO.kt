package dao.impl

import dao.DatabaseFactory
import dao.GenericDAO
import model.ConstructableFromRow
import model.LongIdEntity
import model.ConvertableToDBBuilder
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update

abstract class AbstractGenericDAO<
    Entity,
    Table : LongIdTable
>(
    private val dbEntityCompanion: ConstructableFromRow<Entity>,
    private val table: Table
): GenericDAO<Entity> where
    Entity : LongIdEntity,
    Entity : ConvertableToDBBuilder {

    override suspend fun findById(id: Long): Entity? = DatabaseFactory.dbQuery {
        table
            .select { table.id eq id }
            .map { resultRow -> dbEntityCompanion.fromResultRow(resultRow) }
            .singleOrNull()
    }

    override suspend fun delete(id: Long): Boolean = DatabaseFactory.dbQuery {
        table.deleteWhere { table.id eq id } > 0
    }

    override suspend fun upsert(entity: Entity): Long? = DatabaseFactory.dbQuery {
        val id = entity.id ?: return@dbQuery table.insertAndGetId {
            entity.fillUpdatedBuilder(it)
        }.value
        val update = table.update({ table.id eq id }) {
            entity.fillUpdatedBuilder(it)
        }
        if (update == 0) null else id
    }

}