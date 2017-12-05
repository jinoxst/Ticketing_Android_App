package jp.ne.jinoxst.mat.itg.pojo.json;

import java.util.List;

import jp.ne.jinoxst.mat.itg.pojo.SaleRecord;
import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class Response093 {
    @JsonKey(decamelize = true)
    private int status;

    @JsonKey(decamelize = true)
    private String message;

    @JsonKey(decamelize = true)
    private String ym;

    @JsonKey(decamelize = true)
    private List<SaleRecord> salerecordlist;

    public int getStatus() {
        return status;
    }

    public String getYm() {
        return ym;
    }

    public void setYm(String ym) {
        this.ym = ym;
    }

    public List<SaleRecord> getSalerecordlist() {
        return salerecordlist;
    }

    public void setSalerecordlist(List<SaleRecord> salerecordlist) {
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