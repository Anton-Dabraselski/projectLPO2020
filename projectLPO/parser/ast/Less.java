package projectLPO.parser.ast;

import projectLPO.visitors.Visitor;

public class Less extends BinaryOp{
    public Less(Exp left, Exp right) {
        super(left, right);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitLess(left, right);
    }
}
