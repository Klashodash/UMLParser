package domain.types;

import java.util.ArrayList;

public class ClassType {
    private String name;
    private ArrayList<MethodType> methods = new ArrayList<>();
    private ArrayList<VariableType> variables = new ArrayList<>();

    public ClassType(String name) {
        this.name = name;
    }

    public void addMethod(MethodType method) {
        methods.add(method);
    }

    public void addVariable(VariableType variable) {
        variables.add(variable);
    }

    public String toJSON() {
        return "{" +
                "\"name\": \"" + name + "\"" +
                "}";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<MethodType> getMethods() {
        return methods;
    }

    public ArrayList<VariableType> getVariables() {
        return variables;
    }
}
