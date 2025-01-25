import java.util.List;


class ParserException extends RuntimeException {
    public ParserException(String message) {
        super(message);
    }
}

public class Parser {
    private List<Token> tokens;
    private int currentTokenIndex;
    private Token currentToken;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.currentTokenIndex = 0;
        this.currentToken = tokens.get(0);
    }

    private void nextToken() {
        currentTokenIndex++;
        if (currentTokenIndex < tokens.size()) {
            currentToken = tokens.get(currentTokenIndex);
        } else {
            currentToken = null;  // 无更多 token
        }
    }


    private void error(String message) {
        throw new ParserException("在第" + currentTokenIndex+"个Token处发生错误: " + message);
    }

    public boolean parse() {
        try {
            return parseA() && currentToken == null; // 完整解析，并确保所有输入都已处理
        } catch (ParserException e) {
            System.err.println(e.getMessage());  // 输出错误信息
            return false;
        }
    }

    private boolean parseA() {
        if (currentToken != null && currentToken.getValue().equals("{")) {
            nextToken();
            if (parseM() && parseN()) {
                if (currentToken != null && currentToken.getValue().equals("}")) {
                    nextToken();
                    return true;
                } else {
                    error("应为 '}'");
                }
            }
        }
        error("应为 '{'");
        return false;
    }

    private boolean parseM() {
        if (parseP()) {
            return parseM();
        }
        return true;  // ε 为空
    }

    private boolean parseP() {
        if (parseD()) {
            if (currentToken != null && currentToken.getType() == 0) {  // 标识符 i
                nextToken();
                if (currentToken != null && currentToken.getValue().equals(";")) {
                    nextToken();
                    return true;
                } else {
                    error("标识符后应为 ';'");
                }
            } else {
                error("类型声明后应为标识符");
            }
        }
        return false;
    }

    private boolean parseD() {
        if (currentToken != null && (currentToken.getValue().equals("int") || currentToken.getValue().equals("float"))) {
            nextToken();
            return true;
        }
        return false;
    }

    private boolean parseN() {
        if (parseQ()) {
            return parseN();
        }
        return true;  // ε 为空
    }

    private boolean parseQ() {
        if (currentToken != null && currentToken.getType() == 0) {  // 标识符 i
            nextToken();
            if (currentToken != null && currentToken.getValue().equals("=")) {
                nextToken();
                if (parseE()) {
                    if (currentToken != null && currentToken.getValue().equals(";")) {
                        nextToken();
                        return true;
                    } else {
                        error("表达式后应为 ';'");
                    }
                } else {
                    error("无效的表达式");
                }
            } else {
                error("标识符后应为 '='");
            }
        }
        return false;
    }

    private boolean parseE() {
        if (parseT() && parseEPrime()) {
            return true;
        }
        return false;
    }

    private boolean parseEPrime() {
        if (currentToken != null && (currentToken.getValue().equals("+") || currentToken.getValue().equals("-"))) {
            nextToken();
            if (parseT()) {
                return parseEPrime();
            }
            error("运算符后应为项");
        }
        return true;  // ε 为空
    }

    private boolean parseT() {
        if (parseF() && parseTPrime()) {
            return true;
        }
        return false;
    }

    private boolean parseTPrime() {
        if (currentToken != null && (currentToken.getValue().equals("*") || currentToken.getValue().equals("/"))) {
            nextToken();
            if (parseF()) {
                return parseTPrime();
            }
            error("运算符后应为因子");
        }
        return true;  // ε 为空
    }

    private boolean parseF() {
        if (currentToken != null) {
            if (currentToken.getValue().equals("(")) {
                nextToken();
                if (parseE()) {
                    if (currentToken != null && currentToken.getValue().equals(")")) {
                        nextToken();
                        return true;
                    } else {
                        error("应为 ')'");
                    }
                } else {
                    error("括号内无效的表达式");
                }
            } else if (currentToken.getType() == 0 || currentToken.getType() == 30) {  // 标识符 i 或 数字 d
                nextToken();
                return true;
            } else {
                error("应为标识符或数字");
            }
        }
        return false;
    }
}
