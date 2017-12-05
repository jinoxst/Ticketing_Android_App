package jp.ne.jinoxst.mat.itg.pojo;

import java.io.Serializable;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class SaleRecordItemDetail implements Serializable {
     @JsonKey(decamelize = true)
    private String orderTime;

    @JsonKey(decamelize = true)
    private String manageno;

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
}
