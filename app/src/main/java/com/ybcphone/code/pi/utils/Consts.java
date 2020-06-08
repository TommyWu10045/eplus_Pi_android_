package com.ybcphone.code.pi.utils;


import com.ybcphone.myhttplibrary.utils.MyUtils;

public @interface Consts {

    /**
     * 常數
     */
    int POST_TYPE_API_login = 900;
    int POST_TYPE_API_profile_update = 901;
    int POST_TYPE_API_get_channel_list = 902;
    int POST_TYPE_API_auction_bid = 903;
    int POST_TYPE_API_get_channel_data = 904;
    int POST_TYPE_API_shopping_cart_add = 905;
    int POST_TYPE_API_get_myorder_list = 906;
    int POST_TYPE_API_set_live_status = 907;
    int POST_TYPE_API_get_profile = 908;
    int POST_TYPE_API_orderId = 909;
    int POST_TYPE_API_get_shopping_cart = 910;
    int POST_TYPE_API_get_shipsetting = 911;
    int POST_TYPE_API_shipsetting_update = 912;
    int POST_TYPE_API_create_order = 913;
    int POST_TYPE_API_shopping_cart_update = 914;
    int POST_TYPE_API_get_orderdetail = 915;

    String Login_Type_normal = "1";//登入類型  1：一般  2：facebook
    String Login_Type_facebook = "2";//登入類型  1：一般  2：facebook

    int MESSAGE_close_activity = 201;
    int MESSAGE_jumapLoginActivity = 202;
    int MESSAGE_jumapMainActivity = 203;
    int MESSAGE_reload = 204;
    int MESSAGE_PI_BUY_0 = 205;
    int MESSAGE_PI_BUY_desc = 207;
    int MESSAGE_PI_BUY_buy = 208;
    int MESSAGE_get_orderdetail = 209;
    int MESSAGE_get_payMessage = 210;


    int REQUEST_CODE_facebook_register = 300;


    int ACTIVITE_RESULE_pick_photo_for_crop_1 = 400;


    //  <!--  fragmen_pay_way_chose_1付款方式，上傳數值即可 {1=>貨到付款, 2=>ATM 轉帳, 5=>蝦皮, 6=>PChome}    -->
    int Payment_type1_1_SELF_REC = 1;
    int Payment_type1_2_ATM = 2;
    int Payment_type1_5_XIAPI = 5;
    int Payment_type1_6_PCHOME = 6;

    int EPLUS_SHOW_FUNCTION_BAR = 0;
    int OrderListPayType_1_waitSend = 1;
    int OrderListPayType_2_waitRec = 2;
    int OrderListPayType_3_cancel = 3;




    /*
    1=正常推播通知訊息，需要顯示
2=推播賣場+1直購開始內容
3=推播賣場競標開始內容
4=推播賣場競標結束內容
5=推播賣場競標模式的最高出價資料
     */
    String pi_mode_type_1 = "1";
    String pi_mode_type_2 = "2";
    String pi_mode_type_3 = "3";
    String pi_mode_type_4 = "4";
    String pi_mode_type_5 = "5";


    String DATA_KEYBO_HEIGHT = "DATA_KEYBO_HEIGHT";

    String Shared_KEY_usermodel_userId = "Shared_KEY_usermodel_userId";
    String Shared_KEY_usermodel_fbname = "Shared_KEY_usermodel_fbname";
    String Shared_KEY_usermodel_name = "Shared_KEY_usermodel_name";
    String Shared_KEY_usermodel_birthday = "Shared_KEY_usermodel_birthday";
    String Shared_KEY_usermodel_sex = "Shared_KEY_usermodel_sex";
    String Shared_KEY_usermodel_telephone = "Shared_KEY_usermodel_telephone";
    String Shared_KEY_usermodel_mobile = "Shared_KEY_usermodel_mobile";
    String Shared_KEY_usermodel_contactEmail = "Shared_KEY_usermodel_contactEmail";
    String Shared_KEY_usermodel_zipcode = "Shared_KEY_usermodel_zipcode";
    String Shared_KEY_usermodel_address = "Shared_KEY_usermodel_address";
    String Shared_KEY_usermodel_picture = "Shared_KEY_usermodel_picture";


    String USER_DATA_SEX_BOY = "M";
    String USER_DATA_SEX_GIRL = "F";


    String PACKAGE_NAME_netflix = "com.netflix.mediaclient";
    String PACKAGE_NAME_spotify = "com.spotify.music";
    String PACKAGE_NAME_eplus = "com.cctech.evil.eplus";
    String PACKAGE_NAME_my = "com.ybcphone.code.pi";




    String BROADCAST_MESSAGE_intentFilter = "com.ybcphone.code.superpi";
    String BROADCAST_MESSAGE_KEY_DATATPYE_KEY = "dataType";
    String BROADCAST_MESSAGE_KEY_DATATPYE_DATA = "dataValue";
    int SERVER_dataType_stopView = 100;
    int SERVER_dataType_show_pi_desc = 101;
    int SERVER_dataType_push_bid = 102;//有人出價
    int SERVER_dataType_push_new_bid = 103;//新的拍賣
    int SERVER_dataType_push_new_stop = 104;//結束拍賣


    String Shared_KEY_userdata_UserID = "UserID";
    String Shared_KEY_fcm_token = "fcm_token";

    /**
     * Fragment
     */
    String RegisterAccFragment = "RegisterAccFragment";
    String RegisterOtherFragment = "RegisterOtherFragment";
    String HomeFragment = "QuestionListFragment";
    String AccountFragment = "AccountFragment";


    /**
     * Type
     */
    int Fragment_mode_main = 1;//主頁模式 -第一個主鍵

    // 拍賣模式1=+1直購,2=拍賣價高者得, 0=拍賣結束
    int Type_pi_mode_stop = 0;
    int Type_pi_mode_num = 1;
    int Type_pi_mode_bid = 2;


    String search_channel_mode_all = "all";//1.	mode=“all”，查詢所有節目表。不論是否在直播，不論是否已追蹤。
    String search_channel_mode_like = "like";//  2.	mode=“like”，查詢使用者追蹤的節目表。不論是否在直播。
    String search_channel_mode_live = "live";//    3.	mode=“live”，查詢直播中的節目表。不論是否已追蹤。


}
