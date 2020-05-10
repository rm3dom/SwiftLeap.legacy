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
package org.swiftleap.common.codegen;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by ruans on 2017/06/17.
 */
class ElmGenerator extends CodeGenerator {

    @Override
    public void genTypes(PrintStream pw, Collection<Class<?>> classes) throws Exception {
        classes.forEach(this::parseClass);

        pw.println(helper());
        pw.println();
        pw.println();
        for (ClassDef def : definitions.values()) {
            genType(pw, def);
            pw.println();
            pw.println();
        }
    }

    @Override
    public void genTypes(String outDir, Collection<Class<?>> classes) throws FileNotFoundException {
        new File(outDir + "/" + "Types").mkdirs();

        classes.forEach(this::parseClass);

        try (PrintStream pw = new PrintStream(new FileOutputStream(outDir + "/Types/Helper.elm"))) {
            pw.println(helper());
        }

        for (ClassDef def : definitions.values()) {
            String outFile = outDir + "/Types/" + def.getName() + ".elm";
            try (PrintStream pw = new PrintStream(new FileOutputStream(outFile))) {
                genType(pw, def);
                pw.println();
                pw.println();
            }
        }
    }

    private String helper() {
        return "module Types.Helper exposing (..)\n" +
                "\n" +
                "import Json.Decode as JD exposing (fail, succeed)\n" +
                "import Json.Encode as JE\n" +
                "import Time.DateTime as DateTime exposing (DateTime)\n" +
                "import Dict exposing (Dict)\n" +
                "\n" +
                "\n" +
                "encodeMaybe : (a -> JE.Value) -> Maybe a -> JE.Value\n" +
                "encodeMaybe f =\n" +
                "    Maybe.map f\n" +
                "        >> Maybe.withDefault JE.null\n" +
                "\n" +
                "\n" +
                "initDateTime : DateTime\n" +
                "initDateTime =\n" +
                "    DateTime.fromTuple ( 1900, 1, 1, 0, 0, 0, 0 )\n" +
                "\n" +
                "\n" +
                "dateTimeDecoder : JD.Decoder DateTime\n" +
                "dateTimeDecoder =\n" +
                "    let\n" +
                "        convert raw =\n" +
                "            case ( DateTime.fromISO8601 raw, DateTime.fromISO8601 (String.slice 0 19 raw ++ \"Z\") ) of\n" +
                "                ( Ok date, _ ) ->\n" +
                "                    succeed date\n" +
                "\n" +
                "                ( _, Ok date ) ->\n" +
                "                    succeed date\n" +
                "\n" +
                "                ( Err e, _ ) ->\n" +
                "                    Debug.log \"Helper.dateTimeDecoder: \" (fail e)\n" +
                "    in\n" +
                "    JD.string |> JD.andThen convert\n" +
                "\n" +
                "\n" +
                "dateTimeEncoder : DateTime -> JE.Value\n" +
                "dateTimeEncoder val =\n" +
                "    case DateTime.compare val (DateTime.dateTime DateTime.zero) of\n" +
                "        EQ ->\n" +
                "            JE.string \"0000-00-00\"\n" +
                "\n" +
                "        _ ->\n" +
                "            JE.string (DateTime.toISO8601 val)" +
                "\n" +
                "\n" +
                "dictEncoder: (Dict String String) -> JE.Value\n" +
                "dictEncoder dict =\n" +
                "    dict\n" +
                "        |> Dict.toList\n" +
                "        |> List.map (\\( k, v ) -> ( k, JE.string v ))\n" +
                "        |> JE.object";
    }

