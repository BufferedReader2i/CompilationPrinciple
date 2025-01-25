import java.io.*;
import java.util.*;

public class LRParser {

     static  Map<Integer, Map<String, String>> ACTION = new HashMap<>();
     static  Map<Integer, Map<String, Integer>> GOTO = new HashMap<>();
     static  List<String> PRODUCTIONS = Arrays.asList(
            "E' -> E", "E -> E + T", "E -> E - T", "E -> T",
            "T -> T * F", "T -> T / F", "T -> F", "F -> ( E )", "F -> i"
    );


    static {
        Map<String, String> action0 = new HashMap<>();
        action0.put("i", "S5");
        action0.put("(", "S4");
        ACTION.put(0, action0);

        Map<String, String> action1 = new HashMap<>();
        action1.put("+", "S6");
        action1.put("-", "S12");
        action1.put("#", "ACC");
        ACTION.put(1, action1);

        Map<String, String> action2 = new HashMap<>();
        action2.put("+", "R3");
        action2.put("-", "R3");
        action2.put("*", "S7");
        action2.put("/", "S13");
        action2.put(")", "R3");
        action2.put("#", "R3");
        ACTION.put(2, action2);

        Map<String, String> action3 = new HashMap<>();
        action3.put("+", "R6");
        action3.put("-", "R6");
        action3.put("*", "R6");
        action3.put("/", "R6");
        action3.put(")", "R6");
        action3.put("#", "R6");
        ACTION.put(3, action3);

        Map<String, String> action4 = new HashMap<>();
        action4.put("i", "S5");
        action4.put("(", "S4");
        ACTION.put(4, action4);

        Map<String, String> action5 = new HashMap<>();
        action5.put("+", "R8");
        action5.put("-", "R8");
        action5.put("*", "R8");
        action5.put("/", "R8");
        action5.put(")", "R8");
        action5.put("#", "R8");
        ACTION.put(5, action5);

        Map<String, String> action6 = new HashMap<>();
        action6.put("i", "S5");
        action6.put("(", "S4");
        ACTION.put(6, action6);

        Map<String, String> action7 = new HashMap<>();
        action7.put("i", "S5");
        action7.put("(", "S4");
        ACTION.put(7, action7);

        Map<String, String> action8 = new HashMap<>();
        action8.put("+", "S6");
        action8.put("-", "S12");
        action8.put(")", "S11");
        action8.put("#", "ACC");
        ACTION.put(8, action8);

        Map<String, String> action9 = new HashMap<>();
        action9.put("+", "R1");
        action9.put("-", "R1");
        action9.put("*", "S7");
        action9.put("/", "S13");
        action9.put(")", "R1");
        action9.put("#", "R1");
        ACTION.put(9, action9);

        Map<String, String> action10 = new HashMap<>();
        action10.put("+", "R4");
        action10.put("-", "R4");
        action10.put("*", "R4");
        action10.put("/", "R4");
        action10.put(")", "R4");
        action10.put("#", "R4");
        ACTION.put(10, action10);

        Map<String, String> action11 = new HashMap<>();
        action11.put("+", "R7");
        action11.put("-", "R7");
        action11.put("*", "R7");
        action11.put("/", "R7");
        action11.put(")", "R7");
        action11.put("#", "R7");
        ACTION.put(11, action11);

        Map<String, String> action12 = new HashMap<>();
        action12.put("i", "S5");
        action12.put("(", "S4");
        ACTION.put(12, action12);

        Map<String, String> action14 = new HashMap<>();
        action14.put("+", "R2");
        action14.put("-", "R2");
        action14.put("*", "R2");
        action14.put("/", "R2");
        action14.put(")", "R2");
        action14.put("#", "R2");
        ACTION.put(14, action14);

        Map<String, String> action13 = new HashMap<>();
        action13.put("i", "S5");
        action13.put("(", "S4");
        ACTION.put(13, action13);

        Map<String, String> action15 = new HashMap<>();
        action15.put("+", "R5");
        action15.put("-", "R5");
        action15.put("*", "R5");
        action15.put("/", "R5");
        action15.put(")", "R5");
        action15.put("#", "R5");
        ACTION.put(15, action15);


        Map<String, Integer> goto0 = new HashMap<>();
        goto0.put("E", 1);
        goto0.put("T", 2);
        goto0.put("F", 3);
        GOTO.put(0, goto0);

        Map<String, Integer> goto4 = new HashMap<>();
        goto4.put("E", 8);
        goto4.put("T", 2);
        goto4.put("F", 3);
        GOTO.put(4, goto4);

        Map<String, Integer> goto6 = new HashMap<>();
        goto6.put("T", 9);
        goto6.put("F", 3);
        GOTO.put(6, goto6);

        Map<String, Integer> goto7 = new HashMap<>();
        goto7.put("F", 10);
        GOTO.put(7, goto7);

        Map<String, Integer> goto12 = new HashMap<>();
        goto12.put("T", 14);
        goto12.put("F", 3);
        GOTO.put(12, goto12);

        Map<String, Integer> goto13 = new HashMap<>();
        goto13.put("F", 15);
        GOTO.put(13, goto13);
    }


