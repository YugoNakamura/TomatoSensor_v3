/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import stageControll.MainStageController;

/**
 * このプログラムのMainクラス<br>
 * mainStageの設定を行っている<br>
 * 特記がない限り参照型の引数を持つ関数にはnullを入れてはならない
 * @author NakamuraYugo
 */
public class Main extends Application {

    /**
     * mainStageのレイアウトを定義している"MainStageLayout.fxml"を読み込みmainStageに反映している<br>
     * この関数は内部で自動的に呼ばれるものである。
     * @param stage
     * @throws IOException 
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MainStageLayout.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        MainStageController mainStageController = loader.getController();
        mainStageController.setStage(stage);
        stage.setScene(scene);
        stage.setTitle("トマト流速計測システム");
        //Windowを閉じるときの処理
        stage.setOnCloseRequest(WindowEvent -> {
            //シリアル通信を切る
            mainStageController.getSettingStageController().getSerialIO().closeSerialPort();
            //すべてのウィンドウを閉じる
            Platform.exit();
        });
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
