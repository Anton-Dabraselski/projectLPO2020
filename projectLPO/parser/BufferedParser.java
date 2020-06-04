package projectLPO.parser;

import static java.util.Objects.requireNonNull;
import static projectLPO.parser.TokenType.*;
import static java.lang.System.err;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import projectLPO.parser.ast.*;

/*
Prog ::= StmtSeq EOF
StmtSeq ::= Stmt (';' StmtSeq)?
Stmt ::= 'var'? IDENT '=' Exp | 'print' Exp |  'if' '(' Exp ')' '{' StmtSeq '}' ('else' '{' StmtSeq '}')? 
Exp ::= Eq ('&&' Eq)* 
Eq ::= Add ('==' Add)*
Add ::= Mul ('+' Mul)*
Mul::= Atom ('*' Atom)*
Atom ::= '<<' Exp ',' Exp '>>' | 'fst' Atom | 'snd' Atom | '-' Atom | '!' Atom | BOOL | NUM | IDENT | '(' Exp ')'
*/

public class BufferedParser implements Parser {

	private final BufferedTokenizer buf_tokenizer; // the buffered tokenizer used by the parser

	/*
	 * reads the next token through the buffered tokenizer associated with the
	 * parser; TokenizerExceptions are chained into corresponding ParserExceptions
	 */
	private void nextToken() throws ParserException {
		try {
			buf_tokenizer.next();
		} catch (TokenizerException e) {
			throw new ParserException(e);
		}
	}

	// decorates error message with the corresponding line number
	private String line_err_msg(String msg) {
		return "on line " + buf_tokenizer.getLineNumber() + ": " + msg;
	}

	/*
	 * checks whether the token type of the currently recognized token matches
	 * 'expected'; if not, it throws a corresponding ParserException
	 */
	private void match(TokenType expected) throws ParserException {
		final var found = buf_tokenizer.tokenType();
		if (found != expected)
			throw new ParserException(line_err_msg(
					"Expecting " + expected + ", found " + found + "('" + buf_tokenizer.tokenString() + "')"));
	}

	/*
	 * checks whether the token type of the currently recognized token matches
	 * 'expected'; if so, it reads the next token, otherwise it throws a
	 * corresponding ParserException
	 */
	private void consume(TokenType expected) throws ParserException {
		match(expected);
		nextToken();
	}

	// throws a ParserException because the current token was not expected
	private void unexpectedTokenError() throws ParserException {
		throw new ParserException(line_err_msg(
				"Unexpected token " + buf_tokenizer.tokenType() + "('" + buf_tokenizer.tokenString() + "')"));
	}

	// associates the parser with a corresponding non-null buffered tokenizer
	public BufferedParser(BufferedTokenizer tokenizer) {
		this.buf_tokenizer = requireNonNull(tokenizer);
	}

	@Override
	public Prog parseProg() throws ParserException {
		nextToken(); // one look-ahead symbol
		var prog = new ProgAST(parseStmtSeq());
		match(EOF); // last token must have type EOF
		return prog;
	}

	@Override
	public void close() throws IOException {
		if (buf_tokenizer != null)
			buf_tokenizer.close();
	}

	// parses a non empty sequence of statements, MoreStmt binary operator is right
	// associative
	private StmtSeq parseStmtSeq() throws ParserException {
		var stmt = parseStmt();
		if (buf_tokenizer.tokenType() == STMT_SEP) {
			nextToken();
			return new MoreStmt(stmt, parseStmtSeq());
		}
		return new SingleStmt(stmt);
	}

	// parses statements
	private Stmt parseStmt() throws ParserException {
		switch (buf_tokenizer.tokenType()) {
		default:
			unexpectedTokenError();
		case PRINT:
			return parsePrintStmt();
		case VAR:
			return parseVarStmt();
		case IDENT:
			return parseAssignStmt();
		case IF:
			return parseIfStmt();
			case FOR:
				return parseForStmt();
		}
	}

	private ForStmt parseForStmt() throws ParserException{
		consume(FOR);
		//consume(IDENT);
		var exp = parseVarIdent();
		consume(TO);
		var exp2 = parseExp();
		var forBlock = parseBlock();

		return new ForStmt(exp, exp2, forBlock);
	}

	// parses the 'print' statement
	private PrintStmt parsePrintStmt() throws ParserException {
		consume(PRINT); // or nextToken() since PRINT has already been recognized
		return new PrintStmt(parseExp());
	}

	// parses the 'var' statement
	private VarStmt parseVarStmt() throws ParserException {
		consume(VAR); // or nextToken() since VAR has already been recognized
		var ident = parseVarIdent();
		consume(ASSIGN);
		return new VarStmt(ident, parseExp());
	}

	// parses the assignment statement
	private AssignStmt parseAssignStmt() throws ParserException {
		var ident = parseVarIdent();
		consume(ASSIGN);
		return new AssignStmt(ident, parseExp());
	}

	// parses the if_else statement
	private IfStmt parseIfStmt() throws ParserException {
		consume(IF); // or nextToken() since IF has already been recognized
		consume(OPEN_PAR);
		var exp = parseExp();
		consume(CLOSE_PAR);
		var thenBlock = parseBlock();
		if (buf_tokenizer.tokenType() != ELSE)
			return new IfStmt(exp, thenBlock);
		consume(ELSE); // or nextToken() since ELSE has already been recognized
		var elseBlock = parseBlock();
		return new IfStmt(exp, thenBlock, elseBlock);
	}

	// parses a block statement
	private Block parseBlock() throws ParserException {
		consume(OPEN_BLOCK);
		var stmts = parseStmtSeq();
		consume(CLOSE_BLOCK);
		return new Block(stmts);
	}

