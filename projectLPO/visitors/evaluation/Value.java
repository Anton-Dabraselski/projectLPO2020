package projectLPO.visitors.evaluation;

public interface Value {
	/* default conversion methods */
	default int toInt() {
		throw new EvaluatorException("Expecting an integer");
	}

	default boolean toBool() {
		throw new EvaluatorException("Expecting a boolean");
	}

	default String toSeason() {
		throw new EvaluatorException("Expecting a season");
	}

	default PairValue toProd() {
		throw new EvaluatorException("Expecting a pair");
	}

	boolean less(Value object);
}
