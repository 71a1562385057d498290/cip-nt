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



public class Clock {

    private long cycles;
    private ClockedDevice[] devices
            = new ClockedDevice[0];



    public Clock() { }



    public Clock(ClockedDevice device) {
        addClockedDevice(device);
    }



    public void addClockedDevice(ClockedDevice device) {
        // not included with sample
    }



    /**
     * Perform one clock cycle.
     * This method is used for driving the
     * cpu and other attached {@link ClockedDevice}s.
     * When the clock steps, so does all the
     * other {@link ClockedDevice}s.
     *
     * @return this method always returns 1
     */
    public int step() {
        cycles += 1;

        // not included with sample
        return 1;
    }



    /**
     * Get the total number of cycles
     * performed since the beginning of time
     *
     * @return the total number of cycles performed
     */
    public long getCycles() {
        return cycles;
    }
}
