package model

import kotlinx.serialization.Serializable

@Serializable
data class EventVisitor(val id: Long?, val eventId: Long, val fullName: String, val visitStatus: VisitStatus) {
    enum class VisitStatus(val code: String) {
        R("registered"),
        M("missed"),
        V("visited")
    }
}
