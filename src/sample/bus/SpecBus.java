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


package sample.bus;



import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;



/**
 * Emulate a ZX Spectrum 48k, Issue 3 bus
 *
 * <pre>
 * Notes:
 * Because of the way the ZX Spectrum 48k bus is organized,
 * some magic happens between bit 4 OUT and bit 6 IN on port 0xFE.
 * On an Issue 3, an OUT 0xFE with bit 4 reset will give a reset bit 6 for IN 0xFE.
 * See the table from {@link #ioWriteByte(int, int)}.
 *
 * Any even port will access the ULA, but traditionally one port is used: 0xFE.
 * More details can be found here: http://rk.nvg.ntnu.no/sinclair/faq/tech_48.html
 *
 * IN from port xxFE will read the keyboard state and tape input.
 * OUT to the same port will set the border colour to (b2, b1, b0), drive the MIC socket
 * with b3 and the loudspeaker with b4. b5-b7 are not used.
 * The high byte is ignored on OUT.
 * </pre>
 */
public final class SpecBus extends GenericBus {

    // used for keyboard input/output operations
    private final int[] keyboardIO = new int[256];

    // used for kempston joystick input/output operations
    private final int[] kempstonJoystickIO = new int[1];

    // used for kempston mouse input/output operations
    private final int[] kempstonMouseIO = new int[3];

    // keep track of the EAR input bit 6.
    // apparently, when the line is silent, its value is zero.
    private int bit6EARIn = 0x00;

    // keep track of the EAR output bit 4,
    // in order to emulate the effect it has on EAR input bit 6.
    // the EAR output bit 4 is also the one providing the audio data.
    // assume default value is 0.
    private int bit4EAROut = 0x00;

    // keep track of the set border color
    private int borderColor = 0x07;

    // used for rearranging the screen memory on the fly.
    // at the end of a frame this should contain the frame's bitmap data
    // arranged linearly. it does not contain attribute data.
    private final int linearPixelBytes[] = new int[6144];

    // keep track of lines that need a repaint.
    // not yet implemented.
    private boolean[] dirtyLines = new boolean[192];



    public SpecBus() { Arrays.fill(keyboardIO, 0x1f); } // we only care about the low 5 bits



    /**
     * "keyboard addresses and keys, in order bits 0..4.
     * bit reads low 0 if key is pressed, 1 otherwise."
     */
    public int ioKeyboardReadByte(int address) {
        int addr = (address >> 8) & 0xff;

        int data = 0x1f;    // don't care about the high 3 bits
        int high = 0xfe;    // row starting point

        // not included with sample

        return data;
    }



    /**
     * this method is only used when setting keyboard row data from the ULA's side.
     * the row data indicates which keys are pressed. only the low 5 bits are used.
     *
     * @param address the row for which the value is to be set
     * @param data the actual row data.
     */
    public void ioKeyboardWriteByte(int address, int data) {
        int addr = (address >> 8) & 0xff;
        keyboardIO[addr] = data & 0x1f;
    }



    /**
     * this method is only used when querying kempston joystick data
     * from the CPU's side.
     *
     * @return the kempston joystick data
     */
    public int ioKempstonJoystickReadByte() {
        return kempstonJoystickIO[0];
    }



    /**
     * this method is only used when setting kempston joystick
     * data from the ULA's side.
     * bits: 000FUDLR, active high
     *
     * @param data the kempston joystick data
     */
    public void ioKempstonJoystickWriteByte(int data) {
        kempstonJoystickIO[0] = data & 0x1f;
    }



    /**
     * <pre>
     * this method is only used when querying kempston mouse data
     * from the CPU's side.
     *
     * type of data:
     * 0 - x
     * 1 - y
     * 2 - mouse buttons
     * </pre>
     *
     * @return the kempston mouse data for the specified type
     */
    public int ioKempstonMouseReadByte(int type) {
        return kempstonMouseIO[type];
    }



    /**
     * <pre>
     * this method is only used when setting kempston mouse
     * data from the ULA's side.
     *
     * type of data:
     * 0 - x
     * 1 - y
     * 2 - mouse buttons
     * </pre>
     *
     * @param type the data type
     * @param data the kempston mouse data for the specified type
     */
    public void ioKempstonMouseWriteByte(int type, int data) {
        kempstonMouseIO[type] = data & 0xff;
    }



    /**
     * Write one byte to the specified memory address.
     * This method will write to any address, including the ROM.
     * Should only be used for initial loading purposes.
     *
     * @param address the memory address
     * @param data the byte to be written to the specified address
     */
    public void writeByteUnrestricted(int address, int data) {
        super.writeByte(address, data);
    }



    /**
     * Write one byte to the specified memory address.
     * If the memory address happens to be in the screen
     * memory area, compute the linear address of the current pixel
     * byte and in addition to the main memory, also store the data
     * to the newly computed linear address.
     *
     * @param address the memory address
     * @param data the byte to be written to the specified address
     */
    @Override
    public void writeByte(int address, int data) {
        if (address < 0x4000) { return; }   // do not poke the ROM
        if (address < 0x5800) {     // screen bitmap address range
            /*
             * compute on the fly the 'linear address' for the current pixel byte.
             * that is, rearrange the pixel bytes from the spectrum format into a
             * linear fashion just like the attribute bytes are arranged.
             *
             * addr = 010_tt_ppp_rrr_ccccc
             * t    = one of the thirds of the screen (0..2)
             * p    = pixel line number inside character row (0..7)
             * r    = character row within one of the thirds of the screen (0..7)
             * c    = the character column number (0..31)
             *
             * linear address = ((t * 64) + (r * 8) + p) * 32 + c
             */

            // not included with sample
        }
        super.writeByte(address, data);
    }



    /**
     * Port 0xFE (254), IN
     *
     * Bits 5 and 7 are not used when doing an IN.
     *
     * "bit 6 of IN-port FE is the EAR input bit. when the line is silent,
     * its value is zero, except in the early Model 2 of the Spectrum, where it was one.
     * when there is a signal, this bit toggles. bits 5 and 7 are always one."
     *
     * .-------------------------------------------------------.
     * | BIT   |  7  |  6  |  5  |  4  |  3  |  2  |  1  |  0  |
     * |-------------------------------------------------------|
     * |       |  -  | EAR |  -  |            KEYS             |
     * `-------------------------------------------------------'
     */
    @Override
    public int ioReadByte(int address) {
        int data = 0xff;

        if ((address & 0x1) == 0) { // all even ports would address the ULA.
            // not included with sample
        }
        return data;
    }



    /**
     * Port 0xFE (254), OUT
     *
     * <pre>
     * Bits 5-7 are not used when OUT-ing.
     * </pre>
     */
    @Override
    public void ioWriteByte(int address, int data) {
        if ((address & 0x1) == 0) {  // target is ULA. set border, EAR, MIC.
            borderColor = data & 0x7;
            bit4EAROut = (data >> 4) & 0x1;
        }
    }



    public void setEARInBit(int value) { bit6EARIn = value; }



    public int getEAROutBit() { return bit4EAROut; }



    /**
     * Get the previously set border color
     *
     * @return the previously set 4-bit RGBI border color
     */
    public int getBorderColor() { return borderColor; }



    /**
     * Set the border color.
     *
     * @param borderColor the 4-bit RGBI border color
     */
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }



    /**
     * Provide direct access to the underlying
     * rearranged linear screen memory array instead
     * of the entire screen memory.
     *
     * @return an array representing the rearranged linear screen memory
     */
    public int[] getScreenFile() { return linearPixelBytes; }
}
