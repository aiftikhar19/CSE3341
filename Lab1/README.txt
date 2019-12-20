Aisha Iftikhar
CORE Interpreter Project

Files:
/src
    Main.java:		main
    Scanner.java: 	generates token stream
    Tokenizer.java: 	data extraction from input files
    Parser.java: 	generates parse tree & defines classes for nodes; classes contain parse, print and execute functions 

Instructions:
Inside the src folder containing .java files, run the following command:

	javac Tokenizer.java Scanner.java Parser.java Main.java

This will generate the class objects for each file
Then, to run the program:

	java Main <code file> <data file> > <output file>

To check for differences between output and expected, use:

	diff <output file> <expected file>


