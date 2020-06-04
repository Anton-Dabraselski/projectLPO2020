package projectLPO.visitors.evaluation;

public class IntValue extends PrimValue<Integer> {

	public IntValue(Integer value) {
		super(value);
	}

	@Override
	public boolean less(Value object) {
		if (this == object)
			return false;

		return this.toInt() < object.toInt();
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof IntValue))
			return false;
		return value.equals(((IntValue) obj).value);
	}

	@Override
	public int toInt() {
		return value;
	}

}
