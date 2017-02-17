/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialControll;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import util.DateAndFile;

/**
 *
 * @author NakamuraYugo
 */
class SapFlowGenerator {

    /**
     * 電圧と初期電圧が等しくなったとみる際の許容誤差[mV]
     */
    final double PERMISSIBLE_ERROR = 0.02;
    /**
     * PICが電位差を測定する周期[s]
     */
    final double SAMPLING_CYCLE = 0.2;
    /**
     * ヒートパルスをかける時間[s]
     */
    final double HEAT_PLUSE_LENGTH = 5.0;
    /**
     * 一つのセンサにおける温度センサの間隔[mm]
     */
    final double TEMP_SENSOR_DISTANCE = 15;
    /**
     * 日付等を管理するためのインスタンス
     */
    private DateAndFile dateAndFile;

    SapFlowGenerator() {
        dateAndFile = new DateAndFile();
    }

    /**
     * 生データから流速データを計算する。文字列から{@link RawData}のインスタンスのリストを生成して計算する
     *
     * @see RawData
     * @return
     */
    List<Double> rawToFlow(List<String> strData) {
        //計算結果を入れるList
        List<Double> flowList = new ArrayList<>(6);
        //Stringの生データをそれぞれクラスに変換して整理しやすくする
        List<RawData> rawDataList = new ArrayList<>(6);
        for (int i = 0; i < strData.size(); i++) {
            rawDataList.add(new RawData(strData.get(i)));
        }
        //初期電圧を取得する
        double[] initalVoltage = getInitalVoltage(rawDataList);
        double flowVelocity;
        for (int i = 0; i < rawDataList.size(); i++) {
            for (int n = 0; n < rawDataList.get(i).getSensorData().size(); n++) {
                if (initalVoltage[n] - PERMISSIBLE_ERROR < rawDataList.get(i).getSensorData().get(n)) {
                    flowVelocity = HEAT_PLUSE_LENGTH / (2 * SAMPLING_CYCLE - HEAT_PLUSE_LENGTH);
                    flowList.add(flowVelocity);
                    break;
                }
            }
        }
        return flowList;
    }

    /**
     * 得られた流速をファイルに追記する
     */
    void saveSapFlow(List<Double> sapFlow) {
        //記録を行うファイル
        File logFile = new File(DateAndFile.LOG_DIR_NAME + dateAndFile.dateToFileName(DateAndFile.TODAY));
        //書き込む内容
        //形式を整えて現在時刻をStringに変換
        LocalTime time = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        //ファイルに書き込む内容
        String writeStr = time.format(formatter) + "," + sapFlow.get(0) + ","
                + sapFlow.get(1) + "," + sapFlow.get(2) + "," + sapFlow.get(3) + ","
                + sapFlow.get(4) + "," + sapFlow.get(5) + "\n";

        FileWriter fileWriter = null;
        try {
            //ファイルが既に存在している場合、追記を行う
            fileWriter = new FileWriter(logFile, true);
            //新規にファイルを作成する場合はヘッダを先に書き込む
            if (!logFile.exists()) {
                fileWriter.write(DateAndFile.LOG_FILE_HEADER);
            }
            fileWriter.write(writeStr);
            fileWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    private final int AVERAGE_SAMPLE = 10;

    /**
     * 定常状態の電圧を求める為に1つのセンサごとに末端の平均をとる
     *
     * @param rawData
     * @return
     */
    private double[] getInitalVoltage(List<RawData> rawData) {
        double[] ave = new double[6];
        for (int n = 0; n < AVERAGE_SAMPLE; n++) {
            for (int i = 0; i < ave.length; i++) {
                ave[i] += rawData.get(n).getSensorData().get(i);
            }
        }
        for (int i = 0; i < ave.length; i++) {
            ave[i] /= AVERAGE_SAMPLE;
        }
        return ave;
    }
}

/**
 * {@link RawDataUtil#rawToFlow(java.util.List) }で流速を算出する際、
 * データを整理するためのクラス。メンバはそれぞれのセンサの値(Double型)のリストとその時
 * ヒータが稼働していたかを示すフラグ(boolean型)およびそれらのgetterである。 コンストラクタで値を設定する
 *
 * @author 中村 勇吾
 */
class RawData {

    /**
     * 取得された6つのセンサのデータ(サイズは6)
     */
    private List<Double> sensorData;
    /**
     * そのデータを取得した時、ヒータは加熱しているか
     */
    private boolean heating;

    /**
     * String型の受信データを受け取り、それをもとにこのクラスのインスタンスを作成する
     *
     * @param rawDataStr 受信データ
     */
    RawData(String rawDataStr) {
        sensorData = new ArrayList<>();
        String[] data = rawDataStr.split(",");
        for (int i = 0; i < data.length; i++) {
            sensorData.add(Double.valueOf(data[i]));
        }
    }

    /**
     * センサの値を返すgetter
     *
     * @return センサの値をまとめたリスト
     */
    List<Double> getSensorData() {
        return sensorData;
    }

    /**
     * このとき過熱を行っていたかを返す
     *
     * @return 過熱していた時true
     */
    boolean isHeating() {
        return heating;
    }
}
