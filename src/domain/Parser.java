package domain;

import org.json.JSONObject;
import domain.types.ClassType;
import domain.types.MethodType;
import domain.types.VariableType;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private boolean comment = false;
    private boolean end = false;
    private boolean foundClass = false;
    private boolean methodBody = false;
    private ClassType classType = null;
    private Pattern getClassName = Pattern.compile("(.* class) (\\w+)");
    private Pattern getVariables = Pattern.compile("(private |public |protected )?(static )?(final )?([a-zA-Z]+?)(<.*>)? ([a-zA-Z0-9]+)[;| =]");
    private Pattern getConstructor = Pattern.compile("(private |public |protected )([a-zA-Z0-9]+)\\((.*)\\) \\{");
    private Pattern getMethodHeader = Pattern.compile("(private |public |protected )?(static )?(?!public)([a-zA-Z0-9]+) ([a-zA-Z0-9]+)\\((.*)\\)[^;]");
    private Stack<String> brackets = new Stack<>();

    public Parser() {

    }

    private void loadLanguageFile(Language language) {
        Path file = null;
        switch (language) {
            case JAVA:
                file = FileSystems.getDefault().getPath("src/languageFiles", "java.lang");
                break;
            default:
                break;
        }
    }

    public JSONObject parse(Path file) {
        BufferedReader reader;
        try {
            reader = Files.newBufferedReader(file, StandardCharsets.UTF_8);
            String l;
            while ((l = reader.readLine()) != null) {
                if (checkComment(l))
                    continue;
                Matcher m;
                if (!foundClass) {
                    m = getClassName.matcher(l);
                    if (m.find()) {
                        classType = new ClassType(m.group(2));
                        foundClass = true;
                        continue;
                    }
                }
                m = getConstructor.matcher(l);
                if (m.find() && classType != null) {
                    if (brackets.isEmpty()) {
                        MethodType method = new MethodType(m.group(2), "", m.group(3));
                        classType.addMethod(method);
                        brackets.push("{");
                        continue;
                    }
                }
                m = getMethodHeader.matcher(l);
                if (m.find() && classType != null) {
                    if (brackets.isEmpty()) {
                        MethodType method = new MethodType(m.group(4), m.group(3), m.group(5));
                        classType.addMethod(method);
                        brackets.push("{");
                        continue;
                    }
                }
                m = getVariables.matcher(l);
                if (m.find() && classType != null) {
                    if (brackets.isEmpty()) {
                        classType.addVariable(new VariableType(m.group(6), m.group(4)));
                        continue;
                    }
                }
                if (l.contains("{") && !l.contains("\"{\"") && classType != null) {
                    brackets.push("{");
                }
                if (l.contains("}") && !l.contains("\"}\"") && !brackets.isEmpty() && classType != null) {
                    brackets.pop();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return makeJSONObject();
    }

    private JSONObject makeJSONObject() {
        JSONObject classObject = new JSONObject();

        classObject.put("name", classType.getName());

        ArrayList<JSONObject> methods = new ArrayList<>();

        for (MethodType m : classType.getMethods()) {
            methods.add(m.toJSON());
        }

        classObject.put("methods", methods);

        ArrayList<JSONObject> variables = new ArrayList<>();

        for (VariableType v : classType.getVariables()) {
            variables.add(v.toJSON());
        }

        classObject.put("variables", variables);

        return classObject;
    }

    private boolean checkComment(String l) {
        if (end) {
            comment = false;
            end = false;
        }
        if (l.contains("/**") || l.contains("/*") || l.startsWith("//"))
            comment = true;
        if (l.contains("*/"))
            end = true;
        return comment;
    }
}
