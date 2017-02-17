/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stageControll;

import util.DateAndFile;
import flowSeries.SapFlowSeries;
import flowSeries.SapFlowToXYSeries;
import java.io.File;
import java.io.IOException;
import stageControll.table.TableData;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.WritableImage;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;

/**
 * 最初に出てくるウィンドウの振る舞いを設定しているクラス
 *
 * @author NakamuraYugo
 */
public class MainStageController implements Initializable {

    private Stage mainStage;
    /**
     *
     */
    @FXML
    private LineChart<String, Double> mainChart;
    @FXML
    private Tab tab1, tab2, tab3, tab4, tab5, tab6;
    private List<Tab> tabList;
    @FXML
    private LineChart<String, Double> sensorChart1, sensorChart2, sensorChart3, sensorChart4, sensorChart5, sensorChart6;
    private List<LineChart<String, Double>> sensorChartList;

    @FXML
    private TableView<TableData> table1, table2, table3, table4, table5, table6;
    private List<TableView<TableData>> tableList;

    @FXML
    private TableColumn<TableData, String> timeCol1, timeCol2, timeCol3, timeCol4, timeCol5, timeCol6;
    private List<TableColumn<TableData, String>> timeColList;

    @FXML
    private TableColumn<TableData, String> valueCol1, valueCol2, valueCol3, valueCol4, valueCol5, valueCol6;
    private List<TableColumn<TableData, String>> valueColList;

    @FXML
    private Label sumRateLabel1, sumRateLabel2, sumRateLabel3, sumRateLabel4, sumRateLabel5, sumRateLabel6;
    private List<Label> sumRateLabelList;

    private List<SapFlowSeries> flowSeriesList;
    private List<XYChart.Series<String, Double>> chartSeries;
    private SapFlowSeries sapFlowSeries;

    @FXML
    private Button settingButton, sumRateButton,updateBtn;
    @FXML
    Label dateLabel;
    /**
     * グラフに表示する日付を変更するボタン。次の日に変更される。
     */
    @FXML
    private Button nextBtn;
    /**
     * グラフに表示する日付を変更するボタン。前の日に変更される。
     */
    @FXML
    private Button prevBtn;

    //SettingStage関係
    private Stage settingStage;
    private SettingStageController settingStageController;

    //SumRateStage関係
    private Stage sumRateStage;
    private DateAndFile dateAndFile;

    //日付変更の際に、基準の日から何日動いたか記録する
    private LocalDate baceDate;

    /**
     * モーダル化をするためにウィンドウを開く直前1回だけメソッドを動かす必要があるため、 boolean型で動かした後、反転する
     */
    private boolean settingModaled = false;
    /**
     * settingウィンドウを開く。最初に開いたときのみモーダル化のメソッドを実行する
     */
    @FXML
    private void openSettingStage() {
        if (!settingModaled) {
            settingStage.initModality(Modality.WINDOW_MODAL);
            settingStage.initOwner(mainStage);
        }
        settingModaled = true;
        settingStage.showAndWait();
        //設定の変更を反映させる
        updateSeries();
    }

    private boolean sumRateModaled = false;

    @FXML
    private void openSumRateStage() {
        if (!sumRateModaled) {
            sumRateStage.initModality(Modality.WINDOW_MODAL);
            sumRateStage.initOwner(mainStage);
        }
        sumRateModaled = true;
        sumRateStage.showAndWait();
    }

    @FXML
    private void update(ActionEvent event) {
        updateSeries();

    }

