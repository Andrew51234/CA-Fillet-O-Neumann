public class Architecture {

    private static int [] mainMem = new int [2048]; // Data_location = Location + 1024
    private static int [] registers  = new int[33];

    public Architecture(){

         registers[0] = 0;  //Zero register
         registers[32] = 0; //PC register

    }

    public void writeRegister (String register, int value) throws ArchitectureExceptions {

        if(register.equals("R0"))
            throw new ArchitectureExceptions("The zero register cannot be overwritten");

        switch(register){
            case "R1" :  registers[1] = value; break;
            case "R2" :  registers[2] = value; break;
            case "R3" :  registers[3] = value; break;
            case "R4" :  registers[4] = value; break;
            case "R5" :  registers[5] = value; break;
            case "R6" :  registers[6] = value; break;
            case "R7" :  registers[7] = value; break;
            case "R8" :  registers[8] = value; break;
            case "R9" :  registers[9] = value; break;
            case "R10" : registers[10] = value; break;
            case "R11" : registers[11] = value; break;
            case "R12" : registers[12] = value; break;
            case "R13" : registers[13] = value; break;
            case "R14" : registers[14] = value; break;
            case "R15" : registers[15] = value; break;
            case "R16" : registers[16] = value; break;
            case "R17" : registers[17] = value; break;
            case "R18" : registers[18] = value; break;
            case "R19" : registers[19] = value; break;
            case "R20" : registers[20] = value; break;
            case "R21" : registers[21] = value; break;
            case "R22" : registers[22] = value; break;
            case "R23" : registers[23] = value; break;
            case "R24" : registers[24] = value; break;
            case "R25" : registers[25] = value; break;
            case "R26" : registers[26] = value; break;
            case "R27" : registers[27] = value; break;
            case "R28" : registers[28] = value; break;
            case "R29" : registers[29] = value; break;
            case "R30" : registers[30] = value; break;
            case "R31" : registers[31] = value; break;
            case "PC"  : registers[32] = value; break;
            default:
        }

    }

    public int readRegister (String register) throws ArchitectureExceptions {

        switch(register){
            case "R0" :  return registers[0] ;
            case "R1" :  return registers[1] ;
            case "R2" :  return registers[2] ;
            case "R3" :  return registers[3] ;
            case "R4" :  return registers[4] ;
            case "R5" :  return registers[5] ;
            case "R6" :  return registers[6] ;
            case "R7" :  return registers[7] ;
            case "R8" :  return registers[8] ;
            case "R9" :  return registers[9] ;
            case "R10" : return registers[10] ;
            case "R11" : return registers[11] ;
            case "R12" : return registers[12] ;
            case "R13" : return registers[13] ;
            case "R14" : return registers[14] ;
            case "R15" : return registers[15] ;
            case "R16" : return registers[16] ;
            case "R17" : return registers[17] ;
            case "R18" : return registers[18] ;
            case "R19" : return registers[19] ;
            case "R20" : return registers[20] ;
            case "R21" : return registers[21] ;
            case "R22" : return registers[22] ;
            case "R23" : return registers[23] ;
            case "R24" : return registers[24] ;
            case "R25" : return registers[25] ;
            case "R26" : return registers[26] ;
            case "R27" : return registers[27] ;
            case "R28" : return registers[28] ;
            case "R29" : return registers[29] ;
            case "R30" : return registers[30] ;
            case "R31" : return registers[31] ;
            case "PC"  : return registers[32] ;
            default:
        }
        return 0;

    }

    public void writeMem (int word, int value, boolean instruction) throws ArchitectureExceptions {

        if (instruction){
            if (word<=1023 && word>=0)
                mainMem[word] = value;
            else
                throw new ArchitectureExceptions("Invalid instruction location (Accessed data memory)");
        }

        else {
            if (word > 2048)
                throw new ArchitectureExceptions("Out of memory bounds");
            else
                mainMem[word+1024] = value;
        }
    }

    public int readMem (int word, boolean instruction) throws ArchitectureExceptions {
        if (instruction){
            if (word<=1023 && word>=0)
                return mainMem[word];
            else
                throw new ArchitectureExceptions("Invalid instruction location (Accessed data memory)");
        }

        else {
            if (word > 2048)
                throw new ArchitectureExceptions("Out of memory bounds");
            else
                return mainMem[word+1024];
        }
    }

    public void fetch() throws ArchitectureExceptions {

        int pc = readRegister("PC");
        int instruction = 0;

        if(pc<1023) {
            instruction = readMem(pc, true);
        }
        else {
            writeRegister("PC",0);
        }

        decode(instruction);
        writeRegister("PC", pc+1);

    }

    public void decode(int instruction) throws ArchitectureExceptions {

        int opcode = 0;      // bits31:28
        int r1 = 0;          // bits27:23
        int r2 = 0;          // bits22:18
        int r3 = 0;          // bits17:13
        int shamt = 0;       // bits12:0
        int immediate = 0;   // bits17:0
        int address = 0;     // bits27:0

        int valueR1 = 0;
        int valueR2 = 0;
        int valueR3 = 0;

        //bit-masking

        opcode    = (instruction & 0b11110000000000000000000000000000) >> 28;
        r1        = (instruction & 0b00001111100000000000000000000000) >> 23;
        r2        = (instruction & 0b00000000011111000000000000000000) >> 18;
        r3        = (instruction & 0b00000000000000111110000000000000) >> 13;
        shamt     = (instruction & 0b00000000000000000001111111111111);
        immediate = (instruction & 0b00000000000000111111111111111111);
        address   = (instruction & 0b00001111111111111111111111111111);

        if (opcode == 0 || opcode == 1 || opcode == 8 || opcode == 9){
            execR(opcode, r1, r2, r3, shamt);
        }

        else if (opcode == 2 || opcode == 3 || opcode == 4 || opcode == 5 || opcode == 6 || opcode == 10 || opcode == 11){
            execI(opcode, r1, r2, immediate);
        }
        else if (opcode == 7){
            execJ(address);
        }
    }


    public void execR(int opcode, int r1, int r2, int r3, int shamt) throws ArchitectureExceptions {

        String r1Pos = "R"+r1;
        int r2Value = readRegister("R"+r2);
        int r3Value = readRegister("R"+r3);

        if (opcode == 0){  //ADD
            writeRegister(r1Pos, r2Value + r3Value);
        }

        if (opcode == 1){  //SUB
            writeRegister(r1Pos, r2Value - r3Value);
        }

        if (opcode == 8){  //SLL
            writeRegister(r1Pos, r2Value << shamt);
        }

        if (opcode == 9){  //SRL
            writeRegister(r1Pos, r2Value >> shamt);
        }
    }

    public void execI(int opcode, int r1, int r2, int immediate) throws ArchitectureExceptions {

        String r1Pos = "R"+r1;
        int r1Value = readRegister("R"+r1);
        int r2Value = readRegister("R"+r2);

        if (opcode == 2){  //MULI
            writeRegister(r1Pos, r2Value * immediate);
        }

        if (opcode == 3){  //ADDI
            writeRegister(r1Pos, r2Value + immediate);
        }

        if (opcode == 4){  //BNE
            if(r1Value != r2Value){
                writeRegister("PC", readRegister("PC") + 1 + immediate );
            }
        }

        if (opcode == 5){  //ANDI
            writeRegister(r1Pos, r2Value & immediate);
        }

        if (opcode == 6){  //ORI
            writeRegister(r1Pos, r2Value | immediate);
        }

        if (opcode == 10){  //LW
            writeRegister(r1Pos, readMem(r2Value + immediate, false));
        }

        if (opcode == 11){  //SW
            writeMem(r2Value + immediate, r1Value, false);
        }

    }

    public void execJ(int address) throws ArchitectureExceptions {  //J

        int newPC  = (readRegister("PC") & 0b01111000000000000000000000000000) >> 27;
        String newPCStr = Integer.toBinaryString(newPC);
        String addressStr = Integer.toBinaryString(address);
        int value = Integer.parseInt((newPCStr + addressStr),2);
        writeRegister("PC",value);

    }

    public static void main(String[] args) {

    }

}