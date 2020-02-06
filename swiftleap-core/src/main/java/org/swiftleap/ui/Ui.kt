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

package org.swiftleap.ui

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.swiftleap.common.util.orElse
import kotlin.math.max

interface MenuEntry {
    val key: String
    val text: String
    val icon: String
    val link: String
    val title: String
    val children: Iterable<MenuEntry>
    val roles: Array<String>
    val order: Int
}

data class BasicMenuEntry(
        override val key: String,
        override val text: String = "",
        override val icon: String = "",
        override val link: String = "",
        override val children: Iterable<MenuEntry> = emptyList(),
        override val title: String = "",
        override val order: Int = 0,
        override val roles: Array<String> = emptyArray())
    : MenuEntry {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BasicMenuEntry

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }
}


interface Module {
    fun name(): String
    fun menu(): Iterable<MenuEntry>
}

fun Iterable<MenuEntry>.merge(entries: Iterable<MenuEntry>): Iterable<MenuEntry> {
    return this
            .map { l ->
                when (val o = entries.firstOrNull { r -> r.key == l.key }) {
                    null -> l
                    else -> BasicMenuEntry(
                            key = l.key.orElse(o.key),
                            text = l.text.orElse(o.text),
                            icon = l.text.orElse(o.icon),
                            link = l.link.orElse(o.link),
                            title = l.title.orElse(o.title),
                            order = max(l.order, o.order),
                            children = l.children.merge(o.children))
                }
            }
            .union(entries.filter { l -> this.none { o -> o.key == l.key } })
            .sortedBy { l -> l.order }

}

@Service
open class UiManager(
        @Autowired(required = false) private val modules: List<Module> = emptyList()
) {
    fun getModules() = modules
    fun getMenuEntries() = modules.fold(emptyList<MenuEntry>().asIterable(), { l, r -> l.merge(r.menu()) })
}



