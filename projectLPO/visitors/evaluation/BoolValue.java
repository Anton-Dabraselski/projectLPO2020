package projectLPO.visitors.evaluation;

public class BoolValue extends PrimValue<Boolean> {

	public BoolValue(Boolean value) {
		super(value);
	}

	@Override
	public boolean less(Value object) {
		if (this == object)
			return false;
		this.toBool();
		object.toBool();
		return this.toBool() == false && object.toBool() == true;
	}

		@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof BoolValue))
			return false;
		return value.equals(((BoolValue) obj).value);
	}

	@Override
	public boolean toBool() {
		return value;
	}

}
