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

package org.swiftleap.storage

import kotlinx.coroutines.Job
import org.swiftleap.common.event.EventListener
import java.io.Closeable
import java.io.InputStream
import java.io.OutputStream

const val STORAGE_OPTIONS_KEY = "@storage.options"

enum class StorageOption {
    RAW,
    ENCRYPTED,
    COMPRESSED,
    COMPRESSED_ENCRYPTED
}

interface StorageNode : Closeable {
    val meta: Map<String, String>
    val path: String
    val children: Iterable<StorageNode>
    val hasChildren: Boolean
    val hasData: Boolean
    val data: InputStream
}

/**
 * Data is compressed and encrypted.
 *
 * Storage events is received through the eventing system, the event key prefix is "storage:".
 *
 * Directories must end with "/" else its considered as a file.
 *
 * Files may have children, depending on the storage type.
 *
 * Use joinPath("1/", "/") to ensure its a directory.
 */
interface Storage {
    fun createFolder(path: String, meta: Map<String, String> = mapOf())

    /**
     * Asynchronously create a file.
     * The onLoad callback provides a stream to write to.
     */
    fun createFile(path: String, onLoad: (OutputStream) -> Unit, meta: Map<String, String> = mapOf(), option: StorageOption = StorageOption.RAW): Job

    fun getNode(path: String): StorageNode

    fun watch(paths: Array<String>, listener: EventListener)
}


fun basename(path: String): String {
    if (path.endsWith("/"))
        return path
    val i = path.lastIndexOf('/')
    if (i < 1)
        return path
    return path.substring(path.lastIndexOf('/') + 1)
}

fun dirname(path: String): String {
    if (path.endsWith("/"))
        return path
    val i = path.lastIndexOf('/')
    if (i < 1)
        return path
    return path.substring(0, path.lastIndexOf('/'))
}

fun joinPath(vararg parts: Any): String {
    val sep = "/"
    var ret = ""
    for (i in 0 until parts.size) {
        val p = parts[i].toString()
        if (i < parts.size - 1 && !p.endsWith(sep))
            ret += p + sep
        else if (p != sep)
            ret += p
    }
    return ret
}