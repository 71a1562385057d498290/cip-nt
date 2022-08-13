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

import sample.bus.Bus;
import sample.debug.DebuggerConfig;
import sample.debug.ReadableOpcode;
import sample.device.ClockedDevice;

import java.io.IOException;
import java.time.Instant;
import java.time.Duration;

import static sample.cpu.Helper.*;



public final class Z80 extends InstructionSet implements ClockedDevice {

    // 8-bit main and alternate register set
    private int A = 0, A_ = 0;
    private int B = 0, B_ = 0;
    private int C = 0, C_ = 0;
    private int D = 0, D_ = 0;
    private int E = 0, E_ = 0;
    private int H = 0, H_ = 0;
    private int L = 0, L_ = 0;
    private int F = 0, F_ = 0;

    private int IX = 0; // 16-bit index register
    private int IY = 0; // 16-bit index register

    private int I = 0;  // 8-bit interrupt vector
    private int R = 0;  // 8-bit memory refresh register

    private int PC = 0; // 16-bit program counter
    private int SP = 0; // 16-bit external stack pointer

    private boolean IFF1 = false;   // interrupt flip-flop
    private boolean IFF2 = false;   // interrupt flip-flop backup

    private InterruptMode IRM = InterruptMode.IM0;  // modes are IM0, IM1 and IM2
    private InterruptType IRQ = InterruptType.NIL;  // types are NIL, NMI and INT

    private boolean halted = false;
    private boolean irqSuspended = false;
    private boolean nopAfterNoni = false;

    // indicates that the cpu is busy executing an instruction
    private boolean busy;
    // the number of cycles remaining for the current instruction
    private int busyCycles;

    private final Bus bus;

    private long cycleCount = 0;    // total number of cycles executed
    private long instrCount = 0;    // total number of instructions executed



    /* this is used for internal testing and debugging purposes only */
    private final Debugger debugger = new Debugger();



    /**
     * Construct the CPU and attach the memory to it
     *
     * @param bus the RAM memory
     */
    public Z80(Bus bus) { this.bus = bus; }



    /**
     * Push a byte worth of data onto the stack
     *
     * @param data the byte of data to be pushed onto the stack
     */
    private void stackPushByte(int data) {
        SP(SP - 1);
        writeByte(SP, data & 0xFF);
    }



    /**
     * Pop a byte worth of data from the stack
     *
     * @return a byte of data read from the top of the stack
     */
    private int stackPopByte() {
        int value = readByte(SP);
        SP(SP + 1);

        return value;
    }



    /**
     * Push 16-bit worth of data onto the stack
     *
     * @param data the 16-bit of data to be pushed onto the stack
     */
    private void stackPushWord(int data) {
        SP(SP - 2);
        writeWord(SP, data & 0xFFFF);
    }



    /**
     * Pop 16-bit worth of data from the stack
     *
     * @return the 16-bit of data read from the top of the stack
     */
    private int stackPopWord() {
        int value = readWord(SP);
        SP(SP + 2);

        return value;
    }



    private interface RegisterLookup {
        void setValue(int value);
        int getValue();
    }



    /**
     * We use this lookup table to identify registers based on a predefined
     * bit pattern and then allow get and set operations on said registers.
     * The 6th index is not used.
     *
     * <pre>
     * Possible bit patterns are:
     * -----------
     * | A | 111 |
     * | B | 000 |
     * | C | 001 |
     * | D | 010 |
     * | E | 011 |
     * | H | 100 |
     * | L | 101 |
     * -----------
     * </pre>
     */
    private final RegisterLookup[] registerLookup = new RegisterLookup[8];

    {
        // not included with sample
    }



    /** Reset the CPU to its initial state by setting all registers to zero */
    public void reset() {
        A = F = A_ = F_ = 0;
        B = C = B_ = C_ = 0;
        D = E = D_ = E_ = 0;
        H = L = H_ = L_ = 0;

        I = 0;
        R = 0;

        PC = 0;
        SP = 0;

        IX = IY = 0;
        IRM = InterruptMode.IM0;

        halted = false;
        // TODO analyze what other entities need to be reset here
    }



    @Override
    public int step() {
        // not included with sample
    }




    private int cycle() {
        // not included with sample
    }




    private int handleInterrupts() {
        int tStates = 0;
        if (IRQ == InterruptType.NIL) {
            //  seems to be a bit faster/ish than relying
            //  on the switch statement alone.
            return tStates;
        }

        switch (IRQ) {
            // not included with sample
        }
        return tStates;
    }



    /**
     * Handle a non-maskable interrupt.
     */
    private int handleNmi() {
        wakeUpIfHalted();

        IFF2 = IFF1;
        IFF1 = false;

        stackPushWord(PC); PC(0x66);
        return 11;  // additional cycles needed to get to 0x0066.
    }



    /**
     * Handle a standard maskable interrupt.
     * Three interrupt modes are supported by the cpu: IM0, IM1 and IM2.
     */
    private int handleInt() {
        int tStates = 0;
        if (IFF1 && !irqSuspended) {
            wakeUpIfHalted();

            switch (IRM) {
                case IM0:

                case IM1: {
                    // not included with sample
                }

                case IM2: {
                    // not included with sample
                }
            }
        }

        return tStates;
    }



    /**
     * Wake up from the HALTED state.
     */
    private void wakeUpIfHalted() {
        if (halted) { halted = false; PC(PC + 1); }
    }



