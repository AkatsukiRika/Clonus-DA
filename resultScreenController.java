package Dance;

import SudokuKaiden.StartScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class resultScreenController implements Initializable {
    @FXML
    Pane pane;
    @FXML
    Label title, maxScore;
    @FXML
    Label gameScore, rhythmPoints, inputGain, perfectCnt, greatCnt, goodCnt, poorCnt, rank;

    public void jump(KeyEvent key) throws Exception {
        if(key.getCode() == KeyCode.J) {
            Stage stage = (Stage) pane.getScene().getWindow();
            stage.hide();
            startScreen start = new startScreen();
            start.start(new Stage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //读取result.dat，获取各项信息
        try {
            File file = new File("src/Dance/result.dat");
            DataInputStream in = new DataInputStream(new FileInputStream(file));
            gameScore.setText(String.valueOf(in.readInt()));
            rhythmPoints.setText(String.valueOf(in.readInt()));
            inputGain.setText(String.valueOf(in.readInt()));
            perfectCnt.setText(String.valueOf(in.readInt()));
            greatCnt.setText(String.valueOf(in.readInt()));
            goodCnt.setText(String.valueOf(in.readInt()));
            //int poorCnt = in.readInt();
            poorCnt.setText(String.valueOf(in.readInt()));
            maxScore.setText(String.valueOf(in.readInt()));
            title.setText(in.readUTF());

            /*
            * RANK的测定方式：游戏总分/满分
            * 100%：MAXIMUM
            * 98%：EXCELLENT
            * 90%：SUPREME
            * 80%：WELL-DONE
            * 70%：SAFE
            * 70%以下：ERROR
            */
            double rate = Double.parseDouble(gameScore.getText()) / Double.parseDouble(maxScore.getText());
            if(rate == 1)
                rank.setText("" + (int)(rate*100) + "%");
            else if(rate > 0.98)
                rank.setText("" + (int)(rate*100) + "%");
            else if(rate > 0.90)
                rank.setText("" + (int)(rate*100) + "%");
            else if(rate > 0.80)
                rank.setText("" + (int)(rate*100) + "%");
            else if(rate > 0.70)
                rank.setText("" + (int)(rate*100) + "%");
            else
                rank.setText("" + (int)(rate*100) + "%");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
