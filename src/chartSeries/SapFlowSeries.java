/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartSeries;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 *
 * @author 中村 勇吾
 */
public class SapFlowSeries {

    private ArrayList<SapFlowData> dataList = new ArrayList<>();
    private String seriesName;

    //ファイルからデータを読み込み、sapFlowSeriesのリストを作成する
    public static ArrayList<SapFlowSeries> getSeriesList() {
        FileReader fileReader = null;
        BufferedReader br = null;
        ArrayList<SapFlowSeries> seriesList = new ArrayList<>();
        try {
            fileReader = new FileReader("./logData/sampleData1.csv");
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

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }
    
    public String getSeriesName() {
        return seriesName;
    }

    public ArrayList<SapFlowData> getDataList() {
        return dataList;
    }
}