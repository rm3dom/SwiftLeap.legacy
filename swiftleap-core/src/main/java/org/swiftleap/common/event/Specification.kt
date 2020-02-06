/*
* Copyright (C) 2018 SwiftLeap.com, Ruan Strydom
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package org.swiftleap.common.event

data class EventKey(var key: String = "*", var source: String = "*") {
}

interface Event {
    val key: EventKey
    val data: Map<String, String>
}

interface EventListener {
    fun handleEvent(event: Event)
}

interface EventService {
    fun listen(listener: EventListener, vararg keys: EventKey)
    fun listen(listener: EventListener, keys: Collection<EventKey>)
    fun notify(event: Event)
}