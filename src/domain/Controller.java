package domain;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.MenuItem;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedList;

public class Controller {
    public MenuItem newItem;
    public MenuItem openItem;
    public MenuItem closeItem;
    public MenuItem saveItem;
    public MenuItem saveAsItem;
    public MenuItem export;
    public MenuItem quit;
    public AnchorPane canvasPane;
    public Canvas canvas = new Canvas(800, 800);
    public String className = "";
    public LinkedList<String> variableList = new LinkedList<>();
    public LinkedList<String> methodList = new LinkedList<>();
    private int greatestLength = 0;

    public void initialize() {
        newItem.setOnAction(this::createNew);
        openItem.setOnAction(this::openItem);
        closeItem.setOnAction(this::close);
        quit.setOnAction(this::close);
        saveItem.setOnAction(this::saveItem);
        saveAsItem.setOnAction(this::saveAsItem);
        export.setOnAction(this::export);
        canvasPane.setStyle("-fx-background-color: white");
        canvasPane.getChildren().add(canvas);
    }

    private void close(ActionEvent actionEvent) {
        System.exit(0);
    }

    private void saveAsItem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As...");
        fileChooser.showSaveDialog(openItem.getParentPopup());
    }

    private void saveItem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export...");
        fileChooser.showSaveDialog(openItem.getParentPopup());
    }

    private void createNew(ActionEvent event) {
        canvasPane.getChildren().clear();
        canvas = new Canvas(800, 800);
        canvasPane.getChildren().add(canvas);
        className = "";
        variableList.clear();
        methodList.clear();
        greatestLength = 0;
    }

    private void export(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export...");
        File f = fileChooser.showSaveDialog(openItem.getParentPopup());
        WritableImage image = canvas.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        try {
            ImageIO.write(bufferedImage, "png", f);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openItem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open...");
        Path f;
        try {
            f = fileChooser.showOpenDialog(openItem.getParentPopup()).toPath();
        } catch (Exception e) {
            return;
        }
        Parser parser = new Parser();
        JSONObject jsonObject = parser.parse(f);
        getElements(jsonObject);
        drawClass();
    }

    private void getElements(JSONObject jsonObject) {
        className = jsonObject.getString("name");
        if (className.length() > greatestLength) {
            greatestLength = className.length();
        }
        JSONArray variables = jsonObject.getJSONArray("variables");
        for (int i = 0; i < variables.length(); ++i) {
            JSONObject v = variables.getJSONObject(i);
            String s = String.format("%s: %s", v.getString("name"), v.getString("type"));
            if (s.length() > greatestLength) {
                greatestLength = s.length();
            }
            variableList.add(s);
        }
        JSONArray methods = jsonObject.getJSONArray("methods");
        for (int i = 0; i < methods.length(); ++i) {
            JSONObject m = methods.getJSONObject(i);
            JSONArray parameters = m.getJSONArray("parameters");
            String params = "(";
            for (int j = 0; j < parameters.length(); j++) {
                JSONObject p = parameters.getJSONObject(j);
                params += (j == parameters.length() - 1) ? String.format("%s: %s", p.getString("name"), p.getString("type")) : String.format("%s: %s, ", p.getString("name"), p.getString("type"));
            }
            params += ")";
            String s = String.format("%s%s: %s", m.getString("name"), params, m.getString("returnType"));
            if (s.length() > greatestLength) {
                greatestLength = s.length();
            }
            methodList.add(s);
        }
    }

    private void drawClass() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int classHeight = 50;
        int maxWidth = greatestLength * 5;
        gc.fillText(className, 100, classHeight, maxWidth);
        gc.strokeRect(90, 30, maxWidth + 20, 30);
        int variableLine = classHeight + 10;
        int y = variableLine;
        for (String s : variableList) {
            y += 25;
            gc.fillText(s, 100, y, maxWidth);
        }
        int methodLine = y - classHeight;
        gc.strokeRect(90, variableLine, maxWidth + 20, methodLine);
        y += 10;
        for (String s : methodList) {
            y += 25;
            gc.fillText(s, 100, y, maxWidth);
        }
        gc.strokeRect(90, variableLine + methodLine, maxWidth + 20, y - (variableLine + methodLine - 10));
    }
}
