import java.io.*;
import java.util.*;



public class LL1Parser {
    private List<Token> tokens;
    private int index;
    private Stack<String> stack;
    private PrintWriter writer;
    String currentToken;

    public LL1Parser(List<Token> tokens, PrintWriter writer) {
        this.tokens = tokens;
        this.index = 0;
        this.stack = new Stack<>();
        this.writer = writer;
        stack.push("#"); // 用于表示栈底
        stack.push("E"); // 从文法的开始符号开始
    }

    public boolean parse() {
        while (!stack.isEmpty()) {
            currentToken = currentToken();
            writer.printf("%s\t%s%n", stackToString(), currentToken);
            //System.out.println(stackToString()+"  :"+currentToken);
            String top = stack.pop();


            // 输出当前栈状态和当前处理的token


            if (top.equals("#") && currentToken.equals("#")) {
                return true; // 成功匹配结束符
            }


           //System.out.println(isTerminal(top)+top);
            if (isTerminal(top)) {
                if (top.equals(currentToken)) {
                    // 匹配成功，继续处理下一个token

//                    writer.printf("%s%s\t%s%n", stackToString(),top, currentToken);
//                    System.out.println(stackToString()+top+"  :"+currentToken);
                    index++;
                } else {
                    return false; // 匹配失败
                }
            } else {
                // 根据当前栈顶符号进行匹配

                //System.out.println(top);
                switch (top) {
                    case "E":
                        stack.push("e");
                        stack.push("T");
                        break;
                    case "e":
                        if (currentToken.equals("+")) {
                            stack.push("e");
                            stack.push("T");
                            stack.push("+");
                        } else if (currentToken.equals("-")) {
                            stack.push("e");
                            stack.push("T");
                            stack.push("-");
                        } else {
                            // ε
                        }
                        break;
                    case "T":
                        stack.push("t");
                        stack.push("F");
                        break;
                    case "t":
                        if (currentToken.equals("*")) {
                            stack.push("t");
                            stack.push("F");
                            stack.push("*");
                        } else if (currentToken.equals("/")) {
                            stack.push("t");
                            stack.push("F");
                            stack.push("/");
                        } else {
                            // ε
                        }
                        break;
                    case "F":
                         if (match('i')) {
                        //
                    }
                        else if (currentToken.equals("(")) {
                            stack.push(")");
                            stack.push("E");
                             stack.push("(");
                        } else if (matchIdentifier()) {
                            // ε
                        } else {
                            return false; // 无法匹配
                        }
                        break;
                    default:
                        return false; // 无法匹配
                }
            }
        }
        return index == tokens.size(); // 确保所有tokens都被匹配
    }

    private boolean match(char c) {
        int type=tokens.get(index).getType();

        if ( type==0||type==30) {
            writer.printf("%s%s\t%s%n", stackToString(), c,currentToken);
           // System.out.println(stackToString()+c+"  :"+currentToken);

            index++;
            return true;
        }
        return false;
    }

    private boolean matchIdentifier() {
        if (index < tokens.size() && tokens.get(index).getType() == 0) {
            index++;
            return true;
        }
        return false;
    }

    private String currentToken() {
        return index < tokens.size() ? tokens.get(index).getValue() : "#";
    }

    private boolean isTerminal(String symbol) {

        return !symbol.equals("E") && !symbol.equals("e") && !symbol.equals("T") && !symbol.equals("t") && !symbol.equals("F");
    }

    private String stackToString() {
        StringBuilder sb = new StringBuilder();
        for (String s : stack) {
            sb.append(s);
        }
        return sb.toString().trim(); // 反转栈内容以便输出
    }

    public static void main(String[] args) {
        String inputFile = "testfile.txt";
        String outputFile = "output.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {

            List<Token> tokens = new ArrayList<>();
            String line;
            while ((line = reader.readLine()) != null) {
                tokens.addAll(LexicalAnalyzer.analyze(line));  // 使用词法分析器
            }

            //tokens.forEach(s-> System.out.println(s.toString()));

            LL1Parser parser = new LL1Parser(tokens, writer);  // 语法分析器
            if (parser.parse()) {
                writer.println("yes");
            } else {
                writer.println("no");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
