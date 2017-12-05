package jp.ne.jinoxst.mat.itg.pojo.json;

import java.util.List;

import jp.ne.jinoxst.mat.itg.pojo.SaleRecordItemDetail;
import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class Response096 {
    @JsonKey(decamelize = true)
    private int status;

    @JsonKey(decamelize = true)
    private String message;

    @JsonKey(decamelize = true)
    private List<SaleRecordItemDetail> salerecordlist;

    public int getStatus() {
        return status;
    }

    public List<SaleRecordItemDetail> getSalerecordlist() {
        return salerecordlist;
    }

    public void setSalerecordlist(List<SaleRecordItemDetail> salerecordlist) {
        this.salerecordlist = salerecordlist;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}