    public static boolean parse(List<word> tokens) throws IOException {
        String outputFile = "output.txt";
        PrintWriter writer = new PrintWriter(new FileWriter(outputFile)) ;
        Stack<Integer> stateStack = new Stack<>();
        Stack<String> symbolStack = new Stack<>();
        Stack<String> showStack = new Stack<>();
        stateStack.push(0);
        symbolStack.push("#");
        showStack.push("#");
        int index = 0;

        while (true) {
            int state = stateStack.peek();
            String currentInput = index < tokens.size() ? String.valueOf(tokens.get(index).type) : "#"; // 结束符为 #


            System.out.print(stackIntToString(stateStack)+"\t");
            System.out.print(stackToString(showStack)+"\t");
            System.out.println(tokens.get(index).value);
            writer.print(stackIntToString(stateStack)+"\t");
            writer.print(stackToString(showStack)+"\t");
            writer.println(tokens.get(index).value);


//            String action = ACTION.getOrDefault(state, Collections.emptyMap()).get(currentInput);

            String action = ACTION.get(state).get(currentInput);

            if (action == null) {
                writer.println("no");
                writer.flush();

                return false;
            }

            if (action.startsWith("S")) { // Shift 操作
                stateStack.push(Integer.parseInt(action.substring(1))); // 新状态入栈

                symbolStack.push(currentInput);
                showStack.push(tokens.get(index).value);

                index++;
            } else if (action.startsWith("R")) { // Reduce 操作
                int productionIndex = Integer.parseInt(action.substring(1));
                String production = PRODUCTIONS.get(productionIndex);
                String[] parts = production.split(" -> ");
                String left = parts[0];
                String right = parts[1];

                // 弹出符号和状态
                if (!right.equals("ε")) {
                    int popCount = right.split(" ").length;
                    for (int i = 0; i < popCount; i++) {
                        symbolStack.pop();
                        stateStack.pop();
                        showStack.pop();
                    }
                }

                symbolStack.push(left);

                showStack.push(left);

                int gotoState = GOTO.get(stateStack.peek()).get(left);
                stateStack.push(gotoState);

            } else if (action.equals("ACC")) {
                writer.println("yes");
                writer.flush();

                return true;
            }
        }
    }
    public static String stackToString(Stack<String> stack) {
        StringBuilder sb = new StringBuilder();
        for (String s : stack) {
            sb.append(s);
        }
        return sb.toString().trim();
    }
    public static String stackIntToString(Stack<Integer> stack) {
        StringBuilder sb = new StringBuilder();
        for (Integer s : stack) {
            sb.append(s.toString());
        }
        return sb.toString().trim();
    }
    public static void main(String[] args) throws IOException {
        String inputFile = "testfile.txt";


        BufferedReader reader = new BufferedReader(new FileReader(inputFile));



            String line  = reader.readLine()+"#";



        List<Token> tokens = LexicalAnalyzer.analyze(line);
        HashMap<Integer,String> map=new HashMap<>();
        map.put(30,"i");

        map.put(7,"+");
        map.put(8,"-");
        map.put(9,"*");
        map.put(10,"/");
        map.put(1,"(");
        map.put(2,")");
        map.put(11,",");


        List<word> words=new ArrayList<>();

        for (Token token : tokens) {
            String type=map.get(token.type);
            if (token.value.equals("#"))type="#";
            if (type==null)type="i";
            words.add(new word(type,token.value));



        }
//        for (word word : words) {
//            System.out.println(word);
//        }
        boolean result = parse(words);
        System.out.println(result ? "yes" : "no");
    }
}


 class word {
    String type;
    String value;

    public word(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "word{" +
                "type=" + type +
                ", value=" + value + "}";
    }

    public String getValue() {
        return value;
    }


}