package projectLPO.parser.ast;

import static java.util.Objects.requireNonNull;

public abstract class More<FT,RT> implements AST {
	protected final FT first;
	protected final RT rest;

	protected More(FT first, RT rest) {
		this.first = requireNonNull(first);
		this.rest = requireNonNull(rest);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + first + "," + rest + ")";
	}
}
