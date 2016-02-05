public abstract class Type {
    private String name;

    public Type(String name) {
        this.name = name;
    }

    public abstract String makeHumanRadable(byte[] buf);

    public String getTypeName() {
        return name;
    }
}
