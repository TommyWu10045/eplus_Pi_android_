package com.ybcphone.myhttplibrary.utils;

/**
 * 日期类
 * 字母  日期或时间元素  表示  示例
 * G  Era 标志符  Text  AD
 * y  年  Year  1996; 96
 * M  年中的月份  Month  July; Jul; 07
 * w  年中的周数  Number  27
 * W  月份中的周数  Number  2
 * D  年中的天数  Number  189
 * d  月份中的天数  Number  10
 * F  月份中的星期  Number  2
 * E  星期中的天数  Text  Tuesday; Tue
 * a  Am/pm 标记  Text  PM
 * H  一天中的小时数（0-23）  Number  0
 * k  一天中的小时数（1-24）  Number  24
 * K  am/pm 中的小时数（0-11）  Number  0
 * h  am/pm 中的小时数（1-12）  Number  12
 * m  小时中的分钟数  Number  30
 * s  分钟中的秒数  Number  55
 * S  毫秒数  Number  978
 * z  时区  General time zone  Pacific Standard Time; PST; GMT-08:00
 * Z  时区  RFC 822 time zone  -0800
 */

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Random;

public class VeDate {


    /**
     * 得到剩餘的中文時間
     * 要計算的時間
     */
    public static String getExiTrackStrDay(String nowTime, String cfromDate, String Chin_tian, String Chin_xiaoshi, String Chin_fenZhone, String miao) {
        String result = "";
        cfromDate = cfromDate.replace("/", "-");
        nowTime = nowTime.replace("/", "-");
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date fromDate = myFormatter.parse(cfromDate);
            //     Date nowdate = new Date();//目前時間
            Date nowdate = strToDateTime(nowTime);//目前時間
            //取得兩個時間的Unix時間
            Long ut1 = nowdate.getTime();
            Long ut2 = fromDate.getTime();

            //相減獲得兩個時間差距的毫秒
            Long timeP = ut2 - ut1;//毫秒差
            Long day = timeP / (1000 * 60 * 60 * 24);//日差
            Long hr_ = (timeP / (1000 * 60 * 60)) - (day * 24);//時差
            Long min = (timeP / (1000 * 60)) - (day * 24 * 60) - (hr_ * 60);//分差
            Long sec = (timeP / (1000)) - (day * 24 * 60 * 60) - (hr_ * 60 * 60) - (min * 60);//秒差

            //  MyLog.d("getExiTrackStrDay  :" + day + Chin_tian + hr_ + Chin_xiaoshi + min + Chin_fenZhone);

            if (day > 0) {
                result = day + Chin_tian + hr_ + Chin_xiaoshi + min + Chin_fenZhone;
            } else if (hr_ > 0) {
                result = hr_ + Chin_xiaoshi + min + Chin_fenZhone + sec + miao;
            } else if (min > 0) {
                result = min + Chin_fenZhone + sec + miao;
            } else if (sec > 0) {
                result = sec + miao;
            } else {
                return "";
            }
        } catch (Exception e) {
            MyLog.e("Error: getExiTrackStrDay  :" + e.toString());
            return "";
        }


