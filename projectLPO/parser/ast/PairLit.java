package projectLPO.parser.ast;

import projectLPO.visitors.Visitor;

public class PairLit extends BinaryOp {
	public PairLit(Exp left, Exp right) {
		super(left, right);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitPairLit(left, right);
	}
}
