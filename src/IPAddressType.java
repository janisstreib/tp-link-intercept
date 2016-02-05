public class IPAddressType extends Type {

    public IPAddressType(String name) {
        super(name);
    }

    @Override
    public String makeHumanRadable(byte[] buf) {
        return (buf[0] & 0xFF) + "." + (buf[1] & 0xFF) + "." + (buf[2] & 0xFF)
                + "." + (buf[3] & 0xFF);
    }

}
