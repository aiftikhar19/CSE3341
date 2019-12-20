import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

public class Parser {

    private Parser () { }

    public static HashMap<String,Integer> VARIABLES = new HashMap<String,Integer>();
    public static List<Integer> DATA = new LinkedList<Integer>();

    //Call Scanner/Tokenizer to generate, build, and parse token stream
    public static PROG getParseTree() {
        PROG tree = new PROG(); tree.parse();
        return tree;
    }

    //Execute program given data
    public static void execute(PROG parseTree, String data) {
        getData(data);
        PROG.exec(parseTree);
    }

    //Pretty print program
    public static void prettyPrint(PROG parseTree) { PROG.print(parseTree); }


    //Generate token list of lines from DATA file
    public static void getData(String data) {
        BufferedReader reader = null;
        List<String> lines = new LinkedList<String>();
        try {
            reader = new BufferedReader(new FileReader(new File(data)));
            String line;
            while ((line = reader.readLine()) != null) lines.add(line);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException ignored) { }
        }
        updateDataList(lines);
    }

    //Update DATA list with integers from DATA lines;
    public static void updateDataList(List<String> lines) {
        for (String line : lines) {
            int i = 0, len = line.length();
            while (i < len) {
                String token = "";
                int j = i + 1;
                char first = line.charAt(i);
                if (Character.isDigit(first) || first == '-') {
                    while (j < len && Character.isDigit(line.charAt(j))) j++;
                    token = line.substring(i, j);
                }
                if (token.length() > 0) {
                    try {
                        DATA.add(Integer.parseInt(token));
                    } catch (NumberFormatException e) {
                        System.out.println("ERROR: Input " + token + " does not match a valid integer");
                        System.exit(2);
                    }
                }
                i = j;
            }
        }
    }

    //Set the id field of the idList to the Integer at DATA.remove(0);
    public static void setVarByInput(ID_LIST idList) {
        // Error if no more input tokens;
        if (DATA.size() > 0) {
            // Set value to first input token if id is in VARIABLES and |VARIABLES| > 0;
            String currentId = idList.getId();
            if (VARIABLES.containsKey(currentId)) {
                VARIABLES.put(currentId, DATA.remove(0));
            } else {
                System.out.println("ERROR: variable id " + currentId + " has not been declared");
                System.exit(2);
            }
        } else {
            System.out.println("ERROR: no more input, cannot take input");
            System.exit(2);
        }
    }

    //Print value of id of idList to System.out if in VARIABLES;
    public static void outputVar(ID_LIST idList) {
        String currentId = idList.getId();
        if (VARIABLES.containsKey(currentId)) {
            if (VARIABLES.get(currentId) != null) {
                System.out.println(VARIABLES.get(currentId));
            } else {
                System.out.println("ERROR: variable id " + currentId + " has not been instantiated");
                System.exit(2);
            }
        } else {
            System.out.print("ERROR: variable id " + currentId + " has not been declared");
            System.exit(2);
        }
    }

    //For resolving an ID to a CONST;
    public static int getValueById(String id) {
        int result = 0;
        if (VARIABLES.containsKey(id)) {
            if (VARIABLES.get(id) != null) {
                result = VARIABLES.get(id);
            } else {
                System.out.println("ERROR: variable id " + id + " has not been instantiated");
                System.exit(2);
            }
        } else {
            System.out.println("ERROR: variable id " + id + " has not been declared");
            System.exit(2);
        }
        return result;
    }

    //Helper method for indentation
    public static void indent(int times) { for(int i = 0; i < times; i++) System.out.print("  "); }
}

//Class for the PROG node
class PROG {
    private DECL_SEQ declSeq;
    private STMT_SEQ stmtSeq;

    public void parse() {
        Scanner.match("PROGRAM");
        declSeq = new DECL_SEQ(); declSeq.parse();
        Scanner.match("BEGIN");
        stmtSeq = new STMT_SEQ(); stmtSeq.parse();
        Scanner.match("END");
        Scanner.match("EOF");
    }

    public static void print(PROG prog) {
        System.out.println("program");
        DECL_SEQ.print(prog.getDeclSeq());
        System.out.println("begin");
        STMT_SEQ.print(prog.getStmtSeq(), 1);
        System.out.println("end");
    }

