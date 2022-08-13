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


package sample.device;

import java.util.Arrays;



/**
 * Class representing attached IO devices.
 * <pre>
 * This is not a faithful representation of an IO device.
 * For simplicity, all specialized IO port behaviours
 * are actually handled in the specialized bus implementations.
 * By default, a 16-bit I/O address space is assumed and
 * reading from a port will return 0xFF.
 * </pre>
 */
public class I0 {

    private final int io[] = new int[0xffff + 1];

    public I0() { reset(); }



    /**
     * Provide direct access
     * to the underlying IO registers array.
     *
     * @return an array representing the IO registers
     */
    public int[] getIO() {
        return io;
    }



    /**
     * Read one byte from the specified IO address
     *
     * @param address the IO address
     * @return the byte read from the specified address
     */
    public int readByte(int address) {
        return io[address & 0xFFFF];
    }



    /**
     * Write one byte to the specified IO address
     *
     * @param address the IO address
     * @param data the byte to be written to the specified address
     */
    public void writeByte(int address, int data) {
        io[address & 0xFFFF] = data & 0xFF;
    }



    /**
     * Reset the IO ports to their default state
     * by filling the array with 0xff values.
     */
    public final void reset() {
        Arrays.fill(io, 0xff);
    }
}
