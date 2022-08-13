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



abstract class InstructionSet {

    /**
     * Keep in mind that the 0xDDCB and 0xFDCB prefixes are remapped to
     * 0xAA and 0xBB respectively. There are only 1268 instructions and
     * the rest of the array is filled with holes. These holes are used
     * to detect invalid opcodes.
     */
    private final Instruction[] instructions = new Instruction[0xfdff];

    {
        instructions[0x0000] = (op) -> NOP();                 //  NOP
        instructions[0x0001] = (op) -> LD_BC_NN();            //  LD BC,nn
        instructions[0x0002] = (op) -> LD_BCa_A();            //  LD (BC),A
        instructions[0x0003] = (op) -> INC_BC();              //  INC BC
        instructions[0x0004] = (op) -> INC_R(op);             //  INC R
        instructions[0x0005] = (op) -> DEC_R(op);             //  DEC R
        instructions[0x0006] = (op) -> LD_R_n(op);            //  LD B,n
        instructions[0x0007] = (op) -> RLCA();                //  RLCA
        instructions[0x0008] = (op) -> EX_AF_AF_();           //  EX AF,AF'
        instructions[0x0009] = (op) -> ADD_HL_SS(op);         //  ADD HL,ss
        instructions[0x000A] = (op) -> LD_A_BCa();            //  LD A,(BC)
        instructions[0xFDA6] = (op) -> AND_IYd();             //  AND (IY+d)
        instructions[0xFDAC] = (op) -> XOR_IYH();             //  XOR IYH*
        instructions[0xFDAD] = (op) -> XOR_IYL();             //  XOR IYL*
        instructions[0xFDAE] = (op) -> XOR_IYd();             //  XOR (IY+d)
        instructions[0xFDB4] = (op) -> OR_IYH();              //  OR IYH*
        instructions[0xFDB5] = (op) -> OR_IYL();              //  OR IYL*
        instructions[0xFDB6] = (op) -> OR_IYd();              //  OR (IY+d)
        instructions[0xFDBC] = (op) -> CP_IYH();              //  CP IYH*
        instructions[0xFDBD] = (op) -> CP_IYL();              //  CP IYL*
        instructions[0xFDBE] = (op) -> CP_IYd();              //  CP (IY+d)
        instructions[0xFDE1] = (op) -> POP_IY();              //  POP IY
        instructions[0xFDE3] = (op) -> EX_SPa_IY();           //  EX (SP),IY
        instructions[0xFDE5] = (op) -> PUSH_IY();             //  PUSH IY
        instructions[0xFDE9] = (op) -> JP_IYa();              //  JP (IY)
        instructions[0xFDF9] = (op) -> LD_SP_IY();            //  LD SP,IY
    }

    protected abstract int JR_E();
    protected abstract int LD_A_R();
    protected abstract int SBC_A_R(int op);
    protected abstract int DEC_IYa();
    protected abstract int SLA_HLa();
    protected abstract int BIT_B_IYd(int op);
    protected abstract int DEC_R(int op);
    protected abstract int POP_AF();
    protected abstract int LD_IYL_R(int op);
    protected abstract int POP_DE();
    protected abstract int LD_IX_NNa();
    protected abstract int DEC_IY();
    protected abstract int LD_IYd_R(int op);
    protected abstract int LD_BC_NN();
    protected abstract int RETN();
    protected abstract int RR_HLa();
    protected abstract int LD_A_I();
    protected abstract int ADD_IY_RR(int op);
    protected abstract int SUB_IXL();
    protected abstract int LD_R_IYd(int op);
    protected abstract int RRC_IYd();
    protected abstract int ADC_A_R(int op);
    protected abstract int RR_IXd();
    protected abstract int INC_BC();
    protected abstract int AND_HLa();
    protected abstract int SLA_R(int op);
    protected abstract int LD_DEa_A();
    protected abstract int SLL_IXd_R(int opcode);
    protected abstract int SRL_IXd_R(int opcode);
    protected abstract int RLC_IYd_R(int opcode);
    protected abstract int SLA_IYd_R(int opcode);
    protected abstract int SRA_IYd_R(int opcode);
    protected abstract int SLL_IYd_R(int opcode);
    protected abstract int SRL_IYd_R(int opcode);
    protected abstract int SET_B_IXd_R(int opcode);
    protected abstract int SET_B_IYd_R(int opcode);
    protected abstract int RES_B_IXd_R(int opcode);
    protected abstract int RES_B_IYd_R(int opcode);



    /**
     * Get an instruction based on the provided opcode.
     * Will return null if an instruction with the given opcode
     * is not found.
     *
     * @param opcode the instruction opcode
     * @return the instruction corresponding to the specified opcode.
     */
    protected Instruction getInstruction(int opcode) {
        return instructions[opcode];
    }



    protected interface Instruction {
        int exec(int op);
    }
}
