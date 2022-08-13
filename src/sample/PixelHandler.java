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


package sample;



import sample.bus.SpecBus;

import static sample.utils.PixelColorTools.getRgbColor;


public class PixelHandler {

    private final SpecBus bus;
    private final int[] pixels = new int[256 * 192];



    public PixelHandler(SpecBus bus) { this.bus = bus; }



    /**
     * Get the final decoded screen pixels/bitmap of the current frame.
     * Based on the current frame number and the pixel 'flashiness' the
     * ink and paper colors are exchanged between them.
     *
     * @param frameNumber the current frame number
     * @return the final decoded pixels/bitmap
     */
    public int[] getPixels(int frameNumber) {
        // get the rearranged screen bytes
        int[] linearPixelBytes = bus.getScreenFile();

        for (int addr = 0; addr < linearPixelBytes.length; addr++) {
            // get the attributes address for the current pixel byte.
            int chX = addr % 32;    // the current 8x8 character X coordinate
            int chY = addr >>> 8;   // the current 8x8 character Y coordinate
            int attrAddress = 0x5800 + (chY * 32) + chX;  // the attr address

            // not included with sample
        }
        return pixels;
    }



    /**
     * Get the border color
     *
     * @return an integer representing the RGB border color
     */
    public int getBorderColor() {
        return getRgbColor(bus.getBorderColor());
    }
}
