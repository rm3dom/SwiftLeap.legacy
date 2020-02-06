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
package org.swiftleap.common.util;

import org.krysalis.barcode4j.impl.upcean.EAN13Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by ruans on 2017/06/12.
 */
public class BarcodeUtil {
    public static int calcEan13CheckSum(String in) {
        int checksum = 0;
        for (int i = 1; i < 12; i += 2) {
            checksum += Integer.parseInt(in.substring(i, i + 1));
        }
        checksum *= 3;
        for (int i = 0; i < 12; i += 2) {
            checksum += Integer.parseInt(in.substring(i, i + 1));
        }
        checksum = (10 - checksum % 10) % 10;
        return checksum;
    }

    public static String ean13Encode(String in) {
        if (in == null)
            return "";
        //V 1.0
        //Parameters : a 12 digits length string
        //Return : * a string which give the bar code when it is dispayed with EAN13.TTF font
        //         * an empty string if the supplied parameter is no good
        int i;
        int first;
        String CodeBarre = "";
        boolean tableA;
        //Check for 12 characters
        //And they are really digits
        if (in.matches("^\\d{12}$")) {
            //in += new String(new char[]{(char)((10 - checksum % 10) % 10)});
            //The first digit is taken just as it is, the second one come from table A
            CodeBarre = in.substring(1, 2);
            first = Integer.parseInt(in.substring(0, 1));
            for (i = 2; i <= 6; i++) {
                tableA = false;
                switch (i) {
                    case 2:
                        if (first >= 0 && first <= 3) tableA = true;
                        break;
                    case 3:
                        if (first == 0 || first == 4 || first == 7 || first == 8) tableA = true;
                        break;
                    case 4:
                        if (first == 0 || first == 1 || first == 4 || first == 5 || first == 9) tableA = true;
                        break;
                    case 5:
                        if (first == 0 || first == 2 || first == 5 || first == 6 || first == 7) tableA = true;
                        break;
                    case 6:
                        if (first == 0 || first == 3 || first == 6 || first == 8 || first == 9) tableA = true;
                        break;
                }

                if (tableA)
                    CodeBarre += in.substring(i, i + 1);
                else
                    CodeBarre += (char) (97 + Integer.parseInt(in.substring(i, i + 1)));
            }
            CodeBarre += "-";

            for (i = 7; i <= 11; i++) {
                CodeBarre += (char) (65 + Integer.parseInt(in.substring(i, i + 1)));
            }
            if (in.length() == 13)
                CodeBarre += in.substring(12, 13);
            else
                CodeBarre += (char) (65 + calcEan13CheckSum(in));
        }
        CodeBarre = (char) (81 + Integer.parseInt(in.substring(0, 1))) + "(" + CodeBarre + "(";
        return CodeBarre;
    }


    public static BufferedImage renderEan13(String code, int resolution) throws IOException {
        EAN13Bean bean = new EAN13Bean();

        if (resolution < 1)
            resolution = 660;

        bean.setHeight(10d);

        bean.doQuietZone(false);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        BitmapCanvasProvider provider =
                new BitmapCanvasProvider(bout, "image/x-png", resolution,
                        BufferedImage.TYPE_BYTE_GRAY, false,
                        0);
        bean.generateBarcode(provider, code);

        provider.finish();

        return provider.getBufferedImage();
    }
}