	/*
	 * parses expressions, starting from the lowest precedence operator AND which is
	 * left-associative
	 */
	private Exp parseExp() throws ParserException {
		var exp = parseEq();
		while (buf_tokenizer.tokenType() == AND) {
			nextToken();
			exp = new And(exp, parseEq());
		}
		return exp;
	}

	/*
	 * parses expressions, starting from the lowest precedence operator EQ which is
	 * left-associative
	 */
	private Exp parseEq() throws ParserException {
		var exp = parseLess();
		while (buf_tokenizer.tokenType() == EQ) {
			nextToken();
			exp = new Eq(exp, parseLess());
		}
		return exp;
	}

	private Exp parseLess() throws ParserException {
		var exp = parseAdd();
		while (buf_tokenizer.tokenType() == LESS) {
			nextToken();
			exp = new Less(exp, parseAdd());
		}
		return exp;
	}

	/*
	 * parses expressions, starting from the lowest precedence operator PLUS which
	 * is left-associative
	 */
	private Exp parseAdd() throws ParserException {
		var exp = parseMul();
		while (buf_tokenizer.tokenType() == PLUS) {
			nextToken();
			exp = new Add(exp, parseMul());
		}
		return exp;
	}

	/*
	 * parses expressions, starting from the lowest precedence operator TIMES which
	 * is left-associative
	 */
	private Exp parseMul() throws ParserException {
		var exp = parseAtom();
		while (buf_tokenizer.tokenType() == TIMES) {
			nextToken();
			exp = new Mul(exp, parseAtom());
		}
		return exp;
	}

	// parses expressions of type Atom
	private Exp parseAtom() throws ParserException {
		switch (buf_tokenizer.tokenType()) {
			default:
				unexpectedTokenError();
			case NUM:
				return parseNum();
			case IDENT:
				return parseVarIdent();
			case MINUS:
				return parseMinus();
			case SEASON_NUM:
				return parseSeasonNum();
			case SEASONOF:
				return parseSeasonOf();
			case OPEN_PAR:
				return parseRoundPar();
			case BOOL:
				return parseBoolean();
			case SEASON:
				return parseSeason();
			case NOT:
				return parseNot();
			case START_PAIR:
				return parsePairLit();
			case FST:
				return parseFst();
			case SND:
				return parseSnd();
		}
	}


	private SeasonLiteral parseSeason() throws ParserException {
		var val = buf_tokenizer.seasonValue();
		consume(SEASON); // or nextToken() since BOOL has already been recognized
		return new SeasonLiteral(val);
	}

	// parses natural literals
	private IntLiteral parseNum() throws ParserException {
		var val = buf_tokenizer.intValue();
		consume(NUM); // or nextToken() since NUM has already been recognized
		return new IntLiteral(val);
	}

	// parses boolean literals
	private BoolLiteral parseBoolean() throws ParserException {
		var val = buf_tokenizer.boolValue();
		consume(BOOL); // or nextToken() since BOOL has already been recognized
		return new BoolLiteral(val);
	}

	// parses variable identifiers
	private VarIdentAST parseVarIdent() throws ParserException {
		var name = buf_tokenizer.tokenString();
		consume(IDENT); // or nextToken() since IDENT has already been recognized
		return new VarIdentAST(name);
	}

	// parses MINUS Atom
	private Sign parseMinus() throws ParserException {
		consume(MINUS); // or nextToken() since MINUS has already been recognized
		return new Sign(parseAtom());
	}

	private SeasonNum parseSeasonNum() throws ParserException {
		consume(SEASON_NUM);
		return new SeasonNum(parseAtom());
	}

	private SeasonOf parseSeasonOf() throws ParserException {
		consume(SEASONOF);
		return new SeasonOf(parseAtom());
	}
	// parses FST Atom
	private Fst parseFst() throws ParserException {
		consume(FST); // or nextToken() since FST has already been recognized
		return new Fst(parseAtom());
	}

	// parses SND Atom
	private Snd parseSnd() throws ParserException {
		consume(SND); // or nextToken() since SND has already been recognized
		return new Snd(parseAtom());
	}

	// parses NOT Atom
	private Not parseNot() throws ParserException {
		consume(NOT); // or nextToken() since NOT has already been recognized
		return new Not(parseAtom());
	}

	// parses pairs
	private PairLit parsePairLit() throws ParserException {
		consume(START_PAIR); // or nextToken() since START_PAIR has already been recognized
		var left = parseExp();
		consume(EXP_SEP);
		var right = parseExp();
		consume(END_PAIR);
		return new PairLit(left, right);
	}

	// parses OPEN_PAR Exp CLOSE_PAR
	private Exp parseRoundPar() throws ParserException {
		consume(OPEN_PAR); // or nextToken() since OPEN_PAR has already been recognized
		var exp = parseExp();
		consume(CLOSE_PAR);
		return exp;
	}

	private static BufferedReader tryOpenInput(String inputPath) throws FileNotFoundException {
		return new BufferedReader(inputPath == null ? new InputStreamReader(System.in) : new FileReader(inputPath));
	}

	public static void main(String[] args) {
		try (var buf_reader = tryOpenInput(args.length > 0 ? args[0] : null);
				var buf_tokenizer = new BufferedTokenizer(buf_reader);
				var buf_parser = new BufferedParser(buf_tokenizer);) {
			var prog = buf_parser.parseProg();
			System.out.println(prog);
		} catch (IOException e) {
			err.println("I/O error: " + e.getMessage());
		} catch (ParserException e) {
			err.println("Syntax error " + e.getMessage());
		} catch (Throwable e) {
			err.println("Unexpected error.");
			e.printStackTrace();
		}

	}

}
