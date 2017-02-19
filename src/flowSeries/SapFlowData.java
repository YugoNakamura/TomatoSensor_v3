/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package flowSeries;

/**
 * 流速を観測した時刻とその値の組<br>
 * コンストラクタでフィールドを設定し、getterで取得する
 * 特記がない限り参照型の引数を持つ関数にはnullを入れてはならない
 * @author NakamuraYugo
 * @see SapFlowSeries
 */
public class SapFlowData {
    
    /**
     * 流速を計測した時刻 例:6時1分→"6:01"
     */
    private String time;
    /**
     * その時観測された流速
     */
    private double value;

    /**
     * コンストラクタで値の設定を行う<br>
     * 流速に関して負の数を引数に設定してもプログラムとしては問題ない
     * @param time 流速を計測した時刻 例:6時1分→"6:01"
     * @param value その時観測された流速
     */
    public SapFlowData(String time, double value) {
        this.time = time;
        this.value = value;
    }

    /**
     * 時刻を取得するgetter
     * @return 流速を観測した時刻 例:6時1分→"6:01"
     */
    public String getTime() {
        return time;
    }

    /**
     * ある時刻の流速を取得するgetter
     * @return ある時刻の流速
     */
    public double getValue() {
        return value;
    }
}