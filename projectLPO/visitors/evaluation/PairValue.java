package projectLPO.visitors.evaluation;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.hash;

public class PairValue implements Value {

	private final Value fstVal;
	private final Value sndVal;

	public PairValue(Value fstVal, Value sndVal) {
		this.fstVal = requireNonNull(fstVal);
		this.sndVal = requireNonNull(sndVal);
	}

	public Value getFstVal() {
		return fstVal;
	}

	public Value getSndVal() {
		return sndVal;
	}

	@Override
	public PairValue toProd() {
		return this;
	}

	@Override
	public boolean less(Value object) {
		if (this == object)
			return false;

		var op = object.toProd();
		return fstVal.less(op.fstVal) && sndVal.less(op.sndVal);
	}

	@Override
	public String toString() {
		return "<<" + fstVal + ", " + sndVal + ">>";
	}

	@Override
	public int hashCode() {
		return hash(fstVal,sndVal);
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PairValue))
			return false;
		var op = (PairValue) obj;
		return fstVal.equals(op.fstVal) && sndVal.equals(op.sndVal);
	}
}
