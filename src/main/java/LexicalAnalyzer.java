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
                tokens.add(new Token(30, number));
                index += number.length();
            } else if (OPERATORS.containsKey(ch)) {
                tokens.add(new Token(OPERATORS.get(ch), String.valueOf(ch)));
                index++;
            } else if (DELIMITERS.containsKey(ch)) {
                tokens.add(new Token(DELIMITERS.get(ch), String.valueOf(ch)));
                index++;
            } else {
                tokens.add(new Token(100, String.valueOf(ch)));
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

//    public static void main(String[] args) {
//        String inputFile = "testfile.txt";
//        String outputFile = "output.txt";
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
//             PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
//
//            List<Token> tokens = new ArrayList<>();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                tokens.addAll(LexicalAnalyzer.analyze(line));  // 使用词法分析器
//            }
//
//            Parser parser = new Parser(tokens);  // 语法分析器
//            if (parser.parse()) {
//                writer.println("yes");
//            } else {
//                writer.println("no");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
}
