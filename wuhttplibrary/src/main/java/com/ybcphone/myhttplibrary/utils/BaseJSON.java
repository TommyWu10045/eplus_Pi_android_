package com.ybcphone.myhttplibrary.utils;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON的Base
 *
 * @author forever
 */
public abstract class BaseJSON {
    public static final int PARSER_FAILED = 0;
    public static final int PARSER_SUCCESS = 1;
    private int m_ParserJSONStatus = PARSER_FAILED;

    public String Errorcode = "";
    public String Desc = "";

    public abstract void parser(String jsonStr);

    public void setParserStatus(int parserStatus) {
        m_ParserJSONStatus = parserStatus;
    }

    public int getParserStatus() {
        return m_ParserJSONStatus;
    }


    public String getJson(JSONObject jsonObject, String key) throws JSONException {
        String result="";
        if (!jsonObject.isNull(key)) {

            result= jsonObject.getString(key);
            if(result==null){
                MyLog.e("Value key: "+key+" is null");
            }
        } else{
            MyLog.e("Value key: "+key+" does not exist!");
        }

        return result;
    }
    public String jsonCheckString(JSONObject jj,String key){
        try {
            if(!jj.isNull(key)) {
                return jj.getString(key);
            }else{
                MyLog.e("jsonCheckString 缺少 key:"+key);
                return "";
            }
        }catch (Exception e){
            MyLog.e("jsonCheckString 缺少 key:"+key);
            return "";
        }

    }

    public int jsonCheckInter(JSONObject jj,String key){
        try {
            if(!jj.isNull(key)) {
                return jj.getInt(key);
            }else{
                MyLog.e("jsonCheckString 缺少 key:"+key);
                return 0;
            }
        }catch (Exception e){
            MyLog.e("jsonCheckString 缺少 key:"+key);
            return 0;
        }

    }

}
