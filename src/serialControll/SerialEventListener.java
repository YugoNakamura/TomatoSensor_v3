/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialControll;

import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * シリアル通信によるデータが受信されたときに実行される
 * 大局的な処理を{@link #serialEvent(gnu.io.SerialPortEvent) }で行い、
 * 長い処理は基本的に{@link RawDataUtil}にメソッドとしてまとめた。
 * @see RawDataUtil
 * @author NakamuraYugo
 */
public class SerialEventListener implements SerialPortEventListener {

    private SerialPort serialPort;
    //ある時点のセンサの値
    private StringBuilder buffer;
    //上のbufferをまとめたもの
    private List<String> rawData;
    /**
     * 生データの管理及びファイルに保存する
     */
    private RawDataUtil rawDataUtil;
    /**
     * 流速を計算及びファイルに保存
     */
    private SapFlowGenerator sapFlowGenerator;
    /**
     * 入力ストリームを得るため
     *
     * @param serialPort
     */
    public SerialEventListener() {
        rawData = new ArrayList();
        rawDataUtil = new RawDataUtil();
        sapFlowGenerator = new SapFlowGenerator();
    }

    /**
     * シリアル通信で受信データがバッファにある程度たまったら実行される
     *
     * @param serialEvent
     */
    @Override
    public void serialEvent(SerialPortEvent serialEvent) {
        //通信開始の合図
        final char STX = 0x02;
        //通信終了の合図
        final char ETX = 0x03;
        //受信されたいる時点での
        buffer = new StringBuilder();
        //受信データ(受信失敗の場合-1となるのでCharではなくint)
        int receivedData;

        InputStream in;

        try {
            in = serialPort.getInputStream();
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        //通信完了の合図を受け取ったら必要な部分を切り出してbufferに追加する
        if (serialEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
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

            //bufferの末尾には改行コードがあるのでそれを取り除く
            buffer.deleteCharAt(buffer.toString().length() - 1);
            //通信上のエラーなどにより発生した解釈不能なデータをはじく
            if (!rawDataUtil.checkBuffer(buffer)) {
                return;
            }
            rawData.add(buffer.toString());
            //センサが次の過熱が始まったか判断する
            if (rawDataUtil.chackOffToOn(rawData)) {
                //蓄積されたデータが過熱していた時のデータと、冷却していた時のデータを持っているか判別する
                if (rawDataUtil.checkCycle(rawData)) {
                    rawDataUtil.saveRawData(rawData);
                    sapFlowGenerator.saveSapFlow(sapFlowGenerator.rawToFlow(rawData));
                }
                rawData.clear();
            }
        }
    }
    
    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }
}
