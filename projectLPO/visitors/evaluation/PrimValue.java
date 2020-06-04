package projectLPO.visitors.evaluation;

import static java.util.Objects.requireNonNull;
/* non-structured values of primitive type */
public abstract class PrimValue<T> implements Value {
	protected T value;

	protected PrimValue(T value) {
		this.value = requireNonNull(value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		return value.toString();
	}


}