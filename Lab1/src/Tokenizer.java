import java.io.*;
import java.util.LinkedList;
import java.util.List;

public class Tokenizer {

    private Tokenizer() { }

    private static final String WHITESPACE = " \n\t\r";
    private static final String SPECIAL = ":!<>";
    private static final String SYMBOL = ";,()[]=+-*|";
    private static List<String> TOKENS = new LinkedList<String>();
    private static int COUNT = 0;

    //open file & generate tokens
    public static void begin(String program) {
        BufferedReader reader = null;
        List<String> lines = new LinkedList<String>();
        try {
            reader = new BufferedReader(new FileReader(new File(program)));
            String line;
            while ((line = reader.readLine()) != null) lines.add(line);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException ignored) { }
        }
        tokenize(lines);
    }

    public static String currentToken() { return TOKENS.get(COUNT); }

    public static void nextToken() { COUNT++; }

    public static void resetTokenStream() { COUNT = 0; }

    //generate tokens
    private static void tokenize (List<String> lines) {
        for (String line : lines) {
            int i = 0, len = line.length();
            while (i < len) {
                int j = i + 1;
                char first = line.charAt(i);
                if (Character.isDigit(first)) {
                    while (j < len && Character.isDigit(line.charAt(j))) j++;
                    TOKENS.add(line.substring(i, j));
                } else if (SPECIAL.contains(String.valueOf(first))) {
                    if (i + 1 < len && line.charAt(i + 1) == '=') j++;
                    TOKENS.add(line.substring(i, j));
                } else if (SYMBOL.contains(String.valueOf(first))) {
                    TOKENS.add(line.substring(i, j));
                } else if (!WHITESPACE.contains(String.valueOf(first))) {
                    while (j < len && !WHITESPACE.contains(String.valueOf(line.charAt(j)))
                            && !SYMBOL.contains(String.valueOf(line.charAt(j)))
                            && !SPECIAL.contains(String.valueOf(line.charAt(j)))) j++;
                    TOKENS.add(line.substring(i, j));
                }
                i = j;
            }
        }
        TOKENS.add("EOF");
    }
}