    private void updateSeries() {
        boolean[] selectedSensor = settingStageController.getSelectedSensor();
        flowSeriesList = sapFlowSeries.getSeriesList();
        chartSeries = SapFlowToXYSeries.convert(flowSeriesList);

        //mainChartに乗せるデータを取得
        mainChart.getData().clear();
        for (int i = 0; i < chartSeries.size(); i++) {
            if (selectedSensor[i]) {
                mainChart.getData().add(chartSeries.get(i));
            }
            tabList.get(i).setDisable(!selectedSensor[i]);
        }

        //各センサのグラフと表に乗せるデータを取得
        chartSeries = SapFlowToXYSeries.convert(flowSeriesList);
        for (int i = 0; i < flowSeriesList.size(); i++) {
            sensorChartList.get(i).getData().set(0, chartSeries.get(i));

            tableList.get(i).getItems().clear();
            for (int n = 0; n < flowSeriesList.get(i).getDataList().size(); n++) {
                tableList.get(i).getItems().add(new TableData(flowSeriesList.get(i).getDataList().get(n)));
            }
        }

        //流速合計を取得し、ラベルに乗せる
        for (int i = 0; i < sumRateLabelList.size(); i++) {
            sumRateLabelList.get(i).setText(Double.toString(Math.round(flowSeriesList.get(i).getSapFlowSum())));
        }

        nextBtn.setDisable(dateAndFile.getNewerFile(baceDate) == null);
        prevBtn.setDisable(dateAndFile.getOlderFile(baceDate) == null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabList = new ArrayList<>();
        tabList.add(tab1);
        tabList.add(tab2);
        tabList.add(tab3);
        tabList.add(tab4);
        tabList.add(tab5);
        tabList.add(tab6);

        sensorChartList = new ArrayList<>();
        sensorChartList.add(sensorChart1);
        sensorChartList.add(sensorChart2);
        sensorChartList.add(sensorChart3);
        sensorChartList.add(sensorChart4);
        sensorChartList.add(sensorChart5);
        sensorChartList.add(sensorChart6);

        tableList = new ArrayList<>();
        tableList.add(table1);
        tableList.add(table2);
        tableList.add(table3);
        tableList.add(table4);
        tableList.add(table5);
        tableList.add(table6);

        timeColList = new ArrayList<>();
        timeColList.add(timeCol1);
        timeColList.add(timeCol2);
        timeColList.add(timeCol3);
        timeColList.add(timeCol4);
        timeColList.add(timeCol5);
        timeColList.add(timeCol6);

        valueColList = new ArrayList<>();
        valueColList.add(valueCol1);
        valueColList.add(valueCol2);
        valueColList.add(valueCol3);
        valueColList.add(valueCol4);
        valueColList.add(valueCol5);
        valueColList.add(valueCol6);

        sumRateLabelList = new ArrayList<>();
        sumRateLabelList.add(sumRateLabel1);
        sumRateLabelList.add(sumRateLabel2);
        sumRateLabelList.add(sumRateLabel3);
        sumRateLabelList.add(sumRateLabel4);
        sumRateLabelList.add(sumRateLabel5);
        sumRateLabelList.add(sumRateLabel6);

        //SettingStageの設定
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SettingStageLayout.fxml"));
            Parent settingLayout = loader.load();
            settingStage = new Stage();
            settingStage.setScene(new Scene(settingLayout));
            settingStage.setResizable(false);
            settingStage.setTitle("設定");
            //ウィンドウの枠の設定(最大化ボタンなどを表示させない)
            settingStage.initStyle(StageStyle.UTILITY);
            settingStageController = loader.getController();
            settingStageController.setStage(settingStage);
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "settingStageControllerのFXMLが開けません", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }

        //SumRateStageの設定
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("SumRateStageLayout.fxml"));
            Parent sumRateLayout = loader.load();
            sumRateStage = new Stage();
            sumRateStage.setScene(new Scene(sumRateLayout));
            sumRateStage.setTitle("累計蒸散量の変化");
        } catch (IOException e) {
            new Alert(Alert.AlertType.ERROR, "settingStageControllerのFXMLが開けません", ButtonType.OK).showAndWait();
            e.printStackTrace();
        }

        dateAndFile = new DateAndFile();
        //最初に読み込むファイルは存在する最新のデータを設定する。
        sapFlowSeries = new SapFlowSeries();
        baceDate = LocalDate.now();
        sapFlowSeries.setFileName(dateAndFile.dateToFileName(baceDate));
        //日付のLabelに日付を張り付ける
        dateLabel.setText(dateAndFile.dateToString(baceDate));

        //グラフや表にデータを張り付ける(updateSeriesの中のsetがaddになった版)
        flowSeriesList = sapFlowSeries.getSeriesList();
        chartSeries = SapFlowToXYSeries.convert(flowSeriesList);
        for (int i = 0; i < chartSeries.size(); i++) {
            mainChart.getData().add(chartSeries.get(i));
        }

        //表の設定
        chartSeries = SapFlowToXYSeries.convert(flowSeriesList);
        for (int i = 0; i < flowSeriesList.size(); i++) {
            sensorChartList.get(i).getData().add(chartSeries.get(i));
            timeColList.get(i).setCellValueFactory(new PropertyValueFactory<>("time"));
            valueColList.get(i).setCellValueFactory(new PropertyValueFactory<>("value"));
            for (int n = 0; n < flowSeriesList.get(i).getDataList().size(); n++) {
                tableList.get(i).getItems().add(new TableData(flowSeriesList.get(i).getDataList().get(n)));
            }
        }

        //合計流速をラベルに張り付ける
        for (int i = 0; i < sumRateLabelList.size(); i++) {
            sumRateLabelList.get(i).setText(Double.toString(flowSeriesList.get(i).getSapFlowSum()));
        }

        //ツールチップの設定
        settingButton.setTooltip(new Tooltip("設定ウィンドウを開きます"));
        sumRateButton.setTooltip(new Tooltip("指定した期間の累計蒸散量を計算します"));
        updateBtn.setTooltip(new Tooltip("データを更新します"));
        nextBtn.setTooltip(new Tooltip("次の日に移動します"));
        prevBtn.setTooltip(new Tooltip("前の日に移動します"));
        
        //前日か次の日データがなかった場合日付変更ボタンを不活性化する
        nextBtn.setDisable(dateAndFile.getNewerFile(baceDate) == null);
        prevBtn.setDisable(dateAndFile.getOlderFile(baceDate) == null);

    }

    //表示するデータの日付を一つ戻すメソッド
    @FXML
    private void changePrev(ActionEvent event) {
        baceDate = dateAndFile.getOlderFile(baceDate);
        dateLabel.setText(dateAndFile.dateToString(baceDate));
        sapFlowSeries.setFileName(dateAndFile.dateToFileName(baceDate));
        updateSeries();
        nextBtn.setDisable(dateAndFile.getNewerFile(baceDate) == null);
        prevBtn.setDisable(dateAndFile.getOlderFile(baceDate) == null);
    }

    //表示するデータの日付を一つ進めるメソッド
    @FXML
    private void changeNext(ActionEvent event) {
        baceDate = dateAndFile.getNewerFile(baceDate);
        dateLabel.setText(dateAndFile.dateToString(baceDate));
        sapFlowSeries.setFileName(dateAndFile.dateToFileName(baceDate));
        updateSeries();
        nextBtn.setDisable(dateAndFile.getNewerFile(baceDate) == null);
        prevBtn.setDisable(dateAndFile.getOlderFile(baceDate) == null);
    }

    @FXML
    private void exit() {
        Platform.exit();
    }

    //グラフを画像として保存するメソッド(要改善:画像は保存できるが、StageのそのままのスクショなのでStageの状態により画像が変わってしまう。)
    @FXML
    private void saveChart(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File saveDir = directoryChooser.showDialog(mainStage);
        if (saveDir != null) {
            try {
                saveNodeAsImage(mainChart, new File(saveDir.getAbsolutePath() + "/AllChart.png"));
                for (int i = 0; i < sensorChartList.size(); i++) {
                    saveNodeAsImage(sensorChartList.get(i), new File(saveDir.getAbsolutePath() + "/SensorChart" + (i + 1) + ".png"));
                }
            } catch (IOException e) {
                new Alert(Alert.AlertType.ERROR, "画像を保存できませんでした", ButtonType.OK).showAndWait();
                return;
            }
            new Alert(Alert.AlertType.INFORMATION, "画像を保存しました", ButtonType.OK).showAndWait();
        }
    }

    private void saveNodeAsImage(Node node, File imageFile) throws IOException {
        WritableImage image = node.snapshot(null, null);
        if (imageFile == null) {
            return;
        }
        ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imageFile);
    }

    public void setStage(Stage stage) {
        this.mainStage = stage;
    }

    public SettingStageController getSettingStageController() {
        return settingStageController;
    }
}
