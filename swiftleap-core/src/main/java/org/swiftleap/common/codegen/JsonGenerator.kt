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

package org.swiftleap.common.codegen

import java.io.PrintStream

enum class CgType {
    Array,
    Double,
    String,
    Int32,
    Int64,
    DateTime,
    Date,
    Enum,
    Object
}

enum class CgHttpMethod {
    GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;
}


data class CgEnumValue(
        val doc: String = "",
        val name: String = "",
        val ordinal: Int = 0
)

data class CgEnum(
        val source: String = "",
        val doc: String = "",
        val name: String = "",
        val values: List<CgEnumValue> = emptyList()
)


data class CgTypeDef(
        val source: String = "",
        val doc: String = "",
        val name: String = "",
        val default: String = "",
        val ref: String = "",
        val format: CgType = CgType.String,
        val readonly: Boolean = false,
        val optional: Boolean = false,
        val annotations: List<CgAnnotate> = emptyList()
)


data class CgAnnotate(
        val source: String = "",
        val name: String = "",
        val properties: List<CgTypeDef> = emptyList()
)

data class CgObject(
        val source: String = "",
        val doc: String = "",
        val name: String = "",
        val abstract: Boolean = false,
        val annotations: List<CgAnnotate> = emptyList(),
        val interfaces: List<String> = emptyList(),
        val properties: List<CgTypeDef> = emptyList()
)

data class CgBodyType(
        val ref: String = "",
        val format: CgType = CgType.String
)

data class CgResponseType(
        val status: Int = 200,
        val ref: String = "",
        val format: CgType = CgType.String
)


data class CgEndpoint(
        val source: String = "",
        val doc: String = "",
        val name: String = "",
        val path: String = "",
        val group: String = "",
        val produces: List<String> = emptyList(),
        val consumes: List<String> = emptyList(),
        val method: CgHttpMethod = CgHttpMethod.GET,
        val responses: List<CgResponseType> = emptyList(),
        val body: CgTypeDef = CgTypeDef(),
        val headerParams: List<CgTypeDef> = emptyList(),
        val pathParams: List<CgTypeDef> = emptyList(),
        val queryParams: List<CgTypeDef> = emptyList()
)

data class CgDocument(
        val enums: List<CgEnum> = emptyList(),
        val objects: List<CgObject> = emptyList(),
        val endpoints: List<CgEndpoint> = emptyList()
)


class JsonGenerator(val lang: Language) : CodeGenerator() {
    override fun genTypes(pw: PrintStream?, classes: MutableCollection<Class<*>>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun genTypes(outDir: String?, classes: MutableCollection<Class<*>>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}