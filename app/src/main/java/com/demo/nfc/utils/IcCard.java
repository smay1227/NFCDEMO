package com.demo.nfc.utils;

/**
 * IC卡 ISO14443 A标准
 */
public class IcCard {

    //代缴卡
    public static final String TYPE_SELLER = "01";
	//车主卡
    public static final String TYPE_CUSTOMER = "02";

    public String card_no;// 车主卡编号或代缴卡编号
    /** 01 代缴卡  02代表车主卡 **/
    public String type;// 卡片类型 第1扇区第三块数据，卡片类型(2个字符)
    public String car_owner_no;// 车主卡编号
    public String card_remain;// 卡片剩余额度 卡片金额（4个字符）
    public String card_date;// 发卡日期（10个字符）
}