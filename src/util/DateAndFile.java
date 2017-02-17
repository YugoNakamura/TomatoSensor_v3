/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * ログファイルや日付の管理を行うクラス<br>
 * 特記がない限り参照型の引数を持つ関数にはnullを入れてはならない
 * @author NakamuraYugo
 */
public class DateAndFile {

    /**
     * メソッドの引数として今日を指定したいときに使用する
     */
    public static final int TODAY = 0;

    /**
     * 流速データを記録するホームディレクトリ名
     */
    public static final String LOG_DIR_NAME = "logData/";
    
    /**
     * 各日の流速ファイルにつけるヘッダ。データの凡例を示す。
     */
    public static final String LOG_FILE_HEADER = "Time,Sensor1,Sensor2,Sensor3,Sensor4,Sensor5,Sensor6\n";
    
    public DateAndFile() {
        //流速ファイルが一つもなかった場合、ファイルを作る。
        if (!new File(dateToFileName(TODAY)).exists()) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(DateAndFile.LOG_DIR_NAME + dateToFileName(DateAndFile.TODAY));
                fileWriter.write(LOG_FILE_HEADER);
                fileWriter.flush();
            } catch (IOException ex) {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                    } catch (IOException ex1) {
                    }
                }
            }
        }
    }
    
    /**
     * 引数を日数とし、今日の日付と足し合わせる。その日に対応する流速のログファイルのファイル名を返す<br>
     * 実際にそのファイルが存在しているかは判定していないので
     * {@link DateAndFile#isExistFile(int)}や{@link DateAndFile#isExistFile(java.time.LocalDate)}を使って
     * チェックすることをお勧めする。
     * nullを返すことはない。
     * 0や負の数を入れても構わない。0の場合は今日を指すことになり、負の数は前の日を指すことになる。<br>
     * 引数に今日を指定したいときは{@link DateAndFile#TODAY}という定数が用意されている。(実態はint型の0)
     * @see #isExistFile(java.time.LocalDate) 
     * @see #isExistFile(int) 
     * @param amountToAdd 現在の日付に加える日数
     * @return 変換後のファイル名<br>
     * 出力例:"sensorLog2017_2_1.csv"
     */
    public String dateToFileName(int amountToAdd) {
        LocalDate date = LocalDate.now().plusDays(amountToAdd);
        return String.format("sensorLog%d_%d_%d.csv", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /**
     * 日付インスタンスに対応する流速のログファイルのファイル名を返す。<br>
     * 実際にそのファイルが存在しているかは判定していないので
     * {@link DateAndFile#isExistFile(int)}や{@link DateAndFile#isExistFile(java.time.LocalDate)}を使って
     * チェックすることをお勧めする。
     * nullを返すことはない。
     * @see #isExistFile(java.time.LocalDate) 
     * @see #isExistFile(int) 
     * @param date 変換する日付
     * @return 変換後のファイル名<br>
     * 出力例:sensorLog2017_2_1.csv
     */
    public String dateToFileName(LocalDate date) {
        return String.format("sensorLog%d_%d_%d.csv", date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /**
     * 引数を日数とし、今日の日付と足し合わせる。その日付を表す文字列に変換する。<br>
     * 0や負の数を入れても構わない。0の場合は今日を指すことになり、負の数は前の日を指すことになる。<br>
     * 引数に今日を指定したいときは{@link DateAndFile#TODAY}という定数が用意されている。(実態はint型の0)
     * @param amountToAdd 現在の日付に加える日数
     * @return 変換後の文字列 <br>
     * 出力例:2017-2-1
     */
    public String dateToString(int amountToAdd) {
        return LocalDate.now().plusDays(amountToAdd).toString();
    }

    /**
     * 日付インスタンスに対応する文字列に変換する<br>
     * @param date 変換する日付
     * @return 変換後の文字列<br>
     * 出力例:2017-2-1
     */
    public String dateToString(LocalDate date) {
        return date.toString();
    }

    /**
     * 引数を日数とし、今日の日付と足し合わせる。それに対応する流速のログファイルが存在しているかチェックする<br>
     * 引数に今日を指定したいときは{@link DateAndFile#TODAY}という定数が用意されている。(実態はint型の0)
     * @param amountToAdd 現在の日付に加える日数
     * @return 存在している場合はtrue
     */
    public boolean isExistFile(int amountToAdd) {
        String fileName = dateToFileName(amountToAdd);
        return new File(LOG_DIR_NAME + fileName).exists();
    }
    
    
    /**
     * 引数に指定された日付よりも直近の古いファイルの日付を返す。\br
     * 古いファイルが存在しなかった場合、nullをかえす
     * @param baceDate 基準となる日付
     * @return 直近の古いファイルの日付(存在しない場合、nullを返す)
     */
    public LocalDate getOlderFile(LocalDate baceDate) {
        //流速ファイルの日付のリストを得る
        List<LocalDate> fileDateList = getLogDateList();
        //新しい順に並び替える
        Collections.sort(fileDateList,Comparator.reverseOrder());
        for(int i=0;i<fileDateList.size();i++) {
            if(fileDateList.get(i).isBefore(baceDate)) {
                return fileDateList.get(i);
            }
        }
        return null;
    }
    
    /**
     * 引数に指定された日付よりも直近の新しいファイルの日付を返す。\br
     * 新しいファイルが存在しなかった場合、nullをかえす
     * @param baceDate 基準となる日付
     * @return 直近の新しいファイルの日付(存在しない場合、nullを返す)
     */
    public LocalDate getNewerFile(LocalDate baceDate) {
        List<LocalDate> fileDateList = getLogDateList();
        //古い順に並び替える
        Collections.sort(fileDateList,Comparator.naturalOrder());
        for(int i=0;i<fileDateList.size();i++) {
            if(fileDateList.get(i).isAfter(baceDate)) {
                return fileDateList.get(i);
            }
        }
        return null;
    }
    
    /**
     * 日付インスタンスに対応する流速のログファイルが存在するかチェックする
     * @param date チェックしたい日付
     * @return 存在している場合はtrue
     */
    public boolean isExistFile(LocalDate date) {
        String fileName = dateToFileName(date);
        return new File(LOG_DIR_NAME + fileName).exists();
    }

    /**
     * 流速ファイルが存在している日付をまとめたリストを返す
     * @return 流速のログファイルの日付インスタンスのリスト
     */
    public List<LocalDate> getLogDateList() {
        File logDir = new File(LOG_DIR_NAME);
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
