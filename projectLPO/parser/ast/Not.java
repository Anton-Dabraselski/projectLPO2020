package projectLPO.parser.ast;

import projectLPO.visitors.Visitor;

public class Not extends UnaryOp {

	public Not(Exp exp) {
		super(exp);
	}

	@Override
	public <T> T accept(Visitor<T> visitor) {
		return visitor.visitNot(exp);
	}
}
