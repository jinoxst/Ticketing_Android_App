package jp.ne.jinoxst.mat.itg.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import jp.ne.jinoxst.mat.itg.R;
import jp.ne.jinoxst.mat.itg.pojo.Ticket;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.xmlpull.v1.XmlPullParser;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.Log;
import android.util.Xml;

public class StringUtil {
    public static String getCurrencyFormat(String s) {
        double amount = Double.parseDouble(s);
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    public static String getCurrencyFormat(int s) {
        double amount = Double.parseDouble(String.valueOf(s));
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    public static String putSpaceChar(int cnt) {
        String s = "";
        for (int i = 0; i < cnt; i++) {
            s += " ";
        }
        return s;
    }

    public static String getPrintName(Resources resources, String name) {
        List<String> list = getAssetsDataForList(resources, "print_names.xml", null);
        if (list.contains(name)) {
            return name;
        } else {
            return resources.getString(R.string.default_print_name);
        }
    }

    public static String getReceiptContent(Resources resources, Ticket ticket) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(resources.getString(R.string.receipt_line_start)).append("\n");
        buffer.append(StringUtil.putSpaceChar(15)).append(ticket.getHeader()).append("\n");
        buffer.append(resources.getString(R.string.receipt_line_start)).append("\n");
        buffer.append(ticket.getTitle()).append("\n");
        buffer.append(resources.getString(R.string.receipt_line_equal)).append("\n");
        buffer.append(resources.getString(R.string.receipt_orderdate)).append(ticket.getOrderTime()).append("\n");
        buffer.append(resources.getString(R.string.receipt_shopname)).append(ticket.getShopName()).append("\n");
        buffer.append(resources.getString(R.string.receipt_line_equal)).append("\n");
        buffer.append(resources.getString(R.string.receipt_itemname)).append(ticket.getEmmName()).append("\n");
        buffer.append(resources.getString(R.string.receipt_itemprice))
                .append(StringUtil.getCurrencyFormat(ticket.getEmmPrice()))
                .append(resources.getString(R.string.yen_mark_jp)).append("\n");
        buffer.append(ticket.getSerialnotitle()).append("\n");
        buffer.append(StringUtil.putSpaceChar(3)).append(ticket.getSerialno()).append("\n");
        buffer.append(ticket.getManagenotitle()).append("\n");
        buffer.append(StringUtil.putSpaceChar(3)).append(ticket.getManageno()).append("\n");

        if (!ticket.getCmpSerialno().equals("")) {
            buffer.append(ticket.getCmpTitle()).append("\n");
            buffer.append(ticket.getCmpSerialnotitle()).append("\n");
            buffer.append(StringUtil.putSpaceChar(3)).append(ticket.getCmpSerialno()).append("\n");
            buffer.append(ticket.getCmpManagenotitle()).append("\n");
            buffer.append(StringUtil.putSpaceChar(3)).append(ticket.getCmpManageno()).append("\n");
        }
        buffer.append(resources.getString(R.string.receipt_line_minus)).append("\n");
        if (!ticket.getCmpSerialno().equals("")) {
            buffer.append(ticket.getCmpInfo().replaceAll(Constant.RECEIPT_NEWLINE, "\n")).append("\n");
        }
        buffer.append(ticket.getInfo().replaceAll(Constant.RECEIPT_NEWLINE, "\n")).append("\n");
        return buffer.toString();
    }

    public static String[] getLeftmenuList(Resources resources) {
        List<String> list = getAssetsDataForList(resources, "salerecord_leftmenu.xml", null);
        return list.toArray(new String[0]);
    }

    public static List<String> getAssetsDataForList(Resources resources, String filename, String attrname) {
        if (attrname == null) {
            attrname = "name";
        }
        List<String> list = new ArrayList<String>();
        InputStream is = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            AssetManager asset = resources.getAssets();
            is = asset.open(filename);
            InputStreamReader isr = new InputStreamReader(is);
            parser.setInput(isr);
            String tag = "";
            String value = "";
            for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser.next()) {
                switch (type) {
                case XmlPullParser.START_TAG:
                    tag = parser.getName();
                    break;
                case XmlPullParser.TEXT:
                    value = parser.getText();
                    if (value.trim().length() != 0) {
                        if (tag.equals(attrname)) {
                            list.add(value);
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
                }
            }
        } catch (Exception e) {
            Log.e("JsonFormatException", ExceptionUtils.getStackTrace(e));
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                }
            }
        }

        return list;
    }

    public static String getAssetLineChartHeader(Resources resource) {
        return getAssetTextData(resource, "wv_def_line_header.txt");
    }

    public static String getAssetPieChartHeader(Resources resource) {
        return getAssetTextData(resource, "wv_def_pie_header.txt");
    }

    public static String getAssetChartFooter(Resources resource) {
        return getAssetTextData(resource, "wv_def_footer.txt");
    }

    public static String getAssetTextData(Resources resource, String filename){
        AssetManager as = resource.getAssets();
        InputStream is = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            is = as.open(filename);
            br = new BufferedReader(new InputStreamReader(is));
            String str;
            while ((str = br.readLine()) != null) {
                sb.append(str);
            }
        } catch (Exception e) {
            Log.e("JsonFormatException", ExceptionUtils.getStackTrace(e));
        } finally {
            if (br != null)
                try{
                    br.close();
                }catch(Exception e){}
        }

        return sb.toString();
    }
}
