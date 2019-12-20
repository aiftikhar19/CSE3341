
public class Main {

    public static void main (String[] args) {
        try {
            Scanner.begin(args[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Unknown option or incorrect number of arguments");
            System.exit(2);
        }

        PROG parseTree = Parser.getParseTree();

        Parser.prettyPrint(parseTree);

        try {
            Parser.execute(parseTree, args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Unknown option or incorrect number of arguments");
            System.exit(2);
        }
        System.out.println();
    }
}
