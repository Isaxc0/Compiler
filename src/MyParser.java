
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;


class MyParser implements Parser{
    Token prev; //previous token
    Integer left_count = 0; //left curly bracket counter
    Integer right_count = 0; //right curly bracket counter

    /**
     * Passes list of tokens to parseBLOCK and performs error checking
     *
     * @param input list of tokens
     * @return final_block ast of tokens
     */
    public Block parse ( List<Token> input ) throws SyntaxException, Task2Exception {
        try{
            List<Token> original = new ArrayList<>(input);
            List<Token> result = parseBLOCK(input); //parse list of tokens to parse block
            ArrayList<Token> unchecked = new ArrayList<>(original);
            unchecked.remove(unchecked.size()-1); //removes end curly bracket
            unchecked.remove(0); //removes front curly bracket

            //checks whether a left curly bracket is found before another right curly bracket
            boolean found = false;
            for (Token token: unchecked){
                if (token instanceof T_RightCurlyBracket && !found){
                    throw new SyntaxException("Error");
                }
                if (token instanceof T_LeftCurlyBracket){
                    found = true;
                }
            }
            //checks whether a right curly bracket is found before another left curly bracket
            Collections.reverse(unchecked);
            found = false;
            for (Token token: unchecked){
                if (token instanceof T_LeftCurlyBracket && !found){
                    throw new SyntaxException("Error");
                }
                if (token instanceof T_RightCurlyBracket){
                    found = true;
                }
            }

            //checks whether program is valid
            if (result.size() == 0 && left_count == right_count) {
                Block final_block = generate_final_block(original); //produces ast of tokens
                return final_block;
            }
            else{
                throw new SyntaxException("Error");
            }

        } catch (SyntaxException syn_exception) {
            throw new SyntaxException(syn_exception.msg);

        } catch (Exception task2_exception) {
            throw new Task2Exception(task2_exception.toString());
        }
    }


    /**
     * Generates ast from program list of tokens
     * Is only called if the input program is valid
     * Uses a front and end pointer to traverse list to find blocks
     *
     * @param input list of tokens
     * @return final_block ast of tokens
     */
    public Block generate_final_block(List<Token> input){
        List<Object> all = new ArrayList<>(input);
        List<Exp> exps = new ArrayList<Exp>();
        Integer start = 0; //front pointer
        Integer end = 0; //end pointer

        //loops until ast is finished
        while (all.size() != 1) {
            for (Object t : all) {
                if (t instanceof T_LeftCurlyBracket) {
                    start = all.indexOf(t); //front pointer set
                }
            }
            for (Object t : all.subList(start, all.size())) {
                if (t instanceof T_RightCurlyBracket) {
                    end = all.indexOf(t) + 1; //end pointer set
                    break;
                }
            }
            List<Object> removed = new ArrayList<>(all.subList(start, end)); //block to be removed
            all.removeAll(removed); //remove block
            List<Exp> new_all = new ArrayList<>();

            //generates exps for corresponding inputs
            for(Object r:removed){
                if (!(r instanceof T_LeftCurlyBracket) && !(r instanceof T_RightCurlyBracket)){
                    if(r instanceof T_Skip){
                        new_all.add(new Skip());
                    }
                    else if(r instanceof T_Integer){
                        new_all.add(new IntLiteral(((T_Integer) r).n));
                    }
                    else if(r instanceof ArrayList){
                        ArrayList<Exp> to_block = ArrayList.class.cast(r);
                        new_all.add(new BlockExp(new Block(to_block)));
                    }
                }

            }
            exps.add(new BlockExp(new Block(new_all)));
            all.add(start, new_all);
        }
        return new Block(List.class.cast(all.get(0)));
    }


