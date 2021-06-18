import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class Architecture {
    private static int [] mainMem = new int [2048]; // Data_location = Location + 1024
    private static int [] registers  = new int[33];
    private static int clk;
    private static int numOfIns;
    private static boolean hasIF;
    private static boolean hasID;
    private static boolean hasEXR;
    private static boolean hasEXI;
    private static boolean hasEXJ;
    private static boolean hasMEMR;
    private static boolean hasMEMW;
    private static boolean hasFakeMEM;
    private static boolean hasWB;
    private static boolean hasFakeWB;
    private static boolean decodeSecondClk;
    private static boolean executeSecondClk;
    private static boolean jumped;
    private static int nextDecode;
    private static int nextOpcode;
    private static int nextR1;
    private static int nextR2;
    private static int nextR3;
    private static int nextShamt;
    private static int nextImmediate;
    private static int nextAddress;
    private static String fakeMemReg;
    private static int fakeMemValue;
    private static String wRegReg;
    private static int wRegValue;
    private static int realWord;
    private static int realValue;
    private static String completeRunReg;



    public Architecture(){

        registers[0] = 0;  //Zero register
        registers[32] = 0; //PC register
        clk = 1;
        hasIF = true;
        hasID = false;
        hasEXR = false;
        hasEXI = false;
        hasEXJ = false;
        hasMEMR = false;
        hasMEMW = false;
        hasFakeMEM = false;
        hasWB = false;
        jumped = false;
        //add all the instructions to their appropriate locations in the memory and adjust the numOfIns value
        numOfIns = 3;
    }

    public static int convSigned(String binary){
        String newBin = "";
        if(binary.charAt(0) == '0'){
            return Integer.parseInt(binary,2);
        }
        else{
            newBin += "-0";
            for(int i=1; i<binary.length(); i++){
                if(binary.charAt(i) == '0')
                    newBin += "1";
                else
                    newBin += "0";
            }
            return (Integer.valueOf(newBin,2))-1;
        }
    }

    public static void parse(String location, int offset) throws IOException {
        FileReader fr = new FileReader(location);
        BufferedReader br = new BufferedReader(fr);
        String current;
        String type;
        String binValue;
        Vector<Integer> instructions = new Vector<>();
        while((current = br.readLine())!= null){
            binValue = "";
            type = "";
            String[] instruction = current.split(" ");
            for(int j = 0; j<instruction.length; j++){
                if(instruction[j].equals("ADD")){
                    binValue += "0000";
                    type = "R";
                    continue;
                }
                if(instruction[j].equals("SUB")){
                    binValue += "0001";
                    type = "R";
                    continue;
                }
                if(instruction[j].equals("MULI")){
                    binValue += "0010";
                    type = "I";
                    continue;
                }
                if(instruction[j].equals("ADDI")){
                    binValue += "0011";
                    type = "I";
                    continue;
                }
                if(instruction[j].equals("BNE")){
                    binValue += "0100";
                    type = "I";
                    continue;
                }
                if(instruction[j].equals("ANDI")){
                    binValue += "0101";
                    type = "I";
                    continue;
                }
                if(instruction[j].equals("ORI")){
                    binValue += "0110";
                    type = "I";
                    continue;

                }
                if(instruction[j].equals("J")){
                    binValue += "0111";
                    type = "J";
                    continue;
                }
                if(instruction[j].equals("SLL")){
                    binValue += "1000";
                    type = "R";
                    continue;
                }
                if(instruction[j].equals("SRL")){
                    binValue += "1001";
                    type = "R";
                    continue;
                }
                if(instruction[j].equals("LW")){
                    binValue += "1010";
                    type = "I";
                    continue;
                }
                if(instruction[j].equals("SW")){
                    binValue += "1011";
                    type = "I";
                    continue;
                }
                if(type.equals("R")) {
                    if (instruction[j].charAt(0) == 'R') {
                        String reg = instruction[j].substring(1);
                        int regNo = Integer.parseInt(reg);
                        String tempBinValue = Integer.toBinaryString(regNo);
                        if (tempBinValue.length() <= 5) {
                            String temp = "";
                            int zeroes = 5 - tempBinValue.length();
                            for (int i = 0; i < zeroes; i++) {
                                temp += '0';
                            }
                            temp += tempBinValue;
                            binValue += temp;
                        }

                    } else { //shamt
                        if(binValue.substring(0,4).equals("1000") || binValue.substring(0,4).equals("1001")){
                            binValue+="00000";
                        }
                        int shamt = Integer.parseInt(instruction[j]);
                        String tempShamt = Integer.toBinaryString(shamt);
                        if (tempShamt.length() <= 13) {
                            String temp = "";
                            int zeroes = 13 - tempShamt.length();
                            for (int i = 0; i < zeroes; i++) {
                                temp += '0';
                            }
                            temp += tempShamt;
                            binValue += temp;
                        }
                    }
                }
                if(type.equals("I")){
                    if(instruction[j].charAt(0) == 'R') {
                        String reg = instruction[j].substring(1);
                        int regNo = Integer.parseInt(reg);
                        String tempBinValue = Integer.toBinaryString(regNo);
                        if (tempBinValue.length() <= 5) {
                            String temp = "";
                            int zeroes = 5 - tempBinValue.length();
                            for (int i = 0; i < zeroes; i++) {
                                temp += '0';
                            }
                            temp += tempBinValue;
                            binValue += temp;
                        }
                    }
                    else { //immediate
                        int imm = Integer.parseInt(instruction[j]);
                        String tempImm = Integer.toBinaryString(imm);
                        if (tempImm.length() <= 18) {
                            String temp = "";
                            int zeroes = 18 - tempImm.length();
                            for (int i = 0; i < zeroes; i++) {
                                temp += '0';
                            }
                            temp += tempImm;
                            binValue += temp;
                        }
                    }

                }

                if(type.equals("J")){
                    int address = Integer.parseInt(instruction[j]);
                    String tempAddress = Integer.toBinaryString(address);
                    if (tempAddress.length() <= 28) {
                        String temp = "";
                        int zeroes = 28 - tempAddress.length();
                        for (int i = 0; i < zeroes; i++) {
                            temp += '0';
                        }
                        temp += tempAddress;
                        binValue += temp;
                    }
                }
            }
            instructions.add(convSigned(binValue));
            System.out.println(binValue);
        }
        for (int instruction : instructions){
            mainMem[offset] = instruction;
            offset++;
        }
    }

    public static void writeRegister(String register, int value) throws ArchitectureExceptions {
        if(register.equals("R0")) {
            System.out.println("The zero register cannot be overwritten");
            return;
        }

        if(register.equals("PC")){
            registers[32] = value;
        }
        else if(register.charAt(0)=='R' && Integer.parseInt(register.substring(1))>0 && Integer.parseInt(register.substring(1))<32){
            registers[Integer.parseInt(register.substring(1))] = value;
            System.out.println("write back");

        }
    }

    public static int readRegister(String register) throws ArchitectureExceptions {
        if(register.equals("PC")){
            return registers[32];
        }
        else if(register.charAt(0)=='R' && Integer.parseInt(register.substring(1))>=0 && Integer.parseInt(register.substring(1))<32){
            return registers[Integer.parseInt(register.substring(1))];
        }
        return 0;
    }

    public static void writeMem(int word, int value, boolean instruction) throws ArchitectureExceptions {

        if (instruction){
            if (word<=1023 && word>=0)
                mainMem[word] = value;
            else{
                System.out.println("Invalid instruction location (Accessed data memory)");
                return;
            }
        }

        else {
            if (word >= 1024 || word<0){
                System.out.println("Out of memory bounds");
                return;
            }
            else {
                System.out.println("wrote in memory :)");
                mainMem[word + 1024] = value;
            }
        }
    }

    public static int readMem(int word, boolean instruction) throws ArchitectureExceptions {
        if (instruction){
            if (word<=1023 && word>=0)
                return mainMem[word];

            else {
                System.out.println("Invalid instruction location (Accessed data memory)");
                return readRegister("PC");
            }
        }

        else {
            if (word >= 1024 || word<0) {
                System.out.println("Out of memory bounds");
                return readRegister("PC");
            }
            else
                System.out.println("data accessed");
                return mainMem[word+1024];
        }
    }

    public static void fakeMemAccess(String register, int value) throws ArchitectureExceptions {
        wRegReg = register;
        wRegValue = value;
        System.out.println("fake mem");
        hasWB = true;
    }

    public static void realMemWrite(int word, int value, boolean instruction) throws ArchitectureExceptions {
        writeMem(word, value, instruction);
        hasFakeWB = true;

    }

    public static void realMemRead(String reg, int word, boolean instruction) throws ArchitectureExceptions {
        wRegValue = readMem(word, instruction);
        wRegReg = reg;
        hasWB = true;
    }

    public static  void fakeWB(){
        return;
    }

    public static void fetch() throws ArchitectureExceptions {
        int oldClk = clk;
        int pc = readRegister("PC");
        int instruction = 0;

        if(pc<1023) {
            instruction = readMem(pc, true);
        }
        else {
            writeRegister("PC",0);
        }

        System.out.println("fetched: "+instruction);

        writeRegister("PC", pc+1);
        nextDecode = instruction;
        hasID = true;
        decodeSecondClk = false;
    }

    public static void decode(int instruction) throws ArchitectureExceptions {
        if(!decodeSecondClk){
            decodeSecondClk = true;
            System.out.println("Decoding: "+instruction+ " first clk");
            return;
        }
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

        System.out.println("Decoded: "+instruction+ " second clk");
        System.out.println("Opcode:" +opcode);
        if (opcode == 0 || opcode == 1 || opcode == -8  || opcode == -7 ){
            //execR(opcode, r1, r2, r3, shamt);
            nextOpcode = opcode;
            nextR1= r1;
            nextR2= r2;
            nextR3= r3;
            nextShamt= shamt;
            hasEXR = true;
            hasEXI = false;
            hasEXJ = false;
            executeSecondClk = false;
        }

        else if (opcode == 2 || opcode == 3 || opcode == 4 || opcode == 5 || opcode == 6 || opcode == -6 || opcode == -5){
            nextOpcode = opcode;
            nextR1= r1;
            nextR2= r2;
            nextImmediate= immediate;
            hasEXR = false;
            hasEXI = true;
            hasEXJ = false;
            executeSecondClk = false;
        }
        else if (opcode == 7){
            nextAddress = address;
            hasEXR = false;
            hasEXI = false;
            hasEXJ = true;
            executeSecondClk = false;
        }
    }

    public static void execR(int opcode, int r1, int r2, int r3, int shamt) throws ArchitectureExceptions {

        if(!executeSecondClk){
            executeSecondClk = true;
            System.out.println("Executing: "+opcode+ " first clk");

            return;
        }

        String r1Pos = "R"+r1;
        int r2Value = readRegister("R"+r2);
        int r3Value = readRegister("R"+r3);
        System.out.println("Executed: "+opcode+ " second clk");
        if (opcode == 0){  //ADD
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value + r3Value;
            hasFakeMEM = true;


        }

        if (opcode == 1){  //SUB
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value - r3Value;
            hasFakeMEM = true;
        }

        if ( opcode == -8){  //SLL
            System.out.println("shamt: "+ shamt);
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value << shamt;
            hasFakeMEM = true;
        }

        if (opcode == -7){  //SRL
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value >> shamt;
            hasFakeMEM = true;
        }
    }

    public static void execI(int opcode, int r1, int r2, int immediate) throws ArchitectureExceptions {

        if(!executeSecondClk){
            executeSecondClk = true;
            System.out.println("Executing: "+opcode+ " first clk");
            return;
        }

        String r1Pos = "R"+r1;
        int r1Value = readRegister("R"+r1);
        int r2Value = readRegister("R"+r2);
        System.out.println("Executed: "+opcode+ " second clk");
        if (opcode == 2){  //MULI
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value * immediate;
            hasFakeMEM = true;


        }

        if (opcode == 3){  //ADDI
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value + immediate;
            hasFakeMEM = true;
        }

        if (opcode == 4){  //BNE
            if(r1Value != r2Value){
                fakeMemReg = "PC";
                fakeMemValue = readRegister("PC") + immediate;
                hasFakeMEM = true;
                jumped = true;
            }
        }

        if (opcode == 5){  //ANDI
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value & immediate;
            hasFakeMEM = true;
        }

        if (opcode == 6){  //ORI
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value | immediate;
            hasFakeMEM = true;
        }

        if (opcode == -6){  //LW
            completeRunReg = r1Pos;
            realWord = r2Value + immediate;
            hasMEMR = true;
        }

        if (opcode == -5){  //SW
            realWord = r2Value + immediate;
            realValue = r1Value;
            hasMEMW = true;
        }

    }

    public static void execJ(int address) throws ArchitectureExceptions {  //J

        if(!executeSecondClk){
            executeSecondClk = true;
            System.out.println("Executing: jump first clk");
            return;
        }

        int newPC  = (readRegister("PC") & 0b11110000000000000000000000000000) >> 28;
        String newPCStr = Integer.toBinaryString(newPC);
        String addressStr = Integer.toBinaryString(address);
        int value = Integer.parseInt((newPCStr + addressStr),2);

        fakeMemReg = "PC";
        fakeMemValue = value;
        hasFakeMEM = true;
        jumped = true;

        System.out.println("Executed: jump second clk");
    }

    public static void dispatcher() throws ArchitectureExceptions {
        int totalClks = 7 + ((numOfIns-1)*2);
        while(clk<=totalClks){

            System.out.println("clock cycle: "+clk);
            if(clk==1){
                hasIF=true;
                hasID=false;
                hasEXR=false;
                hasEXI=false;
                hasEXJ=false;
                hasMEMR=false;
                hasMEMW=false;
                hasFakeMEM=false;
                hasWB=false;
                hasFakeWB=false;
            }

            if(!hasIF && !hasID && !hasEXR && !hasEXI && !hasEXJ && !hasMEMR && !hasMEMW && !hasFakeMEM && !hasWB){
                return;
            }
            if(clk>=(totalClks-5)){
                hasIF = false;
                if(clk>=(totalClks-3)){
                    hasID = false;
                    if(clk>=(totalClks-1)){
                        hasEXR = false;
                        hasEXI = false;
                        hasEXJ = false;
                        if(clk==totalClks){
                            hasMEMR = false;
                            hasMEMW = false;
                            hasFakeMEM = false;
                        }
                    }
                }
            }
            if(hasFakeWB){
                fakeWB();
            }
            if(hasWB){
                writeRegister(wRegReg, wRegValue);
            }
            if(hasFakeMEM){
                if(hasIF){ //collision avoidance
                    hasIF=false;
                }
                fakeMemAccess(fakeMemReg, fakeMemValue);
            }
            if(hasMEMW){
                if(hasIF){ //collision avoidance
                    hasIF=false;
                }
                realMemWrite(realWord, realValue, false);
            }
            if(hasMEMR){
                if(hasIF){ //collision avoidance
                    hasIF=false;
                }
                realMemRead(completeRunReg, realWord, false);
            }
            if(hasEXR){
                execR(nextOpcode, nextR1, nextR2, nextR3, nextShamt);
            }
            if(hasEXI){
                execI(nextOpcode, nextR1, nextR2, nextImmediate);
            }
            if(hasEXJ){
                execJ(nextAddress);
            }
            if(hasID){
                decode(nextDecode);
            }
            if(hasIF){
                fetch();
            }
            if(jumped){
                jumped=false;
                hasIF = false;
                hasID = false;
                hasEXR = false;
                hasEXI = false;
                hasEXJ = false;
            }
            clk++;
            if(clk%2==1){
                hasIF = true;
            }
            else{
                hasIF = false;
            }
        }
    }
    public static void main(String[]args) throws IOException, ArchitectureExceptions {
        Architecture arch = new Architecture();
        arch.parse("test.txt", 0);

        arch.writeRegister("R2", 3);
        arch.writeRegister("R3", 7);
        arch.writeRegister("R5", 8);
        arch.writeRegister("R6", 2);

        arch.writeMem(0, 8937472, true);
        arch.writeMem(1, 278183936, true);
        arch.writeMem(2, 579338244, true);

        arch.dispatcher();

        System.out.println("R1: "+ arch.readRegister("R1"));
        System.out.println("R5: "+ arch.readRegister("R5"));
    }
}
