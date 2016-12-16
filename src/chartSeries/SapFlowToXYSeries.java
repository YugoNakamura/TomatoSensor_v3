/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartSeries;

import java.util.ArrayList;
import javafx.scene.chart.XYChart;

/**
 *
 * @author 中村 勇吾
 */
public class SapFlowToXYSeries {

    public static ArrayList<XYChart.Series<String, Double>> convert(ArrayList<SapFlowSeries> dataSeriesList) {
        ArrayList<XYChart.Series<String, Double>> chartSeriesList = new ArrayList<>();
        for (int i = 0; i < dataSeriesList.size(); i++) {
            chartSeriesList.add(new XYChart.Series<>());
            chartSeriesList.get(i).setName(dataSeriesList.get(i).getSeriesName());
            for (int n = 0; n < dataSeriesList.get(i).getDataList().size(); n++) {
                SapFlowData data = dataSeriesList.get(i).getDataList().get(n);
                chartSeriesList.get(i).getData().add(new XYChart.Data<>(data.getTime(), data.getValue()));
            }
        }
        return chartSeriesList;
    }
}
