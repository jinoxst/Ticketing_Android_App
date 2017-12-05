package jp.ne.jinoxst.mat.itg.pojo.json;

import java.util.List;

import jp.ne.jinoxst.mat.itg.pojo.SaleRecord;
import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class Response094 {
    @JsonKey(decamelize = true)
    private int status;

    @JsonKey(decamelize = true)
    private String message;

    @JsonKey(decamelize = true)
    private String year;

    @JsonKey(decamelize = true)
    private List<SaleRecord> salerecordlist;

    public int getStatus() {
        return status;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
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