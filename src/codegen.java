import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

class Codegenerator implements Codegen {
    List<String> output = new ArrayList<String>(); //list of  risc-v commands


    /**
     * Uses recursion to create risc-v program
     *
     * @param p Program
     * @return String Final string containing all risc-v commands separated by new lines
     *
     */
    public String codegen(Program p) throws CodegenException {
        try {
            genProg(p); //generates risc-v program

            String final_string = "";
            //adds strings together and adds new lines to each command
            for (String o : output) {
                final_string += o + "\n";
            }
            return final_string;

        } catch (CodegenException e) {
            throw e;
        }
    }


    /**
     * Passes each declaration to genDecl
     *
     * @param p Program
     *
     */
    public void genProg(Program p) throws CodegenException{
        try {
            for (Declaration d : p.decls) {
                genDecl(d);
            }
        }
        catch (Exception e){
            throw new CodegenException("Error");
        }
    }


    /**
     * Passes each declaration body to codegenBlock.
     * Adds all commands for declaring a procedure
     *
     * @param d declaration
     *
     */
    public void genDecl(Declaration d) throws CodegenException {
        try {
            Integer AR_size = (d.numOfArgs + 2) * 4;
            output.add(String.format("%s_entry:", d.id)); //name of label is name of function
            output.add("mv x8 sp"); //stack pointer is set to frame pointer (points to AR)
            output.add("sw ra 0(sp)"); //return address is on stack
            output.add("addi sp sp -4");
            codegenBlock(d.body); //generate risc-v commands for the body
            output.add("lw ra 4(sp)"); //return address loaded
            output.add(String.format(("addi sp sp %d"), AR_size)); //pop entire AR
            output.add("lw x8 0(sp)"); //FP set back to
            output.add("jr ra");
        }
        catch (Exception e){
            throw new CodegenException("Error");
        }
    }


    /**
     * Passes each declaration body to codegenBlock.
     * Adds all commands for declaring a function
     *
     * @param blocks list containing all initial blocks in procedure
     *
     */
    public void codegenBlock(List<Exp> blocks) throws CodegenException{
        try {
            for (Exp b : blocks) { //iterates over each block
                List<Exp> exps = ((Block) b)._expl; //gets list of expressions
                for (Exp exp : exps) {
                    codegenExp(exp); //generates risc-v commands for expressions in block
                }
            }
        }
        catch (Exception e){
            throw new CodegenException("Error");
        }
    }


