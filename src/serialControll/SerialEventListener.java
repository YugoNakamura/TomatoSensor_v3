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

/**
 * シリアル通信によるデータが受信されたときに実行される
 * @author NakamuraYugo
 */
public class SerialEventListener implements SerialPortEventListener {
    private SerialPort serialPort;
    private StringBuilder buffer;
    /**
     * 入力ストリームを得るため
     * @param serialPort 
     */
    public SerialEventListener(SerialPort serialPort) {
        this.serialPort = serialPort;
    }
    
    /**
     * シリアル通信で受信データがバッファにある程度たまったら実行される
     * @param serialEvent
     */
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
        buffer = new StringBuilder();
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
                System.out.print(buffer.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException ex) {
                    return;
                }
            }
            
        }
    }
    
    private boolean checkWritable(String rawData) {
        String data[] = rawData.split("\n");
        int heating = -1,prevHeating = -1;
        boolean onToOff=false;
        for(int i=0;i<data.length;i++) {
            heating = data[i].charAt(3);
            prevHeating = data[i-1].charAt(3);
            //加熱が終了した
            if(prevHeating==1&&heating==0) {
                onToOff=true;
            }
            if(onToOff && prevHeating==0&&heating==1) {
                return true;
            }
            
        }
    }
}
