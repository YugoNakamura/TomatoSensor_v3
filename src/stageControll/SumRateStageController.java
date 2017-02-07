/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stageControll;

import util.DateAndFile;
import flowSeries.SapFlowSeries;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.Callback;

/**
 * 累積蒸散量を示すグラフの振る舞いを定義するクラス
 * @author NakamuraYugo
 */
public class SumRateStageController implements Initializable {
    
    /**
     * 表示するセンサの番号を指定するチェックボックス
     */
    @FXML
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6;
    /**
     * チェックボックスをまとめて操作するためのリスト
     */
    private List<CheckBox> checkBoxList;

    /**
     * 累積蒸散量の変化を表示する折れ線グラフ
     */
    @FXML
    private LineChart<String, Double> sumRateChart;
    /**
     * グラフに表示する初めの日を選択させる日付ピッカー
     */
    @FXML
    private DatePicker fromDatePicker;
    /**
     * グラフに表示する終わりの日を選択させる日付ピッカー
     */
    @FXML
    private DatePicker toDatePicker;

    
    private SapFlowSeries sapFlowSeries = new SapFlowSeries();
    private DateAndFile dateUtil = new DateAndFile();
    private List<SapFlowSeries> dataList = new ArrayList<>();
    
    /**
     * 累積蒸散量を計算し表示する関数
     */
    @FXML
    private void showChart(ActionEvent event) {
        List<XYChart.Series<String, Double>> chartSeriesList = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            chartSeriesList.add(new XYChart.Series<>());
            chartSeriesList.get(i).setName("sensor" + i);
        }

        LocalDate fromDate = fromDatePicker.getValue();
        LocalDate toDate = toDatePicker.getValue();
        //グラフに表示する期間
        int period = fromDate.until(toDate).getDays();
        //指定された期間のデータが存在しなければダイアログを出し、returnする。
        for (int i = 0; i < period; i++) {
            if (!dateUtil.isExistFile(fromDate.plusDays(i))) {
                new Alert(Alert.AlertType.ERROR, "選択された日のデータがありません", ButtonType.OK).show();
                return;
            }
        }
        String date;
        double rateSum;
        for (int i = 0; i < period; i++) {
            //取得したい日付のファイル名を設定し、データをListを取得する
            sapFlowSeries.setFileName(dateUtil.dateToFileName(fromDate.plusDays(i)));
            dataList = sapFlowSeries.getSeriesList();
            date = dateUtil.dateToString(fromDate.plusDays(i));
            for (int n = 0; n < 6; n++) {
                rateSum = dataList.get(n).getSapFlowSum();
                chartSeriesList.get(n).getData().add(i, new XYChart.Data<>(date, rateSum));
            }
        }
        //グラフのデータをリセットする
        sumRateChart.getData().clear();
        //checkBoxが選択されたセンサのデータをプロットしていく
        for (int i = 0; i < 6; i++) {
            if (checkBoxList.get(i).isSelected()) {
                sumRateChart.getData().add(chartSeriesList.get(i));
            }
        }
    }

    /**
     * このクラスの中で最初に実行される<br>
     * チェックボックスをリストにまとめ、日付ピッカーのセルの振る舞いを定義している。
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        checkBoxList = new ArrayList<>();
        checkBoxList.add(checkBox1);
        checkBoxList.add(checkBox2);
        checkBoxList.add(checkBox3);
        checkBoxList.add(checkBox4);
        checkBoxList.add(checkBox5);
        checkBoxList.add(checkBox6);

        fromDatePicker.setValue(LocalDate.now());
        toDatePicker.setValue(LocalDate.now());

        //日付ピッカーの中身を設定する
        //fromDatePickerは流速のログファイルが存在している日付だけ選択できる
        final Callback<DatePicker, DateCell> fromDayCellFactory = (final DatePicker param) -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                List<LocalDate> dateList = dateUtil.getLogDateList();
                if (empty == true || item == null) {
                    setText(null);
                    setGraphic(null);
                } else if (!dateList.contains(item)) {
                    setDisable(true);
                    //セルを薄い赤で塗る
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        };
        fromDatePicker.setDayCellFactory(fromDayCellFactory);

        //toDatePickerはfromDatePickerの条件に加えてfromDatePickerより古い日付も選択できない
        Callback<DatePicker, DateCell> toDayCellFactory = (DatePicker param) -> {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    List<LocalDate> dateList = dateUtil.getLogDateList();
                    if (empty == true || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else if (!dateList.contains(item) || fromDatePicker.getValue().isAfter(item)) {
                        setDisable(true);
                        //セルを薄い赤で塗る
                        setStyle("-fx-background-color: #ffc0cb;");
                    }
                }
            };
        };
        toDatePicker.setDayCellFactory(toDayCellFactory);
    }
}