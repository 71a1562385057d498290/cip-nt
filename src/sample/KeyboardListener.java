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



public final class KeyboardListener implements KeyListener {

    private final int[] keyboardBuffer = new int[8];



    public KeyboardListener() {
        for (int i = 0; i < keyboardBuffer.length; i++) {
            keyboardBuffer[i] = 0x1f; // we only care about the low 5 bits
        }
    }



    @Override
    public void keyTyped(KeyEvent e) { }



    @Override
    public void keyPressed(KeyEvent e) {
        int keyLocation = e.getKeyLocation();
        int keyCode = e.getKeyCode();

        // 0xfefe  CAPS SHIFT?, Z, X, C, V
        if (keyCode == KeyEvent.VK_SHIFT
                && keyLocation == KeyEvent.KEY_LOCATION_LEFT) {
            keyboardBuffer[0] &= 0x1e;
        }
        if (keyCode == KeyEvent.VK_Z) { keyboardBuffer[0] &= 0x1d; }
        if (keyCode == KeyEvent.VK_X) { keyboardBuffer[0] &= 0x1b; }
        if (keyCode == KeyEvent.VK_C) { keyboardBuffer[0] &= 0x17; }
        if (keyCode == KeyEvent.VK_V) { keyboardBuffer[0] &= 0x0f; }
    }



    @Override
    public void keyReleased(KeyEvent e) {
        int keyLocation = e.getKeyLocation();
        int keyCode = e.getKeyCode();

        // 0xfefe  SHIFT, Z, X, C, V
        if (keyCode == KeyEvent.VK_SHIFT
                && keyLocation == KeyEvent.KEY_LOCATION_LEFT) {
            keyboardBuffer[0] |= 0x01;
        }
        if (keyCode == KeyEvent.VK_Z) { keyboardBuffer[0] |= 0x02; }
        if (keyCode == KeyEvent.VK_X) { keyboardBuffer[0] |= 0x04; }
        if (keyCode == KeyEvent.VK_C) { keyboardBuffer[0] |= 0x08; }
        if (keyCode == KeyEvent.VK_V) { keyboardBuffer[0] |= 0x10; }
    }



    public int[] getKeyboardBuffer() { return keyboardBuffer; }
}
