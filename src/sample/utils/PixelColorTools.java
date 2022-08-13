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

public class PixelColorTools {

    /**
     * Get an integer representing the RGB color corresponding
     * to the provided GRB ordered, 4-bit RGBI value.
     *
     * @param grb the provided GRB ordered, 4-bit RGBI value
     * @return an integer representing an RGB color
     */
    public static int getRgbColor(int grb) {
        switch (grb) {
            case 0x0: { return 0x000000; }  // black
            case 0x1: { return 0x0000d7; }  // blue
            case 0x2: { return 0xd70000; }  // red
            case 0x3: { return 0xd700d7; }  // magenta
            case 0x4: { return 0x00d700; }  // green
            case 0x5: { return 0x00d7d7; }  // cyan
            case 0x6: { return 0xd7d700; }  // yellow
            case 0x7: { return 0xffffff; }  // white
            case 0x8: { return 0x000000; }  // black
            case 0x9: { return 0x0000ff; }  // bright blue
            case 0xa: { return 0xff0000; }  // bright red
            case 0xb: { return 0xff00ff; }  // bright magenta
            case 0xc: { return 0x00ff00; }  // bright green
            case 0xd: { return 0x00ffff; }  // bright cyan
            case 0xe: { return 0xffff00; }  // bright yellow
            case 0xf: { return 0xffffff; }  // white

            default: { return 0x000000; }   // default black
        }
    }
}
