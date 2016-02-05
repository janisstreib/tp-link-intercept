public class RawType extends Type {

    public RawType(String name) {
        super(name);
    }

    @Override
    public String makeHumanRadable(byte[] buf) {
        return Intercept.bytesToHex(buf);
    }

}
