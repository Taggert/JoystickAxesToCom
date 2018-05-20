package taggert;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.SneakyThrows;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
public class GuiController implements Initializable {
    public Label left;
    public Label right;
    public Label up;
    public Label down;
    public TextField input;
    public Button submit;
    public TextArea textArea;

    private String tempText = null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        submit.setOnAction(e -> setTempText());


    }

    private void setTempText() {
        tempText = input.getText();
        input.requestFocus();
    }


    public void light(String x, String y) {
        switch (x) {
            case "1":
                left.getStyleClass().addAll("pressed");
                right.getStyleClass().removeAll("pressed");
                break;

            case "2":
                right.getStyleClass().addAll("pressed");
                left.getStyleClass().removeAll("pressed");
                break;

            case "0":
                left.getStyleClass().removeAll("pressed");
                right.getStyleClass().removeAll("pressed");
                break;

            default:
                throw new RuntimeException("wrong input");

        }

        switch (y) {
            case "1":
                up.getStyleClass().addAll("pressed");
                down.getStyleClass().removeAll("pressed");
                break;

            case "2":
                down.getStyleClass().addAll("pressed");
                up.getStyleClass().removeAll("pressed");
                break;

            case "0":
                up.getStyleClass().removeAll("pressed");
                down.getStyleClass().removeAll("pressed");
                break;

            default:
                throw new RuntimeException("wrong input");

        }
    }

    void print(String msg) {
        textArea.appendText(msg + "\n");
    }

    @SneakyThrows
    String getInput() {

        input.clear();
        tempText = null;
        while (!App.mainThread.isInterrupted() && (tempText == null || tempText.isEmpty())) {
            Thread.sleep(100);
        }
        return tempText;
    }


}