    private void genType(PrintStream pw, ClassDef def) {
        StringBuilder ret = new StringBuilder();
        //Header
        ret.append("module Types.")
                .append(def.getName())
                .append(" exposing (..)\n\n");
        ret.append("import Json.Encode as JE\n");
        ret.append("import Json.Decode as JD\n");
        ret.append("import Types.Helper exposing (..)\n");
        ret.append("import Json.Decode.Pipeline as JDP exposing (decode, required, optional)\n");
        ret.append("import Time.DateTime as DateTime exposing (DateTime)\n");
        ret.append("import Dict exposing (Dict)\n");

        //Import the complex types
        def.getProps().values()
                .stream()
                .filter(x -> !x.isSimpleType())
                .map(PropertyDef::getClassDef)
                .forEach(x -> ret.append("import Types.").append(x.getName()).append(" as ").append(x.getName()).append("\n"));

        ret.append("\n\n");

        ret.append("{- Generated from " + def.getType().getCanonicalName() + " -}\n\n");

        //Type fields
        ret.append("type ").append(def.getName()).append("Fields \n");
        List<String> types = def.getProps().values().stream()
                .map(x -> (firstToUpper(x.getName()) + " (" + toElmType(x) + ")"))
                .collect(Collectors.toList());
        ret.append("    = " + types.stream().findFirst().orElse(""));
        types.stream().skip(1).forEach(x -> ret.append("\n    | ").append(x));
        ret.append("\n");
        ret.append("\n");


        //Type alias
        ret.append("type alias ").append(def.getName()).append(" =\n");
        ret.append("    { ");
        types = def.getProps().values().stream()
                .map(x -> (x.getSafeName() + " : " + toElmType(x)))
                .collect(Collectors.toList());
        ret.append(types.stream().findFirst().orElse(""));
        types.stream().skip(1).forEach(x -> ret.append("\n    , ").append(x));
        ret.append("\n    }\n");
        ret.append("\n");


        //New
        ret.append("init: ").append(def.getName()).append("\n");
        ret.append("init = \n");
        ret.append("    { ");
        types = def.getProps().values().stream()
                .map(x -> (x.getSafeName() + " = " + defaultForType(x)))
                .collect(Collectors.toList());
        ret.append(types.stream().findFirst().orElse(""));
        types.stream().skip(1).forEach(x -> ret.append("\n    , ").append(x));
        ret.append("\n    }\n");
        ret.append("\n");

        //Decode
        ret.append("decode: JD.Decoder ").append(def.getName()).append("\n");
        ret.append("decode = \n");
        ret.append("    JDP.decode ").append(def.getName()).append("\n");
        def.getProps().values().stream()
                .map(x -> {
                    String s = "";
                    if (x.isCollection() && x.getType() != Byte.TYPE) {
                        s = "JDP.optional \"" + x.getName() + "\" (" + getJDType(x) + ") " + defaultForType(x);
                    } else if (isNullable(x)) {
                        s = "JDP.required \"" + x.getName() + "\" (" + getJDType(x) + ")";
                    } else {
                        s = "JDP.optional \"" + x.getName() + "\" " + getJDType(x) + " " + defaultForType(x);
                    }
                    return s;
                })
                .forEach(x -> ret.append("        |> ").append(x).append("\n"));
        ret.append("\n");

        //Encode
        ret.append("encode: ").append(def.getName()).append(" -> JE.Value").append("\n");
        ret.append("encode o = \n");
        ret.append("    JE.object \n");
        ret.append("        [");
        types = def.getProps().values().stream()
                .map(x -> {
                    String s = " ( \"" + x.getName() + "\", ";
                    if (x.isCollection() && x.getType() != Byte.TYPE)
                        s += "o." + x.getSafeName() + " |> List.map " + getJEType(x) + " |> JE.list";
                    else if (isNullable(x))
                        s += "o." + x.getSafeName() + " |> encodeMaybe " + getJEType(x);
                    else
                        s += "o." + x.getSafeName() + " |> " + getJEType(x);
                    s += ")";
                    return s;
                })
                .collect(Collectors.toList());
        ret.append(types.stream().findFirst().orElse(""));
        types.stream().skip(1).forEach(x -> ret.append("\n        ,").append(x));
        ret.append("\n        ]");
        pw.println(ret.toString());
    }

