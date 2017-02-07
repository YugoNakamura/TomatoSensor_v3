/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stageControll;

import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TooManyListenersException;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import serialControll.SerialEventListener;
import serialControll.SerialIO;

/**
 * FXML Controller class
 *
 * @author 中村 勇吾
 */
public class SettingStageController implements Initializable {
    @FXML
    private ChoiceBox<String> portChoiceBox;
    private SerialIO serialIO;
    //現在選択されているCOMポート名
    private String selectedCOMName;
    //前に選択されていたCOMポート名
    private String prevCOMName = "";
    
    @FXML
    private CheckBox sensorCheckBox1,sensorCheckBox2,sensorCheckBox3,sensorCheckBox4,sensorCheckBox5,sensorCheckBox6;
    private ArrayList<CheckBox> sensorCheckBoxList;
    
    private boolean[] selectedSensor = new boolean[6];
    private Stage settingStage;
    
    @FXML
    private void submit(ActionEvent event) {
        //checkBoxの状態を保存する
        for(int i=0;i<selectedSensor.length;i++) {
            selectedSensor[i] = sensorCheckBoxList.get(i).isSelected();
        }
        //choiceBoxの状態を保存する
        selectedCOMName = portChoiceBox.getValue();
        
        //何らかのCOMポートが選択され、前回選択されたポートとは別のポートが選択されたとき接続処理を開始する
        if (selectedCOMName != null && !selectedCOMName.matches(prevCOMName)) {
            prevCOMName = selectedCOMName;
            serialIO.closeSerialPort();
            try {
                serialIO.openSerialPort(selectedCOMName);
                serialIO.getSerialPort().addEventListener(new SerialEventListener(serialIO.getSerialPort()));
            } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException | TooManyListenersException ex) {
                ex.printStackTrace();
            }
            //Eventがあったとき、EventListenerに伝える
            serialIO.getSerialPort().notifyOnDataAvailable(true);
        }
        //settingStageを閉じる(隠す)
        settingStage.hide();
    }
    
    @FXML
    private void cancel(ActionEvent event) {
        settingStage.hide();
    }
    
    @FXML
    private void reloadCOM(ActionEvent event) {
        List connectableCOMport = serialIO.getSetialPortList();
        portChoiceBox.setItems(FXCollections.observableArrayList(connectableCOMport));
    }
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        serialIO = new SerialIO();
        
        sensorCheckBoxList = new ArrayList<>();
        sensorCheckBoxList.add(sensorCheckBox1);
        sensorCheckBoxList.add(sensorCheckBox2);
        sensorCheckBoxList.add(sensorCheckBox3);
        sensorCheckBoxList.add(sensorCheckBox4);
        sensorCheckBoxList.add(sensorCheckBox5);
        sensorCheckBoxList.add(sensorCheckBox6);
        
        for(int i=0;i<selectedSensor.length;i++) {
            selectedSensor[i] = sensorCheckBoxList.get(i).isSelected();
        }
        
        List connectableCOMport = serialIO.getSetialPortList();
        portChoiceBox.setItems(FXCollections.observableArrayList(connectableCOMport));
    }
    
    public String getSelectedCOMName() {
        return selectedCOMName;
    }
    
    public boolean[] getSelectedSensor() {
        return selectedSensor;
    }
    
    public SerialIO getSerialIO() {
        return serialIO;
    }
    public void setStage(Stage stage) {
        this.settingStage = stage;
    }
}