    /**
     * Uses recursion to add risc-v commands to the output attribute.
     *
     * @param e expression
     *
     */
    public void codegenExp(Exp e) throws CodegenException{
        try {
            //if expression is an integer
            if (e instanceof IntLiteral) {
                output.add(String.format("li a0 %d", ((IntLiteral) e).n));
            }

            //if expression is calling a procedure
            else if (e instanceof Invoke) {
                output.add("sw x8 0(sp)");
                output.add("addi sp sp -4");
                //right to left order
                List<Exp> args = ((Invoke) e).args;
                for (int i = args.size() - 1; i >= 0; i--) {
                    codegenExp(args.get(i));
                    output.add("sw a0 0(sp)");
                    output.add("addi sp sp -4");
                }
                output.add(String.format("jal %s_entry", ((Invoke) e).name));
            }

            //if expression is a variable
            else if (e instanceof Variable) {
                Integer offset = (((Variable) e).x) * 4; //generates offset
                output.add(String.format("lw a0 %d(x8)", offset)); //creates variable at offset
            }

            //if expression is a block
            else if (e instanceof Block) {
                //calls codegenBlock which handles the expression being a block
                codegenBlock(new ArrayList<Exp>(Arrays.asList((Block) e)));
            }

            //if expression is assigning a value to a variable
            else if (e instanceof Assign) {
                Assign assign = ((Assign) e);
                Integer offset = assign.x * 4; //offset value generated
                codegenExp(assign.e);
                output.add(String.format("sq a0 %d(x8)", offset)); //value set to variable
            }

            //if expression is a while loop
            else if (e instanceof While) {
                While while_do = ((While) e);
                output.add("loop:");//creates loop label
                codegenExp(while_do.l);//recursion for left hand side
                output.add("sw a0 0(sp)");
                output.add("addi sp sp -4");
                codegenExp(while_do.r);//recursion for right hand side
                output.add("lw t1 4(sp)");
                output.add("addi sp sp 4");
                codegenComp(while_do.comp);//recursion for comparator
                output.add("b exit");//branches to exit label
                output.add("then:");//creates then label
                codegenExp(while_do.body);//recursion for body of while loop
                output.add("b loop");//branches to loop label
                output.add("exit:");//creates exit label
            }

            //if expression is repeat until loop
            else if (e instanceof RepeatUntil) {
                RepeatUntil repeat = ((RepeatUntil) e);
                output.add("loop:");
                codegenExp(repeat.body); //recursion for body of repeat until
                codegenExp(repeat.l);
                output.add("sw a0 0(sp)");
                output.add("addi sp sp -4");
                codegenExp(repeat.r);
                output.add("lw t1 4(sp)");
                output.add("addi sp sp 4");
                codegenComp(repeat.comp);
                output.add("b loop");
                output.add("then:");
                output.add("b exit");
                output.add("exit:");
            }

            //if expression is an if statement
            else if (e instanceof If) {
                If conditional = ((If) e);
                codegenExp(conditional.l);
                output.add("sw a0 0(sp)");
                output.add("addi sp sp -4");
                codegenExp(conditional.r);
                output.add("lw t1 4(sp)");
                output.add("addi sp sp 4");
                codegenComp(conditional.comp);
                output.add("else:");
                codegenExp(conditional.elseBody);
                output.add("b exit");
                output.add("then:");
                codegenExp(conditional.thenBody);
                output.add("exit:");
            }

            //if expression is either +,-,*,/
            else if (e instanceof Binexp) {
                Binexp binexp = ((Binexp) e);

                //if +
                if (binexp.binop instanceof Plus) {
                    codegenExp(binexp.l);
                    output.add("sw a0 0(sp)");
                    output.add("addi sp sp -4");
                    codegenExp(binexp.r);
                    output.add("lw t1 4(sp)");
                    output.add("add a0 t1 a0");
                    output.add("addi sp sp 4");
                }

                //if -
                if (binexp.binop instanceof Minus) {
                    codegenExp(binexp.l);
                    output.add("sw a0 0(sp)");
                    output.add("addi sp sp -4");
                    codegenExp(binexp.r);
                    output.add("lw t1 4(sp)");
                    output.add("sub a0 t1 a0");
                    output.add("addi sp sp 4");
                }

                //if *
                if (binexp.binop instanceof Times) {
                    codegenExp(binexp.l);
                    output.add("sw a0 0(sp)");
                    output.add("addi sp sp -4");
                    codegenExp(binexp.r);
                    output.add("lw t1 4(sp)");
                    output.add("mul a0 t1 a0");
                    output.add("addi sp sp 4");
                }

                //if /
                if (binexp.binop instanceof Div) {
                    codegenExp(binexp.l);
                    output.add("sw a0 0(sp)");
                    output.add("addi sp sp -4");
                    codegenExp(binexp.r);
                    output.add("lw t1 4(sp)");
                    output.add("div a0 t1 a0");
                    output.add("addi sp sp 4");
                }
            }

            //if expression is a continue statement
            else if (e instanceof Continue) {
                output.add("b loop"); //branches to loop label
            }

            //if expression is a break statement
            else if (e instanceof Break) {
                output.add("b exit"); //branches to exit label
            }

            //if expression is a skip statement
            else if (e instanceof Skip) {
                output.add("addi x0 x0 0"); //NOP, does nothing
            }
        }
        catch (Exception exc){
            throw new CodegenException("Error");
        }
    }


    /**
     * Adds risc-v commands to the output attribute which are comparators.
     *
     * @param c comparators
     *
     */
    public void codegenComp(Comp c) throws CodegenException{
        try {
            if (c instanceof Equals) {
                output.add("beq a0 t1 then");
            }
            if (c instanceof Less) {
                output.add("blt t1 a0 then");
            }
            if (c instanceof LessEq) {
                output.add("ble t1 a0 then");
            }
            if (c instanceof Greater) {
                output.add("bgt t1 a0 then");
            }
            if (c instanceof GreaterEq) {
                output.add("bge t1 a0 then");
            }
        }
        catch(Exception e){
            throw new CodegenException("Error");
        }
    }
}

class Task3 {
    public static Codegen create() throws CodegenException {
        try {
            return new Codegenerator();
        } catch (Exception e) {
            throw new CodegenException(((CodegenException) e).msg);
        }
    }
}


