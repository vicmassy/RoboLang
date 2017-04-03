parser grammar TParser;

options {

    // Default language but name it anyway
    //
    language  = Java;

    // Produce an AST
    //
    output    = AST;

    // Use a superclass to implement all helper
    // methods, instance variables and overrides
    // of ANTLR default methods, such as error
    // handling.
    //
    superClass = AbstractTParser;

    // Use the vocabulary generated by the accompanying
    // lexer. Maven knows how to work out the relationship
    // between the lexer and parser and will build the 
    // lexer before the parser. It will also rebuild the
    // parser if the lexer changes.
    //
    tokenVocab = TLexer;
}

// Some imaginary tokens for tree rewrites
//
tokens {
    LIST_INSTRUCTIONS;
    FUNCTION;
    PARAMS;
    COND;
}

// What package should the generated source exist in?
//
@header {

    package me.pauarge.robolang;
}

prog        :   list_instr -> ^(LIST_INSTRUCTIONS list_instr);

list_instr  :   (instr SMICLN!)+ ;

instr       :   while
            |   if
            |   assign
            //|   func
            ;

assign      :   VAR ASSIGN^ expr;

cond        :   if elseif else? -> ^(COND if elseif else?);

if          :   IF^ LPAR! expr RPAR! LBRA! list_instr RBRA! ;

elseif      :   (ELIF^ LPAR! expr RPAR! LBRA! list_instr RBRA!)* ;

else        :   ELSE^ LBRA! list_instr RBRA! ;

while       :   WHILE^ LPAR! expr RPAR! LBRA! list_instr RBRA! ;

expr        :   boolterm (OR^ boolterm)* ;

boolterm    :   boolfact (AND^ boolfact)* ;

boolfact    :   num_expr ((EQUAL^ | NOT_EQUAL^ | LT^ | LE^ | GT^ | GE^) num_expr)? ;

num_expr    :   term ( (ADD^ | SUB^) term)* ;

term        :   factor ( (TIMES^ | DIV^ | MOD^) factor)* ;

factor      :   (ADD^ | SUB^)? atom ;

atom        :   VAR
            |   funcall
            |   NUM
            |   DOLLAR^ VAR
            ;

funcall     :   VAR LPAR expr_list RPAR -> ^(FUNCTION VAR ^(PARAMS expr_list));
expr_list   :   expr (COMMA! expr)* ;
