// Do not modify the code below except for replacing the "..."!  Don't
// add anything (including "public" declarations), don't remove
// anything (including "public" declarations). Don't wrap it in a
// package, don't make it an innner class of some other class.  If
// your IDE suggsts to change anything below, ignore your IDE. You are
// welcome to add new classes! Please put them into separate files.

import java.util.List;
import java.util.ArrayList;
import static java.util.Arrays.asList;

class Program { // Used in Tasks 1, 2, 3.
    // Leaves in the accumulator what running the first declaration leaves in the accumulator.
    public List <Declaration> decls;
    public Program ( List <Declaration> _decls ) {
        assert ( _decls.size () > 0 ); // ensures that we have at least one declaration the
        // first element in decls is the the procedure executed
        // at startup. We assume that this initial procedure takes
        // 0 arguments. NOTE: the entry procedure does NOT have
        // have to be called "main". It can have ANY valid identifier
        // as name.
        assert ( _decls.get ( 0 ).numOfArgs == 0 ); // ensures that the first declaration takes
        // 0 arguments.
        decls = _decls; } }


class Declaration { // Used in Tasks 1, 2, 3.
    public String id;
    public int numOfArgs; // Used for computing the size of the AR.
    public List <Exp> body;

    public Declaration ( String _id, int _numOfargs, List <Exp> _body ) {
        assert ( _numOfargs >= 0 );
        id = _id;
        numOfArgs = _numOfargs;
        body = _body; } }


abstract class Exp {} // Used in Tasks 1, 2, 3.

class IntLiteral extends Exp { // Used in Tasks 1, 2, 3.
    // Leaves n in the accumulator.
    public int n;
    IntLiteral ( int _n ) { n = _n; } }

class Variable extends Exp { // Used in Tasks 1, 2, 3.
    // Leaves the content of the variables x in the accumulator.  Note
    // that the integer x represents the OFFSET of the variable relative
    // to the encompassing procedure declaration (which is used by the
    // code generator to compute, at compile time, the place, relative
    // to the frame pointer, where the variable is stored at
    // run-time). It does NOT hold the values the variable takes at
    // run-time. E.g. in
    //
    //      def f ( a, b, c, d ) = ...
    //
    // the variable a has offset 1, b has offset 2, c has 3 and d has
    // 4. There are no other offsets for this procedure.
    public int x;
    public Variable ( int _x ) {
        assert ( _x > 0 ); // ensures that 1 is the smallest possible offset.
        x = _x; } }

class If extends Exp { // Used in Tasks 1, 2, 3.
    // If the comparison is true, executes the thenBody.
    // Otherwise the elseBody is executed.
    public Exp l;
    public Comp comp;
    public Exp r;
    public Exp thenBody;
    public Exp elseBody;
    public If ( Exp _l, Comp _comp, Exp _r, Exp _thenBody, Exp _elseBody ) {
        l = _l;
        comp = _comp;
        r = _r;
        thenBody = _thenBody;
        elseBody = _elseBody; } }

class Binexp extends Exp { // Used in Tasks 1, 2, 3.
    // Evaluates l, then r, and applies binop to both.
    public Exp l;
    public Binop binop;
    public Exp r;
    public Binexp ( Exp _l, Binop _binop, Exp _r ) {
        l = _l;
        binop = _binop;
        r = _r; } }

class Invoke extends Exp { // Used in Tasks 1, 2, 3.
    // Evaluates the arguments right-to-left. Then calls the procedure name with
    // these arguments.
    public String name;
    public List<Exp> args;
    public Invoke ( String _name, List<Exp> _args ) {
        name = _name;
        args = _args; } }

class While extends Exp { // Used in Tasks 2, 3.
    // Implements the standard while-loop,
    // Can return anything in accumulator
    public Exp l;
    public Comp comp;
    public Exp r;
    public Exp body;
    public While ( Exp _l, Comp _comp, Exp _r, Exp _body ) {
        l = _l;
        comp = _comp;
        r = _r;
        body = _body; } }

class RepeatUntil extends Exp { // Used in Tasks 2, 3.
    // Implements the standard repeat-unitl loop,
    // Can return anything in accumulator.
    public Exp body;
    public Exp l;
    public Comp comp;
    public Exp r;
    public RepeatUntil ( Exp _body, Exp _l, Comp _comp, Exp _r ) {
        body = _body;
        l = _l;
        comp = _comp;
        r = _r; } }

class Assign extends Exp { // Used in Tasks 2, 3.
    // Evaluates the right-hand side e, and stores the result
    // Can return anything in accumulator.
    public int x;
    public Exp e;
    public Assign ( int _x, Exp _e ) {
        assert ( _x > 0 );
        x = _x;
        e = _e; } }

class Block extends Exp { // Used in Tasks 2, 3.
    // Evaluates a list of expressions.
    // Returns the final expression in the accumulator.
    public List <Exp> _expl;
    public Block ( List<Exp> expls ) {
        _expl = expls; } }

class Skip extends Exp { // Used in Tasks 2, 3.
    // Does nothing.
    // Can return anything in accumulator.
    public Skip () {} }

class Break extends Exp { // Used in Task 3 only.
    // Exits the nearest enclosing loop. Leaves
    // Can return anything in accumulator.
    public Break () {} }

class Continue extends Exp { // Used in Task 3 only.
    // Abandons the current iteration of the  nearest enclosing
    // loop, jumping straight to the checking of the conditional.
    // Can return anything in accumulator.
    public Continue () {} }


abstract class Comp {} // Used in Tasks 1, 2, 3.

class Less extends Comp { // Used in Tasks 2, 3.
    public Less () {} }

class LessEq extends Comp { // Used in Tasks 2, 3.
    public LessEq () {} }

class Equals extends Comp { // Used in Tasks 1, 2, 3.
    public Equals () {} }

class Greater extends Comp { // Used in Tasks 2, 3.
    public Greater () {} }

class GreaterEq extends Comp { // Used in Tasks 2, 3.
    public GreaterEq () {} }


abstract class Binop {}

class Plus extends Binop { // Used in Tasks 1, 2, 3.
    public Plus () {} }

class Minus extends Binop { // Used in Tasks 1, 2, 3.
    public Minus () {} }

class Times extends Binop { // Used in Tasks 2, 3.
    public Times () {} }

class Div extends Binop { // Used in Task 3 only.
    public Div () {} }