    public static void exec(PROG prog) {
        DECL_SEQ.exec(prog.getDeclSeq());
        STMT_SEQ.exec(prog.getStmtSeq());
    }

    public DECL_SEQ getDeclSeq() { return declSeq; }
    public STMT_SEQ getStmtSeq() { return stmtSeq; }
}

//Class for the DECL_SEQ node
class DECL_SEQ {

    private int altNo = 0;
    private DECL decl;          // 0 = <decl>;
    private DECL_SEQ declSeq;   // 1 = <decl><declSeq>;

    public void parse() {
        decl = new DECL(); decl.parse();
        // Parse another DECL_SEQ if no BEGIN token;
        if (!Scanner.currentToken().equals("BEGIN")) {
            altNo = 1;
            declSeq = new DECL_SEQ(); declSeq.parse();
        }
    }

    public static void print(DECL_SEQ declSeq) {
        Parser.indent(1);
        DECL.print(declSeq.getDecl());

        if (declSeq.getAltNo() == 1) {
            DECL_SEQ.print(declSeq.getDeclSeq());
        }
    }

    public static void exec(DECL_SEQ declSeq) {
        DECL.exec(declSeq.getDecl());

        if (declSeq.getAltNo() == 1) {
            DECL_SEQ.exec(declSeq.getDeclSeq());
        }
    }

    public int getAltNo() { return altNo; }
    public DECL getDecl() { return decl; }
    public DECL_SEQ getDeclSeq() { return declSeq; }
}

//Class for the DECL node
class DECL {

    private ID_LIST idList;

    public void parse() {
        Scanner.match("INT");
        idList = new ID_LIST(); idList.parse();
        Scanner.match("SEMICOLON");
    }

    public static void print(DECL decl) {
        System.out.print("int ");
        ID_LIST.print(decl.getIdList());
        System.out.println(";");
    }

    public static void exec(DECL decl) {
        ID_LIST.exec(decl.getIdList());
    }

    public ID_LIST getIdList() { return idList; }
}

//Class for the ID_LIST node
class ID_LIST {

    private int altNo = 0;
    private String id;      // 0 = id;
    private ID_LIST idList; // 1 = id<idList>;

    public void parse() {
        id = Scanner.getID();
        // Parse another ID_LIST if COMMA
        if(Scanner.currentToken().equals("COMMA")) {
            altNo = 1;
            Scanner.nextToken();
            idList = new ID_LIST(); idList.parse();
        }
    }

    public static void print(ID_LIST idList) {
        System.out.print(idList.getId());
        if (idList.getAltNo() == 1) {
            System.out.print(",");
            ID_LIST.print(idList.getIdList());
        }
    }

    public static void exec(ID_LIST idList) {
        // Add new variable to list;
        String var = idList.getId();
        if (!Parser.VARIABLES.containsKey(var)) {
            Parser.VARIABLES.put(var, null);
        } else {
            System.out.println("ERROR: Variable " + var + " has already been instantiated.");
            System.exit(2);
        }

        if (idList.getAltNo() == 1) {
            ID_LIST.exec(idList.getIdList());
        }
    }

    public int getAltNo() { return altNo; }
    public String getId() { return id; }
    public ID_LIST getIdList() { return idList; }
}

//Class for the STMT_SEQ node
class STMT_SEQ {

    private int altNo = 0;
    private STMT stmt;          // 0 = <stmt>;
    private STMT_SEQ stmtSeq;   // 1 = <stmt><stmtSeq>;

    public void parse() {
        stmt = new STMT(); stmt.parse();
        // Parse another STMT_SEQ if not END, ENDIF, ENDWHILE, ELSE
        String token = Scanner.currentToken();
        if (!token.equals("END") && !token.equals("ENDIF")
                && !token.equals("ENDWHILE") && !token.equals("ELSE")) {
            altNo = 1;
            stmtSeq = new STMT_SEQ(); stmtSeq.parse();
        }
    }

