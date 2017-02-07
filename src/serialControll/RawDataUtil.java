/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serialControll;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import util.DateAndFile;

/**
 * シリアル通信で得られた生のデータをファイルに一時的に保存し、流速を計算する
 * {@link SerialEventListener}でしか使わない関数なのでアクセス修飾子は同パッケージ内
 * のみしかアクセスできない修飾子なしとする
 * @author NakamuraYugo
 */
class RawDataUtil {

    /**
     * ログデータを記録するホームディレクトリ名
     */
    public static final String LOG_DIR_NAME = "logData/";
    /**
     * 生データを記録するホームディレクトリ名
     */
    public static final String RAW_DIR_HOME_NAME = LOG_DIR_NAME + "rawData/";
    /**
     * 日付に関する取り扱いを決めるインスタンス
     */
    private DateAndFile dateUtil;

    /**
     * コンストラクタで保存に必要なディレクトリが存在していなかったら作成する
     */
    RawDataUtil() {
        dateUtil = new DateAndFile();
        //必要なディレクトリが作られていなかったら作成する
        if (!new File(LOG_DIR_NAME).isDirectory()) {
            new File(LOG_DIR_NAME).mkdir();
        }
        if (!new File(RAW_DIR_HOME_NAME).isDirectory()) {
            new File(RAW_DIR_HOME_NAME).mkdir();
        }
    }

    /**
     * Tera Termなどでセンサから来たデータを確認していると、通信開始直後などに
     * 不完全なデータが受信されていることに気づいた。そこで一回受信するごとに
     * チェックを入れ先ほど受信したデータが形式に則っているかを確認する必要がある。
     *
     * @param buffer 受信した一行分のデータ
     * @return bufferが形式に従っていた場合true
     */
    boolean checkBuffer(StringBuilder buffer) {
        buffer = buffer.deleteCharAt(buffer.length() - 1);
        String bufferStr = buffer.toString();
        String data[] = bufferStr.split(",");
        if (data.length < 8) {

            return false;
        }
        for (int i = 0; i < data.length; i++) {
            try {
                Double.valueOf(data[i]);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * 受信したデータに加熱し始めた瞬間があるか判別する
     *
     * @param rawData 得られた生データの集まり
     * @return 加熱し始めたらtrue
     */
    boolean chackOffToOn(List<String> rawData) {
        //判別のため、サンプルが2つ以上ないとだめ
        if (rawData.size() >= 2) {
            for (int i = 1; i < rawData.size(); i++) {
                //加熱し始めた瞬間があったらtrueを返す
                if (String.valueOf(rawData.get(i - 1).charAt(3)).equals("0") && String.valueOf(rawData.get(i).charAt(3)).equals("1")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 通信の開始直後に得られた生データは加熱→冷却の加熱の部分が抜けている可能性が 高い。
     * そこで一周期分のデータがあるか判別を行う。(加熱→冷却の瞬間があるか判別する)
     * @param rawData 生データ
     * @return
     */
    boolean checkCycle(List<String> rawData) {
        if (rawData.size() >= 2) {
            for (int i = 1; i < rawData.size(); i++) {
                //加熱をやめた瞬間があったらtrueを返す
                if (String.valueOf(rawData.get(i - 1).charAt(3)).equals("1") && String.valueOf(rawData.get(i).charAt(3)).equals("0")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 生データを指定されたディレクトリに保存する
     *
     * @param rawData
     */
    void saveRawData(List<String> rawData) {
        //生データのファイル名は"rawData<0から始まる数字>.csv"である
        final String RAW_FILE_NAME_BACE = "RawData";
        String rawDirName = RAW_DIR_HOME_NAME + dateUtil.dateToString(DateAndFile.TODAY) + "/";
        //最終的な生データのファイル名(相対パスを含む)
        String rawFileName = "";

        //その日の生データを保存するディレクトリが存在していなかった場合、作成する。
        File rawDataDir = new File(rawDirName);
        if (!rawDataDir.isDirectory()) {
            rawDataDir.mkdir();
        }

        /*ディレクトリの中にデータが存在していなかった場合、ファイル名は"RawData0.csv"とする
          存在していた場合、前のファイルのナンバリングに1を加えたものとする
         */
        String[] fileNames = rawDataDir.list();
        int fileNum = 0;
        if (fileNames.length == 0) {
            rawFileName = rawDirName + RAW_FILE_NAME_BACE + "0.csv";
        } else {
            String latestFileName = "";
            int fromIndex, toIndex;
            latestFileName = fileNames[fileNames.length - 1];
            fromIndex = RAW_FILE_NAME_BACE.length();
            toIndex = latestFileName.lastIndexOf(".");
            fileNum = Integer.valueOf(latestFileName.substring(fromIndex, toIndex));

            fileNum++;
            rawFileName = rawDirName + RAW_FILE_NAME_BACE + Integer.toString(fileNum) + ".csv";
        }

        /*ファイルに保存する*/
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(rawFileName);
            for (int i = 0; i < rawData.size(); i++) {
                fileWriter.write(rawData.get(i)+"\n");
            }
            fileWriter.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /**
     * 生データから流速データを計算する
     *
     * @return
     */
    List<Double> rawToFlow(List<String> rawData) {
        String rawDataArray[][] = new String[8][rawData.size()];
        return null;
    }
}
