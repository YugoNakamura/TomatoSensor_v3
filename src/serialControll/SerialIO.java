/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialControll;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import javafx.scene.control.ProgressBar;

/**
 *
 * @author NakamuraYugo
 */
public class SerialIO implements SerialPortEventListener {

    //Openしているシリアルポート
    private SerialPort serialPort;
    //シリアルポートが使われているか
    private boolean serialOpened;
    //シリアル通信で得られた生データ
    private List<List<Double>> rawDataList;
    //データの受信状況のプログレスバー
    private ProgressBar progressBar;
    //一回の観測で得られるデータの行数
    private final int MAX_RECEIVE_DATA = 3630;

    /**
     * 利用できるシリアルポートの一覧を取得する
     *
     * @return
     */
    public List<String> getSetialPortList() {
        List<String> portList = new ArrayList<>();
        //システムにあるシリアルポート一覧を返す(プログラムに使われてみるかは調べない)
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier port = (CommPortIdentifier) portEnum.nextElement();
            //プログラムに使われているか判断
            if (!port.isCurrentlyOwned()) {
                if (port.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    portList.add(port.getName());
                }
            }
        }
        return portList;
    }

    /**
     * 指定されたシリアルポートを開く ボーレート：9600 データビット：8 ストップビット：1 パリティ：イーブン フロー制御：無し
     */
    public void openSerialPort(String portName) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException {
        final int TIME_OUT = 2000;
        final int BAUDRATE = 9600;
        CommPortIdentifier portID;
        portID = CommPortIdentifier.getPortIdentifier(portName);
        //シリアルポートをopenするとき、文字列を与えて、競合が起こった時に対応できるようにする。同時にタイムアウトを定めて置く。[msec]
        serialPort = (SerialPort) portID.open(getClass().getSimpleName(), TIME_OUT);
        //ボーレート・データビット数などを設定する。
        serialPort.setSerialPortParams(BAUDRATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_EVEN);
        //フロー制御はしない
        serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
        serialOpened = true;
    }

    public void closeSerialPort() {
        if (serialOpened) {
            serialPort.close();
            serialOpened = false;
        }
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public List<List<Double>> getRawDataList() {
        return rawDataList;
    }

    public void setProgressbar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    //シリアル信号が入力されたときの処理
    @Override
    public void serialEvent(SerialPortEvent serialEvent) {
        //通信開始の合図
        final char STX = 0x02;
        //通信終了の合図
        final char ETX = 0x03;
        
        String[] rawData;
        //受信データ(受信失敗の場合-1となるのでCharではなくint)
        int receivedData;
        //大量の文字列の連結を行うのでStringBuilderを使用する。
        StringBuilder buffer = new StringBuilder();
        InputStream in = null;

        //通信完了の合図を受け取ったら必要な部分を切り出してbufferに追加する
        if (serialEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                in = serialPort.getInputStream();
                while (true) {
                    receivedData = in.read();
                    if (receivedData == STX) {
                        continue;
                    }
                    if (receivedData == ETX || receivedData == -1) {
                        break;
                    }
                    //中身のデータのみをbufferに追加する
                    buffer.append((char) receivedData);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    return;
                }
            }

            //カンマで分割し、各センサごとの電位差を蓄積していく
            rawData = buffer.toString().split(",");
            for (int i = 0; i < rawDataList.size(); i++) {
                rawDataList.get(i).add(Double.valueOf(rawData[i + 2]));
            }
            progressBar.setProgress((double)rawDataList.get(0).size()/MAX_RECEIVE_DATA);
        }
    }
}
