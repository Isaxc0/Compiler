import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;


class LexicalException extends Exception {
    public String msg;
    public LexicalException ( String _msg ) { msg = _msg; } }

class Task1Exception extends Exception {
    public String msg;
    public Task1Exception ( String _msg ) { msg = _msg; } }

interface Lexer {
    public List<Token> lex ( String input )
            throws LexicalException, Task1Exception; }

class Task1 {
    public static Lexer create(){
        return new MyLexer();
    }
}
