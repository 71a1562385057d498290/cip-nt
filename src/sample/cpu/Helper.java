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


package sample.cpu;

public class Helper {

    /**
     * A couple of helper methods for computing overflow,
     * carry and borrow.
     */

    /**
     * Returns true if a + b produces a two's complement overflow.
     * See the HCS12/CPU12 manual.
     */
    public static boolean isOverflowAdd(int a, int b, int r) {
        // not included with sample
    }



    /**
     * Returns true if a + b produces a two's complement overflow.
     * See the HCS12/CPU12 manual.
     */
    public static boolean isOverflowAdd16bit(int a, int b, int r) {
        // not included with sample
    }



    /**
     * Returns true if a - b produces a two's complement overflow.
     * See the HCS12/CPU12 manual.
     */
    public static boolean isOverflowSub(int a, int b, int r) {
        // not included with sample
    }



    /**
     * Returns true if a - b produces a two's complement overflow.
     * See the HCS12/CPU12 manual.
     */
    public static boolean isOverflowSub16bit(int a, int b, int r) {
        // not included with sample
    }



    /**
     * Returns true if a carry from bit 3 occurs when adding a + b.
     * See the HCS12/CPU12 manual.
     */
    public static boolean isCarryBit3(int a, int b, int r) {
        // not included with sample
    }



    /**
     * Returns true if a carry from bit 7 occurs when adding a + b.
     * See the HCS12/CPU12 manual.
     */
    public static boolean isCarryBit7(int a, int b, int r) {
        int A7 = (a >> 7) & 0x1;
        int B7 = (b >> 7) & 0x1;
        int R7 = (r >> 7) & 0x1;

        return (((A7 & B7) | (B7 & ~R7) | (~R7 & A7)) == 1);
    }



    /**
     * Returns true if a carry from bit 11 occurs when adding a + b.
     * See the HCS12/CPU12 manual.
     */
    public static boolean isCarryBit11(int a, int b, int r) {
        // not included with sample
    }



    /**
     * Returns true if a carry from bit 15 occurs when adding a + b.
     * See the HCS12/CPU12 manual.
     */
    public static boolean isCarryBit15(int a, int b, int r) {
        // not included with sample
    }



    /**
     * Returns true if a borrow from bit 4 occurs when subtracting a - b.
     * See the HCS12/CPU12 manual.
     */
    public static boolean isBorrowBit4(int a, int b, int r) {
        // not included with sample
    }



    /**
     * Returns true if a borrow from 'bit 8' occurs when subtracting a - b.
     * See the HCS12/CPU12 manual.
     */
    public static boolean isBorrowBit8(int a, int b, int r) {
        // not included with sample
    }



    /**
     * Returns true if a borrow from bit 12 occurs when subtracting a - b.
     * See the HCS12/CPU12 manual.
     */
    public static boolean isBorrowBit12(int a, int b, int r) {
        // not included with sample
    }



    /**
     * Returns true if a borrow from 'bit 16' occurs when subtracting a - b.
     * See the HCS12/CPU12 manual.
     */
    public static boolean isBorrowBit16(int a, int b, int r) {
        // not included with sample
    }



    /**
     * Returns true if the provided '8 bit' number has an even parity.
     * Inspired by:
     * https://www.freecodecamp.org/news/
     * algorithmic-problem-solving-efficiently-computing-the-parity-of-a-stream-of-numbers-cd652af14643
     */
    public static boolean isParityEven(int r) {
        // not included with sample
    }



    /**
     * Circular shift an '8 bit' number to the right with one
     */
    public static int rotateRight(int x) {
        int a = x & 0xff;
        return (((a >>> 1) | (a << 7))) & 0xff;
    }



    /**
     * Circular shift an '8 bit' number to the left with one
     */
    public static int rotateLeft(int x) {
        // not included with sample
    }
}
