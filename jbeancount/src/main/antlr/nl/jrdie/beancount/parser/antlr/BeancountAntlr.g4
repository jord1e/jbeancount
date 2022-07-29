grammar BeancountAntlr;

tokens { INDENT , DEDENT }
@ header
{
package nl.jrdie.beancount.parser.antlr;
import com.yuvalshavit.antlr4.DenterHelper;
}
@ lexer :: members
{
private final DenterHelper denter = new DenterHelper(BeancountAntlrParser.EOL,
    BeancountAntlrParser.INDENT,
    BeancountAntlrParser.DEDENT,
    BeancountAntlrParser.COMMENT) {
  @Override
  protected Token pullToken() {
    return BeancountAntlrLexer.super.nextToken();
  }
};

@Override
public Token nextToken() {
  return denter.nextToken();
}
}
journal
   : declarations? EOF
   ;

declarations
   : decs += declaration+
   ;

declaration
   : d = directive
   | p = pragma
   | c = COMMENT
   | e = EOL
   ;

directive
   : transaction
   | price
   | balance
   | open
   | close
   | commodity
   | pad
   | document
   | note
   | event
   | query
   | custom
   ;

pragma
   : include
   | pushtag
   | poptag
   // | pushmeta
   
   // | popmeta
   | option
   | plugin
   ;
   // Pragmas
   
pushtag
   : PUSHTAG TAG c = COMMENT? EOL
   ;

poptag
   : POPTAG TAG c = COMMENT? EOL
   ;
   //pushmeta : POPTAG KEY COLON meta_value EOL;
   
   //popmeta : POPMETA KEY COLON EOL;
   
option
   : OPTION name = STRING value = STRING c = COMMENT? EOL
   ;
   //option : option_unary | option_binary;
   
   //option_unary : OPTIONS STRING EOL;
   
   //option_binary : OPTION STRING STRING EOL;
   
include
   : INCLUDE filename = STRING c = COMMENT? EOL
   ;

plugin
   : PLUGIN name = STRING config = STRING? c = COMMENT? EOL
   ;
   // Directives
   
transaction
   : tl = transactionLine
   | tl = transactionLine INDENT m = metadata pl = postingList DEDENT
   ;

transactionLine
   : date = DATE flag = txn pn = payeeNarration? tagsAndLinks c = COMMENT? EOL
   ;

price
   : date = DATE PRICE c = CURRENCY a = amount tagsAndLinks comment = COMMENT? EOL m = indentedMetadata
   ;

balance
   : date = DATE BALANCE account amountWithTolerance tagsAndLinks c = COMMENT? EOL m = indentedMetadata
   ;

open
   : date = DATE OPEN a = account cl = commodityList? bm = bookingMethod? tagsAndLinks c = COMMENT? EOL m = indentedMetadata
   ;

close
   : date = DATE CLOSE a = account tagsAndLinks c = COMMENT? EOL m = indentedMetadata
   ;

commodity
   : date = DATE COMMODITY c = CURRENCY tagsAndLinks comment = COMMENT? EOL m = indentedMetadata
   ;

pad
   : date = DATE PAD sourceAccount = account targetAccount = account tagsAndLinks c = COMMENT? EOL m = indentedMetadata
   ;

document
   : date = DATE DOCUMENT a = account filename = STRING tagsAndLinks c = COMMENT? EOL m = indentedMetadata
   ;

note
   : date = DATE NOTE account noteComment = STRING tagsAndLinks c = COMMENT? EOL m = indentedMetadata
   ;

event
   : date = DATE EVENT type = STRING description = STRING tagsAndLinks c = COMMENT? EOL m = indentedMetadata
   ;

query
   : date = DATE QUERY name = STRING sql = STRING tagsAndLinks c = COMMENT? EOL m = indentedMetadata
   ;

custom
   : date = DATE CUSTOM name = STRING mvl = customValueList tagsAndLinks c = COMMENT? EOL m = indentedMetadata
   ;
   // Metadata
   
indentedMetadata
   : INDENT m = metadata DEDENT
   |
   ;

metadata
   : ml += metadataLine*
   ;

metadataLine
   : key = KEY mv = metadataValue? EOL
   | t = TAG EOL
   | l = LINK EOL
   | c = COMMENT EOL
   ;

simpleValue
   : s = STRING
   | c = CURRENCY
   | a = account
   | t = TAG
   | l = LINK
   | d = DATE
   | b = BOOL
   | n = NULL
   | e = expr
   ;

metadataValue
   : sv = simpleValue
   | a = amount
   ;

customValueList
   : v += simpleValue*
   ;

tagsAndLinks
   : (TAG | LINK)*
   ;
   // String matchers
   
bookingMethod
   : s = STRING
   ;

payeeNarration
   : payee = STRING PIPE? narration = STRING
   | narration = STRING
   ;

account
   : ACCOUNT
   ;

commodityList
   : c += CURRENCY (COMMA c += CURRENCY)*
   ;
   // Arithmetic expressions
   
expr
   : c = NUMBER # Constant
   | l = expr PLUS r = expr # Addition
   | l = expr MINUS r = expr # Subtraction
   | l = expr ASTERISK r = expr # Multiplication
   | l = expr SLASH r = expr # Division
   | MINUS e = expr # Negation
   | PLUS e = expr # Plus
   | LPAREN e = expr RPAREN # Parenthesised
   ;
   // Single-character flags & other constants
   
txn
   : TXN # TxnFlag
   | FLAG # Flag
   | ASTERISK # Asterisk
   | HASH # Hash
   ;

