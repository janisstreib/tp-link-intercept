public class BooleanType extends Type {

    public BooleanType(String name) {
        super(name);
        // TODO Auto-generated constructor stub
    }

    @Override
    public String makeHumanRadable(byte[] buf) {
        if (buf[0] == 1) {
            return "True";
        }
        return "False";
    }

}