        return result;
    }


    public static int getNowDateShort_year() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        String dateString = formatter.format(currentTime);

        return Integer.parseInt(dateString);
    }

    public static int getNowDateShort_month() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("MM");
        String dateString = formatter.format(currentTime);

        return Integer.parseInt(dateString) - 1;
    }

    public static int getNowDateShort_day() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
        String dateString = formatter.format(currentTime);

        return Integer.parseInt(dateString);
    }


    public static int getNowDateShort_hour() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("HH");
        String dateString = formatter.format(currentTime);

        return Integer.parseInt(dateString);
    }


    public static int getNowDateShort_min() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("mm");
        String dateString = formatter.format(currentTime);

        return Integer.parseInt(dateString);
    }

    public static String longStrToC(String ss) {
        //2016070821 13 27
        try {
            return ss.substring(8, 10) + ":" + ss.substring(10, 12) + ":" + ss.substring(12, 14);
        } catch (Exception e) {
            MyLog.e("ERROR ---   VeDate.longStrToC   ---" + e.toString());
            return "error!";
        }


    }


    public static boolean isToday(String ss) {
        try {
            String yy = ss.substring(0, 4);
            String mm = ss.substring(4, 6);
            String dd = ss.substring(6, 8);

            return getNowDateShort_s().equals(yy + mm + dd);

        } catch (Exception e) {
            MyLog.e("ERROR ---    VeDate.isToday      ---" + e.toString());
            return false;
        }

    }

    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyy/MM/dd HH:mm:ss
     */
    public static Date getNowDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    /**
     * 获取现在时间
     *
     * @return返回短时间格式 yyyy/MM/dd
     */
    public static Date getNowDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(currentTime);
        ParsePosition pos = new ParsePosition(8);
        Date currentTime_2 = formatter.parse(dateString, pos);
        return currentTime_2;
    }

    public static String getNowDateShort_s() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        String dateString = formatter.format(currentTime);

        return dateString;
    }


    public static String getNowDateShortString() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    public static String getNowDataTimemy() {

        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(currentTime);

        SimpleDateFormat formatter2 = new SimpleDateFormat("HH");
        String hh = formatter2.format(currentTime);
        int hi = Integer.parseInt(hh);

        SimpleDateFormat formatter3 = new SimpleDateFormat("mm");
        String mm = formatter3.format(currentTime);
        int mi = Integer.parseInt(mm);
        if (mi < 30) {
            mm = "30";
        } else {
            hi++;
            mm = "00";
        }

        hh = hi + "";
        if (hh.length() == 1) {
            hh = "0" + hh;
        }

        if (hh.equals("24")) {
            dateString = getNextDay(dateString, 1);
            hh = "00";
        }


        return dateString + " " + hh + ":" + mm + ":00";
    }


    public static String getStringDateToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(currentTime) + " 00:00:00";
        return dateString;
    }


    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy/MM/dd HH:mm:ss
     */
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    public static boolean overOneDay(String newDateTime) {
        String newDate = dateToShortDate(newDateTime);
        return !getStringDateShort().equals(newDate);
    }

    /**
     * 获取现在时间
     *
     * @return返回字符串格式 yyyy/MM/dd HH:mm:ss
     */
    public static String getStringDate3() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取现在时间
     *
     * @return 返回短时间字符串格式yyyy/MM/dd
     */
    public static String getStringDateShort() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 获取时间 小时:分;秒 HH:mm:ss
     *
     * @return
     */
    public static String getTimeShort() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date currentTime = new Date();
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 将长时间格式字符串转换为时间 yyyy/MM/dd HH:mm:ss
     *
     * @param strDate
     * @return
     */
    public static Date strToDate2(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 将长时间格式时间转换为字符串 yyyy/MM/dd HH:mm:ss
     *
     * @param dateDate
     * @return
     */
    public static String dateToStrLong(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    /**
     * 将短时间格式时间转换为字符串 yyyy/MM/dd
     *
     * @param dateDate
     * @param
     * @return
     */

    public static String dateToStr(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(dateDate);
        return dateString;
    }


    public static String dateToStrByFormat(Date dateDate, String ff) {
        SimpleDateFormat formatter = new SimpleDateFormat(ff);
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static String dateToStrMDW(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("M/d");
        String dateString = formatter.format(dateDate);//+"("+DateOfWeekStr(dateDate)+")";
        //String dateString = formatter.format(dateDate)+"("+DateOfWeekStr(dateDate)+")";

        return dateString;
    }


    public static String dateToShortTime2(Date dateDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:MM");
        String dateString = formatter.format(dateDate);


        return "AM " + dateString;
    }


    public static String dateToShortDate(String dd) {
        try {
            Date currentTime = strToDate2(dd);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
            String dateString = formatter.format(currentTime);
            return dateString;
        } catch (Exception e) {
            return "----";
        }
    }


    public static String dateToShortTime(String dateString) {
        try {
            String hour;
            hour = dateString.substring(11, 13);
            String min;
            min = dateString.substring(14, 16);
            return hour + ":" + min;
        } catch (Exception e) {
            return "----";
        }

    }

    public static long changeDate2Long(Date dd) {
        try {
            return dd.getTime();   //得到秒数，Date类型的getTime()返回毫秒数
        } catch (Exception e) {
            return 0;
        }
    }


    /**
     * 将短时间格式字符串转换为时间 yyyy/MM/dd
     *
     * @param strDate
     * @return
     */
    public static Date strToDate(String strDate) {
        strDate = strDate.replace("-","/");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    public static Date strToDateTime(String strDate) {
        strDate = strDate.replace("/","-");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }


    public static Date strToDate_s(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 得到现在时间
     *
     * @return
     */
    public static Date getNow() {
        Date currentTime = new Date();
        return currentTime;
    }

    /**
     * 提取一个月中的最后一天
     *
     * @param day
     * @return
     */
    public static Date getLastDate(long day) {
        Date date = new Date();
        long date_3_hm = date.getTime() - 3600000 * 34 * day;
        Date date_3_hm_date = new Date(date_3_hm);
        return date_3_hm_date;
    }


    public static String getFileNameByTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 得到现在时间
     *
     * @return 字符串 yyyyMMdd HHmmss
     */
    public static String getStringToday() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HHmmss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 得到现在小时
     */
    public static String getHour() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String hour;
        hour = dateString.substring(11, 13);
        return hour;
    }

    /**
     * 得到现在分钟
     *
     * @return
     */
    public static String getTime() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        String min;
        min = dateString.substring(14, 16);
        return min;
    }


    /**
     * 根据用户传入的时间表示格式，返回当前时间的格式 如果是yyyyMMdd，注意字母y不能大写。
     *
     * @param sformat yyyyMMddhhmmss
     * @return
     */
    public static String getUserDate(String sformat) {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat(sformat);
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    /**
     * 二个小时时间间的差值,必须保证二个时间都是"HH:MM"的格式，返回字符型的分钟
     */
    public static String getTwoHour(String st1, String st2) {
        String[] kk = null;
        String[] jj = null;
        kk = st1.split(":");
        jj = st2.split(":");
        if (Integer.parseInt(kk[0]) < Integer.parseInt(jj[0]))
            return "0";
        else {
            double y = Double.parseDouble(kk[0]) + Double.parseDouble(kk[1])
                    / 60;
            double u = Double.parseDouble(jj[0]) + Double.parseDouble(jj[1])
                    / 60;
            if ((y - u) > 0)
                return y - u + "";
            else
                return "0";
        }
    }

    /**
     * 得到二个日期间的间隔天数
     */
    public static String getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy/MM/dd");
        long day = 0;
        try {
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return "";
        }
        return day + "";
    }


    /**
     * 得到二个日期间的间隔天数
     */
    public static long getTwoDay(Date sj1, Date sj2) {
        long day = 0;
        try {
            day = (sj1.getTime() - sj2.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return 0;
        }
        return day;
    }

    /**
     * 时间前推或后推分钟,其中JJ表示分钟.
     */
    public static String getPreTime(String sj1, String jj) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String mydate1 = "";
        try {
            Date date1 = format.parse(sj1);
            long Time = (date1.getTime() / 1000) + Integer.parseInt(jj) * 60;
            date1.setTime(Time * 1000);
            mydate1 = format.format(date1);
        } catch (Exception e) {
        }
        return mydate1;
    }


    public static String getPreTimeSec(String sj1, String ss) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String mydate1 = "";
        try {
            Date date1 = format.parse(sj1);
            long Time = (date1.getTime() / 1000) + Integer.parseInt(ss);
            date1.setTime(Time * 1000);
            mydate1 = format.format(date1);
        } catch (Exception e) {
        }
        return mydate1;
    }


    public static long getStrTimelong(String ss) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String mydate1 = "";
        try {
            Date date1 = format.parse(ss);
            return (date1.getTime() / 1000);

        } catch (Exception e) {

            MyLog.e("ERROR  getStrTimelong + " + ss);
        }
        return -1;
    }

    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     */
    public static String getNextDay(String nowdate, int delay) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            String mdate = "";
            Date d = strToDate(nowdate);
            long myTime = (d.getTime() / 1000) + delay * 24
                    * 60 * 60;
            d.setTime(myTime * 1000);
            mdate = format.format(d);
            return mdate;
        } catch (Exception e) {
            return "";
        }
    }


    /**
     * 得到一个时间延后或前移几天的时间,nowdate为时间,delay为前移或后延的天数
     */
    public static Date getNextDay_d(Date nowdate, int delay) {
        try {
            long myTime = (nowdate.getTime() / 1000) + delay * 24
                    * 60 * 60;
            nowdate.setTime(myTime * 1000);
        } catch (Exception e) {

        }
        return nowdate;
    }

    public static Date getAppendDay(Date d, int delay) {
        Date newDate = new Date();
        if (delay == 0)
            return d;
        try {
            long myTime = (d.getTime() / 1000) + delay * 24 * 60 * 60;
            newDate.setTime(myTime * 1000);
            return newDate;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断是否润年
     *
     * @param ddate
     * @return
     */
    public static boolean isLeapYear(String ddate) {

        /**
         * 详细设计： 1.被400整除是闰年，否则： 2.不能被4整除则不是闰年 3.能被4整除同时不能被100整除则是闰年
         * 3.能被4整除同时能被100整除则不是闰年
         */
        Date d = strToDate(ddate);
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(d);
        int year = gc.get(Calendar.YEAR);
        if ((year % 400) == 0)
            return true;
        else if ((year % 4) == 0) {
            if ((year % 100) == 0)
                return false;
            else
                return true;
        } else
            return false;
    }


    public static boolean gethourBetween_3(Date endDate) {

        try {
            Date nowDate = new Date();
            // MyLog.e("   ======  hours : 11111" );
            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;
            // long ns = 1000;
            // 获得两个时间的毫秒时间差异
            //   MyLog.e("   ======  hours : 22222" );
            long diff = endDate.getTime() - nowDate.getTime();

            //   MyLog.e("   ======  hours : 3333" );
            // 计算差多少小时
            long hour = diff % nd / nh;
            MyLog.e("   ======  差多少小时 : " + hour);

            return hour < 3;
        } catch (Exception e) {

            MyLog.e("   ======  ERROR : gethourBetween_3    :   " + e.toString());
            return false;
        }

    }


    public static String get2Timebetween(Date endDate) {

        Date nowDate = new Date();

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = nowDate.getTime() - endDate.getTime();


     //   MyLog.v("==diff===" + diff);
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;


        long month = day / 30;
        long year = month / 12;


        String result = "1 分鐘前";

        if (year > 0) {
            result = year + " 年前";
        } else if (month > 0) {
            result = month + " 月前";
        } else if (day > 0) {
            result = day + " 天前";
        } else if (hour > 0) {
            result = hour + " 小時前";
        } else if (min > 0) {
            result = min + " 分鐘前";
        }


        return result;
        //	return day + "天" + hour + "小时" + min + "分钟";


/*

		MyLog.v( "==date2==="+ VeDate.dateToStrLong(date2));

		Date date1 = new Date();
		long tt= (date1.getTime()-date2.getTime())/(1000*60);//分
		String result = "1 分鐘前";

		tt = tt/60;
		if(tt>59 && tt< 60*24 ){
			 result = (int)(tt/60)+" 小時前";
		}else if( tt > 60*24 && tt < 30*60*24){
			 result = (int)(tt/(60*24))+" 天前";
		}else if(tt >30*60*24 && tt < 30*60*12*24){
			result = (int)(tt/(60*24*30))+" 月前";
		}else if( tt > 30*60*12*24){
			result = (int)(tt/(60*24*30*12))+" 年前";
		}

		return result;*/
    }
    public static String get2Timebetween(String nowTime,String endTime) {
        try {

            Date nowDate = strToDateTime(nowTime);
            Date endDate = strToDateTime(endTime);

            long nd = 1000 * 24 * 60 * 60;
            long nh = 1000 * 60 * 60;
            long nm = 1000 * 60;
            // long ns = 1000;
            // 获得两个时间的毫秒时间差异
            long diff = nowDate.getTime() - endDate.getTime();


        //    MyLog.v("==diff===" + diff);
            // 计算差多少天
            long day = diff / nd;
            // 计算差多少小时
            long hour = diff % nd / nh;
            // 计算差多少分钟
            long min = diff % nd % nh / nm;
            // 计算差多少秒//输出结果
            // long sec = diff % nd % nh % nm / ns;

            long month = day / 30;
            long year = month / 12;


            String result = "1 分鐘前";

            if (year > 0) {
                result = year + " 年前";
            } else if (month > 0) {
                result = month + " 月前";
            } else if (day > 0) {
                result = day + " 天前";
            } else if (hour > 0) {
                result = hour + " 小時前";
            } else if (min > 0) {
                result = min + " 分鐘前";
            }


            return result;
        }catch (Exception e){
            return "很久以前";
        }

    }

    public static String getDatePoor(Date endDate, Date nowDate) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }


    /**
     * 返回美国时间格式 26 Apr 2006
     *
     * @param str
     * @return
     */
    public static String getEDate(String str) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(str, pos);
        String j = strtodate.toString();
        String[] k = j.split(" ");
        return k[2] + k[1].toUpperCase() + k[5].substring(2, 4);
    }

    /**
     * 获取一个月的最后一天
     */
    public static String getEndDateOfMonth(int yy, int mm, int dd) {// yyyy/MM/dd
        String smm = mm + "";
        if (smm.length() == 1) {
            smm = "0" + smm;
        }

        String sdd = dd + "";
        if (sdd.length() == 1) {
            sdd = "0" + sdd;
        }
        String dat = yy + "/" + smm + "/" + sdd;
        String str = dat.substring(0, 8);
        String month = dat.substring(5, 7);
        int mon = Integer.parseInt(month);
        if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8
                || mon == 10 || mon == 12) {
            str += "31";
        } else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
            str += "30";
        } else {
            if (isLeapYear(dat)) {
                str += "29";
            } else {
                str += "28";
            }
        }
        return str;
    }

    public static String getEndDateOfMonth(String dat) {// yyyy/MM/dd,
        String str = "";
        String month = dat.substring(5, 7);
        int mon = Integer.parseInt(month);

        if (mon == 1 || mon == 3 || mon == 5 || mon == 7 || mon == 8
                || mon == 10 || mon == 12) {
            str = "31";
        } else if (mon == 4 || mon == 6 || mon == 9 || mon == 11) {
            str = "30";
        } else {
            if (isLeapYear(dat)) {
                str = "29";
            } else {
                str = "28";
            }
        }
        return str;
    }

    /**
     * 产生周序列,即得到当前时间所在的年度是第几周
     *
     * @return
     */
    public static String getSeqWeek() {
        Calendar c = Calendar.getInstance(Locale.CHINA);
        String week = Integer.toString(c.get(Calendar.WEEK_OF_YEAR));
        if (week.length() == 1)
            week = "0" + week;
        String year = Integer.toString(c.get(Calendar.YEAR));
        return year + week;
    }

    /**
     * 获得一个日期所在的周的星期几的日期，如要找出2002年2月3日所在周的星期一是几号
     *
     * @param sdate
     * @param num
     * @return
     */
    public static String getWeek(String sdate, String num) {
        // 再转换为时间
        Date dd = VeDate.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(dd);
        if (num.equals("1")) // 返回星期一所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        else if (num.equals("2")) // 返回星期二所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        else if (num.equals("3")) // 返回星期三所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        else if (num.equals("4")) // 返回星期四所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        else if (num.equals("5")) // 返回星期五所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        else if (num.equals("6")) // 返回星期六所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        else if (num.equals("0")) // 返回星期日所在的日期
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return new SimpleDateFormat("yyyy/MM/dd").format(c.getTime());
    }

    /**
     * 根据一个日期，返回是星期几的字符串
     *
     * @param sdate
     * @return
     */
    public static String getWeek(String sdate) {
        // 再转换为时间
        Date date = VeDate.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        // int hour=c.get(Calendar.DAY_OF_WEEK);
        // hour中存的就是星期几了，其范围 1~7
        // 1=星期日 7=星期六，其他类推
        return new SimpleDateFormat("EEEE").format(c.getTime());
    }

    public static String getWeekStr(String sdate) {
        String str = "";
        str = VeDate.getWeek(sdate);
        if ("1".equals(str)) {
            str = "星期日";
        } else if ("2".equals(str)) {
            str = "星期一";
        } else if ("3".equals(str)) {
            str = "星期二";
        } else if ("4".equals(str)) {
            str = "星期三";
        } else if ("5".equals(str)) {
            str = "星期四";
        } else if ("6".equals(str)) {
            str = "星期五";
        } else if ("7".equals(str)) {
            str = "星期六";
        }
        return str;
    }


    // 星期幾
    public static String DateOfWeekStr(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DAY_OF_WEEK);
        switch (day) {
            case 2:
                return "一";
            case 3:
                return "二";
            case 4:
                return "三";
            case 5:
                return "四";
            case 6:
                return "五";
            case 7:
                return "六";
            case 1:
                return "日";
        }
        return "";
    }

    /**
     * 两个时间之间的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static long getDays(String date1, String date2) {
        if (date1 == null || date1.equals(""))
            return 0;
        if (date2 == null || date2.equals(""))
            return 0;
        // 转换为标准时间
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        Date mydate = null;
        try {
            date = myFormatter.parse(date1);
            mydate = myFormatter.parse(date2);
        } catch (Exception e) {
        }
        long day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        return day;
    }

    /**
     * 形成如下的日历 ， 根据传入的一个时间返回一个结构 星期日 星期一 星期二 星期三 星期四 星期五 星期六 下面是当月的各个时间
     * 此函数返回该日历第一行星期日所在的日期
     *
     * @param sdate
     * @return
     */
    public static String getNowMonth(String sdate) {
        // 取该时间所在月的一号
        sdate = sdate.substring(0, 8) + "01";

        // 得到这个月的1号是星期几
        Date date = VeDate.strToDate(sdate);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int u = c.get(Calendar.DAY_OF_WEEK);
        String newday = VeDate.getNextDay(sdate, (1 - u));
        return newday;
    }

    /**
     * 取得数据库主键 生成格式为yyyymmddhhmmss+k位随机数
     *
     * @param k 表示是取几位随机数，可以自己定
     */

    public static String getNo(int k) {

        return getUserDate("yyyyMMddhhmmss") + getRandom(k);
    }

    /**
     * 返回一个随机数
     *
     * @param i
     * @return
     */
    public static String getRandom(int i) {
        Random jjj = new Random();
        // int suiJiShu = jjj.nextInt(9);
        if (i == 0)
            return "";
        String jj = "";
        for (int k = 0; k < i; k++) {
            jj = jj + jjj.nextInt(9);
        }
        return jj;
    }

    /**
     *

     */
    public static boolean RightDate(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        ;
        if (date == null)
            return false;
        if (date.length() > 10) {
            sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        } else {
            sdf = new SimpleDateFormat("yyyy/MM/dd");
        }
        try {
            sdf.parse(date);
        } catch (ParseException pe) {
            return false;
        }
        return true;
    }


}