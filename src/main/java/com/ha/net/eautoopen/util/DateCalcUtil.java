package com.ha.net.eautoopen.util;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCalcUtil {

    private final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private final static SimpleDateFormat NARROW_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private final static SimpleDateFormat NO_SIGNAL_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
    private final static SimpleDateFormat HOUR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH");
    private final static SimpleDateFormat SIMPLE_DATE_FORMAT_SLASH = new SimpleDateFormat("yyyy/MM/dd");

    private final static String SHORT_YEAR = "^[1-9]{1}[0-9]{1}$";
    private final static String SHORT_MONTH = "^(0)[1-9]$|^(1)[0-2]$";

    /**
     * String to Date
     * yyyy-MM-dd HH:mm:ss
     * @param inputDate
     * @return
     * @throws Exception
     */
    public static Date parseDateTime(String inputDate) throws Exception {
        if(StringUtils.hasText(inputDate)) {
            return DATE_FORMAT.parse(inputDate);
        }
        return null;
    }

    /**
     * Date to String
     * yyyy-MM-dd HH:mm:ss
     * @param date
     * @return
     */
    public static String formatDatetime(Date date)throws Exception {
        if(date != null){
            return paseDateToString(date, DATE_FORMAT);
        }
        return "";
    }

    /**
     * String to Simple Date
     * yyyy-MM-dd
     * @param inputDate
     * @return
     * @throws Exception
     */
    public static Date parseSimpleDate(String inputDate) throws Exception {
        if(StringUtils.hasText(inputDate)) {
            return SIMPLE_DATE_FORMAT.parse(inputDate);
        }
        return null;
    }

    /**
     *  String to Simple Date
     *   yyyy-MM-dd
     * @param date
     * @return
     * @throws Exception
     */
    public static String formatSimpleDateSlash(Date date){
        if(date != null){
            return paseDateToString(date, SIMPLE_DATE_FORMAT_SLASH);
        }else {
            return "";
        }
    }

    /**
     *  String to Simple Date
     *   yyyy/MM/dd
     * @param date
     * @return
     * @throws Exception
     */
    public static String formatSimpleDate(Date date){
        if(date != null){
            return paseDateToString(date, SIMPLE_DATE_FORMAT);
        }else {
            return "";
        }
    }

    /**
     * String to Narrow Date
     * yyyyMMdd
     * @param inputDate
     * @return
     * @throws Exception
     */
    public static Date narrowParseDate(String inputDate) throws Exception {
        if(StringUtils.hasText(inputDate)) {
            return NARROW_DATE_FORMAT.parse(inputDate);
        }
        return null;
    }

    /**
     * Date to Narrow String
     * yyyyMMdd
     * @param inputDate
     * @return
     * @throws Exception
     */
    public static String formatNarrowParseDate(Date inputDate) throws Exception {
        if(inputDate != null){
            return paseDateToString(inputDate,NARROW_DATE_FORMAT);
        }
        return "";
    }

    /**
     * String to NoSignal Date
     * yyyyMMddHHmmss
     * @param source
     * @return
     * @throws ParseException
     */
    public static Date paseNoSignalDate(String source) throws ParseException {
        return NO_SIGNAL_DATE_FORMAT.parse(source);
    }

    /**
     * Date to NoSignal String
     * yyyyMMddHHmmss
     * @param date
     * @return
     */
    public static String formatNoSignalDate(Date date) {
        if(date != null){
            return NO_SIGNAL_DATE_FORMAT.format(date);
        }else {
            return "";
        }
    }

    /**
     * String to Hour Date
     * yyyy-MM-dd HH
     * @param inputDate
     * @return
     * @throws Exception
     */
    public static Date paseHourDate(String inputDate) throws Exception {
        if(StringUtils.hasText(inputDate)) {
            return HOUR_DATE_FORMAT.parse(inputDate);
        }
        return null;
    }

    /**
     * Date to Hour String
     * yyyy-MM-dd HH
     * @param inputDate
     * @return
     * @throws Exception
     */
    public static String formatHourDate(Date inputDate) throws Exception {
        if(inputDate != null){
            return paseDateToString(inputDate,HOUR_DATE_FORMAT);
        }
        return "";
    }




    private static String paseDateToString(Date date, DateFormat format) {
        if (format == null) return null;
        return format.format(date);
    }

    public static Timestamp parseTimestamp(String inputDate) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parseSimpleDate(inputDate));
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static String getDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return new Integer(calendar.get(Calendar.DAY_OF_WEEK)).toString();
    }

    public static Integer getHourOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return new Integer(calendar.get(Calendar.HOUR_OF_DAY));
    }

    public static String getHourAndMinuteOfDay(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String time = dateFormat.format(date);
        return time;
    }

    public static Date getNearDay(Date date, int offset) {
        if (offset == 0)
            return date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int target = offset;
        int step = 1;
        if (target < 0) {
            target *= -1;
            step = -1;
        }
        while (target > 0) {
            calendar.add(Calendar.DAY_OF_YEAR, step);
            target--;
        }
        return calendar.getTime();
    }

    public static Date getNearDateTime(Date date, int offset, int type) {
        if (offset == 0)
            return date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int target = offset;
        int step = 1;
        if (target < 0) {
            target *= -1;
            step = -1;
        }
        while (target > 0) {
            calendar.add(type, step);
            target--;
        }
        return calendar.getTime();
    }

    public static int GetDateDifferenceByType(Date date1, Date date2, int CalendarType) {
        Date earlier;
        Date later;
        Boolean switched = false;
        Calendar calendarEarlier = Calendar.getInstance();
        Calendar calendarLater = Calendar.getInstance();
        if (date1.equals(date2)) {
            return 0;
        }
        if (date1.after(date2)) {
            earlier = date2;
            later = date1;
            switched = true;
        } else {
            earlier = date1;
            later = date2;
            switched = false;
        }
        calendarEarlier.setTime(earlier);
        calendarLater.setTime(later);
        int count = 0;
        while (calendarEarlier.before(calendarLater)) {
            calendarEarlier.add(CalendarType, 1);
            count++;
        }
        if (switched == true) {
            count *= -1;
        }
        return count;
    }

    public static Date GetNextHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, 1);
        return calendar.getTime();
    }

    public static Date GetEndTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, 59);
        calendar.add(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Date getDatePart(Date date) throws ParseException {
        String dateStr = SIMPLE_DATE_FORMAT.format(date);
        return SIMPLE_DATE_FORMAT.parse(dateStr);
    }

    public static Date getBeginDateTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    public static Date getEndDateTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public static Long compareDates(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return null;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Long dateDiff;
        try {
            dateDiff = df.parse(df.format(date1)).getTime() - df.parse(df.format(date2)).getTime();
            return dateDiff;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Integer getAge(String birthDate) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        Date birth = myFormatter.parse(birthDate);
        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = calendar.get(Calendar.DAY_OF_MONTH);
        calendar.setTime(birth);
        int yearBirth = calendar.get(Calendar.YEAR);
        int monthBirth = calendar.get(Calendar.MONTH);
        int dayOfMonthBirth = calendar.get(Calendar.DAY_OF_MONTH);
        Integer age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else if (monthNow < monthBirth) {
                age--;
            }
        }
        return age;
    }


    public static int getShortYear() {
        return Integer.valueOf(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)).substring(2));
    }

    /**
     * 正常止期计算
     * @param startDate
     * @return
     */
    public static Date getInsuranceEndDate(Date startDate) {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(startDate);
        calendarStart.add(Calendar.YEAR,1);
        calendarStart.add(Calendar.SECOND,-1);
        return  calendarStart.getTime();
    }

    /**
     * 根据起期计算十分秒相同的止期
     * @param startDate
     * @return
     */
    public static Date getInsuranceSameTimeEndDate(Date startDate) {
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.setTime(startDate);
        calendarStart.add(Calendar.YEAR,1);
        return  calendarStart.getTime();
    }

    public static Date endWith235959(Date endDate){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public  static Date paseGHDate(String  dateString){
        if (StringUtils.isEmpty(dateString)) {
            return null;
        }
        String[]  dateFormats ={"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss"};
        try {
            return  DateUtils.parseDate(dateString, dateFormats);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 时间+1
     * @return
     */
    public static Date pulsOneDay(Date date)throws Exception{
        if(date == null)
            return null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);//+1今天的时间加一天
        date = calendar.getTime();
        return date;
    }




}