    public static void print(STMT_SEQ stmtSeq, int indent) {
        Parser.indent(indent);
        STMT.print(stmtSeq.getStmt(), indent);
        if (stmtSeq.getAltNo() == 1) {
            STMT_SEQ.print(stmtSeq.getStmtSeq(), indent);
        }
    }

    public static void exec(STMT_SEQ stmtSeq) {
        STMT.exec(stmtSeq.getStmt());
        if (stmtSeq.getAltNo() == 1) {
            STMT_SEQ.exec(stmtSeq.getStmtSeq());
        }
    }

    public int getAltNo() { return altNo; }
    public STMT getStmt() { return stmt; }
    public STMT_SEQ getStmtSeq() { return stmtSeq; }
}

//Class for the STMT node
class STMT {

    private int altNo;  
    private ASSIGN s1;  // 1 = <assign>;
    private IF s2;      // 2 = <if>;
    private LOOP s3;    // 3 = <loop>;
    private IN s4;      // 4 = <input>;
    private OUT s5;     // 5 = <output>;
    private CASE s6;    // 6 = <case>;

    public void parse() {
        String token = Scanner.currentToken();
        if (token.contains("ID")) {
            altNo = 1;
            s1 = new ASSIGN(); s1.parse();
        } else if (token.equals("IF")) {
            altNo = 2;
            s2 = new IF(); s2.parse();
        } else if (token.equals("WHILE")) {
            altNo = 3;
            s3 = new LOOP(); s3.parse();
        } else if (token.equals("INPUT")) {
            altNo = 4;
            s4 = new IN(); s4.parse();
        } else if (token.equals("OUTPUT")) {
            altNo = 5;
            s5 = new OUT(); s5.parse();
        } else if (token.equals("CASE")) {
            altNo = 6;
            s6 = new CASE(); s6.parse();
        } else {
            System.out.println("ERROR: Expected a statement, found " + token);
            System.exit(2);
        }
        //end with SEMICOLON;
        Scanner.match("SEMICOLON");
    }

    public static void print (STMT stmt, int indent) {
        switch (stmt.getAltNo()) {
            case 1: 
                ASSIGN.print(stmt.getAssign());
                break;
            case 2: 
                IF.print(stmt.getIf(), indent);
                break;
            case 3: 
                LOOP.print(stmt.getLoop(), indent);
                break;
            case 4: 
                IN.print(stmt.getIn());
                break;
            case 5: 
                OUT.print(stmt.getOut());
                break;
            case 6: 
                CASE.print(stmt.getCase(), indent);
                break;
            default:
                break;
        }
        System.out.println(";");
    }

    public static void exec(STMT stmt) {
        switch (stmt.getAltNo()) {
            case 1:
                ASSIGN.exec(stmt.getAssign());
                break;
            case 2:
                IF.exec(stmt.getIf());
                break;
            case 3:
                LOOP.exec(stmt.getLoop());
                break;
            case 4:
                IN.exec(stmt.getIn());
                break;
            case 5:
                OUT.exec(stmt.getOut());
                break;
            case 6:
                CASE.exec(stmt.getCase());
                break;
            default:
                break;
        }
    }

    public int getAltNo() { return altNo; }
    public ASSIGN getAssign() { return s1; }
    public IF getIf() { return s2; }
    public LOOP getLoop() { return s3; }
    public IN getIn() { return s4; }
    public OUT getOut() { return s5; }
    public CASE getCase() { return s6; }
}

//Class for the ASSIGN node
class ASSIGN {

    private EXPR expr;
    private String value;

    public void parse() {
        value = Scanner.getID();
        Scanner.match("ASSIGN");
        expr = new EXPR(); expr.parse();
    }

    public static void print(ASSIGN assignStmt) {
        System.out.print(assignStmt.getValue() + ":=");
        EXPR.print(assignStmt.getExpr());
    }

    public static void exec(ASSIGN assignStmt) {
        String id = assignStmt.getValue();
        if (Parser.VARIABLES.containsKey(id)) {
            Parser.VARIABLES.put(id, EXPR.exec(assignStmt.getExpr()));
        } else {
            System.out.println("ERROR: variable id " + id + " has not been declared");
            System.exit(2);
        }
    }

