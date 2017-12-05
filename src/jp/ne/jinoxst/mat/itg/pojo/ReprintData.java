package jp.ne.jinoxst.mat.itg.pojo;

import java.io.Serializable;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class ReprintData implements Serializable {
    @JsonKey(decamelize = true)
    private String smlSid;

    @JsonKey(decamelize = true)
    private int emmId;

    @JsonKey(decamelize = true)
    private String brandImg;

    @JsonKey(decamelize = true)
    private int emmPrice;

    @JsonKey(decamelize = true)
    private String manageno;

    @JsonKey(decamelize = true)
    private String orderTime;

    @JsonKey(decamelize = true)
    private String reissueTime;

    @JsonKey(decamelize = true)
    private int reissueCount;

    public String getManageno() {
        return manageno;
    }

    public void setManageno(String manageno) {
        this.manageno = manageno;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getReissueTime() {
        return reissueTime;
    }

    public void setReissueTime(String reissueTime) {
        this.reissueTime = reissueTime;
    }

    public int getReissueCount() {
        return reissueCount;
    }

    public void setReissueCount(int reissueCount) {
        this.reissueCount = reissueCount;
    }

    public String getSmlSid() {
        return smlSid;
    }

    public void setSmlSid(String smlSid) {
        this.smlSid = smlSid;
    }

    public int getEmmId() {
        return emmId;
    }

    public void setEmmId(int emmId) {
        this.emmId = emmId;
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