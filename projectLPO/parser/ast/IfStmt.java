package projectLPO.parser.ast;

import static java.util.Objects.requireNonNull;

import projectLPO.visitors.Visitor;

public class IfStmt implements Stmt {
	private final Exp exp;
	private final Block thenBlock;
	private final Block elseBlock;

	public IfStmt(Exp exp, Block thenBlock, Block elseBlock) {
		this.exp = requireNonNull(exp);
		this.thenBlock = requireNonNull(thenBlock);
		this.elseBlock = elseBlock;
	}

	public IfStmt(Exp exp, Block thenBlock) {
		this(exp, thenBlock, null);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + exp + "," + thenBlock + "," + elseBlock + ")";
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitIfStmt(exp, thenBlock, elseBlock);
	}

}
