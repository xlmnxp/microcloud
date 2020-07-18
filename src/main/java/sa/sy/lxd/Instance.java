package sa.sy.lxd;

import elemental.json.Json;
import elemental.json.JsonObject;
import java.io.IOException;

public class Instance implements Cloneable {
    public enum Type {
        Container,
        VirtualMachine
    }

    public enum State {
        START,
        STOP,
        RESTART,
        FREEZE,
        UNFREEZE
    };

    private final Lxd client;
    private String name = "-";
    private String type = "-";
    private String status = "-";
    private JsonObject instance;

    public Instance(Lxd client, JsonObject instance) throws IOException {
        this.client = client;
        this.instance = instance;
        this.name = this.instance.getString("name");
        this.type = this.instance.getString("type");
        this.status = this.instance.getString("status");
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status.toUpperCase();
    }

    public JsonObject getState() throws Exception {
        return Json.parse(client.request(Lxd.Method.GET, "/1.0/instances/" + this.name + "/state")).getObject("metadata");
    }

    public String getIPv4() throws Exception {
        JsonObject network = this.getState().getObject("network").getObject("eth0").getArray("addresses").getObject(0);
        if(!network.getString("family").equalsIgnoreCase("inet"))
            throw new Exception("IPv4 not found");
        return network.getString("address");
    }

    public String getIPv6() throws Exception {
        JsonObject network = this.getState().getObject("network").getObject("eth0").getArray("addresses").getObject(1);
        if(!network.getString("family").equalsIgnoreCase("inet6"))
            throw new Exception("IPv6 not found");
        return network.getString("address");
    }

    public void setState(State state) throws Exception {
        JsonObject jsonBody = Json.createObject();
        jsonBody.put("action", state.toString().toLowerCase());
//        jsonBody.put("force", true);
        jsonBody.put("timeout", -1);
        String jsonBodyString = jsonBody.toJson();
        client.request(Lxd.Method.PUT, "/1.0/instances/" + this.name + "/state", "Content-type: text/json\r\n" + "Content-length: " + jsonBodyString.length(), jsonBodyString);
    }

    public boolean isContainer() {
        if(this.type.equalsIgnoreCase("container"))
            return true;

        return false;
    }

    public boolean isVirtualMachine() {
        if(this.type.equalsIgnoreCase("virtual-machine"))
            return true;

        return false;
    }
}
