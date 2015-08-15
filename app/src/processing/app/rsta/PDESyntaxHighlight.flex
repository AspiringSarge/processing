/*
 * Generated on 8/15/15 3:13 AM
 */
package processing.app.rsta;

import java.io.*;
import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.*;


/**
 * 
 */
%%

%public
%class PDESyntaxHighlight
%extends AbstractJFlexCTokenMaker
%unicode
/* Case sensitive */
%type org.fife.ui.rsyntaxtextarea.Token


%{


	/**
	 * Constructor.  This must be here because JFlex does not generate a
	 * no-parameter constructor.
	 */
	public PDESyntaxHighlight() {
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 * @see #addToken(int, int, int)
	 */
	private void addHyperlinkToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, true);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 */
	private void addToken(int tokenType) {
		addToken(zzStartRead, zzMarkedPos-1, tokenType);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param tokenType The token's type.
	 * @see #addHyperlinkToken(int, int, int)
	 */
	private void addToken(int start, int end, int tokenType) {
		int so = start + offsetShift;
		addToken(zzBuffer, start,end, tokenType, so, false);
	}


	/**
	 * Adds the token specified to the current linked list of tokens.
	 *
	 * @param array The character array.
	 * @param start The starting offset in the array.
	 * @param end The ending offset in the array.
	 * @param tokenType The token's type.
	 * @param startOffset The offset in the document at which this token
	 *        occurs.
	 * @param hyperlink Whether this token is a hyperlink.
	 */
	public void addToken(char[] array, int start, int end, int tokenType,
						int startOffset, boolean hyperlink) {
		super.addToken(array, start,end, tokenType, startOffset, hyperlink);
		zzStartRead = zzMarkedPos;
	}


	/**
	 * {@inheritDoc}
	 */
	public String[] getLineCommentStartAndEnd(int languageIndex) {
		return new String[] { "//", null };
	}


	/**
	 * Returns the first token in the linked list of tokens generated
	 * from <code>text</code>.  This method must be implemented by
	 * subclasses so they can correctly implement syntax highlighting.
	 *
	 * @param text The text from which to get tokens.
	 * @param initialTokenType The token type we should start with.
	 * @param startOffset The offset into the document at which
	 *        <code>text</code> starts.
	 * @return The first <code>Token</code> in a linked list representing
	 *         the syntax highlighted text.
	 */
	public Token getTokenList(Segment text, int initialTokenType, int startOffset) {

		resetTokenList();
		this.offsetShift = -text.offset + startOffset;

		// Start off in the proper state.
		int state = Token.NULL;
		switch (initialTokenType) {
						case Token.COMMENT_MULTILINE:
				state = MLC;
				start = text.offset;
				break;

						case Token.COMMENT_DOCUMENTATION:
				state = DOCCOMMENT;
				start = text.offset;
				break;

			default:
				state = Token.NULL;
		}

		s = text;
		try {
			yyreset(zzReader);
			yybegin(state);
			return yylex();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return new TokenImpl();
		}

	}


	/**
	 * Refills the input buffer.
	 *
	 * @return      <code>true</code> if EOF was reached, otherwise
	 *              <code>false</code>.
	 */
	private boolean zzRefill() {
		return zzCurrentPos>=s.offset+s.count;
	}


	/**
	 * Resets the scanner to read from a new input stream.
	 * Does not close the old reader.
	 *
	 * All internal variables are reset, the old input stream 
	 * <b>cannot</b> be reused (internal buffer is discarded and lost).
	 * Lexical state is set to <tt>YY_INITIAL</tt>.
	 *
	 * @param reader   the new input stream 
	 */
	public final void yyreset(Reader reader) {
		// 's' has been updated.
		zzBuffer = s.array;
		/*
		 * We replaced the line below with the two below it because zzRefill
		 * no longer "refills" the buffer (since the way we do it, it's always
		 * "full" the first time through, since it points to the segment's
		 * array).  So, we assign zzEndRead here.
		 */
		//zzStartRead = zzEndRead = s.offset;
		zzStartRead = s.offset;
		zzEndRead = zzStartRead + s.count - 1;
		zzCurrentPos = zzMarkedPos = zzPushbackPos = s.offset;
		zzLexicalState = YYINITIAL;
		zzReader = reader;
		zzAtBOL  = true;
		zzAtEOF  = false;
	}


%}

Letter							= [A-Za-z]
LetterOrUnderscore				= ({Letter}|"_")
NonzeroDigit						= [1-9]
Digit							= ("0"|{NonzeroDigit})
HexDigit							= ({Digit}|[A-Fa-f])
OctalDigit						= ([0-7])
AnyCharacterButApostropheOrBackSlash	= ([^\\'])
AnyCharacterButDoubleQuoteOrBackSlash	= ([^\\\"\n])
EscapedSourceCharacter				= ("u"{HexDigit}{HexDigit}{HexDigit}{HexDigit})
Escape							= ("\\"(([btnfr\"'\\])|([0123]{OctalDigit}?{OctalDigit}?)|({OctalDigit}{OctalDigit}?)|{EscapedSourceCharacter}))
NonSeparator						= ([^\t\f\r\n\ \(\)\{\}\[\]\;\,\.\=\>\<\!\~\?\:\+\-\*\/\&\|\^\%\"\']|"#"|"\\")
IdentifierStart					= ({LetterOrUnderscore}|"$")
IdentifierPart						= ({IdentifierStart}|{Digit}|("\\"{EscapedSourceCharacter}))

LineTerminator				= (\n)
WhiteSpace				= ([ \t\f]+)

CharLiteral	= ([\']({AnyCharacterButApostropheOrBackSlash}|{Escape})[\'])
UnclosedCharLiteral			= ([\'][^\'\n]*)
ErrorCharLiteral			= ({UnclosedCharLiteral}[\'])
StringLiteral				= ([\"]({AnyCharacterButDoubleQuoteOrBackSlash}|{Escape})*[\"])
UnclosedStringLiteral		= ([\"]([\\].|[^\\\"])*[^\"]?)
ErrorStringLiteral			= ({UnclosedStringLiteral}[\"])

MLCBegin					= "/*"
MLCEnd					= "*/"

DocCommentBegin	= "/**"
DocCommentEnd		= "*/"

LineCommentBegin			= "//"

IntegerLiteral			= ({Digit}+[lL]?)
HexLiteral			= (0x{HexDigit}+[lL]?)
FloatLiteral			= (({Digit}+)("."{Digit}+)?(e[+-]?{Digit}+)?[fd]? | ({Digit}+)?("."{Digit}+)(e[+-]?{Digit}+)?[fd]? | {Digit}+[fd])
ErrorNumberFormat			= (({IntegerLiteral}|{HexLiteral}|{FloatLiteral}){NonSeparator}+)
BooleanLiteral				= ("true"|"false")

Separator					= ([\(\)\{\}\[\]])
Separator2				= ([\;,.])

Identifier				= ({IdentifierStart}{IdentifierPart}*)

URLGenDelim				= ([:\/\?#\[\]@])
URLSubDelim				= ([\!\$&'\(\)\*\+,;=])
URLUnreserved			= ({LetterOrUnderscore}|{Digit}|[\-\.\~])
URLCharacter			= ({URLGenDelim}|{URLSubDelim}|{URLUnreserved}|[%])
URLCharacters			= ({URLCharacter}*)
URLEndCharacter			= ([\/\$]|{Letter}|{Digit})
URL						= (((https?|f(tp|ile))"://"|"www.")({URLCharacters}{URLEndCharacter})?)


/* No string state */
/* No char state */
%state MLC
%state DOCCOMMENT
%state EOL_COMMENT

%%

<YYINITIAL> {

	/* Keywords */
	"abstract" |
"break" |
"class" |
"continue" |
"default" |
"enum" |
"extends" |
"false" |
"final" |
"finally" |
"implements" |
"import" |
"instanceof" |
"interface" |
"native" |
"new" |
"null" |
"package" |
"private" |
"protected" |
"public" |
"static" |
"strictfp" |
"throws" |
"transient" |
"true" |
"void" |
"volatile"		{ addToken(Token.RESERVED_WORD); }

	/* Keywords 2 (just an optional set of keywords colored differently) */
	"length" |
"pixels"		{ addToken(Token.RESERVED_WORD_2); }

	/* Keywords 3 (just an optional set of keywords colored differently) */
	"catch" |
"do" |
"else" |
"for" |
"if" |
"switch" |
"synchronized" |
"try" |
"while"    { addToken(Token.RESERVED_WORD_3); }

	/* Keywords 4 (just an optional set of keywords colored differently) */
	"displayHeight" |
"displayWidth" |
"focused" |
"frameCount" |
"frameRate" |
"height" |
"key" |
"keyCode" |
"keyPressed" |
"mouseButton" |
"mousePressed" |
"mouseX" |
"mouseY" |
"pixelWidth" |
"pixels" |
"pmouseX" |
"pmouseY" |
"width"    { addToken(Token.RESERVED_WORD_4); }

	/* Keywords 5 (just an optional set of keywords colored differently) */
	"assert" |
"case" |
"return" |
"super" |
"this" |
"throw"    { addToken(Token.RESERVED_WORD_5); }

	/* Data types */
	"Array" |
"ArrayList" |
"Boolean" |
"BufferedReader" |
"Byte" |
"Character" |
"Class" |
"Double" |
"Float" |
"FloatDict" |
"FloatList" |
"HashMap" |
"IntDict" |
"IntList" |
"Integer" |
"JSONArray" |
"JSONObject" |
"PFont" |
"PGraphics" |
"PImage" |
"PShader" |
"PShape" |
"PVector" |
"PrintWriter" |
"String" |
"StringBuffer" |
"StringBuilder" |
"StringDict" |
"StringList" |
"Table" |
"TableRow" |
"Thread" |
"XML" |
"boolean" |
"byte" |
"char" |
"color" |
"double" |
"float" |
"int" |
"long" |
"short"		{ addToken(Token.DATA_TYPE); }

	/* Functions */
	/* No functions */

	/* Functions 1*/
	"ArrayList" |
"HashMap" |
"PVector" |
"abs" |
"acos" |
"addChild" |
"alpha" |
"ambient" |
"ambientLight" |
"append" |
"applyMatrix" |
"arc" |
"arrayCopy" |
"asin" |
"atan" |
"atan2" |
"background" |
"beginCamera" |
"beginContour" |
"beginRaw" |
"beginRecord" |
"beginShape" |
"bezier" |
"bezierDetail" |
"bezierPoint" |
"bezierTangent" |
"bezierVertex" |
"binary" |
"blend" |
"blendColor" |
"blendMode" |
"blue" |
"boolean" |
"box" |
"breakShape" |
"brightness" |
"byte" |
"camera" |
"ceil" |
"char" |
"clear" |
"clip" |
"color" |
"colorMode" |
"concat" |
"constrain" |
"copy" |
"cos" |
"createFont" |
"createGraphics" |
"createImage" |
"createInput" |
"createOutput" |
"createPath" |
"createReader" |
"createShape" |
"createWriter" |
"cursor" |
"curve" |
"curveDetail" |
"curvePoint" |
"curveTangent" |
"curveTightness" |
"curveVertex" |
"day" |
"degrees" |
"directionalLight" |
"displayDensity" |
"dist" |
"ellipse" |
"ellipseMode" |
"emissive" |
"end" |
"endCamera" |
"endContour" |
"endRaw" |
"endRecord" |
"endShape" |
"exit" |
"exp" |
"expand" |
"fill" |
"filter" |
"float" |
"floor" |
"frameRate" |
"frustum" |
"fullScreen" |
"get" |
"green" |
"hex" |
"hint" |
"hour" |
"hue" |
"image" |
"imageMode" |
"int" |
"join" |
"launch" |
"lerp" |
"lerpColor" |
"lightFalloff" |
"lightSpecular" |
"lights" |
"line" |
"list" |
"loadBytes" |
"loadFont" |
"loadImage" |
"loadJSONArray" |
"loadJSONObject" |
"loadMatrix" |
"loadPixels" |
"loadShader" |
"loadShape" |
"loadStrings" |
"loadTable" |
"loadXML" |
"log" |
"loop" |
"mag" |
"map" |
"match" |
"matchAll" |
"max" |
"millis" |
"min" |
"minute" |
"modelX" |
"modelY" |
"modelZ" |
"month" |
"mouseReleased" |
"nf" |
"nfc" |
"nfp" |
"nfs" |
"noClip" |
"noCursor" |
"noFill" |
"noLights" |
"noLoop" |
"noSmooth" |
"noStroke" |
"noTint" |
"noise" |
"noiseDetail" |
"noiseSeed" |
"norm" |
"normal" |
"ortho" |
"parseBoolean" |
"parseByte" |
"parseChar" |
"parseFloat" |
"parseInt" |
"parseXML" |
"perspective" |
"pixelDensity" |
"pixelHeight" |
"point" |
"pointLight" |
"popMatrix" |
"popStyle" |
"pow" |
"print" |
"printArray" |
"printCamera" |
"printMatrix" |
"printProjection" |
"println" |
"pushMatrix" |
"pushStyle" |
"quad" |
"quadraticVertex" |
"radians" |
"random" |
"randomGaussian" |
"randomSeed" |
"rect" |
"rectMode" |
"red" |
"redraw" |
"requestImage" |
"resetMatrix" |
"resetShader" |
"reverse" |
"rotate" |
"rotateX" |
"rotateY" |
"rotateZ" |
"round" |
"saturation" |
"save" |
"saveBytes" |
"saveFile" |
"saveFrame" |
"saveJSONArray" |
"saveJSONObject" |
"savePath" |
"saveStream" |
"saveStrings" |
"saveTable" |
"saveXML" |
"scale" |
"screenX" |
"screenY" |
"screenZ" |
"second" |
"selectFolder" |
"selectInput" |
"selectOutput" |
"set" |
"shader" |
"shape" |
"shapeMode" |
"shearX" |
"shearY" |
"shininess" |
"shorten" |
"sin" |
"size" |
"sketchFile" |
"sketchPath" |
"smooth" |
"sort" |
"specular" |
"sphere" |
"sphereDetail" |
"splice" |
"split" |
"splitTokens" |
"spotLight" |
"sq" |
"sqrt" |
"start" |
"stop" |
"str" |
"stroke" |
"strokeCap" |
"strokeJoin" |
"strokeWeight" |
"subset" |
"tan" |
"text" |
"textAlign" |
"textAscent" |
"textDescent" |
"textFont" |
"textLeading" |
"textMode" |
"textSize" |
"textWidth" |
"texture" |
"textureMode" |
"textureWrap" |
"thread" |
"tint" |
"translate" |
"triangle" |
"trim" |
"unbinary" |
"unhex" |
"updatePixels" |
"vertex" |
"year"    { addToken(Token.FUNCTION1); }

	/* Functions 2*/
	"PShader" |
"add" |
"add" |
"add" |
"add" |
"add" |
"addChild" |
"addChild" |
"addColumn" |
"addRow" |
"angleBetween" |
"append" |
"append" |
"append" |
"append" |
"array" |
"array" |
"array" |
"array" |
"beginContour" |
"beginDraw" |
"beginShape" |
"blend" |
"cache" |
"charAt" |
"clear" |
"clear" |
"clear" |
"clear" |
"clear" |
"clear" |
"clearRows" |
"close" |
"copy" |
"copy" |
"cross" |
"disableStyle" |
"dist" |
"div" |
"div" |
"div" |
"div" |
"div" |
"dot" |
"enableStyle" |
"endContour" |
"endDraw" |
"endShape" |
"equals" |
"filter" |
"findRow" |
"findRows" |
"flush" |
"format" |
"fromAngle" |
"get" |
"get" |
"get" |
"get" |
"get" |
"get" |
"get" |
"get" |
"getAttributeCount" |
"getBoolean" |
"getBoolean" |
"getChild" |
"getChild" |
"getChildCount" |
"getChildren" |
"getColumnCount" |
"getContent" |
"getContent" |
"getContent" |
"getFloat" |
"getFloat" |
"getFloat" |
"getFloat" |
"getFloat" |
"getInt" |
"getInt" |
"getInt" |
"getInt" |
"getInt" |
"getIntArray" |
"getJSONArray" |
"getJSONArray" |
"getJSONObject" |
"getJSONObject" |
"getName" |
"getParent" |
"getRow" |
"getRowCount" |
"getString" |
"getString" |
"getString" |
"getString" |
"getString" |
"getStringArray" |
"getStringColumn" |
"getVertex" |
"getVertexCount" |
"hasAttribute" |
"hasChildren" |
"hasKey" |
"hasKey" |
"hasKey" |
"hasValue" |
"hasValue" |
"hasValue" |
"heading" |
"increment" |
"increment" |
"indexOf" |
"isVisible" |
"keyArray" |
"keyArray" |
"keyArray" |
"keys" |
"keys" |
"keys" |
"length" |
"lerp" |
"limit" |
"listAttributes" |
"listChildren" |
"loadPixels" |
"lower" |
"mag" |
"magSq" |
"mask" |
"matchRow" |
"matchRows" |
"max" |
"max" |
"min" |
"min" |
"mult" |
"mult" |
"mult" |
"mult" |
"mult" |
"normalize" |
"print" |
"println" |
"random2D" |
"random3D" |
"readLine" |
"remove" |
"remove" |
"remove" |
"remove" |
"remove" |
"remove" |
"remove" |
"removeChild" |
"removeColumn" |
"removeRow" |
"removeTokens" |
"resetMatrix" |
"resize" |
"reverse" |
"reverse" |
"reverse" |
"rotate" |
"rotate" |
"rotateX" |
"rotateY" |
"rotateZ" |
"rows" |
"save" |
"scale" |
"set" |
"set" |
"set" |
"set" |
"set" |
"set" |
"set" |
"set" |
"setBoolean" |
"setBoolean" |
"setContent" |
"setFloat" |
"setFloat" |
"setFloat" |
"setFloat" |
"setFloat" |
"setInt" |
"setInt" |
"setInt" |
"setInt" |
"setInt" |
"setJSONArray" |
"setJSONArray" |
"setJSONObject" |
"setJSONObject" |
"setMag" |
"setName" |
"setString" |
"setString" |
"setString" |
"setString" |
"setString" |
"setVertex" |
"setVisible" |
"shuffle" |
"shuffle" |
"shuffle" |
"size" |
"size" |
"size" |
"size" |
"size" |
"size" |
"size" |
"sort" |
"sort" |
"sort" |
"sortKeys" |
"sortKeys" |
"sortKeys" |
"sortKeysReverse" |
"sortKeysReverse" |
"sortKeysReverse" |
"sortReverse" |
"sortReverse" |
"sortReverse" |
"sortValues" |
"sortValues" |
"sortValues" |
"sortValuesReverse" |
"sortValuesReverse" |
"sortValuesReverse" |
"sub" |
"sub" |
"sub" |
"sub" |
"sub" |
"substring" |
"toLowerCase" |
"toString" |
"toUpperCase" |
"translate" |
"trim" |
"updatePixels" |
"upper" |
"valueArray" |
"valueArray" |
"valueArray" |
"values" |
"values" |
"values"    { addToken(Token.FUNCTION2); }

	/* Functions 3*/
	"catch" |
"do" |
"for" |
"if" |
"switch" |
"synchronized" |
"while"    { addToken(Token.FUNCTION3); }

	/* Functions 4*/
	"draw" |
"keyPressed" |
"keyReleased" |
"keyTyped" |
"mouseClicked" |
"mouseDragged" |
"mouseMoved" |
"mousePressed" |
"mouseWheel" |
"settings" |
"setup"    { addToken(Token.FUNCTION4); }

	{BooleanLiteral}			{ addToken(Token.LITERAL_BOOLEAN); }

	{LineTerminator}				{ addNullToken(); return firstToken; }

	{Identifier}					{ addToken(Token.IDENTIFIER); }

	{WhiteSpace}					{ addToken(Token.WHITESPACE); }

	/* String/Character literals. */
	{CharLiteral}				{ addToken(Token.LITERAL_CHAR); }
{UnclosedCharLiteral}		{ addToken(Token.ERROR_CHAR); addNullToken(); return firstToken; }
{ErrorCharLiteral}			{ addToken(Token.ERROR_CHAR); }
	{StringLiteral}				{ addToken(Token.LITERAL_STRING_DOUBLE_QUOTE); }
{UnclosedStringLiteral}		{ addToken(Token.ERROR_STRING_DOUBLE); addNullToken(); return firstToken; }
{ErrorStringLiteral}			{ addToken(Token.ERROR_STRING_DOUBLE); }

	/* Comment literals. */
	{MLCBegin}	{ start = zzMarkedPos-2; yybegin(MLC); }
	{DocCommentBegin}	{ start = zzMarkedPos-3; yybegin(DOCCOMMENT); }
	{LineCommentBegin}			{ start = zzMarkedPos-2; yybegin(EOL_COMMENT); }

	/* Separators. */
	{Separator}					{ addToken(Token.SEPARATOR); }
	{Separator2}					{ addToken(Token.IDENTIFIER); }

	/* Operators. */
	"!" |
"!=" |
"%" |
"%=" |
"&" |
"&&" |
"*" |
"*=" |
"+" |
"++" |
"+=" |
"," |
"-" |
"--" |
"-=" |
"." |
"/" |
"/=" |
"<" |
"<<" |
"<=" |
"=" |
"==" |
">" |
">=" |
">>" |
"?" |
"|" |
"||"		{ addToken(Token.OPERATOR); }

	/* Numbers */
	{IntegerLiteral}				{ addToken(Token.LITERAL_NUMBER_DECIMAL_INT); }
	{HexLiteral}					{ addToken(Token.LITERAL_NUMBER_HEXADECIMAL); }
	{FloatLiteral}					{ addToken(Token.LITERAL_NUMBER_FLOAT); }
	{ErrorNumberFormat}			{ addToken(Token.ERROR_NUMBER_FORMAT); }

	/* Ended with a line not in a string or comment. */
	<<EOF>>						{ addNullToken(); return firstToken; }

	/* Catch any other (unhandled) characters. */
	.							{ addToken(Token.IDENTIFIER); }

}


/* No char state */

/* No string state */

<MLC> {

	[^hwf\n*]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_MULTILINE); start = zzMarkedPos; }
	[hwf]					{}

	\n						{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }
	{MLCEnd}					{ yybegin(YYINITIAL); addToken(start,zzStartRead+2-1, Token.COMMENT_MULTILINE); }
	"*"						{}
	<<EOF>>					{ addToken(start,zzStartRead-1, Token.COMMENT_MULTILINE); return firstToken; }

}


<DOCCOMMENT> {

	[^hwf\n*]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_DOCUMENTATION); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_DOCUMENTATION); start = zzMarkedPos; }
	[hwf]					{}

	\n						{ addToken(start,zzStartRead-1, Token.COMMENT_DOCUMENTATION); return firstToken; }
	{DocCommentEnd}			{ yybegin(YYINITIAL); addToken(start,zzStartRead+2-1, Token.COMMENT_DOCUMENTATION); }
	"*"						{}
	<<EOF>>					{ yybegin(YYINITIAL); addToken(start,zzEndRead, Token.COMMENT_DOCUMENTATION); return firstToken; }

}


<EOL_COMMENT> {
	[^hwf\n]+				{}
	{URL}					{ int temp=zzStartRead; addToken(start,zzStartRead-1, Token.COMMENT_EOL); addHyperlinkToken(temp,zzMarkedPos-1, Token.COMMENT_EOL); start = zzMarkedPos; }
	[hwf]					{}
	\n						{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
	<<EOF>>					{ addToken(start,zzStartRead-1, Token.COMMENT_EOL); addNullToken(); return firstToken; }
}

