package com.automacorp.model

enum class WindowStatus { OPENED, CLOSED }

data class WindowDto(
    val id: Long,
    val name: String,
    val roomName: String,
    val roomId: Long,
    val windowStatus: WindowStatus
)
data class WindowCommandDto(
    val name: String,
    val roomId: Long,
    val windowStatus: WindowStatus,
    val id: Long,
)
