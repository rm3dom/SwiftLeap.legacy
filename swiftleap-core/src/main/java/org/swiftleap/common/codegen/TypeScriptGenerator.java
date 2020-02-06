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
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Date;

/**
 * Created by ruans on 2017/06/14.
 */
class TypeScriptGenerator extends CodeGenerator {

    @Override
    public void genTypes(PrintStream pw, Collection<Class<?>> classes) throws Exception {
        classes.forEach(this::parseClass);

        StringBuilder headers = new StringBuilder();
        pw.println("import {serialize, deserialize} from \"serializer.ts/Serializer\";");
        pw.println("import {Type} from \"serializer.ts/Decorators\";");

        pw.println(helpers());
        pw.println();
        for (ClassDef def : definitions.values()) {
            genType(pw, def);
            pw.println();
            pw.println();
        }
    }

    @Override
    public void genTypes(String outDir, Collection<Class<?>> classes) throws Exception {
        new File(outDir).mkdirs();

        genTypes(new PrintStream(new FileOutputStream(outDir + "/model.ts")), classes);
    }


    private String helpers() {
        return "";
    }

    private void genType(PrintStream pw, ClassDef def) {
        pw.println("export class " + def.getName() + " {");
        def.getProps().values().forEach(p -> {
            if (!p.isSimpleType())
                pw.println("    @Type(() => " + p.getClassDef().getName() + ")");
            else if (p.getType() == Date.class)
                pw.println("    @Type(() => Date)");
            pw.println("    " + p.getName() + ": " + getTsType(p) + ";");
        });
        pw.println("}");
    }

    private String getTsType(PropertyDef p) {
        String ret = "";
        if (p.isCollection() && p.getType() == Byte.TYPE)
            return "string | null";
        if (!p.isSimpleType())
            ret += p.getClassDef().getName();
        else if (isNumber(p.getType()))
            ret += "number";
        else if (p.getType() == Date.class)
            ret += "Date";
        else if (p.getType() == Boolean.TYPE || p.getType() == Boolean.class)
            ret += "boolean";
        else
            ret += "string";

        if (p.isCollection())
            ret = ret + "[] | null";
        else if (p.isNullable())
            ret = ret + " | null";

        return ret;
    }
}
