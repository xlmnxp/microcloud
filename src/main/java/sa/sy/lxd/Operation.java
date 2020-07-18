package sa.sy.lxd;

import elemental.json.JsonObject;

public class Operation {
    public enum State {
        success,
        running,
        error
    }

    private final Lxd client;
    private JsonObject operation;
    private String id;

    public Operation(Lxd client, JsonObject operation) throws Exception {
        this.client = client;
        this.operation = operation;
        this.id = this.operation.getString("id");
    }

    public String doWait() throws Exception {
        return this.client.request(Lxd.Method.GET, this.id);
    }

    public boolean cancelable() throws Exception {
        return this.operation.getBoolean("may_cancel");
    }
}
