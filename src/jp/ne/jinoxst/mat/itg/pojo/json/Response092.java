package jp.ne.jinoxst.mat.itg.pojo.json;

import java.util.List;

import jp.ne.jinoxst.mat.itg.pojo.SaleRecordDayDetail;
import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class Response092 {
    @JsonKey(decamelize = true)
    private int status;

    @JsonKey(decamelize = true)
    private String message;

    @JsonKey(decamelize = true)
    private List<SaleRecordDayDetail> daydetaillist;

    public int getStatus() {
        return status;
    }

    public List<SaleRecordDayDetail> getDaydetaillist() {
        return daydetaillist;
    }

    public void setDaydetaillist(List<SaleRecordDayDetail> daydetaillist) {
        this.daydetaillist = daydetaillist;
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