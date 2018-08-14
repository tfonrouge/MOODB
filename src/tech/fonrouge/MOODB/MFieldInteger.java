package tech.fonrouge.MOODB;

public class MFieldInteger extends MField<Integer> {

    protected MFieldInteger(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Integer emptyValue() {
        return 0;
    }
}
