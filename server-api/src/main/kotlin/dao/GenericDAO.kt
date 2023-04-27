package dao

interface GenericDAO<E> {

    /**
     * Read entity with curtain [id].
     *
     * @return entity with given [id] or null
     */
    suspend fun findById(id: Long): E?

    /**
     * Upsert operation for [entity].
     *
     * @return [entity] id if upsert operation succeed, null otherwise
     */
    suspend fun upsert(entity: E): Long?

    /**
     * Upsert operation for entity with curtain [id].
     *
     * @return true if deletion succeed, false otherwise
     */
    suspend fun delete(id: Long): Boolean
}