package jp.ne.jinoxst.mat.itg.pojo;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class Master {
    @JsonKey(decamelize = true)
    private int emmSeq;

    @JsonKey(decamelize = true)
    private String emmNm;

    @JsonKey(decamelize = true)
    private String emmKana;

    @JsonKey(decamelize = true)
    private String leftmenuName;

    @JsonKey(decamelize = true)
    private int salesChannel;

    @JsonKey(decamelize = true)
    private int emmCategoryType;

    @JsonKey(decamelize = true)
    private int status;

    @JsonKey(decamelize = true)
    private int lastseqno;

    @JsonKey(decamelize = true)
    private int showNum;

    public int getEmmSeq() {
        return emmSeq;
    }

    public void setEmmSeq(int emmSeq) {
        this.emmSeq = emmSeq;
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

    public String getLeftmenuName() {
        return leftmenuName;
    }

    public void setLeftmenuName(String leftmenuName) {
        this.leftmenuName = leftmenuName;
    }

    public int getSalesChannel() {
        return salesChannel;
    }

    public void setSalesChannel(int salesChannel) {
        this.salesChannel = salesChannel;
    }

    public int getEmmCategoryType() {
        return emmCategoryType;
    }

    public void setEmmCategoryType(int emmCategoryType) {
        this.emmCategoryType = emmCategoryType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getLastseqno() {
        return lastseqno;
    }

    public void setLastseqno(int lastseqno) {
        this.lastseqno = lastseqno;
    }

    public int getShowNum() {
        return showNum;
    }

    public void setShowNum(int showNum) {
        this.showNum = showNum;
    }
}