    public EXPR getExpr() { return expr; }
    public String getValue() { return value; }
}

//Class for the IF node
class IF {

    private int altNo = 0;
    private COND cond;              // 0 = if <cond> then <stmtSeq>;
    private STMT_SEQ stmtSeq;       // 0 = if <cond> then <stmtSeq>;
    private STMT_SEQ elseStmtSeq;   // 1 = if <cond> then <stmtSeq> else <stmtSeq>;

    public void parse() {
        Scanner.match("IF");
        cond = new COND(); cond.parse();
        Scanner.match("THEN");
        stmtSeq = new STMT_SEQ(); stmtSeq.parse();
        String token = Scanner.currentToken();
        if (token.equals("ELSE")) {
            altNo = 1;
            Scanner.nextToken();
            elseStmtSeq = new STMT_SEQ(); elseStmtSeq.parse();
        }
        Scanner.match("ENDIF");
    }

    public static void print(IF ifStmt, int indent) {
        System.out.print("if");
        COND.print(ifStmt.getCond());
        System.out.println("then");
        STMT_SEQ.print(ifStmt.getStmtSeq(), indent + 1);
        if (ifStmt.getAltNo() == 1) {
            Parser.indent(indent);
            System.out.println("else");
            STMT_SEQ.print(ifStmt.getElseStmtSeq(), indent + 1);
        }
        Parser.indent(indent);
        System.out.print("endif");
    }

    public static void exec(IF ifStmt) {
        if (COND.exec(ifStmt.getCond())) {
            STMT_SEQ.exec(ifStmt.getStmtSeq());
        } else if (ifStmt.getAltNo() == 1) {
            STMT_SEQ.exec(ifStmt.getElseStmtSeq());
        }
    }

    public int getAltNo() { return altNo; }
    public COND getCond() { return cond; }
    public STMT_SEQ getStmtSeq() { return stmtSeq; }
    public STMT_SEQ getElseStmtSeq() { return elseStmtSeq; }
}

//Class for the LOOP node
class LOOP {

    private STMT_SEQ stmtSeq;
    private COND cond;

    public void parse() {
        Scanner.match("WHILE");
        cond = new COND(); cond.parse();
        Scanner.match("BEGIN");
        stmtSeq = new STMT_SEQ(); stmtSeq.parse();
        Scanner.match("ENDWHILE");
    }

    public static void print(LOOP loopStmt, int indent) {
        System.out.println("while");
        COND.print(loopStmt.getCond());
        System.out.print(" begin");
	STMT_SEQ.print(loopStmt.getStmtSeq(), indent + 1);
        Parser.indent(indent);
        System.out.print("endwhile");
    }

    public static void exec(LOOP loopStmt) {
        do {
            STMT_SEQ.exec(loopStmt.getStmtSeq());
        } while (COND.exec(loopStmt.getCond()));
    }

    public STMT_SEQ getStmtSeq() { return stmtSeq; }
    public COND getCond() { return cond; }
}

//Class for the IN node
class IN {

    private ID_LIST idList;

    public void parse() {
        Scanner.match("INPUT");
        idList = new ID_LIST(); idList.parse();
    }

    public static void print(IN inputStmt) {
        System.out.print("input ");
        ID_LIST.print(inputStmt.getIdList());
    }

    public static void exec(IN inputStmt) {
        ID_LIST idList = inputStmt.getIdList();
        Parser.setVarByInput(idList);

        while (idList.getAltNo() == 1) {
            idList = idList.getIdList();
            Parser.setVarByInput(idList);
        }
    }

    public ID_LIST getIdList() { return idList; }
}

//Class for the OUT node
class OUT {

    private ID_LIST idList;

    public void parse() {
        Scanner.match("OUTPUT");
        idList = new ID_LIST(); idList.parse();
    }

    public static void print(OUT outputStmt) {
        System.out.print("output ");
        ID_LIST.print(outputStmt.getIdList());
    }

    public static void exec(OUT outputStmt) {
        ID_LIST idList = outputStmt.getIdList();
        Parser.outputVar(idList);

        while (idList.getAltNo() == 1) {
            idList = idList.getIdList();
            Parser.outputVar(idList);
        }
    }

