package jp.ne.jinoxst.mat.itg.pojo.json;

import java.util.List;

import jp.ne.jinoxst.mat.itg.pojo.Master;

import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class Response011 {
    @JsonKey(decamelize = true)
    private int status;

    @JsonKey(decamelize = true)
    private String message;

    @JsonKey(decamelize = true)
    private List<Master> masters;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<Master> getMasters() {
        return masters;
    }

    public void setMasters(List<Master> masters) {
        this.masters = masters;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
