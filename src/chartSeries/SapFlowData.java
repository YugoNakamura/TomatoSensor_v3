/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chartSeries;

/**
 *
 * @author NakamuraYugo
 */
public class SapFlowData {
    String time;
    double value;

    public SapFlowData(String time, double value) {
        this.time = time;
        this.value = value;
    }

    public String getTime() {
        return time;
    }

    public double getValue() {
        return value;
    }
}
