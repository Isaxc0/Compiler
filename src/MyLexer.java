import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * The MyLexer is a hand-written lexer that takes in a string
 * and returns a list with corresponding tokens.
 */
class MyLexer implements Lexer{
    //all special characters allowed in the specified program
    List<String> special = new ArrayList<String>(Arrays.asList(";","(",")","=","==","<",">","<=",">=",",","{","}",":=","+","*","-","/"));
    //patterns used in regex
    Pattern non_special_characters = Pattern.compile("[^A-Za-z0-9]");
    Pattern alpha = Pattern.compile("[^A-Za-z]");
    Pattern numeric = Pattern.compile("[^0-9]");

    /**
     * The lexer that takes in a string
     * and returns a list with corresponding tokens.
     * It also performs error checking.
     *
     * @param input String that needs to be tokenized
     * @return List<String> List containing tokens
     * @throws LexicalException is thrown when the start of an identifier is not a lowercase letter or
     *                          when an illegal character is found (not in specification)
     * @throws Task1Exception is thrown whenever any other error occurs
     */
    public List<Token> lex(String input) throws LexicalException, Task1Exception {
        List<String> separated = new ArrayList<String>();
        try {
            List<String> no_whitespace = new ArrayList<String>(Arrays.asList(input.split("\\s+")));
            for (String s : no_whitespace) {
                List<String> characters = new ArrayList<String>(Arrays.asList(s.split("")));
                for (String sep : longestMatch(s)) {
                    separated.add(sep);
                }
            }
            for (String s : separated) {
                List<String> characters = new ArrayList<String>(Arrays.asList(s.split("")));
                String first_character = characters.get(0);

                if (first_character.equals("_") || (first_character.equals(first_character.toUpperCase()) && numeric.matcher(s).find() && !non_special_characters.matcher(s).find())) {
                    throw new LexicalException(s + " token is not valid");
                }
                else if (!special.contains(s) && non_special_characters.matcher(s).find()) {
                    throw new LexicalException(s + " token is not valid");
                }
            }

        }catch(LexicalException e){
            throw new LexicalException(e.msg);
        } catch(Exception e){
            throw new Task1Exception(e.toString());
        }
        return tokenize(separated);
    }

    /**
     * Performs longest match on a given string.
     * No lexical error checking is performed, if there is an error it is caught in lex(String input)
     *
     * @param input String to be separately by longest match
     * @return List<String> List containing lexemes separately
     *
     */
    public List<String> longestMatch(String input){
        List<String> separated = new ArrayList<String>();

        //uses a two pointers to traverse the string
        Integer head_pointer = 0;
        Integer tail_pointer = input.length();

        //halts when longest match has finished
        while (head_pointer != tail_pointer){
            String current = input.substring(head_pointer, tail_pointer);

            //checks whether a special character is in the current string
            boolean contains_special = false;
            for(String c :current.split("")){
                if(special.contains(c)){
                    contains_special = true;
                }
            }

            /*
            Tests the current string to see whether it is a special character, integer or identifier/key word
            */

            //if current is an identifier or a key word
            if (numeric.matcher(current.substring(0,1)).find() && !contains_special && !current.contains(":")){
                separated.add(current);
                //corrects the pointers to point to the next part of the string
                head_pointer = head_pointer + current.length();
                tail_pointer = input.length();
            }
            //if current is an integer
            else if (!numeric.matcher(current).find() && alpha.matcher(current).find() && !contains_special){
                separated.add(current);
                //corrects the pointers to point to the next part of the string
                head_pointer = head_pointer + current.length();
                tail_pointer = input.length();
            }
            //if current is a special character
            else if (special.contains(current) || current.equals(":")){
                separated.add(current);
                //corrects the pointers to point to the next part of the string
                head_pointer = head_pointer + current.length();
                tail_pointer = input.length();
            }
            //if current is not valid
            else{
                tail_pointer --;
            }
        }
        return separated;
    }

    /**
     * Uses switch case statements to correctly identify tokens.
     *
     * @param lexemes List of strings containing all lexemes correctly separately as per the specification.
     * @return List<String> List containing tokens
     *
     */
    public List<Token> tokenize(List<String> lexemes){
        List<Token> tokens = new ArrayList<Token>();

        //performs switch case statements for each lexeme
        for (String l:lexemes) {
            switch (l) {
                case ("def"):
                    tokens.add(new T_Def());
                    break;
                case ("skip"):
                    tokens.add(new T_Skip());
                    break;
                case ("if"):
                    tokens.add(new T_If());
                    break;
                case ("then"):
                    tokens.add(new T_Then());
                    break;
                case ("else"):
                    tokens.add(new T_Else());
                    break;
                case ("while"):
                    tokens.add(new T_While());
                    break;
                case ("do"):
                    tokens.add(new T_Do());
                    break;
                case ("repeat"):
                    tokens.add(new T_Repeat());
                    break;
                case ("until"):
                    tokens.add(new T_Until());
                    break;
                case ("break"):
                    tokens.add(new T_Break());
                    break;
                case ("continue"):
                    tokens.add(new T_Continue());
                    break;
                case (";"):
                    tokens.add(new T_Semicolon());
                    break;
                case ("("):
                    tokens.add(new T_LeftBracket());
                    break;
                case (")"):
                    tokens.add(new T_RightBracket());
                    break;
                case ("="):
                    tokens.add(new T_EqualDefines());
                    break;
                case ("=="):
                    tokens.add(new T_Equal());
                    break;
                case ("<"):
                    tokens.add(new T_LessThan());
                    break;
                case (">"):
                    tokens.add(new T_GreaterThan());
                    break;
                case ("<="):
                    tokens.add(new T_LessEq());
                    break;
                case (">="):
                    tokens.add(new T_GreaterEq());
                    break;
                case (","):
                    tokens.add(new T_Comma());
                    break;
                case ("{"):
                    tokens.add(new T_LeftCurlyBracket());
                    break;
                case ("}"):
                    tokens.add(new T_RightCurlyBracket());
                    break;
                case (":="):
                    tokens.add(new T_Assign());
                    break;
                case ("+"):
                    tokens.add(new T_Plus());
                    break;
                case ("*"):
                    tokens.add(new T_Times());
                    break;
                case ("-"):
                    tokens.add(new T_Minus());
                    break;
                case ("/"):
                    tokens.add(new T_Div());
                    break;
                default:
                    //lexeme is an identifier
                    if(numeric.matcher(l).find()){
                        tokens.add(new T_Identifier(l));
                    }
                    //lexeme is an integer
                    else {
                        tokens.add(new T_Integer(Integer.parseInt(l)));
                    }
            }
        }
        return tokens;
    }
}
