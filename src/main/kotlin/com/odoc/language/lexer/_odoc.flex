package com.odoc.language.lexer;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.odoc.lang.OdocTypes;

import static com.intellij.psi.TokenType.BAD_CHARACTER;
import static com.intellij.psi.TokenType.WHITE_SPACE;

@SuppressWarnings("ALL")
%%

%unicode
%public
%class _OdocLexer
%implements FlexLexer
%function advance
%type IElementType
%eof{  return;
%eof}

%{
  private int tokenStartIndex;
  private int codeDepth;
  private IElementType tag;

   public _OdocLexer() {
      this((java.io.Reader)null);
  }

  // Store the start index of a token
  private void tokenStart() {
    tokenStartIndex = zzStartRead;
  }

  // Set the start index of the token to the stored index
  private void tokenEnd() {
    zzStartRead = tokenStartIndex;
  }
%}

EOL=\n|\r|\r\n
WHITE_SPACE_CHAR=[\ \t\f]
WHITE_SPACE={WHITE_SPACE_CHAR}+
DIGIT=[0-9]
DIGITS={DIGIT}+
TAG_CHARACTER = [a-zA-Z]
INPUT_CHARACTER = [^\r\n\ \t\f\[\{\}\*]

%state INITIAL
%state IN_CODE
%state IN_PRE
%state IN_MARKUP

%%

<YYINITIAL>  {
    [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    "(**"                         { return OdocTypes.COMMENT_START; }
    "*)"                          { return OdocTypes.COMMENT_END; }
    "["                           { yybegin(IN_CODE); codeDepth = 1; tokenStart(); }
    "{{:"                         { return OdocTypes.LINK_START; }
    "{["                          { yybegin(IN_PRE); tokenStart(); }
    "{b"                          { yybegin(IN_MARKUP); tag = OdocTypes.BOLD; tokenStart(); }
    "{i"                          { yybegin(IN_MARKUP); tag = OdocTypes.ITALIC; tokenStart(); }
    "{e"                          { yybegin(IN_MARKUP); tag = OdocTypes.EMPHASIS; tokenStart(); }
    "{!"                          { yybegin(IN_MARKUP); tag = OdocTypes.CROSS_REF; tokenStart(); }
    "{ol" {WHITE_SPACE}*          { return OdocTypes.O_LIST; }
    "{ul" {WHITE_SPACE}*          { return OdocTypes.U_LIST; }
    "{-" {WHITE_SPACE}*           { return OdocTypes.LIST_ITEM_START; }
    "{" {DIGITS} {WHITE_SPACE}*   { return OdocTypes.SECTION; }
    ":"                           { return OdocTypes.COLON; }
    "}"                           { return OdocTypes.RBRACE; }
    "@" {TAG_CHARACTER}+          { return OdocTypes.TAG; }
    {WHITE_SPACE}                 { return WHITE_SPACE; }
    {EOL}                         { return OdocTypes.NEW_LINE; }
    {INPUT_CHARACTER}+            { return OdocTypes.ATOM; }
}

<IN_CODE> {
   "["           { codeDepth += 1; }
   "]"           { codeDepth -= 1; if (codeDepth == 0) { yybegin(INITIAL); tokenEnd(); return OdocTypes.CODE; } }
   . | {EOL}     { }
   <<EOF>>       { yybegin(INITIAL); tokenEnd(); return OdocTypes.CODE; }
}

<IN_PRE> {
   "]}"          { yybegin(INITIAL); tokenEnd(); return OdocTypes.PRE; }
   . | {EOL}     { }
   <<EOF>>       { yybegin(INITIAL); tokenEnd(); return OdocTypes.PRE; }
}

<IN_MARKUP> {
   "}"           { yybegin(INITIAL); tokenEnd(); return tag; }
   . | {EOL}     { }
   <<EOF>>       { yybegin(INITIAL); tokenEnd(); return tag; }
}

[^] { return BAD_CHARACTER; }
