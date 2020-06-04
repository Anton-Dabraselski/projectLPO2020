package projectLPO.visitors.evaluation;

import projectLPO.parser.SeasonTypeConvertor;

public class SeasonValue extends PrimValue<Integer>{

    public SeasonValue(Integer value) {
        super(value);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof SeasonValue))
            return false;
        return value.equals(((SeasonValue) obj).value);
    }

    @Override
    public boolean less(Value object) {
        if (this == object)
            return false;

        return SeasonTypeConvertor.toInt(this.toSeason()) < SeasonTypeConvertor.toInt(object.toSeason());
    }

    /*@Override
    public int toInt() {
        return value;
    }*/

    @Override
    public String toSeason() {
        return SeasonTypeConvertor.toString(value);
    }

    @Override
    public String toString() {
        return toSeason();
    }
}
