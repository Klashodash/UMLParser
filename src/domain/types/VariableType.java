package domain.types;

import org.json.JSONObject;


public class VariableType {
    private String name;
    private String type;

    public VariableType(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

        obj.put("name", name);
        obj.put("type", type);

        return obj;
    }
}