    public ID_LIST getIdList() { return idList; }
}

//Class for the COND node
class COND {

    private int altNo;
    private COND neg;   // 0 = !(<cond>);
    private COND sen;   // 0 = !(<cond>);
    private COND cond;  // 2 = <cmpr> or <cond>;
    private CMPR cmpr;  // 2 = <cmpr>;

    public void parse() {
        String token = Scanner.currentToken();
        if (token.equals("NOT")) {
            altNo = 0;
            Scanner.nextToken();
            neg = new COND(); neg.parse();
        } else if (token.equals("LEFT_PAREN")) {
            altNo = 1;
            Scanner.nextToken();
            sen = new COND(); sen.parse();
            Scanner.match("RIGHT_PAREN");
        } else {
            altNo = 2;
            cmpr = new CMPR(); cmpr.parse();
	    token = Scanner.currentToken();
	    if (token.equals("OR")) {
		Scanner.nextToken();
		cond = new COND(); cond.parse();
	    }
        }
    }

    public static void print(COND cond) {
        switch (cond.getAltNo()) {
            case 0:
                System.out.print("!");
                COND.print(cond.getNeg());
                break;
            case 1:
                System.out.print("(");
                COND.print(cond.getSen());
                System.out.print(")");
                break;
            case 2:
                CMPR.print(cond.getCmpr());
                break;
            default:
                break;
        }
    }

    public static Boolean exec(COND cond) {
        Boolean result = true;
        switch (cond.getAltNo()) {
            case 0:
                result = !COND.exec(cond.getNeg());
                break;
            case 2:
                result = CMPR.exec(cond.getCmpr());
                break;
            default:
                break;
        }
        return result;
    }

    public int getAltNo() { return altNo; }
    public CMPR getCmpr() { return cmpr; }
    public COND getNeg() { return neg; }
    public COND getSen() { return sen; }
    public COND getCond() {return cond; }
}

//Class for the CMPR node;
class CMPR {

    private CMPR_OP op;
    private EXPR expr1;
    private EXPR expr2;

    public void parse() {
        expr1 = new EXPR(); expr1.parse();
        op = new CMPR_OP(); op.parse();
        expr2 = new EXPR(); expr2.parse();
    }

    public static void print(CMPR cmpr) {
        EXPR.print(cmpr.getExpr1());
        CMPR_OP.print(cmpr.getOp());
        EXPR.print(cmpr.getExpr2());
    }

    public static Boolean exec(CMPR cmpr) {
        Boolean result = true;
        CMPR_OP cmprOp = cmpr.getOp();
        if (cmprOp.getOp().equals("EQUALS")) {
            result = (EXPR.exec(cmpr.getExpr1()) == EXPR.exec(cmpr.getExpr2()));
        } else if (cmprOp.getOp().equals("LESS_THAN")) {
            result = (EXPR.exec(cmpr.getExpr1()) < EXPR.exec(cmpr.getExpr2()));
        } else if (cmprOp.getOp().equals("LESS_EQUAL")) {
            result = (EXPR.exec(cmpr.getExpr1()) <= EXPR.exec(cmpr.getExpr2()));
	}
        return result;
    }

    public CMPR_OP getOp() { return op; }
    public EXPR getExpr1() { return expr1; }
    public EXPR getExpr2() { return expr2; }
}

//Class for the CMPR_OP node
class CMPR_OP {

    private String op;

    public void parse() {
        String token = Scanner.currentToken();
        if (token.equals("EQUALS") || token.equals("LESS_THAN") || token.equals("LESS_EQUAL")) {
            op = token;
        } else {
            System.out.println("ERROR: Expected a comparison operator, found " + token);
            System.exit(2);
        }
        Scanner.nextToken();
    }

    public static void print(CMPR_OP cmprOp) {
        if (cmprOp.getOp().equals("EQUALS")) {
            System.out.print("=");
        } else if (cmprOp.getOp().equals("LESS_THAN")) {
            System.out.print("<");
        } else if (cmprOp.getOp().equals("LESS_EQUAL")) {
            System.out.print("<=");
        }
    }

