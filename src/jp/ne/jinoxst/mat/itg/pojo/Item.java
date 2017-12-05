package jp.ne.jinoxst.mat.itg.pojo;

import java.io.Serializable;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class Item implements Serializable{
    @JsonKey(decamelize = true)
    private int emmSeq;

    @JsonKey(decamelize = true)
    private int emmId;

    @JsonKey(decamelize = true)
    private int emmPrice;

    @JsonKey(decamelize = true)
    private String emmNm;

    @JsonKey(decamelize = true)
    private int stockCnt;

    @JsonKey(decamelize = true)
    private int underageWarningyn;

    @JsonKey(decamelize = true)
    private String brandImg;

    public int getEmmSeq() {
        return emmSeq;
    }

    public void setEmmSeq(int emmSeq) {
        this.emmSeq = emmSeq;
    }

    public int getEmmId() {
        return emmId;
    }

    public void setEmmId(int emmId) {
        this.emmId = emmId;
    }

    public int getEmmPrice() {
        return emmPrice;
    }

    public void setEmmPrice(int emmPrice) {
        this.emmPrice = emmPrice;
    }

    public String getEmmNm() {
        return emmNm;
    }

    public void setEmmNm(String emmNm) {
        this.emmNm = emmNm;
    }

    public int getStockCnt() {
        return stockCnt;
    }

    public void setStockCnt(int stockCnt) {
        this.stockCnt = stockCnt;
    }

    public int getUnderageWarningyn() {
        return underageWarningyn;
    }

    public void setUnderageWarningyn(int underageWarningyn) {
        this.underageWarningyn = underageWarningyn;
    }

    public String getBrandImg() {
        return brandImg;
    }

    public void setBrandImg(String brandImg) {
        this.brandImg = brandImg;
    }
}
