package tech.fonrouge.MOODB;

public class MFieldInteger extends MField<Integer> {

    public MFieldInteger(MTable owner, String name) {
        super(owner, name);
    }

    @Override
    public Integer emptyValue() {
        return 0;
    }
}
