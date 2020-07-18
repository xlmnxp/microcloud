/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sa.sy.lxd;

import java.io.File;
import java.nio.charset.StandardCharsets;

import com.vaadin.flow.component.notification.Notification;
import elemental.json.*;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

/**
 *
 * @author xlmnxp
 */
public class Lxd {
    protected final File socketFile = new File("/var/snap/lxd/common/lxd/unix.socket");
    AFUNIXSocket client;
    
    enum Method {
        GET,
        POST,
        PUT,
        DELETE
    }

    public Lxd() throws Exception {
        client = AFUNIXSocket.newInstance();
    }
    
    void connect() throws Exception {
        client.connect(new AFUNIXSocketAddress(socketFile));
    }
    
    void close() throws Exception {
        client.close();
    }
    
    String request(Method method, String path) throws Exception {
        return this.request(method, path, "", "");
    }

    String request(Method method, String path, String header, String body) throws Exception {
        connect();
        String requestString = (method.toString() + " " + path + " HTTP/1.0" + (header.length() > 0 ? "\r\n" + header : "") + "\r\n\r\n" + body);
        client.getOutputStream().write(requestString.getBytes());
        byte[] buffer = new byte[client.getReceiveBufferSize()];
        client.getInputStream().read(buffer);
        return new String(buffer, StandardCharsets.UTF_8).split("\r\n\r\n")[1];
    }
    
    public Instance getInstance(String name) throws Exception {
        return new Instance(this, Json.parse((String) this.request(Method.GET, name)).getObject("metadata"));
    }

    public Instance[] getInstances() throws Exception {
        JsonArray instances = Json.parse(this.request(Method.GET, "/1.0/instances")).getArray("metadata");
        Instance[] instancesList = new Instance[instances.length()];
        for (int i = 0; i < instancesList.length; i++) {
            instancesList[i] = this.getInstance(instances.getString(i));
        }

        return instancesList;
    }

    public Operation createInstance(String name, Instance.Type type, String fingerPrint) throws Exception {
        JsonObject jsonBody = Json.createObject();
        jsonBody.put("name", name);
        jsonBody.put("architecture", "x86_64");
        JsonArray profiles = Json.createArray();
        profiles.set(0, "default");
        jsonBody.put("profiles", profiles);
        jsonBody.put("ephemeral", false);

        JsonObject config = Json.createObject();
//        config.put("limits.cpu", "2");
        jsonBody.put("config", config);

        if(type == Instance.Type.Container) {
            jsonBody.put("type", "container");
        } else if (type == Instance.Type.VirtualMachine) {
            jsonBody.put("type", "virtual-machine");
        }

        JsonObject source = Json.createObject();
        source.put("type", "image");
        source.put("fingerprint", fingerPrint);
        jsonBody.put("source", source);
        String jsonBodyString = jsonBody.toJson();
        String result = this.request(Lxd.Method.POST, "/1.0/instances", "Content-type: text/json\r\n" + "Content-length: " + jsonBodyString.length(), jsonBodyString);

        return this.getOperation(Json.parse(result).getString("operation"));
    }


    public Image getImage(String name) throws Exception {
        return new Image(this, Json.parse(this.request(Method.GET, name)).getObject("metadata"));
    }

    public Image[] getImages() throws Exception {
        JsonArray images = Json.parse(this.request(Method.GET, "/1.0/images")).getArray("metadata");
        Image[] imagesList = new Image[images.length()];
        for (int i = 0; i < imagesList.length; i++) {
            imagesList[i] = this.getImage(images.getString(i));
        }

        return imagesList;
    }

    public Image[] getImages(Image.Type type) throws Exception {
        Image[] images = this.getImages();
        Image[] tmpImages = new Image[images.length];
        int arrLen = 0;
        for (Image image : images) {
            if (image.getType() == Image.Type.All || image.getType() == type) {
                tmpImages[arrLen++] = image;
            }
        }

        Image[] finalImages = new Image[arrLen];
        for (int arrInd = 0; arrInd < arrLen; arrInd++) {
            finalImages[arrInd] = tmpImages[arrInd];
        }

        return finalImages;
    }

    public Project getProject(String name) throws Exception {
        return new Project(this, Json.parse(this.request(Method.GET, name)).getObject("metadata"));
    }

    public Project[] getProjects() throws Exception {
        JsonArray projects = Json.parse(this.request(Method.GET, "/1.0/projects")).getArray("metadata");
        Project[] projectsList = new Project[projects.length()];
        for (int i = 0; i < projectsList.length; i++) {
            projectsList[i] = this.getProject(projects.getString(i));
        }

        return projectsList;
    }

    public Operation getOperation(String name) throws Exception {
        return new Operation(this, Json.parse(this.request(Method.GET, name)).getObject("metadata"));
    }

    public Operation[] getOperations(Operation.State state) throws Exception {
        JsonArray operations = Json.parse(this.request(Method.GET, "/1.0/operations")).getObject("metadata").getArray(state.toString().toLowerCase());
        Operation[] operationsList = new Operation[operations.length()];
        for (int i = 0; i < operationsList.length; i++) {
            operationsList[i] = this.getOperation(operations.getString(i));
        }

        System.out.println("I found your executionz");

        return operationsList;
    }
}
