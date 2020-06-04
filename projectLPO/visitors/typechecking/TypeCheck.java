package projectLPO.visitors.typechecking;

import static projectLPO.visitors.typechecking.PrimtType.*;
import projectLPO.parser.ast.*;
import projectLPO.environments.EnvironmentException;
import projectLPO.environments.GenEnvironment;
import projectLPO.visitors.Visitor;

public class TypeCheck implements Visitor<Type> {

	private final GenEnvironment<Type> env = new GenEnvironment<>();

	private void checkBinOp(Exp left, Exp right, Type type) {
		type.checkEqual(left.accept(this));
		type.checkEqual(right.accept(this));
	}

	// static semantics for programs; no value returned by the visitor

	@Override
	public Type visitProg(StmtSeq stmtSeq) {
		try {
			stmtSeq.accept(this);
		} catch (EnvironmentException e) { // undeclared variable
			throw new TypecheckerException(e);
		}
		return null;
	}

	// static semantics for statements; no value returned by the visitor

	@Override
	public Type visitAssignStmt(VarIdent ident, Exp exp) {
		Type found = env.lookup(ident);
		found.checkEqual(exp.accept(this));
		return null;
	}

	@Override
	public Type visitPrintStmt(Exp exp) {
		exp.accept(this);
		return null;
	}

	@Override
	public Type visitVarStmt(VarIdent ident, Exp exp) {
		var t = exp.accept(this);
		env.dec(ident, exp.accept(this));
		return null;
	}

	@Override
	public Type visitForStmt(VarIdent ident, Exp exp, Block block) {
		INT.checkEqual(ident.accept(this));
		INT.checkEqual(exp.accept(this));
		block.accept(this);
		return null;
	}

	@Override
	public Type visitIfStmt(Exp exp, Block thenBlock, Block elseBlock) {
		BOOL.checkEqual(exp.accept(this));
		thenBlock.accept(this);
		if (elseBlock == null)
			return null;
		elseBlock.accept(this);
		return null;
	}

	@Override
	public Type visitBlock(StmtSeq stmtSeq) {
		env.enterScope();
		stmtSeq.accept(this);
		env.exitScope();
		return null;
	}

	// static semantics for sequences of statements
	// no value returned by the visitor

	@Override
	public Type visitSingleStmt(Stmt stmt) {
		stmt.accept(this);
		return null;
	}

	@Override
	public Type visitMoreStmt(Stmt first, StmtSeq rest) {
		first.accept(this);
		rest.accept(this);
		return null;
	}

	// static semantics of expressions; a type is returned by the visitor

	@Override
	public Type visitAdd(Exp left, Exp right) {
		checkBinOp(left, right, INT);
		return INT;
	}

	@Override
	public Type visitIntLiteral(int value) {
		return INT;
	}

	@Override
	public Type visitMul(Exp left, Exp right) {
		checkBinOp(left, right, INT);
		return INT;
	}

	@Override
	public Type visitSign(Exp exp) {
		return INT.checkEqual(exp.accept(this));
	}

	@Override
	public Type visitSeasonNum(Exp exp) {
		SEASON.checkEqual(exp.accept(this));
		return INT;
	}

	@Override
	public Type visitSeasonOf(Exp exp) {
		INT.checkEqual(exp.accept(this));
		return SEASON;
	}

	@Override
	public Type visitLess(Exp left, Exp right) {
		left.accept(this).checkEqual(right.accept(this)); //сравниваем тип данных
		return BOOL;
	}

	@Override
	public Type visitVarIdent(VarIdent id) {
		return env.lookup(id);
	}

	@Override
	public Type visitNot(Exp exp) {
		return BOOL.checkEqual(exp.accept(this));
	}

	@Override
	public Type visitAnd(Exp left, Exp right) {
		checkBinOp(left, right, BOOL);
		return BOOL;
	}

	@Override
	public Type visitBoolLiteral(boolean value) {
		return BOOL;
	}

	@Override
	public Type visitEq(Exp left, Exp right) {
		left.accept(this).checkEqual(right.accept(this));
		return BOOL;
	}

	@Override
	public Type visitPairLit(Exp left, Exp right) {
		return new ProdType(left.accept(this), right.accept(this));
	}

	@Override
	public Type visitFst(Exp exp) {
		return exp.accept(this).getFstProdType();
	}

	@Override
	public Type visitSnd(Exp exp) {
		return exp.accept(this).getSndProdType();
	}

	@Override
	public Type visitSeasonLiteral(int value) {
		return SEASON;
	}

}
