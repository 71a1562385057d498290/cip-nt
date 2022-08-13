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

import sample.utils.CircularBuffer;

import javax.sound.sampled.*;



public class AudioPlayer implements Runnable {

    private final CircularBuffer buffer = CircularBuffer.getInstance();
    private SourceDataLine sdl;



    @Override
    public void run() {
        try {
            // not included with sample

        } catch (LineUnavailableException e) {
            System.out.println("Line unavailable: " + e.getMessage());

        } catch (InterruptedException e) {
            System.out.println("Thread interrupted: " + e.getMessage());

        } finally {
            if (sdl != null) {
                sdl.drain();
                sdl.stop();
                sdl.close();
            }
        }
    }



    /**
     * It seems that the line buffer gets saturated pretty quickly
     * and sound playback starts to lag. As a temporary workaround,
     * we empty the buffer when a certain threshold is reached.
     * However, this might introduce some, more or less noticeable,
     * artifacts.
     * We might look for other alternatives in the future, but for
     * now this seems to produce reasonable good results.
     */
    private void maintain() {
        if (sdl != null) {
            // not included with sample
        }
    }
}