    /**
     * Used by peripherals to trigger a NMI.
     */
    public void nmi() {
        IRQ = InterruptType.NMI;
    }



    /**
     * Used by peripherals to trigger an INT.
     */
    public void interrupt() {
        IRQ = InterruptType.INT;
    }



    /**
     * Fetch, decode and execute an instruction.
     *
     * @return the number of T-states for the executed instruction
     */
    private int fdeInstruction() {
        int tStates = 0;
        int refresh = 0;

        int pc = PC;
        // not included with sample

        switch (opcode) { // fetch and decode opcode
            // not included with sample
            }
        }

        Instruction instr = getInstruction(opcode);
        // not included with sample

        return tStates;
    }



    // ************************************************************************************************ //
    //                                                                                                  //
    //                              THE Z80 INSTRUCTION SET IMPLEMENTATION                              //
    //                                                                                                  //
    // ************************************************************************************************ //




    /**
     * SCF
     *
     * The Carry flag in the F Register is set.
     *
     * S is not affected.
     * Z is not affected.
     * H is reset.
     * P/V is not affected.
     * N is reset.
     * C is set.
     */
    @Override
    protected int SCF() {
        // -----------------------------------------------
        // | opcode (37) | 0 | 0 | 1 | 1 | 0 | 1 | 1 | 1 |
        // -----------------------------------------------
        // | M-cycle | 1 |
        // | T-cycle | 4 |
        // ---------------
        FH(false); FN(false); FC(true);
        return 4;
    }



    /**
     * NOP
     *
     * The CPU performs no operation during this machine cycle.
     */
    @Override
    protected int NOP() {
        // -----------------------------------------------
        // | opcode (00) | 0 | 0 | 0 | 0 | 0 | 0 | 0 | 0 |
        // -----------------------------------------------
        // | M-cycle | 1 |
        // | T-cycle | 4 |
        // ---------------
        return 4;
    }


    /**
     * HALT
     *
     * <pre>
     * The HALT instruction suspends CPU operation until a subsequent interrupt or reset is
     * received. While in the HALT state, the processor executes NOPs to maintain memory
     * refresh logic.
     *
     * Note: The HALT instruction is repeated [...]. See pages 28 and 29 of the official manual.
     * </pre>
     */
    @Override
    protected int HALT() {
        // -----------------------------------------------
        // | opcode (76) | 0 | 1 | 1 | 1 | 0 | 1 | 1 | 0 |
        // -----------------------------------------------
        // | M-cycle | 1 |
        // | T-cycle | 4 |
        // ---------------
        halted = true; PC(PC - 1);
        return 4;
    }


    /**
     * IM 0
     *
     * The IM 0 instruction sets Interrupt Mode 0. In this mode, the interrupting device can insert
     * any instruction on the data bus for execution by the CPU. The first byte of a multi-byte
     * instruction is read during the interrupt acknowledge cycle. Subsequent bytes are read in by
     * a normal memory read sequence.
     */
    @Override
    protected int IM0() {
        // -----------------------------------------------
        // | prefix (ED) | 1 | 1 | 1 | 0 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (46) | 0 | 1 | 0 | 0 | 0 | 1 | 1 | 0 |
        // -----------------------------------------------
        // | M-cycle | 2 |
        // | T-cycle | 8 |
        // ---------------
        IRM = InterruptMode.IM0;
        return 8;
    }



    /**
     * IM 1
     *
     * The IM 1 instruction sets Interrupt Mode 1. In this mode, the processor responds to an
     * interrupt by executing a restart at address 0038h.
     */
    @Override
    protected int IM1() {
        // -----------------------------------------------
        // | prefix (ED) | 1 | 1 | 1 | 0 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (56) | 0 | 1 | 0 | 1 | 0 | 1 | 1 | 0 |
        // -----------------------------------------------
        // | M-cycle | 2 |
        // | T-cycle | 8 |
        // ---------------
        IRM = InterruptMode.IM1;
        return 8;
    }



    /**
     * IM 2
     *
     * The IM 2 instruction sets the vectored Interrupt Mode 2. This mode allows an indirect call
     * to any memory location by an 8-bit vector supplied from the peripheral device. This vector
     * then becomes the least-significant eight bits of the indirect pointer, while the I Register in
     * the CPU provides the most-significant eight bits. This address points to an address in a
     * vector table that is the starting address for the interrupt service routine.
     */
    @Override
    protected int IM2() {
        // -----------------------------------------------
        // | prefix (ED) | 1 | 1 | 1 | 0 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (5E) | 0 | 1 | 0 | 1 | 1 | 1 | 1 | 0 |
        // -----------------------------------------------
        // | M-cycle | 2 |
        // | T-cycle | 8 |
        // ---------------
        IRM = InterruptMode.IM2;
        return 8;
    }



    /**
     * DI
     * DI disables the maskable interrupt by resetting the interrupt enable flip-flops (IFF1 and
     * IFF2).
     */
    @Override
    protected int DI() {
        // -----------------------------------------------
        // | opcode (F3) | 1 | 1 | 1 | 1 | 0 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | M-cycle | 1 |
        // | T-cycle | 4 |
        // ---------------
        IFF1 = IFF2 = false;
        return 4;
    }



    /**
     * EI
     *
     * <pre>
     * The enable interrupt instruction sets both interrupt enable flip flops (IFF1 and IFF2) to a
     * logic 1, allowing recognition of any maskable interrupt.
     *
     * Note: During the execution of this instruction and the following instruction, maskable interrupts
     * are disabled.
     * </pre>
     */
    @Override
    protected int EI() {
        // -----------------------------------------------
        // | opcode (FB) | 1 | 1 | 1 | 1 | 0 | 1 | 1 | 1 |
        // -----------------------------------------------
        // | M-cycle | 1 |
        // | T-cycle | 4 |
        // ---------------
        IFF1 = IFF2 = true;
        irqSuspended = true;

        // allow interrupts only after the instruction following the EI
        // instruction has finished executing.

        return 4;
    }



    /**
     * JP nn
     *
     * Operand nn is loaded to register pair Program Counter (PC). The next instruction is
     * fetched from the location designated by the new contents of the PC.
     */
    @Override
    protected int JP_NN() {
        // -----------------------------------------------
        // | opcode (C3) | 1 | 1 | 0 | 0 | 0 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | n           | n | n | n | n | n | n | n | n |
        // -----------------------------------------------
        // | n           | n | n | n | n | n | n | n | n |
        // -----------------------------------------------
        // | M-cycle | 3  |
        // | T-cycle | 10 |
        // ----------------
        int nn = readWord(PC);
        PC(nn);
        return 10;
    }



    /**
     * RRA
     *
     * The contents of the Accumulator (Register A) are rotated right 1 bit position through the
     * Carry flag. The previous contents of the Carry flag are copied to bit 7. Bit 0 is the least significant
     * bit.
     *
     * S is not affected.
     * Z is not affected.
     * H is reset.
     * P/V is not affected.
     * N is reset.
     * C is data from bit 0 of Accumulator.
     */
    @Override
    protected int RRA() {
        // -----------------------------------------------
        // | opcode (1F) | 0 | 0 | 0 | 1 | 1 | 1 | 1 | 1 |
        // -----------------------------------------------
        // | M-cycle | 1  |
        // | T-cycle | 4  |
        // ----------------
        int a = A;

        int cf = FC();
        int b0 = a & 0x1;

        a = ((a >>> 1) | (cf << 7)) & 0xff;
        A(a);

        FH(false); FN(false); FC(b0 == 1);
        return 4;
    }



    /**
     * RLD
     *
     * The contents of the low-order four bits (bits 3, 2, 1, and 0) of the memory location (HL)
     * are copied to the high-order four bits (7, 6, 5, and 4) of that same memory location; the
     * previous contents of those high-order four bits are copied to the low-order four bits of the
     * Accumulator (Register A); and the previous contents of the low-order four bits of the
     * Accumulator are copied to the low-order four bits of memory location (HL). The contents
     * of the high-order bits of the Accumulator are unaffected.
     *
     * S is set if the Accumulator is negative after an operation; otherwise, it is reset.
     * Z is set if the Accumulator is 0 after an operation; otherwise, it is reset.
     * H is reset.
     * P/V is set if the parity of the Accumulator is even after an operation; otherwise, it is reset.
     * N is reset.
     * C is not affected.
     */
    @Override
    protected int RLD() {
        // -----------------------------------------------
        // | prefix (ED) | 1 | 1 | 1 | 0 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (6F) | 0 | 1 | 1 | 0 | 1 | 1 | 1 | 1 |
        // -----------------------------------------------
        // | M-cycle | 5  |
        // | T-cycle | 18 |
        // ----------------
        int a = A;
        int m = readByte(HL());

        int ah = (a >> 4) & 0x0f;
        int al = a & 0x0f;

        int mh = (m >> 4) & 0x0f;
        int ml = m & 0x0f;

        a = (ah << 4) | mh;
        m = (ml << 4) | al;

        A(a);
        writeByte(HL(), m);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false);

        return 18;
    }



    /**
     * RRD
     *
     * The contents of the low-order four bits (bits 3, 2, 1, and 0) of memory location (HL) are
     * copied to the low-order four bits of the Accumulator (Register A). The previous contents
     * of the low-order four bits of the Accumulator are copied to the high-order four bits (7, 6, 5,
     * and 4) of location (HL); and the previous contents of the high-order four bits of (HL) are
     * copied to the low-order four bits of (HL). The contents of the high-order bits of the Accumulator
     * are unaffected.
     *
     * S is set if the Accumulator is negative after an operation; otherwise, it is reset.
     * Z is set if the Accumulator is 0 after an operation; otherwise, it is reset.
     * H is reset.
     * P/V is set if the parity of the Accumulator is even after an operation; otherwise, it is reset.
     * N is reset.
     * C is not affected.
     */
    @Override
    protected int RRD() {
        // -----------------------------------------------
        // | prefix (ED) | 1 | 1 | 1 | 0 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (67) | 0 | 1 | 1 | 0 | 0 | 1 | 1 | 1 |
        // -----------------------------------------------
        // | M-cycle | 5  |
        // | T-cycle | 18 |
        // ----------------
        int a = A;
        int m = readByte(HL());

        int ah = (a >> 4) & 0x0f;
        int al = a & 0x0f;

        int mh = (m >> 4) & 0x0f;
        int ml = m & 0x0f;

        a = (ah << 4) | ml;
        m = (al << 4) | mh;

        A(a);
        writeByte(HL(), m);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false);

        return 18;
    }



    /**
     * RLC r
     *
     * <pre>
     * The contents of register r are rotated left 1 bit position. The contents of bit 7 are copied to
     * the Carry flag and also to bit 0.
     *
     * -----------
     * | A | 111 |
     * | B | 000 |
     * | C | 001 |
     * | D | 010 |
     * | E | 011 |
     * | H | 100 |
     * | L | 101 |
     * -----------
     *
     * Opcodes: 00000rrr
     *
     * A: 00000111 0x07
     * B: 00000000 0x00
     * C: 00000001 0x01
     * D: 00000010 0x02
     * E: 00000011 0x03
     * H: 00000100 0x04
     * L: 00000101 0x05
     *
     *          .-------------------.
     * .----.   |   .----------.    |
     * | CY |<--+---| 7 <--- 0 |<---'
     * '----'       '----------'
     *
     * S is set if result is negative; otherwise, it is reset.
     * Z is set if result is 0; otherwise, it is reset.
     * H is reset.
     * P/V is set if parity even; otherwise, it is reset.
     * N is reset.
     * C is data from bit 7 of source register.
     *
     * </pre>
     */
    @Override
    protected int RLC_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 0 | 0 | 0 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 2 |
        // | T-cycle | 8 |
        // ---------------
        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];

        int a = reg.getValue();
        int b7 = (a >>> 7) & 0x1;

        a = rotateLeft(a) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b7 == 1);

        return 8;
    }



    /**
     * SLA r
     *
     * <pre>
     * An arithmetic shift left 1 bit position is performed on the contents of operand r. The contents
     * of bit 7 are copied to the Carry flag. Bit 0 is the least-significant bit.
     *
     * -----------
     * | A | 111 |
     * | B | 000 |
     * | C | 001 |
     * | D | 010 |
     * | E | 011 |
     * | H | 100 |
     * | L | 101 |
     * -----------
     *
     * Opcodes: 00100rrr
     *
     * A: 00100111  0x27
     * B: 00100000  0x20
     * C: 00100001  0x21
     * D: 00100010  0x22
     * E: 00100011  0x23
     * H: 00100100  0x24
     * L: 00100101  0x25
     *
     *  .----.      .----------.
     *  | CY |<-----| 7 <--- 0 |<---- 0
     *  '----'      '----------'
     *
     * S is set if result is negative; otherwise, it is reset.
     * Z is set if result is 0; otherwise, it is reset.
     * H is reset.
     * P/V is set if parity is even; otherwise, it is reset.
     * N is reset.
     * C is data from bit 7.
     *
     * </pre>
     */
    @Override
    protected int SLA_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 1 | 0 | 0 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 2 |
        // | T-cycle | 8 |
        // ---------------
        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];

        int a = reg.getValue();
        int b7 = (a >>> 7) & 0x1;

        a = (a << 1) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b7 == 1);

        return 8;
    }



    /**
     * BIT b, (IX + d)
     *
     * S is unknown.
     * Z is set if specified bit is 0; otherwise, it is reset.
     * H is set.
     * P/V is unknown.
     * N is reset.
     * C is not affected.
     */
    @Override
    protected int BIT_B_IXd(final int opcode) {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 1 | b | b | b | 1 | 1 | 0 |
        // -----------------------------------------------
        // | M-cycle | 5  |
        // | T-cycle | 20 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int bit = (opcode >> 3) & 0x07;

        int a = readByte(IX + d);
        int b = (a >>> bit) & 0x1;

        FZ(b == 0);
        FS(b == 1 && bit == 7);
        FH(true); FP(b == 0); FN(false);

        return 20;
    }



    /**
     * BIT b, (IY + d)
     *
     * S is unknown.
     * Z is set if specified bit is 0; otherwise, it is reset.
     * H is set.
     * P/V is unknown.
     * N is reset.
     * C is not affected.
     */
    @Override
    protected int BIT_B_IYd(final int opcode) {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 1 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 1 | b | b | b | 1 | 1 | 0 |
        // -----------------------------------------------
        // | M-cycle | 5  |
        // | T-cycle | 20 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int bit = (opcode >> 3) & 0x07;

        int a = readByte(IY + d);
        int b = (a >>> bit) & 0x1;

        FZ(b == 0);
        FS(b == 1 && bit == 7);
        FH(true); FP(b == 0); FN(false);

        return 20;
    }



    /**
     * IN r (C)
     *
     * <pre>
     * The contents of Register C are placed on the bottom half (A0 through A7) of the address bus to
     * select the I/O device at one of 256 possible ports. The contents of Register B are placed on
     * the top half (A8 through A15) of the address bus at this time. Then one byte from the selected
     * port is placed on the data bus and written to register r in the CPU. Register r identifies any
     * of the CPU registers shown in the following table, which also indicates the corresponding 3bit
     * r field for each. The flags are affected, checking the input data.
     *
     * -----------
     * | A | 111 |
     * | B | 000 |
     * | C | 001 |
     * | D | 010 |
     * | E | 011 |
     * | H | 100 |
     * | L | 101 |
     * -----------
     *
     * Opcodes: 01rrr000
     *
     * A: 01111000  0x78
     * B: 01000000  0x40
     * C: 01001000  0x48
     * D: 01010000  0x50
     * E: 01011000  0x58
     * H: 01100000  0x60
     * L: 01101000  0x68
     * F: 01110000  0x70 Undefined op code; set the flag. Result not written to any register, but flags are set.
     *
     * S is set if input data is negative; otherwise, it is reset.
     * Z is set if input data is 0; otherwise, it is reset.
     * H is reset.
     * P/V is set if parity is even; otherwise, it is reset.
     * N is reset.
     * C is not affected.
     * </pre>
     */
    @Override
    protected int IN_R_Ca(final int opcode) {
        // -----------------------------------------------
        // | prefix (ED) | 1 | 1 | 1 | 0 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode      | 0 | 1 | r | r | r | 0 | 0 | 0 |
        // -----------------------------------------------
        // | M-cycle | 3  |
        // | T-cycle | 12 |
        // ----------------
        int dst = (opcode >> 3) & 0x07;

        int addr = BC();
        int data = ioReadByte(addr);

        registerLookup[dst].setValue(data);

        FS(data > 0x7f); FZ(data == 0);
        FH(false); FP(isParityEven(data)); FN(false);

        return 12;
    }



    /**
     * IND
     *
     * <pre>
     * (HL) <- (C), B <- B - 1, HL <- HL - 1
     *
     * The contents of Register C are placed on the bottom half (A0 through A7) of the address bus
     * to select the I/O device at one of 256 possible ports. Register B can be used as a byte
     * counter, and its contents are placed on the top half (A8 through A15) of the address bus at
     * this time. Then one byte from the selected port is placed on the data bus and written to
     * the CPU. The contents of the HL register pair are placed on the address bus and the input
     * byte is written to the corresponding location of memory. Finally, the byte counter and
     * register pair HL are decremented.
     *
     * S is unknown.
     * Z is set if B – 1 = 0; otherwise, it is reset.
     * H is unknown.
     * P/V is unknown.
     * N is set.
     * C is not affected.
     * </pre>
     */
    @Override
    protected int IND() {
        // -----------------------------------------------
        // | prefix (ED) | 1 | 1 | 1 | 0 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (AA) | 1 | 0 | 1 | 0 | 1 | 0 | 1 | 0 |
        // -----------------------------------------------
        // | M-cycle | 4  |
        // | T-cycle | 16 |
        // ----------------
        int addr = BC();
        int data = ioReadByte(addr);
        writeByte(HL(), data);

        B(B - 1);
        HL(HL() - 1);

        FZ(B() == 0); FN(true);
        return 16;
    }



    /**
     * INDR
     *
     * <pre>
     * (HL) <- (C), B <- B - 1, HL <- HL - 1
     *
     * The contents of Register C are placed on the bottom half (A0 through A7) of the address
     * bus to select the I/O device at one of 256 possible ports. Register B is used as a byte
     * counter, and its contents are placed on the top half (A8 through A15) of the address bus
     * at this time. Then one byte from the selected port is placed on the data bus and written
     * to the CPU. The contents of the HL register pair are placed on the address bus and the
     * input byte is written to the corresponding location of memory. Then HL and the byte
     * counter are decremented.
     * If decrementing causes B to go to 0, the instruction is terminated.
     * If B is not 0, the Program Counter is decremented by two and the instruction repeated.
     * Interrupts are recognized and two refresh cycles are executed after each data transfer.
     * When B is set to 0 prior to instruction execution, 256 bytes of data are input.
     *
     * S is unknown.
     * Z is set.
     * H is unknown.
     * P/V is unknown.
     * N is set.
     * C is not affected.
     * </pre>
     */
    @Override
    protected int INDR() {
        // -----------------------------------------------
        // | prefix (ED) | 1 | 1 | 1 | 0 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (BA) | 1 | 0 | 1 | 1 | 1 | 0 | 1 | 0 |
        // -----------------------------------------------
        // | M-cycle | 5   |   B != 0   |
        // | T-cycle | 21  |            |
        // ------------------------------
        // | M-cycle | 4   |   B == 0   |
        // | T-cycle | 16  |            |
        // ------------------------------
        int addr = BC();
        int data = ioReadByte(addr);
        writeByte(HL(), data);

        B(B - 1);
        HL(HL() - 1);

        FZ(true); FN(true);

        if (B() != 0) { PC(PC - 2); return 21; } // repeat instruction
        return 16;
    }



    /**
     * OUT (n), A
     *
     * <pre>
     * The operand n is placed on the bottom half (A0 through A7) of the address bus to select the
     * I/O device at one of 256 possible ports. The contents of the Accumulator (Register A) also
     * appear on the top half (A8 through A15) of the address bus at this time. Then the byte
     * contained in the Accumulator is placed on the data bus and written to the selected
     * peripheral device.
     * </pre>
     */
    @Override
    protected int OUT_Na_A() {
        // .---------------------------------------------.
        // | opcode (D3) | 1 | 1 | 0 | 1 | 0 | 0 | 1 | 1 |
        // '---------------------------------------------'
        // |             | n | n | n | n | n | n | n | n |
        // '---------------------------------------------'
        // | M-cycle | 3  |
        // | T-cycle | 11 |
        // '--------------'
        int lo = readByte(PC); PC(PC + 1);
        int hi = A;

        int address = (hi << 8) | lo;
        ioWriteByte(address, A);

        return 11;
    }



    /**
     * OTIR
     *
     * <pre>
     * (C) <- (HL), B <- B - 1, HL <- HL + 1
     *
     * The contents of the HL register pair are placed on the address bus to select a location
     * in memory. The byte contained in this memory location is temporarily stored in the CPU.
     * Then, after the byte counter (B) is decremented, the contents of Register C are placed on
     * the bottom half (A0 through A7) of the address bus to select the I/O device at one of 256
     * possible ports. Register B can be used as a byte counter, and its decremented value is
     * placed on the top half (A8 through A15) of the address bus at this time. Next, the byte
     * to be output is placed on the data bus and written to the selected peripheral device.
     * Then register pair HL is incremented. If the decremented B Register is not 0, the
     * Program Counter (PC) is decremented by two and the instruction is repeated. If B has gone
     * to 0, the instruction is terminated. Interrupts are recognized and two refresh cycles are
     * executed after each data transfer.
     *
     * S is unknown.
     * Z is set.
     * H is unknown.
     * P/V is unknown.
     * N is set.
     * C is not affected.
     * </pre>
     */
    @Override
    protected int OTIR() {
        // -----------------------------------------------
        // | prefix (ED) | 1 | 1 | 1 | 0 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (B3) | 1 | 0 | 1 | 1 | 0 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | M-cycle | 5   |   B != 0   |
        // | T-cycle | 21  |            |
        // ------------------------------
        // | M-cycle | 4   |   B == 0   |
        // | T-cycle | 16  |            |
        // ------------------------------
        int data = readByte(HL());
        B(B - 1);
        int addr = BC();
        ioWriteByte(addr, data);

        HL(HL() + 1);

        FZ(true); FN(true);

        if (B() != 0) { PC(PC - 2); return 21; } // repeat instruction
        return 16;
    }



    /**
     * OR IXL
     */
    @Override
    protected int OR_IXL() {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (B5) | 1 | 0 | 1 | 1 | 0 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int a = A;
        int b = IX & 0xff;
        int r = (a | b) & 0xff;
        A(r);

        FS(r > 0x7f); FZ(r == 0);
        FH(false); FP(isParityEven(r)); FN(false); FC(false);
        return 8;
    }



    /**
     * OR IYH
     */
    @Override
    protected int OR_IYH() {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (B4) | 1 | 0 | 1 | 1 | 0 | 1 | 0 | 0 |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int a = A;
        int b = (IY >> 8) & 0xff;
        int r = (a | b) & 0xff;
        A(r);

        FS(r > 0x7f); FZ(r == 0);
        FH(false); FP(isParityEven(r)); FN(false); FC(false);
        return 8;
    }



    /**
     * OR IYL
     */
    @Override
    protected int OR_IYL() {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (B5) | 1 | 0 | 1 | 1 | 0 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int a = A;
        int b = IY & 0xff;
        int r = (a | b) & 0xff;
        A(r);

        FS(r > 0x7f); FZ(r == 0);
        FH(false); FP(isParityEven(r)); FN(false); FC(false);
        return 8;
    }



    /**
     * XOR IXH
     */
    @Override
    protected int XOR_IXH() {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (AC) | 1 | 0 | 1 | 0 | 1 | 1 | 0 | 0 |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int a = A;
        int b = (IX >> 8) & 0xff;
        int r = (a ^ b) & 0xff;
        A(r);

        FS(r > 0x7f); FZ(r == 0);
        FH(false); FP(isParityEven(r)); FN(false); FC(false);
        return 8;
    }



    /**
     * XOR IXL
     */
    @Override
    protected int XOR_IXL() {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (AD) | 1 | 0 | 1 | 0 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int a = A;
        int b = IX & 0xff;
        int r = (a ^ b) & 0xff;
        A(r);

        FS(r > 0x7f); FZ(r == 0);
        FH(false); FP(isParityEven(r)); FN(false); FC(false);
        return 8;
    }



    /**
     * XOR IYH
     */
    @Override
    protected int XOR_IYH() {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (AC) | 1 | 0 | 1 | 0 | 1 | 1 | 0 | 0 |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int a = A;
        int b = (IY >> 8) & 0xff;
        int r = (a ^ b) & 0xff;
        A(r);

        FS(r > 0x7f); FZ(r == 0);
        FH(false); FP(isParityEven(r)); FN(false); FC(false);
        return 8;
    }



    /**
     * XOR IYL
     */
    @Override
    protected int XOR_IYL() {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (AD) | 1 | 0 | 1 | 0 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int a = A;
        int b = IY & 0xff;
        int r = (a ^ b) & 0xff;
        A(r);

        FS(r > 0x7f); FZ(r == 0);
        FH(false); FP(isParityEven(r)); FN(false); FC(false);
        return 8;
    }



    /**
     * CP IXH
     */
    @Override
    protected int CP_IXH() {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (BC) | 1 | 0 | 1 | 1 | 1 | 1 | 0 | 0 |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int a = A;
        int b = (IX >> 8) & 0xff;
        int r = (a - b) & 0xff;

        FS(r > 0x7f); FZ(r == 0);
        FH(isBorrowBit4(a, b, r)); FP(isOverflowSub(a, b, r)); FN(true); FC(isBorrowBit8(a, b, r));
        return 8;
    }



    /**
     * LD IXH,IXL
     */
    @Override
    protected int LD_IXH_IXL() {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (65) | 0 | 1 | 1 | 0 | 0 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int ixl = IX & 0xff;
        IX((IX & 0x00ff) | (ixl << 8));

        return 8;
    }



    /**
     * LD IXL,IXH
     */
    @Override
    protected int LD_IXL_IXH() {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (6C) | 0 | 1 | 1 | 0 | 1 | 1 | 0 | o |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int ixh = (IX >> 8) & 0xff;
        IX((IX & 0xff00) | ixh);

        return 8;
    }



    /**
     * LD IYH,IYL
     */
    @Override
    protected int LD_IYH_IYL() {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 1 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (65) | 0 | 1 | 1 | 0 | 0 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int iyl = IY & 0xff;
        IY((IY & 0x00ff) | (iyl << 8));

        return 8;
    }



    /**
     * LD IYL,IYH
     */
    @Override
    protected int LD_IYL_IYH() {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 1 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (6C) | 0 | 1 | 1 | 0 | 1 | 1 | 0 | o |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int iyh = (IY >> 8) & 0xff;
        IY((IY & 0xff00) | iyh);

        return 8;
    }



    /**
     * LD IXH,n
     */
    @Override
    protected int LD_IXH_N() {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (26) | 0 | 0 | 1 | 0 | 0 | 1 | 1 | 0 |
        // -----------------------------------------------
        // |             | n | n | n | n | n | n | n | n |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int n = readByte(PC);
        PC(PC + 1);

        IX((IX & 0x00ff) | (n << 8));
        return 11;
    }



    /**
     * LD IXL,n
     */
    @Override
    protected int LD_IXL_N() {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (2E) | 0 | 0 | 1 | 0 | 0 | 1 | 1 | 0 |
        // -----------------------------------------------
        // |             | n | n | n | n | n | n | n | n |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int n = readByte(PC);
        PC(PC + 1);

        IX((IX & 0xff00) | n);
        return 11;
    }



    /**
     * LD IYH,n
     */
    @Override
    protected int LD_IYH_N() {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 1 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (26) | 0 | 0 | 1 | 0 | 0 | 1 | 1 | 0 |
        // -----------------------------------------------
        // |             | n | n | n | n | n | n | n | n |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int n = readByte(PC);
        PC(PC + 1);

        IY((IY & 0x00ff) | (n << 8));
        return 11;
    }



    /**
     * LD IYL,n
     */
    @Override
    protected int LD_IYL_N() {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 1 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | opcode (2E) | 0 | 0 | 1 | 0 | 0 | 1 | 1 | 0 |
        // -----------------------------------------------
        // |             | n | n | n | n | n | n | n | n |
        // -----------------------------------------------
        // | M-cycle | ?  |
        // | T-cycle | ?  |
        // ----------------
        int n = readByte(PC);
        PC(PC + 1);

        IY((IY & 0xff00) | n);
        return 11;
    }



    /**
     * LD R,RR (IX+d)* / RR (IX+d),R*
     *
     * DDCB d 18
     * DDCB d 19
     * DDCB d 1A
     * DDCB d 1B
     * DDCB d 1C
     * DDCB d 1D
     * DDCB d 1F
     *
     * Opcode: 00011rrr
     */
    @Override
    protected int RR_IXd_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 0 | 1 | 1 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 6  |
        // | T-cycle | 23 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];
        int a = readByte(IX + d);

        int b0 = a & 0x1;
        int cf = FC();

        a = ((a >>> 1) | (cf << 7)) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b0 == 1);

        writeByte(IX + d, a);

        return 23;
    }



    /**
     * LD R,RL (IX+d)* / RL (IX+d),R*
     *
     * DDCB d 10
     * DDCB d 11
     * DDCB d 12
     * DDCB d 13
     * DDCB d 14
     * DDCB d 15
     * DDCB d 17
     *
     * Opcode: 00010rrr
     */
    @Override
    protected int RL_IXd_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 0 | 1 | 0 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 6  |
        // | T-cycle | 23 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];
        int a = readByte(IX + d);

        int b7 = (a >>> 7) & 0x1;
        int cf = FC();

        a = ((a << 1) | cf) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b7 == 1);

        writeByte(IX + d, a);

        return 23;
    }



    /**
     * LD R,RRC (IX+d)* / RRC (IX+d),R*
     *
     * DDCB d 08
     * DDCB d 09
     * DDCB d 0A
     * DDCB d 0B
     * DDCB d 0C
     * DDCB d 0D
     * DDCB d 0F
     *
     * Opcode: 00001rrr
     */
    @Override
    protected int RRC_IXd_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 0 | 0 | 1 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 6  |
        // | T-cycle | 23 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];
        int a = readByte(IX + d);
        int b0 = a & 0x1;

        a = rotateRight(a) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b0 == 1);

        writeByte(IX + d, a);

        return 23;
    }



    /**
     * LD R,RR (IY+d)* / RR (IY+d),R*
     *
     * FDCB d 18
     * FDCB d 19
     * FDCB d 1A
     * FDCB d 1B
     * FDCB d 1C
     * FDCB d 1D
     * FDCB d 1F
     *
     * Opcode: 00011rrr
     */
    @Override
    protected int RR_IYd_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 1 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 0 | 1 | 1 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 6  |
        // | T-cycle | 23 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];
        int a = readByte(IY + d);

        int b0 = a & 0x1;
        int cf = FC();

        a = ((a >>> 1) | (cf << 7)) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b0 == 1);

        writeByte(IY + d, a);

        return 23;
    }



    /**
     * LD R,RL (IY+d)* / RL (IY+d),R*
     *
     * FDCB d 10
     * FDCB d 11
     * FDCB d 12
     * FDCB d 13
     * FDCB d 14
     * FDCB d 15
     * FDCB d 17
     *
     * Opcode: 00010rrr
     */
    @Override
    protected int RL_IYd_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 1 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 0 | 1 | 0 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 6  |
        // | T-cycle | 23 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];
        int a = readByte(IY + d);

        int b7 = (a >>> 7) & 0x1;
        int cf = FC();

        a = ((a << 1) | cf) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b7 == 1);

        writeByte(IY + d, a);

        return 23;
    }



    /**
     * LD R,RRC (IY+d)* / RRC (IY+d),R*
     *
     * FDCB d 08
     * FDCB d 09
     * FDCB d 0A
     * FDCB d 0B
     * FDCB d 0C
     * FDCB d 0D
     * FDCB d 0F
     *
     * Opcode: 00001rrr
     */
    @Override
    protected int RRC_IYd_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 1 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 0 | 0 | 1 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 6  |
        // | T-cycle | 23 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];
        int a = readByte(IY + d);
        int b0 = a & 0x1;

        a = rotateRight(a) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b0 == 1);

        writeByte(IY + d, a);

        return 23;
    }



    /**
     * LD R,RLC (IX+d)* / RLC (IX+d),R*
     *
     * DDCB d 00
     * DDCB d 01
     * DDCB d 02
     * DDCB d 03
     * DDCB d 04
     * DDCB d 05
     * DDCB d 07
     *
     * Opcode: 00000rrr
     */
    @Override
    protected int RLC_IXd_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 0 | 0 | 0 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 6  |
        // | T-cycle | 23 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];
        int a = readByte(IX + d);
        int b7 = (a >>> 7) & 0x1;

        a = rotateLeft(a) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b7 == 1);

        writeByte(IX + d, a);

        return 23;
    }



    /**
     * LD R,SLA (IX+d)* / SLA (IX+d),R*
     *
     * DDCB d 20
     * DDCB d 21
     * DDCB d 22
     * DDCB d 23
     * DDCB d 24
     * DDCB d 25
     * DDCB d 27
     *
     * Opcode: 00100rrr
     */
    @Override
    protected int SLA_IXd_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 1 | 0 | 0 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 6  |
        // | T-cycle | 23 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];
        int a = readByte(IX + d);
        int b7 = (a >>> 7) & 0x1;

        a = (a << 1) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b7 == 1);

        writeByte(IX + d, a);

        return 23;
    }



    /**
     * LD R,SRA (IX+d)* / SRA (IX+d),R*
     *
     * DDCB d 28
     * DDCB d 29
     * DDCB d 2A
     * DDCB d 2B
     * DDCB d 2C
     * DDCB d 2D
     * DDCB d 2F
     *
     * Opcode: 00101rrr
     */
    @Override
    protected int SRA_IXd_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 1 | 0 | 1 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 6  |
        // | T-cycle | 23 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];
        int a = readByte(IX + d);

        int b0 = a & 0x1;
        int b7 = (a >>> 7) & 0x1;

        a = ((a >>> 1) | (b7 << 7)) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b0 == 1);

        writeByte(IX + d, a);

        return 23;
    }



    /**
     * LD R,SLL (IX+d)* / SLL (IX+d),R*
     *
     * DDCB d 30
     * DDCB d 31
     * DDCB d 32
     * DDCB d 33
     * DDCB d 34
     * DDCB d 35
     * DDCB d 37
     *
     * Opcode: 00110rrr
     */
    @Override
    protected int SLL_IXd_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 1 | 1 | 0 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 6  |
        // | T-cycle | 23 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];
        int a = readByte(IX + d);
        int b7 = (a >>> 7) & 0x1;

        a = ((a << 1) | 0x1) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b7 == 1);

        writeByte(IX + d, a);

        return 23;
    }



    /**
     * LD R,SRL (IX+d)* / SRL (IX+d),R*
     *
     * DDCB d 38
     * DDCB d 39
     * DDCB d 3A
     * DDCB d 3B
     * DDCB d 3C
     * DDCB d 3D
     * DDCB d 3F
     *
     * Opcode: 00111rrr
     */
    @Override
    protected int SRL_IXd_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (DD) | 1 | 1 | 0 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | 0 | 0 | 1 | 1 | 1 | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 6  |
        // | T-cycle | 23 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];
        int a = readByte(IX + d);
        int b0 = a & 0x1;

        a = (a >>> 1) & 0xff;
        reg.setValue(a);

        FS(a > 0x7f); FZ(a == 0);
        FH(false); FP(isParityEven(a)); FN(false); FC(b0 == 1);

        writeByte(IX + d, a);

        return 23;
    }



    /**
     * RES B,(IY+d),R*
     */
    @Override
    protected int RES_B_IYd_R(final int opcode) {
        // -----------------------------------------------
        // | prefix (FD) | 1 | 1 | 1 | 1 | 1 | 1 | 0 | 1 |
        // -----------------------------------------------
        // | prefix (CB) | 1 | 1 | 0 | 0 | 1 | 0 | 1 | 1 |
        // -----------------------------------------------
        // | d           | d | d | d | d | d | d | d | d |
        // -----------------------------------------------
        // | opcode      | x | x | b | b | b | r | r | r |
        // -----------------------------------------------
        // | M-cycle | 6  |
        // | T-cycle | 23 |
        // ----------------
        byte d = (byte) readByte(PC);
        PC(PC + 2);

        int src = opcode & 0x07;
        RegisterLookup reg = registerLookup[src];

        int bit = (opcode >> 3) & 0x07; // the position of the bit to reset
        int a = readByte(IY + d);

        a = a & ~(0x1 << bit);

        reg.setValue(a);
        writeByte(IY + d, a);

        return 23;
    }
}
