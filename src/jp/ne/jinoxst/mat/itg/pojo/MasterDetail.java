package jp.ne.jinoxst.mat.itg.pojo;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class MasterDetail {
    @JsonKey(decamelize = true)
    private int emmSeq;

    @JsonKey(decamelize = true)
    private int emmId;

    @JsonKey(decamelize = true)
    private String barcode;

    @JsonKey(decamelize = true)
    private String emmNm;

    @JsonKey(decamelize = true)
    private String emmKana;

    @JsonKey(decamelize = true)
    private int emmPrice;

    @JsonKey(decamelize = true)
    private int salesChannel;

    @JsonKey(decamelize = true)
    private int status;

    @JsonKey(decamelize = true)
    private String xbigtime;

    @JsonKey(decamelize = true)
    private String xendtime;

    @JsonKey(decamelize = true)
    private String imgUrl;

    @JsonKey(decamelize = true)
    private int lastseqno;

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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getEmmNm() {
        return emmNm;
    }

    public void setEmmNm(String emmNm) {
        this.emmNm = emmNm;
    }

    public String getEmmKana() {
        return emmKana;
    }

    public void setEmmKana(String emmKana) {
        this.emmKana = emmKana;
    }

    public int getEmmPrice() {
        return emmPrice;
    }

    public void setEmmPrice(int emmPrice) {
        this.emmPrice = emmPrice;
    }

    public int getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(int salesChannel) {
        this.salesChannel = salesChannel;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getXbigtime() {
        return xbigtime;
    }

    public void setXbigtime(String xbigtime) {
        this.xbigtime = xbigtime;
    }

    public String getXendtime() {
        return xendtime;
    }

    public void setXendtime(String xendtime) {
        this.xendtime = xendtime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getLastseqno() {
        return lastseqno;
    }

    public void setLastseqno(int lastseqno) {
        this.lastseqno = lastseqno;
    }
}
