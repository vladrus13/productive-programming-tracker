package model

import kotlinx.serialization.Serializable

@Serializable
data class EventVisitor(val id: Long?, val eventId: Long, val fullName: String, val visitStatus: VisitStatus) {
    enum class VisitStatus {
        REGISTERED,
        MISSED,
        VISITED
    }
}
