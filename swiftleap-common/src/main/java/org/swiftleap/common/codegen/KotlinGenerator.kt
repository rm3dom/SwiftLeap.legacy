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

import org.swiftleap.common.util.EnumUtil
import java.io.FileOutputStream
import java.io.PrintStream
import java.nio.file.Paths


class KotlinGenerator(val lang: Language) : CodeGenerator() {

    override fun genTypes(outDir: String?, classes: MutableCollection<Class<*>>?) {
        classes?.forEach { cls -> this.parseClass(cls) }
        PrintStream(FileOutputStream(Paths.get(outDir, "Types.kt").toFile())).use { ps ->
            genIncludes(ps);

            genTypes(ps, classes);
        }
    }

    private fun genIncludes(ps: PrintStream) {
        ps.println("package api.types")
        ps.println()
        ps.println("import api.enums.*")
        ps.println("import kotlinx.serialization.Serializable")
        if (lang == Language.KOTLIN) {
            ps.println("import java.util.*")
        } else {
            ps.println("import kotlin.js.Date")
            ps.println("import util.toJson")
        }
        ps.println()
    }

    override fun genTypes(pw: PrintStream?, classes: MutableCollection<Class<*>>?) {
        classes?.forEach { cls -> this.parseClass(cls) }
        definitions.values.forEach { def ->
            run {
                pw?.println(genType(def))
            }
        }
    }


    private fun genType(def: ClassDef): String {
        val sb = StringBuilder()

        sb.appendln("//Generated from ${def.type.canonicalName}")
        sb.appendln("@Serializable")
        sb.appendln("data class ${def.name} (")

        def.props.values.forEachIndexed { index: Int, prop: PropertyDef ->
            sb.appendln("   ${serializerFor(prop)} val ${prop.name} : ${propType(prop)} = ${defForProp(prop)} ${(if (index + 1 == def.props.count()) "" else ",")}")
        }

        sb.appendln(")")

        /*
        if (lang == Language.KOTLINJS) {
            sb.appendln("{")
            sb.appendln("    companion object {")

            sb.appendln("    fun fromJson(other: dynamic) : ${def.name} {")
            sb.appendln("        return ${def.name} (  ")
            def.props.values.forEachIndexed { index: Int, prop: PropertyDef ->
                if (prop.isCollection && prop.isEnumType)
                    sb.appendln("            ${prop.name} = util.toEnumArray<${toType(prop, false)}>(other.${prop.name})${(if (index + 1 == def.props.count()) "" else ",")}")
                else if (prop.isEnumType)
                    sb.appendln("            ${prop.name} = util.toEnum<${toType(prop, false)}>(other.${prop.name})${(if (index + 1 == def.props.count()) "" else ",")}")
                else if (propType(prop) == "Date")
                    sb.appendln("            ${prop.name} = util.toDateTime(other.${prop.name})${(if (index + 1 == def.props.count()) "" else ",")}")
                else if (propType(prop) == "ByteArray")
                    sb.appendln("            ${prop.name} = util.toByteArray(other.${prop.name})${(if (index + 1 == def.props.count()) "" else ",")}")
                else
                    sb.appendln("            ${prop.name} = other.${prop.name}${(if (index + 1 == def.props.count()) "" else ",")}")

            }
            sb.appendln("          )")
            sb.appendln("     }")
            sb.appendln("     }")
            sb.appendln("}")
            sb.appendln()
            sb.appendln("fun ${def.name}.toJson() : dynamic {")
            sb.appendln("    var ret = js(\"({})\")  ")
            def.props.values.forEachIndexed { index: Int, prop: PropertyDef ->
                sb.appendln("    ret.${prop.name} = this.${prop.name}.toJson()")
            }
            sb.appendln("    return ret")
            sb.appendln("}")

            sb.appendln("fun Array<${def.name}>.toJson() = this.map { e -> e.toJson() }.toTypedArray()")
        }
        */

        sb.appendln()

        return sb.toString()
    }

    private fun serializerFor(prop: PropertyDef): String {
        val type = toType(prop, false)
        //For now nothing
        return ""
    }

    private fun propType(def: PropertyDef): String {
        if (def.isCollection) {
            val type = toType(def, false);

            return when (type) {
                "Byte" -> "ByteArray"
                else -> "List<$type>"
            }
        }


        if (def.isMap)
            return "Map<String, String>"

        return toType(def, !def.isSimpleType)
    }

    private fun defForProp(def: PropertyDef): String {
        if ((def.defaultValue?.all?.length ?: 0) > 0)
            return def.defaultValue.all

        if ((def.defaultValue?.kotlin?.length ?: 0) > 0)
            return def.defaultValue.kotlin

        if (def.isCollection) {
            val type = toType(def, false)
            return when (type) {
                "Byte" -> "ByteArray(0)"
                else -> "emptyList()"
            }
        }

        if (def.isMap)
            return "emptyMap()"

        if (def.isEnumType)
            return "${def.typeName}.${EnumUtil.getFistEnumName(def.type)}"

        return when (def.type.simpleName) {
            "Date" -> "Date()"
            "int" -> "0"
            "Int" -> "0"
            "boolean" -> "false"
            "Boolean" -> "false"
            "String" -> "\"\""
            "Integer" -> "0"
            "byte" -> "0"
            "long" -> "0"
            "Long" -> "0"
            "Double" -> "0.0"
            "double" -> "0.0"
            "BigDecimal" -> "0.0"
            "Float" -> "0.0"
            "float" -> "0.0"
            else -> "null"
        }
    }

    private fun toType(def: PropertyDef, nullable: Boolean = true): String {

        val type = def.type;

        if (def.isEnumType)
            return def.typeName

        return when (type.simpleName) {
            "int" -> "Int"
            "Int" -> "Int"
            "boolean" -> "Boolean"
            "Boolean" -> "Boolean"
            "String" -> "String"
            "Integer" -> "Int"
            "byte" -> "Byte"
            "Float" -> "Float"
            "float" -> "Float"
            "long" -> "Long"
            "Long" -> "Long"
            "Double" -> "Double"
            "double" -> "Double"
            "Date" -> "Date"
            "BigDecimal" -> "Double"
            else -> (if (nullable) "${def.typeName}?" else def.typeName)
        }
    }
}