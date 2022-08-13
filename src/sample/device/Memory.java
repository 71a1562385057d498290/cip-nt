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



public class Memory {

    // 16-bit address space, allowing 64K memory
    private final int mem[] = new int[0xffff + 1];

    public Memory() { }


    /**
     * Read a byte from the specified memory address
     *
     * @param address the memory address
     * @return the byte read from the specified address
     */
    public int readByte(int address) {
        return mem[address & 0xFFFF];
    }



    /**
     * Write one byte to the specified memory address
     *
     * @param address the memory address
     * @param value the byte to be written to the specified address
     */
    public void writeByte(int address, int value) {
        mem[address & 0xFFFF] = value & 0xFF;
    }



    /**
     * Provide direct access to the underlying memory array
     *
     * @return an integer array representing the the memory
     */
    public int[] getMemory() {
        return mem;
    }



    /**
     * Get the memory size
     *
     * @return the memory size in bytes
     */
    public int getSize() {
        return mem.length;
    }



    /**
     * Reset the memory to it's default state
     * by filling the array with zeros.
     */
    public void reset() {
        Arrays.fill(mem, 0);
    }
}
