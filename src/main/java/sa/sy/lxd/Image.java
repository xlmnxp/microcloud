package sa.sy.lxd;

import elemental.json.JsonObject;

import java.io.IOException;

public class Image {
    public enum Type {
        All,
        Container,
        VirtualMachine
    }
    private JsonObject image;
    private Lxd client;
    private boolean isPublic;
    private JsonObject properties;
    private String fingerprint;
    private String filename;
    private int size;
    private boolean autoUpdate;
    private Type type;

    public Image(Lxd client, JsonObject image) throws IOException {
        this.client = client;
        this.image = image;
        this.isPublic = this.image.getBoolean("public");
        this.properties = this.image.getObject("properties");
        this.fingerprint = this.image.getString("fingerprint");
        this.filename = this.image.getString("filename");
        this.size = (int) this.image.getNumber("size");
        this.autoUpdate = this.image.getBoolean("auto_update");

        if (this.image.getString("type").equals("virtual-machine")) {
            this.type = Type.VirtualMachine;
        } else if(this.image.getString("type").equals("container")) {
            this.type = Type.Container;
        }
    }

    public Type getType() {
        return type;
    }

    public String getFingerprint() {
        return fingerprint;
    }

    public JsonObject getProperties() {
        return properties;
    }
}
