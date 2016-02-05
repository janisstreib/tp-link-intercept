public class StringType extends Type {

    public StringType(String name) {
        super(name);
    }

    @Override
    public String makeHumanRadable(byte[] buf) {
        return new String(buf);
    }

}
