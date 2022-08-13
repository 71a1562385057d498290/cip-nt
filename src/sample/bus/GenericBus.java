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

import sample.device.I0;
import sample.device.Memory;



/**
 * Class representing a generic bus.
 * Attached IO and memory should be configurable.
 * However, currently they are not.
 */
public class GenericBus implements Bus {

    private final I0 io;            // used for I/O read/write
    private final Memory memory;    // used for RAM read/write

    public GenericBus() {
        io = new I0();
        memory = new Memory();
    }



    /**
     * Provide direct access
     * to the underlying memory array.
     *
     * @return an array representing the memory
     */
    @Override
    public int[] getMemory() {
        return memory.getMemory();
    }



    /**
     * Provide direct access
     * to the underlying IO registers array.
     *
     * @return an array representing the IO registers
     */
    @Override
    public int[] getIO() {
        return io.getIO();
    }



    /**
     * Provide direct access to the memory device.
     *
     * @return the memory device
     */
    @Override
    public Memory getMemoryDevice() {
        return memory;
    }



    /**
     * Read a byte from the specified memory address.
     *
     * @param address the memory address
     * @return the byte read from the specified address
     */
    @Override
    public int readByte(int address) {
        return memory.readByte(address);
    }



    /**
     * Write one byte to the specified memory address.
     *
     * @param address the memory address
     * @param data the byte to be written to the specified address
     */
    @Override
    public void writeByte(int address, int data) {
        memory.writeByte(address, data);
    }



    /**
     * Read a 16-bit word from the specified memory address.
     *
     * @param address the memory address
     * @return the byte read from the specified address
     */
    @Override
    public int readWord(int address) {
        // not included with sample
    }



    /**
     * Write a 16-bit word to the specified memory address.
     *
     * @param address the memory address
     * @param data the 16-bit word to be written to the specified address
     */
    @Override
    public void writeWord(int address, int data) {
        // not included with sample
    }



    /**
     * Read one byte from the specified IO address.
     */
    @Override
    public int ioReadByte(int address) {
        return io.readByte(address);
    }



    /**
     * Write one byte to the specified IO address.
     */
    @Override
    public void ioWriteByte(int address, int data) {
        io.writeByte(address, data);
    }



    /**
     * Get the data put on the data bus by the interrupting device.
     * Used with interrupt modes IM0 and IM2.
     */
    @Override
    public Object getIrqDeviceProvidedData() {
        return null;
    }



    /**
     * Used by the interrupting device to put data on the data bus.
     * Used with interrupt modes IM0 and IM2.
     */
    @Override
    public void setIrqDeviceProvidedData(Object data) { }
}
