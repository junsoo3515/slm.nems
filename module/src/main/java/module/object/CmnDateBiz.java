package module.object;

import com.ibm.icu.util.ChineseCalendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 날짜관련 Business 로직
 * <p/>
 * User: 현재호
 * Date: 2016.04.26
 * Time: 오후 5:44
 */
public class CmnDateBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnDateBiz.class); // SLF4J Logger

    /**
     * String형 날짜를 가져와서 Date 날짜 패턴으로 변경
     *
     * @param date         String 형 날짜
     * @param pattern      날짜 패턴(ex : yyyy-MM-dd HH:mm:ss)
     * @param num          이후/이전 n 날짜 간격
     * @param calendarType Calendar의 타입
     * @return the date
     */
    public static Date convertDate(String date, String pattern, int num, int calendarType) {

        Date tmpDate = null;
        DateFormat formatter = new SimpleDateFormat(pattern);

        try {
            tmpDate = formatter.parse(date);

            if (num != 0) {
                return addToDate(tmpDate, num, calendarType);
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return tmpDate;
    }

    /**
     * Date형 날짜를 가져와서 String 날짜 패턴으로 변경
     *
     * @param date    Date 형 날짜
     * @param pattern 날짜 패턴(ex : yyyy-MM-dd HH:mm:ss)
     * @return the string
     */
    public static String convertString(Date date, String pattern) {

        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 오늘 날짜 String Type
     *
     * @param pattern 날짜 패턴(ex : yyyy-MM-dd HH:mm:ss)
     * @return the string
     */
    public static String todayString(String pattern) {

        return convertString(addToCalendar(new Date(), 0, Calendar.DATE).getTime(), pattern);
    }

    /**
     * 날짜의 이후/이전 날짜 가져오기
     *
     * @param date         날짜
     * @param num          간격
     * @param calendarType Calendar의 타입
     * @return the calendar
     */
    public static Calendar addToCalendar(Date date, int num, int calendarType) {
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);
        calendarDate.add(calendarType, num);

        return calendarDate;
    }

    /**
     * 날짜의 이후/이전 날짜 가져오기
     *
     * @param date         날짜
     * @param num          간격
     * @param calendarType Calendar의 타입
     * @return the date
     */
    public static Date addToDate(Date date, int num, int calendarType) {

        return addToCalendar(date, num, calendarType).getTime();
    }

    /**
     * 주말/해당 요일일 경우 날짜 이동
     *
     * @param date    현재 날짜
     * @param chkWeek 선택 요일( 0 : 일, 1 : 월, 2 : 화, 3 : 수, 4 : 목, 5 : 금, 6 : 토 )
     * @param isWeek  요일 날짜 이동 유무
     * @return the date
     */
    public static Date setWeekDay(Date date, int chkWeek, boolean isWeek) {

        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);

        // 일, 월, 화, 수, 목, 금, 토
        int nowWeek = calendarDate.get(Calendar.DAY_OF_WEEK) - 1;

        if (isWeek == true) {

            date = addToCalendar(date, (chkWeek - nowWeek), Calendar.DATE).getTime();
        }

        switch (nowWeek) {

            case 0:
                // 일요일
                date = addToCalendar(date, 1, Calendar.DATE).getTime();
                break;
            case 6:
                // 토요일
                date = addToCalendar(date, 2, Calendar.DATE).getTime();
                break;
        }

        return date;
    }

    /**
     * 특정 요일일 경우 날짜 이동
     *
     * @param date    현재 날짜
     * @param chkWeek 선택 요일( 0 : 일, 1 : 월, 2 : 화, 3 : 수, 4 : 목, 5 : 금, 6 : 토 )
     * @param num     간격
     * @return the date
     */
    public static Date setMoveDay(Date date, int chkWeek, int num) {

        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);

        // 일, 월, 화, 수, 목, 금, 토
        int nowWeek = calendarDate.get(Calendar.DAY_OF_WEEK) - 1;

        if (nowWeek == chkWeek) {
            date = addToCalendar(date, num, Calendar.DATE).getTime();
        }

        return date;
    }

    /**
     * <pre>
     * 1. 개요 : 양력(yyyyMMdd) -> 음력(yyyyMMdd)
     * 2. 처리내용 : 양력을 음력으로 변환처리한다.
     * </pre>
     *
     * @param yyyymmdd the yyyymmdd
     * @return the string
     */
    public static String toLunar(String yyyymmdd) {

        Calendar cal = Calendar.getInstance();
        ChineseCalendar cc = new ChineseCalendar();

        if (yyyymmdd == null) {

            return "";
        }

        String date = yyyymmdd.trim();

        if (date.length() != 8) {

            if (date.length() == 4) {

                date = date + "0101";
            } else if (date.length() == 6) {

                date = date + "01";
            } else if (date.length() > 8) {

                date = date.substring(0, 8);
            } else {

                return "";
            }
        }

        cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

        cc.setTimeInMillis(cal.getTimeInMillis());

        // ChinessCalendar.YEAR 는 1~60 까지의 값만 가지고 ,

        // ChinessCalendar.EXTENDED_YEAR 는 Calendar.YEAR 값과 2637 만큼의 차이를 가집니다.
        int y = cc.get(ChineseCalendar.EXTENDED_YEAR) - 2637;
        int m = cc.get(ChineseCalendar.MONTH) + 1;
        int d = cc.get(ChineseCalendar.DAY_OF_MONTH);

        return String.format("%04d%02d%02d", y, m, d);
    }

    /**
     * <pre>
     * 1. 개요 : 음력(yyyyMMdd) -> 양력(yyyyMMdd)
     * 2. 처리내용 : 음력을 양력으로 변환처리한다.
     * </pre>
     *
     * @param yyyymmdd the yyyymmdd
     * @return the string
     */
    public static String fromLunar(String yyyymmdd) {

        Calendar cal = Calendar.getInstance();
        ChineseCalendar cc = new ChineseCalendar();

        if (yyyymmdd == null) {

            return "";
        }

        String date = yyyymmdd.trim();

        if (date.length() != 8) {

            if (date.length() == 4) {

                date = date + "0101";
            } else if (date.length() == 6) {

                date = date + "01";
            } else if (date.length() > 8) {

                date = date.substring(0, 8);
            } else {

                return "";
            }
        }

        cc.set(ChineseCalendar.EXTENDED_YEAR, Integer.parseInt(date.substring(0, 4)) + 2637);
        cc.set(ChineseCalendar.MONTH, Integer.parseInt(date.substring(4, 6)) - 1);
        cc.set(ChineseCalendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));

        cal.setTimeInMillis(cc.getTimeInMillis());

        int y = cal.get(Calendar.YEAR);
        int m = cal.get(Calendar.MONTH) + 1;
        int d = cal.get(Calendar.DAY_OF_MONTH);

        return String.format("%04d%02d%02d", y, m, d);
    }
}
