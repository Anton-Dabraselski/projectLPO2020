package projectLPO.parser.ast;

import projectLPO.visitors.Visitor;

public class SeasonNum extends UnaryOp{

    public SeasonNum(Exp exp)
    {
        super(exp);
    }
    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitSeasonNum(exp);
    }
}
