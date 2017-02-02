/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowSeries;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 内部に{@link SapFlowData}のリストを持ち、1つのセンサから取得した連続した流速をと時刻系を保持するクラス。
 * また、グラフに表示するためのにその系列名を持つ<br>
 * 特記がない限り参照型の引数を持つ関数にはnullを入れてはならない
 * @author 中村 勇吾
 * @see SapFlowToXYSeries
 */
public class SapFlowSeries {
    /**
     * {@link SapFlowData}グラフに表示するデータの集まり
     */
    private List<SapFlowData> dataList = new ArrayList<>();
    /**
     * グラフに表示する際に使う系列名
     */
    private String seriesName;
    /**
     * データを読み込む際に参照するファイルの名前
     */
    private String fileName;

    /**
     * 一つのファイルから6つのセンサのデータをまとめて読み込み、6つのSapFlowSeriesがまとめられたリストが返される。
     * @return 6つのセンサからのデータがまとめられたSapFlowSeriesのリスト
     */
    public List<SapFlowSeries> getSeriesList() {
        FileReader fileReader = null;
        BufferedReader br = null;
        ArrayList<SapFlowSeries> seriesList = new ArrayList<>();
        try {
            fileReader = new FileReader("./logData/"+fileName);
            br = new BufferedReader(fileReader);
            String line;
            String dataStr[];

            line = br.readLine();
            dataStr = line.split(",");
            for (int i = 0; i < dataStr.length-1; i++) {
                seriesList.add(new SapFlowSeries());
                seriesList.get(i).setSeriesName(dataStr[i+1]);
            }
            while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
                }
                dataStr = line.split(",");
                for (int i = 0; i < seriesList.size(); i++) {
                    seriesList.get(i).getDataList().add(new SapFlowData(dataStr[0], Double.valueOf(dataStr[i + 1])));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                fileReader.close();
            } catch (IOException ex) {
            }
        }
        return seriesList;
    }
    
    /**
     * 系列のデータを積分し、1日分の累計蒸散量を返す
     * @return その系列の1日の累計蒸散量
     */
    public double getSapFlowSum() {
        double sum;
        long widthTime;
        double sumFlowRate = 0.0;
        String startTime,endTime;
        int startHour,startMin,endHour,endMin;
        for(int i=0;i<dataList.size()-1;i++) {
            sum = dataList.get(i).getValue()+dataList.get(i+1).getValue();
            startTime = dataList.get(i).getTime();
            endTime = dataList.get(i+1).getTime();
            startHour = Integer.valueOf(startTime.split(":")[0]);
            startMin = Integer.valueOf(startTime.split(":")[1]);
            endHour = Integer.valueOf(endTime.split(":")[0]);
            endMin = Integer.valueOf(endTime.split(":")[1]);
            widthTime = Duration.between(LocalTime.of(startHour, startMin), LocalTime.of(endHour, endMin)).getSeconds();
            sumFlowRate += (sum*widthTime)/2;
        }
        return sumFlowRate;
    }

    /**
     * 系列名を設定する
     * @param seriesName 設定する系列名
     */
    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }
    
    /**
     * 系列名を取得する
     * @return その系列の名前
     */
    public String getSeriesName() {
        return seriesName;
    }
    
    /**
     * 読み込むファイル名を設定する
     * @param fileName 読み込ませたいファイル名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 系列からデータのみを取り出す
     * @return 計測されたデータ
     */
    public List<SapFlowData> getDataList() {
        return dataList;
    }
}