package projectLPO.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.Objects.requireNonNullElse;
import static projectLPO.parser.TokenType.*;

public class BufferedTokenizer implements Tokenizer {

	private static final String regEx; // the regular expression including all valid lexems
	private static final Map<String, TokenType> keywords = new HashMap<>(); // keyword table
	private static final Map<String, TokenType> symbols = new HashMap<>(); // symbol table

	private final LineNumberReader buf_reader; // the numbered buffered reader decorated by the buffered tokenizer
	private String line; // currently processed line
	private final Matcher matcher = Pattern.compile(regEx).matcher(""); // the matcher used by the tokenizer
	private TokenType tokenType; // the type of the currently recognized token
	private String tokenString; // the lexeme of the currently recognized token
	private int intValue; // the integer value if the currently recognized token has type NUM
	private boolean boolValue; // the boolean value if the currently recognized token has type BOOL
	private int seasonValue;

	static { // static initializer to define the regular expression of all valid lexemes
		// remark: groups must correspond to the ordinal of the corresponding
		// token types as defined in enum TokenType
		final var skipRegEx = "(\\s+|//.*)"; // group 1: white spaces or single line comments to be skipped
		final var identRegEx = "([a-zA-Z]\\w*)"; // group 2: identifiers
		final var numRegEx = "(0|[1-9][0-9]*)"; // group 3: radix 10 natural numbers
		final var symbolRegEx = "\\+|\\*|==|=|<<|>>|\\(|\\)|;|,|\\{|\\}|-|!|&&|#|<"; /*
																					 * symbols are singleton lexical
																					 * categories
																					 */
		regEx = skipRegEx + "|" + identRegEx + "|" + numRegEx + "|"
				+ symbolRegEx; /*
								 * global regular expression obtained as union of the different lexical
								 * categories
								 */
	}

	static { // static initializers to define the table of keywords
		keywords.put("print", PRINT);
		keywords.put("var", VAR);
		keywords.put("false", BOOL);
		keywords.put("true", BOOL);
		keywords.put("if", IF);
		keywords.put("else", ELSE);
		keywords.put("fst", FST);
		keywords.put("snd", SND);
		keywords.put("for", FOR);
		keywords.put("to", TO);
		keywords.put("Winter", SEASON);
		keywords.put("Spring", SEASON);
		keywords.put("Summer", SEASON);
		keywords.put("Fall", SEASON);
		keywords.put("seasonof", SEASONOF);
	}

	static { // static initializer to define the table of symbols
		symbols.put("+", PLUS);
		symbols.put("*", TIMES);
		symbols.put("=", ASSIGN);
		symbols.put("(", OPEN_PAR);
		symbols.put(")", CLOSE_PAR);
		symbols.put("<<", START_PAIR);
		symbols.put(">>", END_PAIR);
		symbols.put(";", STMT_SEP);
		symbols.put(",", EXP_SEP);
		symbols.put("{", OPEN_BLOCK);
		symbols.put("}", CLOSE_BLOCK);
		symbols.put("-", MINUS);
		symbols.put("!", NOT);
		symbols.put("&&", AND);
		symbols.put("==", EQ);
		symbols.put("#", SEASON_NUM);
		symbols.put("<", LESS);
	}

	public BufferedTokenizer(BufferedReader br) {
		this.buf_reader = new LineNumberReader(br);
	}

	private boolean hasNext() throws TokenizerException { // checks whether there are still lexemes
		if (matcher.regionEnd() > matcher.regionStart()) // the matcher has still lexemes
			return true;
		while (true) {
			try {
				line = buf_reader.readLine();
			} catch (IOException e) {
				throw new TokenizerException(e);
			}
			if (line == null)
				return false; // EOF reached
			if (line.isEmpty()) // yep, lines can be empty!
				continue;
			matcher.reset(line); // reset the matcher with the new non empty line
			return true;
		}
	}

	private TokenType assignTokenType() { // pre-condition: matcher.lookingAt() returned true
		if (matcher.group(IDENT.ordinal()) != null) // IDENT or BOOL or a keyword
			return requireNonNullElse(keywords.get(tokenString), IDENT);
		if (matcher.group(NUM.ordinal()) != null)
			return NUM;
		if (matcher.group(SKIP.ordinal()) != null)
			return SKIP;
		var ret = symbols.get(tokenString);
		if (ret == null) // this should never happen!
			throw new AssertionError("Fatal error: could not determine the token type!");
		return ret;
	}

	private void resetState() {
		tokenString = null;
		tokenType = null;
	}

	private void semanticAnnotation() { // required for num or bool literals
		switch (tokenType) {
		case NUM:
			intValue = Integer.decode(tokenString);
			break;
		case BOOL:
			boolValue = Boolean.parseBoolean(tokenString);
			break;
			case SEASON:
				seasonValue = SeasonTypeConvertor.toInt(tokenString);
				break;

		default: // no other annotations required
			break;
		}
	}

	private void unrecognizedToken() throws TokenizerException {
		throw new TokenizerException("on line " + buf_reader.getLineNumber() + " unrecognized token starting at '"
				+ line.substring(matcher.regionStart()) + "'");
	}

	public TokenType next() throws TokenizerException {
		resetState();
		do {
			if (!hasNext())
				return tokenType = EOF;
			if (!matcher.lookingAt())
				unrecognizedToken();
			tokenString = matcher.group();
			tokenType = assignTokenType();
			semanticAnnotation();
			matcher.region(matcher.end(), matcher.regionEnd()); // advances in the matcher
		} while (tokenType == SKIP); // keeps advancing when skippable tokens are recognized
		return tokenType;
	}

	private void checkLegalState() {
		if (tokenType == null)
			throw new IllegalStateException("No token was recognized");
	}

	private void checkLegalState(TokenType tokenType) {
		if (this.tokenType != tokenType)
			throw new IllegalStateException("No token of type " + tokenType + " was recognized");
	}

	@Override
	public String tokenString() { // lexeme of the most recently recognized token, if any
		checkLegalState();
		return tokenString;
	}

	@Override
	public boolean boolValue() { // boolean value of the most recently recognized token, if of type BOOL
		checkLegalState(BOOL);
		return boolValue;
	}

	@Override
	public int seasonValue() { // ------------
		checkLegalState(SEASON);
		return seasonValue;
	}

	@Override
	public int intValue() { // integer value of the most recently recognized token, if of type NUM
		checkLegalState(NUM);
		return intValue;
	}

	@Override
	public TokenType tokenType() { // type of the most recently recognized token, if any
		checkLegalState();
		return tokenType;
	}

	@Override
	public int getLineNumber() {
		return buf_reader.getLineNumber();
	}

	@Override
	public void close() throws IOException {
		if (buf_reader != null)
			buf_reader.close();
	}

}
