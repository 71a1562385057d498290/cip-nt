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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;



public class KempstonMouseListener
        implements MouseMotionListener, MouseListener {

    // keep track of previous positions in order to determine the direction of movement
    private int scrX;
    private int scrY;

    // the data as seen by the kempston mouse
    // [0] = x
    // [1] = y
    // [2] = mouse buttons
    private int[] kempstonMouseBuffer = new int[3];



    public KempstonMouseListener() {
        for (int i = 0; i < kempstonMouseBuffer.length; i++) {
            kempstonMouseBuffer[i] = 0xff;
        }
    }



    @Override
    public void mouseDragged(MouseEvent e) {}



    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        // not included with sample
    }



    @Override
    public void mouseClicked(MouseEvent e) {}



    @Override
    public void mousePressed(MouseEvent e) {
        // set the 'pressed' bits for the kempston mouse
        int btn = e.getButton();

        if (btn == MouseEvent.BUTTON1) {
            kempstonMouseBuffer[2] &= 0xfd; // left mouse button
        }

        // not included with sample
    }


    @Override
    public void mouseExited(MouseEvent e) {}



    @Override
    public void mouseEntered(MouseEvent e) {}



    @Override
    public void mouseReleased(MouseEvent e) {
        // set the 'released' bits for the kempston mouse
        int btn = e.getButton();

        // not included with sample
    }



    public int[] getKempstonMouseBuffer() { return kempstonMouseBuffer; }
}
