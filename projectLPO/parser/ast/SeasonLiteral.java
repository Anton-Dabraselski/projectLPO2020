package projectLPO.parser.ast;

import projectLPO.visitors.Visitor;

public class SeasonLiteral extends PrimLiteral<Integer>{

    public SeasonLiteral(int n) {
        super(n);
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitSeasonLiteral(value);
    }
}