    /**
     * CFG for block
     *
     * @param input list of tokens
     * @return input list of tokens with tokens "eaten"
     */
    public List<Token> parseBLOCK( List<Token> input ) throws SyntaxException, Task2Exception{
        try{
            if (input.isEmpty()){throw new SyntaxException("Error"); }
            else{
                if (input.get(0) instanceof T_LeftCurlyBracket || input.get(0) instanceof T_RightCurlyBracket){
                    prev = input.get(0);

                    //adds to curly bracket counters
                    if (input.get(0) instanceof T_LeftCurlyBracket){left_count++;}
                    if (input.get(0) instanceof T_RightCurlyBracket){right_count++;}
                    input.remove(input.get(0)); //eats token

                    //program is valid
                    if(input.isEmpty()){
                        return input;
                    }

                    //recursion for remaining tokens
                    input = parseENE(input);
                    input = parseBLOCK(input);
                }
                else{
                    throw new SyntaxException("Error");
                }
            }
            return input;
            }
        catch(SyntaxException e){
            throw new SyntaxException("Error");
        }
        catch(Exception e){
            throw new Task2Exception(e.toString());
        }
    }


    /**
     * CFG for ENE
     *
     * @param input list of tokens
     * @return input list of tokens with tokens "eaten"
     */
    public List<Token> parseENE( List<Token> input ) throws SyntaxException, Task2Exception{
        try{
            if (input.isEmpty()){throw new SyntaxException("Error"); }
            else{
                input = parseE(input);//recursion for remaining tokens
                Token head = input.get(0);
                if(head instanceof T_Semicolon){
                    if (prev instanceof T_LeftCurlyBracket || input.get(1) instanceof T_RightCurlyBracket){
                        throw new SyntaxException("Error");
                    }
                    input.remove(head); //eats token
                    prev = head; //previous token is set
                    input = parseENE(input);
                }
            }
            return input;
        }
        catch(SyntaxException e){
            throw new SyntaxException("Error");
        }
        catch(Exception e){
            throw new Task2Exception(e.toString());
        }
    }


    /**
     * CFG for E
     *
     * @param input list of tokens
     * @return input list of tokens with tokens "eaten"
     */
    public List<Token> parseE( List<Token> input ) throws SyntaxException, Task2Exception{
        try{
            if (input.isEmpty()){throw new SyntaxException("Error"); }
            else{
                Token head = input.get(0);

                //error checking for integer token
                if (head instanceof T_Integer){
                    input = parseINT(input); //passed to parseINT CFG
                    if (input.get(0) instanceof T_LeftCurlyBracket){
                        throw new SyntaxException("Error");
                    }
                    return input;
                }

                //error checking for skip token
                else if(head instanceof T_Skip){
                    if (input.get(1) instanceof T_LeftCurlyBracket || prev instanceof T_RightCurlyBracket){
                        throw new SyntaxException("Error");
                    }
                    input.remove(head); //token eaten
                    prev = head;
                    return input;
                }

                //error checking for unbalanced brackets
                else if(head instanceof T_LeftCurlyBracket){
                    if (prev instanceof T_RightCurlyBracket){
                        throw new SyntaxException("Error");
                    }
                    return input;
                }
                //error checking for unbalanced brackets
                else if(head instanceof T_RightCurlyBracket ){
                    if (prev instanceof T_LeftCurlyBracket){
                        throw new SyntaxException("Error");
                    }
                    return input;
                }

                //error checking for semicolons
                else if(head instanceof T_Semicolon ){
                    if (prev instanceof T_Semicolon){
                        throw new SyntaxException("Error");
                    }
                    return input;
                }
                else {
                    throw new SyntaxException("Error");
                }
            }
        }
        catch(SyntaxException e){
            throw new SyntaxException("Error");
        }
        catch(Exception e){
            throw new Task2Exception(e.toString());
        }
    }


    /**
     * CFG for INT
     *
     * @param input list of tokens
     * @return input list of tokens with tokens "eaten"
     */
    public List<Token> parseINT( List<Token> input ) throws SyntaxException, Task2Exception{
        try{
            if (input.isEmpty()){throw new SyntaxException("Error"); }
            else{
                if(prev instanceof T_RightCurlyBracket){
                    throw new SyntaxException("Error");
                }
                Token head = input.get(0);
                input.remove(head); //token eaten
                prev = head;
                return input;
            }
        }
        catch(SyntaxException e){
            throw new SyntaxException("Error");
        }
        catch(Exception e){
            throw new Task2Exception(e.toString());
        }
    }
}

