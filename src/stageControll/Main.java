/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stageControll;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author NakamuraYugo
 */
public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
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
