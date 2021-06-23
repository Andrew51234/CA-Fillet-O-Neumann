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
    private static int curPCToDec;
    private static int curPCToExec;
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
        numOfIns = 0;
        for(int i=0; i<=1023; i++){
            mainMem[i] = 2000000000;
        }
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
                        boolean negative = false;
                        if(imm<0){
                            tempImm = tempImm.substring(14);
                            negative = true;
                        }
                        if (tempImm.length() <= 18) {
                            String temp = "";
                            int zeroes = 18 - tempImm.length();
                            for (int i = 0; i < zeroes; i++) {
                                if(negative){
                                    temp += '1';
                                }
                                else temp += '0';
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
            if(binValue.length()<32){ //shift amount not given
                binValue += "0000000000000";
            }
            instructions.add(convSigned(binValue));
        }
        for (int instruction : instructions){
            mainMem[offset] = instruction;
            offset++;
            numOfIns++;
        }
    }

    public static void writeRegister(String register, int value) {
        if(register.equals("R0")) {
            System.out.println("Write Back To R0");
            return;
        }

        if(register.equals("PC")){
            registers[32] = value;
        }
        else if(register.charAt(0)=='R' && Integer.parseInt(register.substring(1))>0 && Integer.parseInt(register.substring(1))<32){
            registers[Integer.parseInt(register.substring(1))] = value;
            System.out.println("Write Back To "+register);

        }
    }

    public static int readRegister(String register){
        if(register.equals("PC")){
            return registers[32];
        }
        else if(register.charAt(0)=='R' && Integer.parseInt(register.substring(1))>=0 && Integer.parseInt(register.substring(1))<32){
            return registers[Integer.parseInt(register.substring(1))];
        }
        return 0;
    }

    public static void writeMem(int word, int value, boolean instruction){

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
                System.out.println("Write In Memory["+word + 1024 +"]");
                mainMem[word + 1024] = value;
            }
        }
    }

    public static int readMem(int word, boolean instruction){
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
                System.out.println("Read From Memory["+word+"]");
                return mainMem[word+1024];
        }
    }

    public static void fakeMemAccess(String register, int value){
        wRegReg = register;
        wRegValue = value;
        System.out.println("Memory Access Stage");
        hasWB = true;
    }

    public static void realMemWrite(int word, int value, boolean instruction) {
        writeMem(word, value, instruction);
        hasFakeWB = true;

    }

    public static void realMemRead(String reg, int word, boolean instruction) {
        wRegValue = readMem(word, instruction);
        wRegReg = reg;
        hasWB = true;
    }

    public static  void fakeWB(){
        System.out.println("Write Back Stage");
    }

    public static void fetch() {
        int pc = readRegister("PC");
        int instruction = 0;

        if(pc<1023) {
            instruction = readMem(pc, true);
            if(instruction==2000000000){
                hasID = false;
                decodeSecondClk = false;
                return;
            }
        }
        else {
            writeRegister("PC",0);
        }
        String binOpCode = Integer.toBinaryString(instruction);
        if (binOpCode.length() <= 32 ) {
            String temp = "";
            int zeroes = 32 - binOpCode.length();
            for (int j = 0; j < zeroes; j++) {
                temp += '0';
            }
            temp += binOpCode;
            binOpCode = temp;
        }
        System.out.println("Fetched: "+binOpCode);

        writeRegister("PC", pc+1);
        nextDecode = instruction;
        curPCToDec = pc;
        hasID = true;
        decodeSecondClk = false;
    }

    public static void decode(int instruction){

        String binOpCode = Integer.toBinaryString(instruction);
        if (binOpCode.length() <= 32 ) {
            String temp = "";
            int zeroes = 32 - binOpCode.length();
            for (int j = 0; j < zeroes; j++) {
                temp += '0';
            }
            temp += binOpCode;
            binOpCode = temp;
        }

        if(!decodeSecondClk){
            decodeSecondClk = true;
            System.out.println("Decoding First Clock Cycle: "+binOpCode);
            return;
        }
        int opcode = 0;      // bits31:28
        int r1 = 0;          // bits27:23
        int r2 = 0;          // bits22:18
        int r3 = 0;          // bits17:13
        int shamt = 0;       // bits12:0
        int immediate = 0;   // bits17:0
        int address = 0;     // bits27:0

        //bit-masking

        opcode    = (instruction & 0b11110000000000000000000000000000) >> 28;
        r1        = (instruction & 0b00001111100000000000000000000000) >> 23;
        r2        = (instruction & 0b00000000011111000000000000000000) >> 18;
        r3        = (instruction & 0b00000000000000111110000000000000) >> 13;
        shamt     = (instruction & 0b00000000000000000001111111111111);
        immediate = (instruction & 0b00000000000000111111111111111111);
        address   = (instruction & 0b00001111111111111111111111111111);

        System.out.println("Decoded Second Clock Cycle: "+binOpCode);

        curPCToExec = curPCToDec;
        if (opcode == 0 || opcode == 1 || opcode == -8  || opcode == -7 ){

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
            if(Integer.toBinaryString(instruction).charAt(14)=='1'){
                immediate = convSigned(Integer.toBinaryString(immediate));
            }
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
        hasID = false;
    }

    public static void execR(int opcode, int r1, int r2, int r3, int shamt) {

        if(!executeSecondClk){
            executeSecondClk = true;
            String binOpCode = Integer.toBinaryString(opcode);
            if(opcode >=0){
                if (binOpCode.length() <= 4 ) {
                    String temp = "";
                    int zeroes = 4 - binOpCode.length();
                    for (int i = 0; i < zeroes; i++) {
                        temp += '0';
                    }
                    temp += binOpCode;
                    binOpCode = temp;
                }
            }
            else if(opcode<0){
                binOpCode = binOpCode.substring(28);
            }
            System.out.println("Executing First Clock Cycle Of Instruction: "+binOpCode);

            return;
        }

        String r1Pos = "R"+r1;
        int r2Value = readRegister("R"+r2);
        int r3Value = readRegister("R"+r3);

        if (opcode == 0){  //ADD
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value + r3Value;
            hasFakeMEM = true;
            System.out.println("Executed Second Clock Cycle Of Instruction: ADD");

        }

        if (opcode == 1){  //SUB
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value - r3Value;
            hasFakeMEM = true;
            System.out.println("Executed Second Clock Cycle Of Instruction: SUB");
        }

        if ( opcode == -8){  //SLL
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value << shamt;
            hasFakeMEM = true;
            System.out.println("Executed Second Clock Cycle Of Instruction: SLL");
        }

        if (opcode == -7){  //SRL
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value >> shamt;
            hasFakeMEM = true;
            System.out.println("Executed Second Clock Cycle Of Instruction: SRL");
        }

        hasEXR=false;
        hasEXI=false;
        hasEXJ=false;
    }

    public static void execI(int opcode, int r1, int r2, int immediate) {

        if(!executeSecondClk){
            executeSecondClk = true;
            String binOpCode = Integer.toBinaryString(opcode);
            if(opcode >=0){
                if (binOpCode.length() <= 4 ) {
                    String temp = "";
                    int zeroes = 4 - binOpCode.length();
                    for (int i = 0; i < zeroes; i++) {
                        temp += '0';
                    }
                    temp += binOpCode;
                    binOpCode = temp;
                }
            }
            else if(opcode<0){
                binOpCode = binOpCode.substring(28);
            }
            System.out.println("Executing First Clock Cycle Of Instruction: "+binOpCode);
            return;
        }

        String r1Pos = "R"+r1;
        int r1Value = readRegister("R"+r1);
        int r2Value = readRegister("R"+r2);

        if (opcode == 2){  //MULI
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value * immediate;
            hasFakeMEM = true;
            System.out.println("Executed Second Clock Cycle Of Instruction: MULI");
        }

        if (opcode == 3){  //ADDI
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value + immediate;
            hasFakeMEM = true;
            System.out.println("Executed Second Clock Cycle Of Instruction: ADDI");
        }

        if (opcode == 4){  //BNE
            if(r1Value != r2Value){
                fakeMemReg = "PC";
                fakeMemValue = curPCToExec + 1 + immediate;
                hasFakeMEM = true;
                jumped = true;
                System.out.println("Executed Second Clock Cycle Of Instruction: BNE");
            }
        }

        if (opcode == 5){  //ANDI
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value & immediate;
            hasFakeMEM = true;
            System.out.println("Executed Second Clock Cycle Of Instruction: ANDI");
        }

        if (opcode == 6){  //ORI
            fakeMemReg = r1Pos;
            fakeMemValue = r2Value | immediate;
            hasFakeMEM = true;
            System.out.println("Executed Second Clock Cycle Of Instruction: ORI");
        }

        if (opcode == -6){  //LW
            completeRunReg = r1Pos;
            realWord = r2Value + immediate;
            hasMEMR = true;
            System.out.println("Executed Second Clock Cycle Of Instruction: LW");
        }

        if (opcode == -5){  //SW
            realWord = r2Value + immediate;
            realValue = r1Value;
            hasMEMW = true;
            System.out.println("Executed Second Clock Cycle Of Instruction: SW");
        }


        hasEXR=false;
        hasEXI=false;
        hasEXJ=false;

    }

    public static void execJ(int address){  //J

        if(!executeSecondClk){
            executeSecondClk = true;
            System.out.println("Executing First Clock Cycle Of Instruction: 0111");
            return;
        }

        int newPC  = (curPCToExec & 0b11110000000000000000000000000000) >> 28;
        String newPCStr = Integer.toBinaryString(newPC);
        String addressStr = Integer.toBinaryString(address);
        int value = Integer.parseInt((newPCStr + addressStr),2);

        fakeMemReg = "PC";
        fakeMemValue = value;
        hasFakeMEM = true;
        jumped = true;

        System.out.println("Executed Second Clock Cycle Of Instruction: J");

        hasEXR=false;
        hasEXI=false;
        hasEXJ=false;
    }

    public static void dispatcher() throws IOException {

        int totalClks = 7 + ((numOfIns-1)*2);
        while(true){
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

            System.out.println("Clock Cycle: "+clk);

            if(hasFakeWB){
                fakeWB();
                hasFakeWB = false;
                hasWB = false;
            }
            if(hasWB){
                writeRegister(wRegReg, wRegValue);
                hasFakeWB = false;
                hasWB = false;
            }
            if(hasFakeMEM){
                if(hasIF){ //collision avoidance
                    hasIF=false;
                }
                fakeMemAccess(fakeMemReg, fakeMemValue);
                hasFakeMEM = false;
                hasMEMR = false;
                hasMEMW = false;
            }
            if(hasMEMW){
                if(hasIF){ //collision avoidance
                    hasIF=false;
                }
                realMemWrite(realWord, realValue, false);
                hasFakeMEM = false;
                hasMEMR = false;
                hasMEMW = false;
            }
            if(hasMEMR){
                if(hasIF){ //collision avoidance
                    hasIF=false;
                }
                realMemRead(completeRunReg, realWord, false);
                hasFakeMEM = false;
                hasMEMR = false;
                hasMEMW = false;
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

            System.out.println("PC: " + registers[32]);
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
            System.out.println("");
            clk++;
            if(clk%2==1){
                hasIF = true;
            }
            else{
                hasIF = false;
            }
        }
    }

    public static void run(String fileName) throws IOException {

        parse(fileName, 0);

        dispatcher();

        System.out.println("~Register Values~");
        for(int i=0; i<registers.length-1; i++){
            System.out.println("R"+i+": "+registers[i]);
        }
        System.out.println("PC: "+registers[32]);

        System.out.println("");

        System.out.println("~Program Instructions~");
        for(int i=0; i<numOfIns; i++){
            String binOpCode = Integer.toBinaryString(mainMem[i]);
            if (binOpCode.length() <= 32 ) {
                String temp = "";
                int zeroes = 32 - binOpCode.length();
                for (int j = 0; j < zeroes; j++) {
                    temp += '0';
                }
                temp += binOpCode;
                binOpCode = temp;
            }
            System.out.println("Mem["+i+"]: "+binOpCode);
        }

        System.out.println("");

        System.out.println("~Memory Data~");
        for(int i=1024; i< mainMem.length; i++){
            if(mainMem[i]!=0)
                System.out.println("Mem["+i+"]: "+mainMem[i]);
        }
        System.out.println("Rest of memory data addresses hold the value zero.");
    }

    public static void main(String[]args) {
        Architecture arch = new Architecture();

        try {
            arch.run("test.txt");
        } catch (IOException e) {
            System.out.println("Couldn't read file");
        }

    }
}
