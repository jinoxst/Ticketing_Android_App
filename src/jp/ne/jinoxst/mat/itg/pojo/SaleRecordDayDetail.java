package jp.ne.jinoxst.mat.itg.pojo;

import java.io.Serializable;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class SaleRecordDayDetail implements Serializable{
    @JsonKey(decamelize = true)
    private String orderTime;

    @JsonKey(decamelize = true)
    private String manageno;

    @JsonKey(decamelize = true)
    private String emmNm;

    @JsonKey(decamelize = true)
    private String brandImg;

    @JsonKey(decamelize = true)
    private int emmPrice;

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getManageno() {
        return manageno;
    }

    public void setManageno(String manageno) {
        this.manageno = manageno;
    }

    public String getEmmNm() {
        return emmNm;
    }

    public void setEmmNm(String emmNm) {
        this.emmNm = emmNm;
    }

    public String getBrandImg() {
        return brandImg;
    }

    public void setBrandImg(String brandImg) {
        this.brandImg = brandImg;
    }

    public int getEmmPrice() {
        return emmPrice;
    }

    public void setEmmPrice(int emmPrice) {
        this.emmPrice = emmPrice;
    }
}
