/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileDate;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ログファイルの日付の管理用クラス
 *
 * @author NakamuraYugo
 */
public class DateUtil {

    /**
     * メソッドの引数として今日を指定したいときに使用する
     */
    public static final int TODAY = 0;

    /**
     * 現在の日付にamountToAddを加えた日付を計算し、それに対応する流速のログファイルのファイル名を返す
     *
     * @param amountToAdd 現在の日付に加える日数
     * @return 変換後のファイル名<br>
     * 出力例:sensorLog2017_2_1.csv
     */
    public String dateToFileName(int amountToAdd) {
        LocalDate date = LocalDate.now().plusDays(amountToAdd);
        return String.format("sensorLog%d_%d_%d.csv", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /**
     * 直接日付を流速のログファイルのファイル名に変換する
     *
     * @param date 変換する日付
     * @return 変換後のファイル名<br>
     * 出力例:sensorLog2017_2_1.csv
     */
    public String dateToFileName(LocalDate date) {
        return String.format("sensorLog%d_%d_%d.csv", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /**
     * 現在の日付にamountToAddを加えた日付を計算し、文字列に変換する
     *
     * @param amountToAdd 現在の日付に加える日数
     * @return 変換後の文字列 <br>
     * 出力例:2017-2-1
     */
    public String dateToString(int amountToAdd) {
        return LocalDate.now().plusDays(amountToAdd).toString();
    }

    /**
     * 直接日付を文字列に変換する
     *
     * @param date 現在の日付に加える日数
     * @return 変換後の文字列<br>
     * 出力例:2017-2-1
     */
    public String dateToString(LocalDate date) {
        return date.toString();
    }

    /**
     * 現在の日付にamountToAddを加えた日付を計算し、それに対応する流速のログファイルが存在しているかチェックする
     *
     * @param amountToAdd 現在の日付に加える日数
     * @return 存在している場合はtrue
     */
    public boolean isExistFile(int amountToAdd) {
        String fileName = dateToFileName(amountToAdd);
        return new File("./logData/" + fileName).exists();
    }

    /**
     * 直接日付に対応する流速のログファイルが存在するかチェックする
     *
     * @param date
     * @return 存在している場合はtrue
     */
    public boolean isExistFile(LocalDate date) {
        String fileName = dateToFileName(date);
        return new File("./logData/" + fileName).exists();
    }

    /**
     * 現在存在している流速のログファイルに対応する日付のリストを返す
     *
     * @return 流速のログファイルの日付のリスト
     */
    public List<LocalDate> getLogDateList() {
        File logDir = new File("./logData");
        String[] fileNames = logDir.list((File dir, String fileName) -> {
            return fileName.matches("sensorLog[0-9]{4}_[0-9][0-9]?_[0-9][0-9]?.csv");
        });
        List<LocalDate> dateList = new ArrayList<>();
        for (int i = 0; i < fileNames.length; i++) {
            int startCharNum, endCharNum;
            //yaerはsensorLog<YEAR>_<MONTH>_<DAY>.csvの"g"と最初の"_"に挟まれた数字
            startCharNum = fileNames[i].indexOf("g") + 1;
            endCharNum = fileNames[i].indexOf("_");
            int year = Integer.valueOf(fileNames[i].substring(startCharNum, endCharNum));
            //monthはsensorLog<YEAR>_<MONTH>_<DAY>.csvの2つの"_"に挟まれた数字
            startCharNum = endCharNum + 1;
            endCharNum = fileNames[i].lastIndexOf("_");
            int month = Integer.valueOf(fileNames[i].substring(startCharNum, endCharNum));
            //dayはsensorLog<YEAR>_<MONTH>_<DAY>.csvの2つめの"_"と"."に挟まれた数字
            startCharNum = endCharNum + 1;
            endCharNum = fileNames[i].indexOf(".");
            int day = Integer.valueOf(fileNames[i].substring(startCharNum, endCharNum));
            dateList.add(LocalDate.of(year, month, day));
        }
        return dateList;
    }
}
