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

import java.util.Arrays;



/**
 * A circular byte buffer used for transferring samples
 * to the sound processing thread.
 */
public final class CircularBuffer {

    private final byte[] buffer;
    private int idxR, idxW, occupied;
    private final int capacity;

    private static final CircularBuffer INSTANCE = new CircularBuffer();



    /**
     * Construct the {@link CircularBuffer} with a fixed
     * capacity of 32768 bytes.
     */
    private CircularBuffer() {
        capacity = 4 * 8192;
        buffer = new byte[capacity];

        idxR = 0;
        idxW = 0;

        occupied = 0;
    }



    /**
     * Get the {@link CircularBuffer} instance.
     *
     * @return the {@link CircularBuffer} instance
     */
    public static CircularBuffer getInstance() {
        return INSTANCE;
    }



    /**
     * Put as many items as possible from the source array into the buffer
     * and return the actual number of items added.
     *
     * @param src the byte array containing the items to be added
     * @return the actual number of items added to the buffer
     */
    public synchronized int put(final byte[] src) {
        // not included with sample
    }



    /**
     * Get as many items as possible from the buffer and place them into
     * the destination array.
     * Return the actual number of items retrieved.
     *
     * @param dst destination where the retrieved items are to be placed
     * @return the actual number of items retrieved from the buffer
     */
    public synchronized int get(final byte[] dst) {
        // not included with sample
    }



    /**
     * Custom {@link String} representation of this {@link CircularBuffer}
     *
     * @return the custom {@link String} representation
     */
    @Override
    public String toString() {
        return "CircularBuffer {"
                + "buffer=" + Arrays.toString(buffer)
                + ", capacity=" + capacity
                + ", occupied=" + occupied
                + ", idxR=" + idxR
                + ", idxW=" + idxW
                + '}';
    }
}
