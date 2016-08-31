package domain.types;

import org.json.JSONObject;

import java.util.ArrayList;

public class MethodType {
    private String name;
    private String returnType;
    private ArrayList<VariableType> parameters = new ArrayList<>();

    public MethodType(String name, String returnType, String parameters) {
        this.name = name;
        this.returnType = returnType;
        parseParameters(parameters);
    }

    private void parseParameters(String parameters) {
        if (parameters.equals("")) {
            return;
        }
        String[] p = parameters.split(",");
        for (String s : p) {
            s = s.trim();
            String[] split = s.split(" ");
            this.parameters.add(new VariableType(split[1], split[0]));
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    public JSONObject toJSON() {
        JSONObject obj = new JSONObject();

        obj.put("name", name);
        obj.put("returnType", returnType);
        obj.put("parameters", parameters);

        return obj;
    }
}
