package Dance;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

public class gameScreenController implements Initializable {
    @FXML
    ProgressBar rhythmBar;
    @FXML
    TextField command;
    @FXML
    Pane pane; // 整个游戏面板
    @FXML
    TextField playerInput;
    @FXML
    Label score; // 分数
    @FXML
    Label judgement; // 判定

    // Timeline动画
    Timeline rhythm;

    // 建立播放器
    Media[] media = new Media[1]; //防止等下内部类里面用不了
    MediaPlayer[] mediaPlayer = new MediaPlayer[1]; //同理，防编译器w

    ArrayList<String> arrayList; //谱面文件
    double timeMs = 0; //节奏条计时器
    double time; //指令输入间隔
    int pos = 8; //谱面位置，从第8行开始
    String in = ""; //玩家通过方向键输入的字符
    int points = 0; //当前得分
    String cmd; //谱面的内容(数字)
    int max = 0; //歌曲满分

    // 下面是记录玩家的判定了
    int judgePf = 0;
    int judgeGr = 0;
    int judgeGd = 0;
    int judgePr = 0;

    public void keyPressed(KeyEvent key) {
        /*
        * 检测全局的按键动作
        * 如果检测到按下的是空格键，则进行判定
        * 判定内容：节奏准确度
        * 判断方式：时机(timing)
        * 位于正负100ms外 -> POOR -> 不加分
        * 位于正负75~100ms -> GOOD -> 1000分
        * 位于正负75~50ms -> GREAT -> 2000分
        * 位于正负50ms内 -> PERFECT -> 4000分
        *
        * 判定内容：输入准确度
        * 判断方式：将command和playerInput中的内容进行比对
        * 比对方式是“最长子序列”算法（匹配的字符不需要连续）
        * 分数加算 = 最长子序列长度*1000
        *
        * 如果检测到的是四个方向键，则将内容写入玩家输入栏
        */
        if(key.getCode() == KeyCode.SPACE) {
            double timing; // 按键时机
            if(timeMs > time/2)
                timing = timeMs - time;
            else
                timing = timeMs;

            // 判定SPACE键的节奏点
            if(Math.abs(timing)>100) {
                judgePr++;
                judgement.setText("POOR...");
                judgement.setVisible(true);
                max += 4000;
                score.setText(String.valueOf(points));
            }
            else if(Math.abs(timing)>75 && Math.abs(timing)<100) {
                judgeGd++;
                judgement.setText("GOOD.");
                judgement.setVisible(true);
                points += 1000;
                max += 4000;
                score.setText(String.valueOf(points));
            }
            else if(Math.abs(timing)>50 && Math.abs(timing)<75) {
                judgeGr++;
                judgement.setText("GREAT!");
                judgement.setVisible(true);
                points += 2000;
                max += 4000;
                score.setText(String.valueOf(points));
            }
            else {
                judgePf++;
                judgement.setText("PERFECT!!!");
                judgement.setVisible(true);
                points += 4000;
                max += 4000;
                score.setText(String.valueOf(points));
            }

            // 根据框内的内容判断出输入得分。
            // 这几条代码搞了很久，不要动，动就出BUG。
            String early = arrayList.get(pos-2);
            String late = arrayList.get(pos-1);
            if(timing < 0) {
                String a = convert(late);
                String b = convert(playerInput.getText());
                if(b.length()>0)
                    points += LCS(a, b) * 1000;
                max += a.length()*1000;
                String c = String.valueOf(points);
                score.setText(String.join("", Collections.nCopies(7-c.length(), "0")) + c);
            }
            else {
                String a = "我知道这里是重复的，但我暂时不想改";
                String a1 = convert(early);
                String b1 = convert(playerInput.getText());
                if(b1.length()>0)
                    points += LCS(a1, b1) * 1000;
                max += a1.length()*1000;
                String c1 = String.valueOf(points);
                score.setText(String.join("", Collections.nCopies(7-c1.length(), "0")) + c1);
            }

            // 清空玩家输入栏
            in = "";
            playerInput.setText(in);
        }

        // 根据键盘输入，设定框内的内容
        if(key.getCode() == KeyCode.LEFT) {
            in += "←";
            playerInput.setText(in);
        }
        if(key.getCode() == KeyCode.RIGHT) {
            in += "→";
            playerInput.setText(in);
        }
        if(key.getCode() == KeyCode.UP) {
            in += "↑";
            playerInput.setText(in);
        }
        if(key.getCode() == KeyCode.DOWN) {
            in += "↓";
            playerInput.setText(in);
        }
    }

    public int LCS(String s1, String s2) {
        // 计算最长子序列
        int[][] c = new int[s1.length()][s2.length()];

        for(int i=0; i<s1.length();i++) {
            if(s1.charAt(i) == s2.charAt(0)) {
                c[i][0] = 1;
            }
        }

        for(int j = 0; j<s2.length();j++) {
            if(s1.charAt(0) == s2.charAt(j)){
                c[0][j] = 1;
            }
        }

        for(int i = 1; i < s1.length(); i++) {
            for(int j = 1; j < s2.length(); j++) {
                if(s1.charAt(i) == s2.charAt(j)) {
                    c[i][j] = c[i - 1][j - 1] + 1;
                }
                else if (c[i][j - 1] > c[i - 1][j]) {
                    c[i][j] = c[i][j - 1];
                }
                else {
                    c[i][j] = c[i - 1][j];
                }
            }
        }
        return c[s1.length() - 1][s2.length() - 1];
    }

