/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package guiControll.table;

import chartSeries.SapFlowData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author NakamuraYugo
 */
public class TableData {

    StringProperty time;
    StringProperty value;

    public TableData(String time, double value) {
        this.time = new SimpleStringProperty(time);
        this.value = new SimpleStringProperty(Double.toString(value));
    }
    
    public TableData(SapFlowData sapFlowData) {
        this.time = new SimpleStringProperty(sapFlowData.getTime());
        this.value = new SimpleStringProperty(Double.toString(sapFlowData.getValue()));
    }
    
    public StringProperty timeProperty() {return time;}
    public StringProperty valueProperty() {return value;}
}
