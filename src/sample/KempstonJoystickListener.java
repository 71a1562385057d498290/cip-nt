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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;



/**
 * The kempston joystick controls are mapped to the
 * keyboard arrow keys and the SPACE key.
 */
public final class KempstonJoystickListener implements KeyListener {

    // using an array just for symmetry with the keyboard :)
    private int[] kempstonJoystickBuffer = new int[1];


    @Override
    public void keyTyped(KeyEvent e) { }



    @Override
    public void keyPressed(KeyEvent e) {

        int keyLocation = e.getKeyLocation();
        int keyCode = e.getKeyCode();

        /*
         * handle kempston joystick mapped keys.
         * keys are active high.
         */
        if (keyCode == KeyEvent.VK_RIGHT) { kempstonJoystickBuffer[0] |= 0x01; }
        if (keyCode == KeyEvent.VK_LEFT)  { kempstonJoystickBuffer[0] |= 0x02; }
        if (keyCode == KeyEvent.VK_DOWN)  { kempstonJoystickBuffer[0] |= 0x04; }
        if (keyCode == KeyEvent.VK_UP)    { kempstonJoystickBuffer[0] |= 0x08; }
        if (keyCode == KeyEvent.VK_SPACE) { kempstonJoystickBuffer[0] |= 0x10; }
    }



    @Override
    public void keyReleased(KeyEvent e) {

        int keyLocation = e.getKeyLocation();
        int keyCode = e.getKeyCode();

        // not included with sample
    }



    public int[] getKempstonJoystickBuffer() { return kempstonJoystickBuffer; }
}
