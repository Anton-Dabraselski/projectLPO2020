package projectLPO.visitors.evaluation;

import java.io.PrintWriter;

import projectLPO.environments.EnvironmentException;
import projectLPO.environments.GenEnvironment;
import projectLPO.parser.SeasonTypeConvertor;
import projectLPO.parser.ast.Block;
import projectLPO.parser.ast.Exp;
import projectLPO.parser.ast.VarIdent;
import projectLPO.parser.ast.Stmt;
import projectLPO.parser.ast.StmtSeq;
import projectLPO.visitors.Visitor;

import static java.util.Objects.requireNonNull;

public class Eval implements Visitor<Value> {

	private final GenEnvironment<Value> env = new GenEnvironment<>();
	private final PrintWriter printWriter; // output stream used to print values

	public Eval() {
		printWriter = new PrintWriter(System.out, true);
	}

	public Eval(PrintWriter printWriter) {
		this.printWriter = requireNonNull(printWriter);
	}

	// dynamic semantics for programs; no value returned by the visitor

	@Override
	public Value visitProg(StmtSeq stmtSeq) {
		try {
			stmtSeq.accept(this);
			// possible runtime errors
			// EnvironmentException: undefined variable
		} catch (EnvironmentException e) {
			throw new EvaluatorException(e);
		}
		catch (IndexOutOfBoundsException e){
			throw new EvaluatorException(e);
		}
		return null;
	}

	// dynamic semantics for statements; no value returned by the visitor

	@Override
	public Value visitAssignStmt(VarIdent ident, Exp exp) {
		env.update(ident, exp.accept(this));
		return null;
	}

	@Override
	public Value visitPrintStmt(Exp exp) {
		printWriter.println(exp.accept(this));
		return null;
	}

	@Override
	public Value visitVarStmt(VarIdent ident, Exp exp) {
		//var v = exp.accept(this);
		env.dec(ident, exp.accept(this));
		return null;
	}

	@Override
	public Value visitForStmt(VarIdent ident, Exp exp, Block block) {
		int start = env.lookup(ident).toInt();
		int end = exp.accept(this).toInt();
		int i = start;
		for (i = start; i <= exp.accept(this).toInt(); i++){
			env.update(ident, new IntValue(i));
			block.accept(this);
		}
		env.update(ident, new IntValue(i));
		return null;
	}

	@Override
	public Value visitIfStmt(Exp exp, Block thenBlock, Block elseBlock) {
		if (exp.accept(this).toBool())
			thenBlock.accept(this);
		else if (elseBlock != null)
			elseBlock.accept(this);
		return null;
	}

	@Override
	public Value visitBlock(StmtSeq stmtSeq) {
		env.enterScope();
		stmtSeq.accept(this);
		env.exitScope();
		return null;
	}

	// dynamic semantics for sequences of statements
	// no value returned by the visitor

	@Override
	public Value visitSingleStmt(Stmt stmt) {
		stmt.accept(this);
		return null;
	}

	@Override
	public Value visitMoreStmt(Stmt first, StmtSeq rest) {
		first.accept(this);
		rest.accept(this);
		return null;
	}

	// dynamic semantics of expressions; a value is returned by the visitor

	@Override
	public Value visitAdd(Exp left, Exp right) {
		return new IntValue(left.accept(this).toInt() + right.accept(this).toInt());
	}

	@Override
	public Value visitIntLiteral(int value) {
		return new IntValue(value);
	}

	@Override
	public Value visitMul(Exp left, Exp right) {
		return new IntValue(left.accept(this).toInt() * right.accept(this).toInt());
	}

	@Override
	public Value visitSign(Exp exp) {
		return new IntValue(-exp.accept(this).toInt());
	}

	@Override
	public Value visitSeasonNum(Exp exp) {
		return new IntValue(SeasonTypeConvertor.toInt(exp.accept(this).toSeason()));
	} //преобразует в целое число

	@Override
	public Value visitSeasonOf(Exp exp) {
		int val = exp.accept(this).toInt();
		if(!(val >= 0 && val <= 3)) throw new  ArrayIndexOutOfBoundsException("Index -1 out of bounds for length 4");
		return new SeasonValue(val);

	}

	@Override
	public Value visitVarIdent(VarIdent id) {
		return env.lookup(id);
	}

	@Override
	public Value visitNot(Exp exp) {
		return new BoolValue(!exp.accept(this).toBool());
	}

	@Override
	public Value visitAnd(Exp left, Exp right) {
		return new BoolValue(left.accept(this).toBool() && right.accept(this).toBool());
	}

	@Override
	public Value visitBoolLiteral(boolean value) {
		return new BoolValue(value);
	}

	@Override
	public Value visitEq(Exp left, Exp right) {
		return new BoolValue(left.accept(this).equals(right.accept(this)));
	}

	@Override
	public Value visitLess(Exp left, Exp right) {
		return new BoolValue(left.accept(this).less(right.accept(this)));
	}

	@Override
	public Value visitPairLit(Exp left, Exp right) {
		return new PairValue(left.accept(this), right.accept(this));
	}

	@Override
	public Value visitFst(Exp exp) {
		return exp.accept(this).toProd().getFstVal();
	}

	@Override
	public Value visitSnd(Exp exp) {
		return exp.accept(this).toProd().getSndVal();
	}

	@Override
	public Value visitSeasonLiteral(int value) {
		return new SeasonValue(value);
	}

}