    public String getOp() { return op; }
}

//Class for the EXPR node
class EXPR {

    private int altNo = 0;  
    private TERM term;      // 0 = <term>;
    private EXPR expr;      // 1 = <term> op <expr>
    private String op;      // 1 = <term> op <expr>

    public void parse() {
        term = new TERM(); term.parse();
        String token = Scanner.currentToken();
        if (token.equals("PLUS") || token.equals("MINUS")) {
            altNo = 1;
            op = token;
            Scanner.nextToken();
            expr = new EXPR(); expr.parse();
        }
    }

    public static void print(EXPR expr) {
        TERM.print(expr.getTerm());
        if (expr.getAltNo() == 1) {
            if (expr.getOp().equals("PLUS")) {
                System.out.print("+");
            } else {
                System.out.print("-");
            }
            EXPR.print(expr.getExpr());
        }
    }

    public static int exec(EXPR expr) {
        int result = TERM.exec(expr.getTerm());
        if (expr.getAltNo() == 1) {
            if (expr.getOp().equals("PLUS")) {
                result += EXPR.exec(expr.getExpr());
            } else {
                result -= EXPR.exec(expr.getExpr());
            }
        }
        return result;
    }

    public int getAltNo() { return altNo; }
    public TERM getTerm() { return term; }
    public EXPR getExpr() { return expr; }
    public String getOp() { return op; }
}

//Class for the TERM node
class TERM {

    private int altNo = 0;  
    private FACTOR factor;  // 0 = <factor>;
    private TERM term;      // 1 = <factor> * <term>;

    public void parse() {
        factor = new FACTOR(); factor.parse();
        if (Scanner.currentToken().equals("TIMES")) {
            altNo = 1;
            Scanner.nextToken();
            term = new TERM(); term.parse();
        }
    }

    public static void print(TERM term) {
        FACTOR.print(term.getFactor());
        if (term.getAltNo() == 1) {
            System.out.print("*");
            TERM.print(term.getTerm());
        }
    }

    public static int exec(TERM term) {
        int result = FACTOR.exec(term.getFactor());
        if (term.getAltNo() == 1) {
            result *= TERM.exec(term.getTerm());
        }
        return result;
    }

    public int getAltNo() { return altNo; }
    public FACTOR getFactor() { return factor; }
    public TERM getTerm() { return term; }
}

//Class for the FACTOR node
class FACTOR {

    private int altNo;
    private int value;      // 0 = const;
    private String id;      // 1 = id;
    private FACTOR factor;  // 2 = -<factor>;
    private EXPR expr;      // 3 = (<expr>);

    public void parse() {
        String token = Scanner.currentToken();
        if (token.contains("CONST")) { // const;
            altNo = 0;
            value = Scanner.getConst();
        } else if (token.contains("ID")) { // id;
            altNo = 1;
            id = Scanner.getID();
        } else if (token.equals("MINUS")) { // -<factor>;
            altNo = 2;
            Scanner.nextToken();
            factor = new FACTOR(); factor.parse();
        } else if (token.equals("LEFT_PAREN")) { // (<expr>);
            altNo = 3;
            Scanner.match("LEFT_PAREN");
            expr = new EXPR(); expr.parse();
            Scanner.match("RIGHT_PAREN");
        }
    }

    public static void print(FACTOR factor) {
        switch (factor.getAltNo()) {
            case 0: // CONST;
                System.out.print(factor.getValue());
                break;
            case 1: // ID;
                System.out.print(factor.getId());
                break;
            case 2: // -<FACTOR>;
                System.out.print("-");
                FACTOR.print(factor.getFactor());
                break;
            case 3: // (EXPR);
                System.out.print("(");
                EXPR.print(factor.getExpr());
                System.out.print(")");
                break;
            default:
                break;
        }
    }

    public static int exec(FACTOR factor) {
        int result = 0;
        switch (factor.getAltNo()) {
            case 0: // CONST;
                result = factor.getValue();
                break;
            case 1: // ID;
                result = Parser.getValueById(factor.getId());
                break;
            case 2: // -FACTOR;
                result = -1*FACTOR.exec(factor.getFactor());
                break;
            case 3: // (EXPR);
                result = EXPR.exec(factor.getExpr());
                break;
            default:
                break;
        }
        return result;
    }

