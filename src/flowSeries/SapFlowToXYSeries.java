/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowSeries;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.chart.XYChart;

/**
 *
 * @author 中村 勇吾
 */
public class SapFlowToXYSeries {

    public static List<XYChart.Series<String, Double>> convert(List<SapFlowSeries> dataSeriesList) {
        List<XYChart.Series<String, Double>> chartSeriesList = new ArrayList<>();
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
