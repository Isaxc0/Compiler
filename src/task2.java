// Do not modify the code below except for replacing the "..."!  Don't
// add anything (including "public" declarations), don't remove
// anything (including "public" declarations). Don't wrap it in a
// package, don't make it an innner class of some other class.  If
// your IDE suggsts to change anything below, ignore your IDE. You are
// welcome to add new classes! Please put them into separate files.

import java.util.List;
import java.util.ArrayList; //remove this

class SyntaxException extends Exception {
    public String msg;
    public SyntaxException ( String _msg ) { msg = _msg; } }

class Task2Exception extends Exception {
    public String msg;
    public Task2Exception ( String _msg ) { msg = _msg; } }

interface Parser {
    public Block parse ( List<Token> input ) throws SyntaxException, 
                                                    Task2Exception; }

class Task2 {
    public static Parser create() {
        return new MyParser();
    }
}