optFlag
   : ASTERISK
   | FLAG
   | HASH
   |
   ;
   // Postings
   
postingList
   : pm += postingWithMetadata+
   ;

postingWithMetadata
   : p = posting m = indentedMetadata
   ;

posting
   : flag = optFlag a = account e = expr? c = CURRENCY? cs = costSpec? comment = COMMENT? EOL
   | flag = optFlag a = account e = expr? c = CURRENCY? cs = costSpec? at = AT pa = priceAnnotation comment = COMMENT? EOL
   | flag = optFlag a = account e = expr? c = CURRENCY? cs = costSpec? atat = ATAT pa = priceAnnotation comment = COMMENT? EOL
   | flag = optFlag a = account c = COMMENT? EOL
   | comment = COMMENT EOL
   ;

priceAnnotation
   : e = expr? c = CURRENCY?
   ;
   // Amounts & costs
   
amount
   : e = expr c = CURRENCY
   ;

amountWithTolerance
   : a = amount
   | e = expr TILDE tolerance = NUMBER c = CURRENCY
   ;

compoundAmount
   : c = CURRENCY
   | ce = compoundExpr c = CURRENCY?
   ;

compoundExpr
   : e = expr
   | l = expr? HASH r = expr?
   ;

costSpec
   : LCURL ccl = costCompList? RCURL # IndividualCostSpec
   | LCURLLCURL ccl = costCompList? RCURLRCURL # DoubleCostSpec
   ;

costCompList
   : cc += costComp (COMMA cc += costComp)*
   ;

costComp
   : ca = compoundAmount
   | date = DATE
   | s = STRING
   // | ASTERISK Not implemented? https://beancount.github.io/docs/a_proposal_for_an_improvement_on_inventory_booking.html#cost-basis-adjustments
   
   ;
   // Tokens
   
TRUE
   : 'TRUE'
   ;

FALSE
   : 'FALSE'
   ;

NULL
   : 'NULL'
   ; // TODO Rename to NONE : 'NULL'?
   
BOOL
   : TRUE
   | FALSE
   ;

INCLUDE
   : 'include'
   ;

PUSHTAG
   : 'pushtag'
   ;

POPTAG
   : 'poptag'
   ;

PUSHMETA
   : 'pushmeta'
   ;

POPMETA
   : 'popmeta'
   ;

OPTION
   : 'option'
   ;

OPTIONS
   : 'options'
   ;

PLUGIN
   : 'plugin'
   ;

TXN
   : 'txn'
   ;

BALANCE
   : 'balance'
   ;

OPEN
   : 'open'
   ;

CLOSE
   : 'close'
   ;

COMMODITY
   : 'commodity'
   ;

PAD
   : 'pad'
   ;

EVENT
   : 'event'
   ;

PRICE
   : 'price'
   ;

NOTE
   : 'note'
   ;

DOCUMENT
   : 'document'
   ;

QUERY
   : 'query'
   ;

CUSTOM
   : 'custom'
   ;

PIPE
   : '|'
   ;

ATAT
   : '@@'
   ;

AT
   : '@'
   ;

LCURLLCURL
   : '{{'
   ;

RCURLRCURL
   : '}}'
   ;

LCURL
   : '{'
   ;

RCURL
   : '}'
   ;

COMMA
   : ','
   ;

TILDE
   : '~'
   ;

HASH
   : '#'
   ;

ASTERISK
   : '*'
   ;

SLASH
   : '/'
   ;

COLON
   : ':'
   ;

PLUS
   : '+'
   ;

MINUS
   : '-'
   ;

LPAREN
   : '('
   ;

RPAREN
   : ')'
   ;

SEMICOLON
   : ';'
   ;

DOT
   : '.'
   ;

FLAG
   : '!'
   | '&'
   | '#'
   | '?'
   | '%'
   ;

DATE
   : [0-9] [0-9] [0-9] [0-9] '-' [0-9] [0-9] '-' [0-9] [0-9]
   | [0-9] [0-9] [0-9] [0-9] '/' [0-9] [0-9] '/' [0-9] [0-9]
   ;

STRING
   : '"' (~ ["\\] | '\\' .)* '"'
   ;

KEY
   : [a-z] ([A-Za-z0-9] | '-' | '_')+ COLON
   ;

fragment TagOrLinkIdentifier
   : ([A-Za-z0-9] | '-' | '_' | '/' | '.')+
   ;

TAG
   : '#' TagOrLinkIdentifier
   ;

LINK
   : '^' TagOrLinkIdentifier
   ;

fragment CurrencyCharset
   : ([A-Z0-9] | '.' | '_' | '-' | '\'')
   ;

CURRENCY
   : [A-Z] CurrencyCharset* [A-Z0-9]?
   | '/' CurrencyCharset* [A-Z] (CurrencyCharset* [A-Z0-9])?
   ;

fragment AccountType
   : [\p{Lu}\p{Lo}] [\p{L}\p{N}\-]*
   ;

fragment AccountName
   : [\p{Lu}\p{Lo}\p{N}] [\p{L}\p{N}\-]*
   ;

ACCOUNT
   : AccountType (COLON AccountName)+
   ;

NUMBER
   : ([0-9]+ | [0-9] [0-9,]+ [0-9]) (DOT [0-9]*)?
   ;

COMMENT
   : SEMICOLON .*? EOL
   ;
   // Trailing whitespace required for INDENT and DEDENT
   
EOL
   : ('\r' '\n' | '\n' | '\r') ' '*
   ;

WS
   : [ \t]+ -> skip
   ;

