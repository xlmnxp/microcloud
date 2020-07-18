package sa.sy.lxd;

import elemental.json.JsonObject;

import java.io.IOException;

public class Project {
    private final Lxd client;
    private JsonObject project;
    private String name;
    private String description;
    private JsonObject config;
    private Instance[] usedBy;

    public Project(Lxd client, JsonObject project) throws Exception {
        this.client = client;
        this.project = project;
        this.name = this.project.getString("name");
        this.description = this.project.getString("description");
        this.config = this.project.getObject("config");

        Instance[] instances = new Instance[this.project.getArray("used_by").length()];
        for (int arrInd = 0; arrInd < instances.length; arrInd++) {
            instances[0] = client.getInstance(this.project.getArray("used_by").getString(arrInd));
        }

        this.usedBy = instances;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public JsonObject getConfig() {
        return config;
    }

    public Instance[] getUsedBy() {
        return usedBy;
    }
}
