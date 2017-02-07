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
import gnu.io.UnsupportedCommOperationException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 *
 * @author NakamuraYugo
 */
public class SerialIO {

    //Openしているシリアルポート
    private SerialPort serialPort;
    //シリアルポートが使われているか
    private boolean serialOpened = false;

    /**
     * このプログラムが動いているシステムの中から利用できるシリアルポートの一覧を取得する
     *
     * @return 利用できるシリアルポート名のリスト(システムによりシリアルポートの数は違うため動かすシステムによってサイズが変わる)
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
     * @param portName 接続したいシリアルポートの名前
     * @throws NoSuchPortException 指定されたポート名が存在しないとき
     * @throws PortInUseException 指定されたポートがすでに使われているとき
     * @throws UnsupportedCommOperationException システムでサポートされていないシリアルポートの扱いをしたとき
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

    /**
     * シリアルポートを開いている場合、そのシリアルポートを閉じる
     */
    public void closeSerialPort() {
        if (serialOpened) {
            serialPort.close();
            serialOpened = false;
        }
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }
}
