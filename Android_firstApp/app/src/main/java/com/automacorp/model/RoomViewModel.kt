package com.automacorp.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.automacorp.service.ApiServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class RoomViewModel: ViewModel() {
    var room by mutableStateOf<RoomDto?>(null)
    val roomsState = MutableStateFlow(RoomList())
    var selectedRoomWindows by mutableStateOf<List<WindowDto>>(emptyList())

    fun findAll() {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.readAllRooms().execute() }
                .onSuccess {
                    val rooms = it.body() ?: emptyList()
                    roomsState.value = RoomList(rooms)
                }
                .onFailure {
                    it.printStackTrace()
                    roomsState.value = RoomList(emptyList(), it.stackTraceToString())
                }
        }
    }

    fun findRoom(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.readOneRoomById(id).execute() }
                .onSuccess {
                    room = it.body()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }
    }

    fun updateRoom(id: Long, roomDto: RoomDto) {
        val command = RoomCommandDto(
            name = roomDto.name,
            targetTemperature = roomDto.targetTemperature?.let { Math.round(it * 10) /10.0 },
            currentTemperature = roomDto.currentTemperature
        )
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.updateRoom(id, command).execute() }
                .onSuccess {
                    room = it.body()
                    findAll()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }
    }

    fun createRoom(name: String, currentTemperature: Double?, targetTemperature: Double?) {
        val command = RoomCommandDto(
            name = name,
            currentTemperature = currentTemperature,
            targetTemperature = targetTemperature
        )
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.createRoom(command).execute() }
                .onSuccess {
                    room = it.body()
                    // Refresh the room list after creation
                    findAll()
                }
                .onFailure {
                    it.printStackTrace()
                    room = null
                }
        }
    }

    fun deleteRoom(id: Long) {
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.deleteRoom(id).execute() }
                .onSuccess {
                    findAll()
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
    }

    fun listRoomWindows(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            runCatching { ApiServices.roomsApiService.readOneRoomById(id).execute() }
                .onSuccess { response ->
                    val fetchedRoom = response.body()
                    if (fetchedRoom != null) {
                        selectedRoomWindows = fetchedRoom.windows
                    } else {
                        selectedRoomWindows = emptyList()
                    }
                }
                .onFailure {
                    it.printStackTrace()
                    selectedRoomWindows = emptyList()
                }
        }
    }

    // Update a single window given its ID and new parameters
    fun updateWindow(windowId: Long, name: String, roomId: Long, status: WindowStatus) {
        val command = WindowCommandDto(
            name = name,
            roomId = roomId,
            windowStatus = status,
            id = windowId
        )
        viewModelScope.launch(context = Dispatchers.IO) {
            runCatching { ApiServices.windowsApiService.updateWindow(windowId, command).execute() }
                .onSuccess {
                    // If we have a current room, refresh windows
                    room?.let {
                        listRoomWindows(it.id)
                    }
                }
                .onFailure {
                    it.printStackTrace()
                }
        }
    }

    // If param is a number, search by ID, otherwise by name
    fun findByNameOrId(param: String) {
        viewModelScope.launch(Dispatchers.IO) {
            if (param.toLongOrNull() != null) {
                // param is a numeric ID
                val id = param.toLong()
                runCatching { ApiServices.roomsApiService.readOneRoomById(id).execute() }
                    .onSuccess {
                        room = it.body()
                    }
                    .onFailure {
                        it.printStackTrace()
                        room = null
                    }
            } else {
                // param is a name: we must load all and filter
                runCatching { ApiServices.roomsApiService.readAllRooms().execute() }
                    .onSuccess {
                        val rooms = it.body() ?: emptyList()
                        room = rooms.find { r -> r.name.equals(param, ignoreCase = true) }
                    }
                    .onFailure {
                        it.printStackTrace()
                        room = null
                    }
            }
        }
    }
}
