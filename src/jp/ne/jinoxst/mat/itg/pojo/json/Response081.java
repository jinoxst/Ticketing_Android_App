package jp.ne.jinoxst.mat.itg.pojo.json;

import java.util.List;

import jp.ne.jinoxst.mat.itg.pojo.Ticket;
import net.vvakame.util.jsonpullparser.annotation.JsonKey;
import net.vvakame.util.jsonpullparser.annotation.JsonModel;

@JsonModel(decamelize = true, treatUnknownKeyAsError = true)
public class Response081 {
    @JsonKey(decamelize = true)
    private int status;

    @JsonKey(decamelize = true)
    private String message;

    @JsonKey(decamelize = true)
    private List<Ticket> reissuelist;

    public int getStatus() {
        return status;
    }

    public List<Ticket> getReissuelist() {
        return reissuelist;
    }

    public void setReissuelist(List<Ticket> reissuelist) {
        this.reissuelist = reissuelist;
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