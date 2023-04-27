package model

import kotlinx.serialization.Serializable

@Serializable
data class TextResponse(val statusCode: Int, val message: String)