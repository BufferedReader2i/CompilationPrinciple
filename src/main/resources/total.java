import java.io.*;
import java.util.*;

public class LexicalAnalyzer {
    private static final Map<String, Integer> KEYWORDS = new HashMap<>();
    private static final Map<Character, Integer> OPERATORS = new HashMap<>();
    private static final Map<Character, Integer> DELIMITERS = new HashMap<>();
    static List<Token> tokens;

    static {
        KEYWORDS.put("int", 21);
        KEYWORDS.put("float", 22);
        KEYWORDS.put("return", 23);
        OPERATORS.put('+', 7);
        OPERATORS.put('-', 8);
        OPERATORS.put('*', 9);
        OPERATORS.put('/', 10);
        DELIMITERS.put('(', 1);
        DELIMITERS.put(')', 2);
        DELIMITERS.put(',', 11);
        DELIMITERS.put('{', 3);
        DELIMITERS.put('}', 4);
        DELIMITERS.put(';', 5);
        DELIMITERS.put('=', 6);
    }

    public static List<Token> analyze(String input) {
        tokens = new ArrayList<>();
        char[] chars = input.toCharArray();
        int index = 0;
        while (index < chars.length) {
            char ch = chars[index];
            if (Character.isWhitespace(ch)) {
                index++;
            } else if (Character.isLetter(ch)) {
                String identifier = readIdentifier(chars, index);
                if (KEYWORDS.containsKey(identifier)) {
                    tokens.add(new Token(KEYWORDS.get(identifier), identifier));
                } else {
                    tokens.add(new Token(0, identifier));
                }
                index += identifier.length();
            } else if (isDigit(ch)) {
                String number = readNumber(chars, index);
                tokens.add(new Token(30, number)); // assuming 30 is for numbers
                index += number.length();
            } else if (OPERATORS.containsKey(ch)) {
                tokens.add(new Token(OPERATORS.get(ch), String.valueOf(ch)));
                index++;
            } else if (DELIMITERS.containsKey(ch)) {
                tokens.add(new Token(DELIMITERS.get(ch), String.valueOf(ch)));
                index++;
            } else {
                tokens.add(new Token(100, String.valueOf(ch))); // unknown character
                index++;
            }
        }
        return tokens;
    }

    private static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private static String readIdentifier(char[] chars, int index) {
        StringBuilder sb = new StringBuilder();
        while (index < chars.length && (Character.isLetter(chars[index]) || Character.isDigit(chars[index]) || chars[index] == '_')) {
            sb.append(chars[index]);
            index++;
        }
        return sb.toString();
    }

    private static String readNumber(char[] chars, int index) {
        StringBuilder sb = new StringBuilder();
        while (index < chars.length && Character.isDigit(chars[index])) {
            sb.append(chars[index]);
            index++;
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String inputFile = "testfile.txt";
        String outputFile = "output.txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            String line;
            List<Token> tokens = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                tokens.addAll(analyze(line));
            }

            Parser parser = new Parser(tokens);
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

// Token类
class Token {
    private int type;
    private String value;

    public Token(int type, String value) {
        this.type = type;
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}

// 语法分析器
class Parser {
    private List<Token> tokens;
    private int currentTokenIndex;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
    }

    public boolean parse() {
        Token token = getCurrentToken();

        // 解析代码块 {}
        if (token.getType() == 3) { // 开始块 {
            nextToken();
            while (getCurrentToken().getType() != 4) { // 直到遇到 }
                if (!parseStatement()) {
                    return false;
                }
            }
            nextToken(); // 跳过 }
            return true;
        }

        // 如果代码不以 { 开始，则处理单条语句
        return parseStatement();
    }

    private boolean parseStatement() {
        Token token = getCurrentToken();

        // 解析变量声明 int a; 或 float b;
        if (token.getType() == 21 || token.getType() == 22) { // int or float
            nextToken();
            Token identifier = getCurrentToken();
            if (identifier.getType() != 0) { // 标识符
                return false;
            }
            nextToken();
            Token semicolon = getCurrentToken();
            if (semicolon.getType() != 5) { // ;
                return false;
            }
            nextToken();
            return true;
        }

        // 解析赋值语句 a = 1 + 2;
        if (token.getType() == 0) { // 标识符
            nextToken();
            Token equal = getCurrentToken();
            if (equal.getType() != 6) { // =
                return false;
            }
            nextToken();
            if (!parseExpression()) {
                return false;
            }
            Token semicolon = getCurrentToken();
            if (semicolon.getType() != 5) { // ;
                return false;
            }
            nextToken();
            return true;
        }

        return false; // 解析失败
    }

    private boolean parseExpression() {
        // 解析表达式：标识符或数字 [运算符] 标识符或数字
        if (getCurrentToken().getType() == 0 || getCurrentToken().getType() == 30) { // 标识符或数字
            nextToken();
            while (getCurrentToken().getType() >= 7 && getCurrentToken().getType() <= 10) { // +, -, *, /
                nextToken();
                if (getCurrentToken().getType() == 0 || getCurrentToken().getType() == 30) { // 标识符或数字
                    nextToken();
                } else {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private Token getCurrentToken() {
        return tokens.get(currentTokenIndex);
    }

    private void nextToken() {
        currentTokenIndex++;
    }
}
