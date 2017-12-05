package jp.ne.jinoxst.mat.itg.pojo;

import java.io.Serializable;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class Ticket implements Serializable {
    @JsonKey(decamelize = true)
    private String header;

    @JsonKey(decamelize = true)
    private String title;

    @JsonKey(decamelize = true)
    private String orderTime;

    @JsonKey(decamelize = true)
    private String shopName;

    @JsonKey(decamelize = true)
    private String emmName;

    @JsonKey(decamelize = true)
    private String brandImg;

    @JsonKey(decamelize = true)
    private int emmPrice;

    public String getBrandImg() {
        return brandImg;
    }

    public void setBrandImg(String brandImg) {
        this.brandImg = brandImg;
    }

    @JsonKey(decamelize = true)
    private String serialnotitle;

    @JsonKey(decamelize = true)
    private String serialno;

    @JsonKey(decamelize = true)
    private String managenotitle;

    @JsonKey(decamelize = true)
    private String manageno;

    @JsonKey(decamelize = true)
    private String cmpTitle;

    @JsonKey(decamelize = true)
    private String cmpInfo;

    @JsonKey(decamelize = true)
    private String cmpSerialnotitle;

    @JsonKey(decamelize = true)
    private String cmpSerialno;

    @JsonKey(decamelize = true)
    private String cmpManagenotitle;

    @JsonKey(decamelize = true)
    private String cmpManageno;

    @JsonKey(decamelize = true)
    private String info;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getEmmName() {
        return emmName;
    }

    public void setEmmName(String emmName) {
        this.emmName = emmName;
    }

    public int getEmmPrice() {
        return emmPrice;
    }

    public void setEmmPrice(int emmPrice) {
        this.emmPrice = emmPrice;
    }

    public String getSerialnotitle() {
        return serialnotitle;
    }

    public void setSerialnotitle(String serialnotitle) {
        this.serialnotitle = serialnotitle;
    }

    public String getSerialno() {
        return serialno;
    }

    public void setSerialno(String serialno) {
        this.serialno = serialno;
    }

    public String getManagenotitle() {
        return managenotitle;
    }

    public void setManagenotitle(String managenotitle) {
        this.managenotitle = managenotitle;
    }

    public String getManageno() {
        return manageno;
    }

    public void setManageno(String manageno) {
        this.manageno = manageno;
    }

    public String getCmpTitle() {
        return cmpTitle;
    }

    public void setCmpTitle(String cmpTitle) {
        this.cmpTitle = cmpTitle;
    }

    public String getCmpInfo() {
        return cmpInfo;
    }

    public void setCmpInfo(String cmpInfo) {
        this.cmpInfo = cmpInfo;
    }

    public String getCmpSerialnotitle() {
        return cmpSerialnotitle;
    }

    public void setCmpSerialnotitle(String cmpSerialnotitle) {
        this.cmpSerialnotitle = cmpSerialnotitle;
    }

    public String getCmpSerialno() {
        return cmpSerialno;
    }

    public void setCmpSerialno(String cmpSerialno) {
        this.cmpSerialno = cmpSerialno;
    }

    public String getCmpManagenotitle() {
        return cmpManagenotitle;
    }

    public void setCmpManagenotitle(String cmpManagenotitle) {
        this.cmpManagenotitle = cmpManagenotitle;
    }

    public String getCmpManageno() {
        return cmpManageno;
    }

    public void setCmpManageno(String cmpManageno) {
        this.cmpManageno = cmpManageno;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
