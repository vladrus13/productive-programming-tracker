package model

import kotlinx.serialization.Serializable

@Serializable
data class Event(val id: Long?, val title: String) {
}


