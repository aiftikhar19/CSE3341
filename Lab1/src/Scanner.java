import java.util.Arrays;

public class Scanner {

    private Scanner() { }

    private static final String ILLEGAL = "~@#$%^&_?/`.";
    private static final String[] KEYWORD = {"program", "begin", "end", "int", "input", "output", "if",
            "then", "else", "endif", "do", "enddo", "while", "endwhile", "case", "of", "OR", "AND", "EOF"};

    public static void begin(String program) { Tokenizer.begin(program); }

    public static String currentToken() { return getParsableToken(Tokenizer.currentToken()); }

    public static void nextToken() { Tokenizer.nextToken(); }

    public static void resetTokenStream() { Tokenizer.resetTokenStream(); }

    //matches to current token 
    public static void match(String keyword) {
        String token = currentToken();
        if (token.equals(keyword)) {
            nextToken();
        } else {
            System.out.println("ERROR: Expected " + keyword + ", found " + token);
            System.exit(2);
        }
    }

    //extract id name from currentToken
    public static String getID() {
        String token = currentToken();
        String id = "";
        if(token.contains("ID")) {
            id = token.substring(token.indexOf("[") + 1, token.indexOf("]"));
            nextToken();
        } else {
            System.out.println("ERROR: Expected ID token, found " + token);
            System.exit(2);
        }
        return id;
    }

    //extract const value from currentToken
    public static int getConst() {
        String token = currentToken();
        String id = "";
        if(token.contains("CONST")) {
            id = token.substring(token.indexOf("[") + 1, token.indexOf("]"));
            nextToken();
        } else {
            System.out.println("ERROR: Expected CONST token, found " + token);
            System.exit(2);
        }
        int value = 0;
        try {
            value = Integer.parseInt(id);
        } catch (NumberFormatException e) {
            System.out.println("ERROR: Data token " + id + " does not match a valid integer");
            System.exit(2);
        }
        return value;
    }

    //Map string from literal symbol to token representation for parser
    private static String getParsableToken(String token) {
        if (containsIllegalChar(token)) return "SCANNER_ERROR[" + token + "]";
        else if (Arrays.asList(KEYWORD).contains(token)) return token.toUpperCase();
        else if (Character.isLetter(token.charAt(0))) return "ID[" + token + "]";
        else if (Character.isDigit(token.charAt(0))) return "CONST[" + token + "]";
        else if (token.equals(";")) return "SEMICOLON";
        else if (token.equals(",")) return "COMMA";
        else if (token.equals("(")) return "LEFT_PAREN";
        else if (token.equals(")")) return "RIGHT_PAREN";
        else if (token.equals("[")) return "LEFT_BRACKET";
        else if (token.equals("]")) return "RIGHT_BRACKET";
        else if (token.equals("=")) return "EQUALS";
        else if (token.equals("+")) return "PLUS";
        else if (token.equals("-")) return "MINUS";
        else if (token.equals("*")) return "TIMES";
        else if (token.equals(":")) return "COLON";
        else if (token.equals("!")) return "NOT";
        else if (token.equals("<")) return "LESS_THAN";
        else if (token.equals(">")) return "GREATER_THAN";
        else if (token.equals("|")) return "BAR";
        else if (token.equals(":=")) return "ASSIGN";
        else if (token.equals("!=")) return "NOT_EQUAL";
        else if (token.equals("<=")) return "LESS_EQUAL";
        else if (token.equals(">=")) return "GREATER_EQUAL";
        else if (token.equals("EOF")) return "EOF";
        else return "SCANNER_ERROR[" + token + "]";
    }

    //checks for illegal characters
    private static Boolean containsIllegalChar(String token) {
        Boolean result = false;
        for (int i = 0; i < token.length(); i++) {
            if (ILLEGAL.contains(String.valueOf(token.charAt(i)))) result = true;
        }
        return result;
    }

    //prints out all tokens - helper function 
    public static void printTokens() {
        String token;
        while (!(token = currentToken()).equals("EOF")) {
            if (token.contains("SCANNER_ERROR")) {
                System.out.println("\nERROR: Token " + token.substring(14, token.indexOf("]"))
                        + " did not match any valid token in the Core language");
                System.exit(2); // Failure Case;
            } else {
                System.out.print(token + " ");
            }
            nextToken();
        }
        System.out.println();
        resetTokenStream();
    }
}
