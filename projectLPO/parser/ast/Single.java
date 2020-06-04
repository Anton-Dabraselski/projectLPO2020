package projectLPO.parser.ast;

import static java.util.Objects.requireNonNull;

public abstract class Single<T> implements AST {
	protected final T single;

	protected Single(T single) {
		this.single = requireNonNull(single);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + single + ")";
	}
}