    public int getAltNo() { return altNo; }
    public int getValue() { return value; }
    public String getId() { return id; }
    public FACTOR getFactor() { return factor; }
    public EXPR getExpr() { return expr; }
}

//Class for the CASE node
class CASE {

    private String id;
    private CASES cases;

    public void parse() {
        Scanner.match("CASE");
        id = Scanner.getID();
        Scanner.match("OF");
        cases = new CASES(); cases.parse();
        Scanner.match("END");
    }

    public static void print(CASE case_stmt, int indent) {
        System.out.println("case " + case_stmt.getId() + " of");
        Parser.indent(indent + 1);
        CASES.print(case_stmt.getCases(), indent + 1);
        System.out.println();
        Parser.indent(indent);
        System.out.print("end");
    }

    public static void exec(CASE caseStmt) {
        String id = caseStmt.getId();
        int value = Parser.getValueById(id);
        CASES.exec(caseStmt.getCases(), id, value);
    }

    public String getId() { return id; }
    public CASES getCases() { return cases; }
}

//Class for the CASES node
class CASES {

    private int altNo = 0;   
    private INT_LIST intList;   // 0 = <intList> : <expr> BAR <cases>;
    private EXPR expr;          // 0 = <intList> : <expr> BAR <cases>;
    private EXPR elseExpr;      // 1 = <intList> : <expr> else <expr>;
    private CASES cases;        // 0 = <intList> : <expr> BAR <cases>;

    public void parse () {
        intList = new INT_LIST(); intList.parse();
        Scanner.match("COLON");
        expr = new EXPR(); expr.parse();
        if (Scanner.currentToken().equals("BAR")) {
            altNo = 1;
            Scanner.nextToken();
            cases = new CASES(); cases.parse();
        } else {
            Scanner.match("ELSE");
            elseExpr = new EXPR(); elseExpr.parse();
        }
    }

    public static void print(CASES cases, int indent) {
        INT_LIST.print(cases.getIntList());
        System.out.print(":");
        EXPR.print(cases.getExpr());
        System.out.println();
        Parser.indent(indent);
        if (cases.getAltNo() == 1) {
            System.out.print("|");
            CASES.print(cases.getCases(), indent);
        } else {
            System.out.print("else ");
            EXPR.print(cases.getElseExpr());
        }
    }

    public static void exec(CASES cases, String id, int value) {
        if (INT_LIST.exec(cases.getIntList(), value)) {
            Parser.VARIABLES.put(id, EXPR.exec(cases.getExpr()));
        } else if (cases.getAltNo() == 1) { 
            CASES.exec(cases.getCases(), id, value);
        } else {
            Parser.VARIABLES.put(id, EXPR.exec(cases.getElseExpr()));
        }
    }

    public int getAltNo() { return altNo; }
    public INT_LIST getIntList() { return intList; }
    public EXPR getExpr() { return expr; }
    public EXPR getElseExpr() { return elseExpr; }
    public CASES getCases() { return cases; }
}

//Class for the INT_LIST node
class INT_LIST {

    private int altNo = 0;
    private int value;          // 0 = int;
    private INT_LIST intList;   // 1 = int, <intList>;

    public void parse() {
        value = Scanner.getConst();

        if(Scanner.currentToken().equals("COMMA")) {
            altNo = 1;
            Scanner.nextToken();
            intList = new INT_LIST(); intList.parse();
        }
    }

    public int getAltNo() { return altNo; }
    public int getValue() { return value; }
    public INT_LIST getIntList() { return intList; }

    public static void print(INT_LIST intList) {
        System.out.print(intList.getValue());

        if (intList.getAltNo() == 1) {
            System.out.print(",");
            INT_LIST.print(intList.getIntList());
        }
    }

    public static Boolean exec(INT_LIST intList, int value) {
        Boolean result = false;
        if (intList.getValue() == value) {
            result = true;
        } else if (intList.getAltNo() == 1) {
            result = INT_LIST.exec(intList.getIntList(), value);
        }
        return result;
    }
}
