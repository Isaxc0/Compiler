# Compiler

Overall received 78/100 on this coursework, for module "Compilers and Computer Architecture". 

Task 1

Lexer. Takes a string containing a program. The lexer tokensises the string and outputs an array of tokens.

Task 2

Parser. Takes array of tokens and creates an AST. Also performs error checking against CFG:

      BLOCK → { ENE }
      ENE →	E | E; ENE
      E →	INT
        |	skip
        |	BLOCK
      INT →	(an integer)
     
Task 3

Code generator. Takes an AST as input and generates RISC-V code for the given AST prograM. Outputs string containg RISC-V code.


