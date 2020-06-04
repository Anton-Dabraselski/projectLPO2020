package projectLPO.visitors.typechecking;

import static java.util.Objects.requireNonNull;

public class ProdType implements Type {

	private final Type fstType;
	private final Type sndType;

	public static final String TYPE_NAME = "PROD";

	public ProdType(Type fstType, Type sndType) {
		this.fstType = requireNonNull(fstType);
		this.sndType = requireNonNull(sndType);
	}

	public Type getFstType() {
		return fstType;
	}

	public Type getSndType() {
		return sndType;
	}

	@Override
	public final boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof ProdType))
			return false;
		ProdType pt = (ProdType) obj;
		return fstType.equals(pt.fstType) && sndType.equals(pt.sndType);
	}

	@Override
	public int hashCode() {
		return fstType.hashCode() + 31 * sndType.hashCode();
	}

	@Override
	public String toString() {
		return "(" + fstType + "*" + sndType + ")";
	}

}
