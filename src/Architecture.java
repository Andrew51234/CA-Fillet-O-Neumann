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

        if(register.equals("PC")){
            registers[32] = value;
        }
        else if(register.charAt(0)=='R' && Integer.parseInt(register.substring(1))>0 && Integer.parseInt(register.substring(1))<32){
            registers[Integer.parseInt(register.substring(1))] = value;
        }
    }

    public int readRegister (String register) throws ArchitectureExceptions {
        if(register.equals("PC")){
            return registers[32];
        }
        else if(register.charAt(0)=='R' && Integer.parseInt(register.substring(1))>=0 && Integer.parseInt(register.substring(1))<32){
            return registers[Integer.parseInt(register.substring(1))];
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
            if (word >= 1024 || word<0)
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
            if (word >= 1024 || word<0)
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

        int newPC  = (readRegister("PC") & 0b11110000000000000000000000000000) >> 28;
        String newPCStr = Integer.toBinaryString(newPC);
        String addressStr = Integer.toBinaryString(address);
        int value = Integer.parseInt((newPCStr + addressStr),2);
        writeRegister("PC",value);

    }

    public static void main(String[] args) {

    }

}