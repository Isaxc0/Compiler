// Do not modify the code below except for replacing the "..."!  Don't
// add anything (including "public" declarations), don't remove
// anything (including "public" declarations). Don't wrap it in a
// package, don't make it an innner class of some other class.  If
// your IDE suggsts to change anything below, ignore your IDE. You are
// welcome to add new classes! Please put them into separate files.

class CodegenException extends Exception {
    public String msg;
    public CodegenException ( String _msg ) { msg = _msg; } }

interface Codegen {
    public String codegen ( Program p ) throws CodegenException; }