    private String getJEType(PropertyDef def) {
        if (def.isMap())
            return "dictEncoder";
        String elmDefType = toElmDefType(def);
        String ret = "";
        if (!def.isSimpleType())
            ret += def.getClassDef().getName() + ".encode";
        else if ("String".equals(elmDefType))
            ret += "JE.string";
        else if ("Int".equals(elmDefType))
            ret += "JE.int";
        else if ("Float".equals(elmDefType))
            ret += "JE.float";
        else if ("Bool".equals(elmDefType))
            ret += "JE.bool";
        else if ("DateTime".equals(elmDefType))
            ret += "dateTimeEncoder";
        return ret;
    }

    private String getJDType(PropertyDef def) {
        Class type = def.getType();
        String elmDefType = toElmDefType(def);
        String ret = "";

        if (def.isMap())
            return "(JD.dict JD.string)";

        if (def.isCollection() && type != Byte.TYPE)
            ret = "JD.list ";
        else if (isNullable(def))
            ret = "JD.nullable ";

        if (!def.isSimpleType())
            ret += def.getClassDef().getName() + ".decode";
        else if ("String".equals(elmDefType))
            ret += "JD.string";
        else if ("Int".equals(elmDefType))
            ret += "JD.int";
        else if ("Float".equals(elmDefType))
            ret += "JD.float";
        else if ("Bool".equals(elmDefType))
            ret += "JD.bool";
        else if ("DateTime".equals(elmDefType))
            ret += "dateTimeDecoder";
        return ret;
    }

    private String defaultForType(PropertyDef def) {
        if (def.getDefaultValue() != null && !def.getDefaultValue().all().isEmpty())
            return def.getDefaultValue().all();

        if (def.getDefaultValue() != null && !def.getDefaultValue().elm().isEmpty())
            return def.getDefaultValue().elm();

        if (def.isMap())
            return "Dict.empty";

        String elmType = toElmType(def);
        Class<?> type = def.getType();
        String ret = "";
        if (def.isCollection()) {
            if (type == Byte.TYPE)
                return "\"\"";
            else
                return "[]";
        }

        if (isNullable(def))
            return "Nothing";

        if (!def.isSimpleType())
            ret += def.getClassDef().getName() + ".init";
        else if ("String".equals(elmType))
            ret += "\"\"";
        else if (isNumber(def.getType()))
            ret += "0";
        else if ("Float".equals(elmType))
            ret += "0";
        else if ("Bool".equals(elmType))
            ret += "False";
        else if ("DateTime".equals(elmType))
            ret += "initDateTime";
        return ret;
    }

    private String toElmDefType(PropertyDef def) {
        Class type = def.getType();

        if (!def.isSimpleType())
            return def.getClassDef().getName() + "." + def.getClassDef().getName();

        if (def.isMap())
            return "(Dict String String)";

        if (def.isCollection() && type == Byte.TYPE)
            return "String";

        if (type == Integer.TYPE
                || type == Long.TYPE
                || type == Short.TYPE
                || type == Byte.TYPE
                || type == Long.class
                || type == Integer.class
                || type == BigInteger.class
                || type == Short.class)
            return "Int";
        if (type == Date.class)
            return "DateTime";
        if (type == Boolean.TYPE)
            return "Bool";
        if (Enum.class.isAssignableFrom(type))
            return "String";
        if (type == BigDecimal.class || type == Float.TYPE || type == Double.TYPE)
            return "Float";
        return "String";
    }

    private boolean isNullable(PropertyDef def) {
        if (def.isMap() || def.isCollection())
            return false;
        Class type = def.getType();
        return !Enum.class.isAssignableFrom(type)
                && def.isNullable()
                && type != String.class
                && type != Date.class
                && type != BigDecimal.class
                && type != BigInteger.class;
    }

    private String toElmType(PropertyDef def) {
        String ret = "";
        Class type = def.getType();
        if (def.isCollection() && type != Byte.TYPE)
            ret = "List ";
        else if (isNullable(def))
            ret = "Maybe ";

        return ret += toElmDefType(def);
    }

    private String firstToUpper(String s) {
        return "" + Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
