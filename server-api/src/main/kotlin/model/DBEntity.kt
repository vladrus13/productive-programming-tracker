package model

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.statements.UpdateBuilder

interface ConstructableFromRow<E> {
    fun fromResultRow(row: ResultRow): E
}

interface ConvertableToDBBuilder {
    fun <B : UpdateBuilder<Int>> fillUpdatedBuilder(builder: B): B
}