    public String convert(String str) {
        // 负责将数字字符串转换成方向再return
        String temp[] = str.split("0");
        if(temp.length != 0) {
            temp[0] = temp[0].replaceAll("8", "↑");
            temp[0] = temp[0].replaceAll("2", "↓");
            temp[0] = temp[0].replaceAll("4", "←");
            temp[0] = temp[0].replaceAll("6", "→");
            return temp[0];
        }
        else return "";
    }

    public void rhythm(ActionEvent e) {
        /*
        * 对节奏条进行设定，规则如下：
        * 当time < 每两次指令间隔时间时，继续计时(单位1ms)，同时设定进度条
        * 当time = 每两次指令间隔时间时，进度条到头，然后返回0
        * 循环以上两步
        * 同时，当每次timeMs == time时，
        * 放出下一条指令并打印在屏幕上
        */
        if(timeMs < time) {
            timeMs++;
            // System.out.println("timeMs = " + timeMs);
            rhythmBar.setProgress(timeMs/time);
        }
        if(timeMs == (int)time/2) {
            // 将判定置为不显示
            // System.out.println("非表示设定中");
            judgement.setVisible(false);
        }
        if(timeMs == time) {
            if(pos == arrayList.size()) {
                /*
                * 游戏结束，生成结果文件(二进制形式)。具体内容如下：
                * 文件名：result.dat
                * 第1个int：游戏得分
                * 第2个int：游戏节奏得分
                * 第3个int：游戏按键得分
                * 第4个int：PERFECT数
                * 第5个int：GREAT数
                * 第6个int：GOOD数
                * 第7个int：POOR数
                * 第8个int：游戏满分
                * 第9个str：歌曲名
                */
                try {
                    int rhythmPts = judgePr * 4000 + judgeGr * 2000 + judgeGd * 1000;
                    File file = new File("src/Dance/result.dat");
                    if (file.exists()) file.delete();
                    DataOutputStream out = new DataOutputStream(new FileOutputStream(file));
                    out.writeInt(points);
                    out.writeInt(rhythmPts);
                    out.writeInt(points - rhythmPts);
                    out.writeInt(judgePf);
                    out.writeInt(judgeGr);
                    out.writeInt(judgeGd);
                    out.writeInt(judgePr);
                    out.writeInt(max);
                    out.writeUTF(arrayList.get(1));
                    rhythm.stop();
                    // 显示最终结果界面
                    resultScreen result = new resultScreen();
                    result.start(new Stage());
                    // 关闭当前页面
                    Stage stage = (Stage) pane.getScene().getWindow();
                    stage.hide();
                    return;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            cmd = arrayList.get(pos);
            String cmds = convert(cmd);
            command.setText(cmds);

            pos++;
            timeMs = 0;
            // System.out.println("timeMs = " + timeMs);
            rhythmBar.setProgress(0);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // 把存所有歌曲信息的文件先读进来
            arrayList = new ArrayList<>();

            File file = new File("src/Dance/music.txt");
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bf = new BufferedReader(inputReader);

            String str;
            while((str=bf.readLine())!=null)
                arrayList.add(str);

            bf.close();
            inputReader.close();

            // 先读二进制文件，读出选择的歌曲名在music.txt中的行数，匹配mp3并播放
            DataInputStream in = new DataInputStream(new FileInputStream("src/Dance/s.dat"));
            int index = in.readInt();
            String mp3 = getClass().getResource(arrayList.get(index+2)).toString();
            System.out.println("选取的歌曲index为：" + index);
            System.out.println("mp3名字为：" + mp3);

            media[0] = new Media(mp3);
            mediaPlayer[0] = new MediaPlayer(media[0]);
            mediaPlayer[0].play();

            // 读取谱面文件，改写arrayList内容
            String txt = arrayList.get(index+3);
            arrayList.clear();
            file = new File("src/Dance/" + txt);
            inputReader = new InputStreamReader(new FileInputStream(file));
            bf = new BufferedReader(inputReader);

            while((str=bf.readLine())!=null)
                arrayList.add(str);

            bf.close();
            inputReader.close();

            // 使节奏条开始动
            rhythmBar.setProgress(0);
            rhythm = new Timeline(new KeyFrame(Duration.millis(1), ex->rhythm(new ActionEvent())));
            rhythm.setCycleCount(Timeline.INDEFINITE);
            rhythm.play();

            // 进行谱面相关操作
            time = (Double.parseDouble(arrayList.get(4)))*1000; //获取指令输入间隙时间，转成毫秒

            // 打log调试专用
            System.out.println("间隔时间：" + time);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
