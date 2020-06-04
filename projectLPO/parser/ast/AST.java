package projectLPO.parser.ast;

import projectLPO.visitors.Visitor;

public interface AST {
	<T> T accept(Visitor<T> visitor);
}
