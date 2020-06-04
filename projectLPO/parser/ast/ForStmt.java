package projectLPO.parser.ast;

import projectLPO.visitors.Visitor;

public class ForStmt implements Stmt{
    private final VarIdent exp1;
    private final Exp exp2;
    private final Block block;
    public ForStmt(VarIdent exp1, Exp exp2, Block block){
        this.exp1 = exp1;
        this.exp2 = exp2;
        this.block = block;
    }

    @Override
    public <T> T accept(Visitor<T> visitor) {
        return visitor.visitForStmt(exp1, exp2, block);
    }
}
