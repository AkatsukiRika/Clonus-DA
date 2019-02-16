package Dance;

import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

public class startScreenController implements Initializable {
    @FXML
    ListView musicList;
    @FXML
    TextArea musicInfo;
    @FXML
    Button startBtn;
    // 建立播放器
    Media[] media = new Media[1]; //防止等下内部类里面用不了
    MediaPlayer[] mediaPlayer = new MediaPlayer[1]; //同理，防编译器w
    // 当前选择的歌曲ID
    int index = -1;

    public void startAct(ActionEvent e) throws Exception {
        // 把当前选好的曲子记录下来，写入一个二进制文件
        if(index != -1) {
            // 若选好歌曲
            File file = new File("src/Dance/s.dat");
            if(file.exists()) file.delete(); //若存在文件，就先删除掉
            DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
            out.writeInt(index);
            System.out.println("已写入：" + index);
            out.close();
        }
        else
            // 如果没选好歌曲，直接不跳转
            return;

        mediaPlayer[0].stop();
        ((Node) (e.getSource())).getScene().getWindow().hide();
        gameScreen game = new gameScreen();
        game.start(new Stage());
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // 读取music.txt文件，把歌曲名写入列表
            ArrayList<String> arrayList = new ArrayList<>();
            ObservableList<String> music = FXCollections.observableArrayList();

            File file = new File("src/Dance/music.txt");
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(inputReader);

            String str;
            while((str=bf.readLine())!=null)
                arrayList.add(str);

            bf.close();
            inputReader.close();

            for(int i=0; i<arrayList.size(); i+=6) {
                System.out.println(arrayList.get(i)); //LOG
                String name = arrayList.get(i);
                music.add(name);
            }
            musicList.setItems(music);

            //开始播放游戏BGM。
            String bgm = getClass().getResource("0.mp3").toString();
            media[0] = new Media(bgm);
            mediaPlayer[0] = new MediaPlayer(media[0]);
            mediaPlayer[0].play();
            mediaPlayer[0].setCycleCount(Timeline.INDEFINITE);

            // 选中一首歌曲时，显示这首歌曲的基本信息，并播放预览
            musicList.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue observable, String oldValue, String newValue) {
                        // 显示基本信息的代码
                        System.out.println("newValue: " + newValue);
                        String info = "";
                        index = arrayList.indexOf(newValue);
                        String artist = arrayList.get(index+1);
                        String bpm = arrayList.get(index+4);
                        String mode = arrayList.get(index+5);
                        info += "歌曲名：" + newValue + '\n';
                        info += "艺术家：" + artist + "\n";
                        info += "BPM：" + bpm + "\n";
                        info += "模式：" + mode;
                        musicInfo.setText(info);
                        // 播放预览的代码
                        mediaPlayer[0].stop();
                        String mp3 = getClass().getResource(arrayList.get(index+2)).toString();
                        media[0] = new Media(mp3); //这样就能在内部类用了
                        mediaPlayer[0] = new MediaPlayer(media[0]); //与编译器斗，其乐无穷！
                        mediaPlayer[0].play();
                        mediaPlayer[0].setCycleCount(Timeline.INDEFINITE);
                    }
                }
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
