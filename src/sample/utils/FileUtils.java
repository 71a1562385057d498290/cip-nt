/*
 * Copyright (c) 71a1562385057d498290
 * All rights reserved.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package sample.utils;

import java.io.IOException;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;



public class FileUtils {

    /**
     * Load the content of a file and return it as a byte array.
     *
     * @param file the file
     * @return the file content as a byte array, or an empty array in case of failure
     */
    public static byte[] loadFile(String file) {
        byte[] data = new byte[0];
        try {
            if (file != null) {
                data = Files.readAllBytes(Paths.get(file));
            }

        } catch (IOException e) {
            System.out.println("Unable to load data from file!" + e.getMessage());
        }
        return data;
    }



    /**
     * Save the provided data to a file.
     *
     * @param file the file
     * @param data the data to be saved.
     */
    public static void saveFile(String file, int[] data) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            for (int i = 0; i < data.length; i++) {
                fos.write(data[i]);
            }
            fos.flush();
            fos.close();

        } catch (IOException e) {
            System.out.println("Unable to save data to file: " + e.getMessage());
        }
    }



    /** SNA file filter */
    public static FileFilter snaFileFilter(boolean allowDirectories) {
        return new FileFilter() {

            @Override
            public boolean accept(File file) {
                return file.isDirectory() || file.getName().toLowerCase().endsWith(".sna");
            }

            @Override
            public String getDescription() {
                if (!allowDirectories) {
                    return ".sna files";
                } else {
                    return ".sna files or directories";
                }
            }
        };
    }
}
