package jp.ne.jinoxst.mat.itg.util.db;

import java.util.ArrayList;
import java.util.List;

import jp.ne.jinoxst.mat.itg.pojo.Master;
import jp.ne.jinoxst.mat.itg.pojo.MasterDetail;
import jp.ne.jinoxst.mat.itg.pojo.OrderLeftMenu;
import jp.ne.jinoxst.mat.itg.util.Constant;
import jp.ne.jinoxst.mat.itg.util.DateUtil;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {
    protected final Context context;
    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;

    public DBAdapter(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }

    //
    // SQLiteOpenHelper
    //

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, Constant.DATABASE_NAME, null, Constant.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "create table master ( " +
                    " id integer primary key autoincrement, " +
                    " emm_seq int not null, " +
                    " emm_nm text not null, " +
                    " emm_kana text not null, " +
                    " leftmenu_name text not null, " +
                    " sales_channel int not null, " +
                    " emm_category_type int not null, " +
                    " status int not null, " +
                    " lastseqno int not null, " +
                    " show_num int not null," +
                    " make_time text not null)");

            db.execSQL(
                    "create table masterdetail ( " +
                    " id integer primary key autoincrement, " +
                    " emm_seq int not null, " +
                    " emm_id int not null, " +
                    " barcode text not null, " +
                    " emm_nm text not null, " +
                    " emm_kana text not null, " +
                    " emm_price int not null, " +
                    " sales_channel int not null, " +
                    " status int not null, " +
                    " xbigtime text null, " +
                    " xendtime text null, " +
                    " img_url text null, " +
                    " lastseqno int not null, " +
                    " make_time text not null)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion) {
            db.execSQL("drop table if exists master");
            Log.d("DBAdapter","master table is droped");
            db.execSQL("drop table if exists masterdetail");
            Log.d("DBAdapter","masterdetail table is droped");
            onCreate(db);
        }
    }

    //
    // Adapter Methods
    //

    public DBAdapter open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    //
    // App Methods
    //

    public boolean deleteTable(String tableName) {
        return db.delete(tableName, null, null) > 0;
    }

    public boolean deleteLeftmenu(int emm_seq) {
        return db.delete("leftmunu", emm_seq + "=" + emm_seq, null) > 0;
    }

    public int isExistLeftMenu(int emm_seq) {
        String query = "select count(1) from master where emm_seq=?";
        String[] params = {emm_seq+""};
        Cursor cursor = db.rawQuery(query, params);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public int getCountLeftMenu() {
        String query = "select count(1) from master";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToNext()){
            return cursor.getInt(0);
        }else{
            return 0;
        }
    }

    public Cursor getAllLeftMenu() {
        return db.query("master", null, null, null, null, null, null);
    }

    public String[] getLeftMenu(int emmCategoryType) {
        String query = "select count(1) from master where emm_category_type=? and status=0";
        String[] params = {emmCategoryType+""};
        Cursor cursor = db.rawQuery(query, params);
        cursor.moveToFirst();
        int cnt = cursor.getInt(0);
        String[] leftmenulist = new String[cnt];
        query = "select leftmenu_name from master where emm_category_type=? and status=0";
        cursor = db.rawQuery(query, params);
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                leftmenulist[i++] = cursor.getString(0);
                Log.d("DBAdapter",cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return leftmenulist;
    }

    public List<OrderLeftMenu> getLeftMenuList(int emmCategoryType) {
        String[] params = {emmCategoryType+""};
        List<OrderLeftMenu> list = new ArrayList<OrderLeftMenu>();
        String query = "select leftmenu_name,emm_seq from master where emm_category_type=? and status=0";
        Cursor cursor = db.rawQuery(query, params);
        if (cursor.moveToFirst()) {
            do {
                OrderLeftMenu menu = new OrderLeftMenu();
                menu.setTitle(cursor.getString(0));
                menu.setEmmSeq(cursor.getInt(1));
                list.add(menu);
                Log.d("DBAdapter",cursor.getString(0));
            } while (cursor.moveToNext());
        }

        return list;
    }

    public int isExistMasterDetail(int emm_id) {
        String query = "select count(1) from masterdetail where emm_id=?";
        String[] params = {emm_id+""};
        Cursor cursor = db.rawQuery(query, params);
        cursor.moveToFirst();
        return cursor.getInt(0);
    }

    public MasterDetail getMasterDetail(int emmId) {
        MasterDetail md = new MasterDetail();
        String query = "select count(1) from masterdetail where emm_id=? and status=0";
        String[] params = {emmId+""};
        Cursor cursor = db.rawQuery(query, params);
        cursor.moveToFirst();
        int cnt = cursor.getInt(0);
        if(cnt > 0){
            query = "select emm_seq,emm_id,emm_nm,emm_kana,emm_price,img_url from masterdetail where emm_id=? and status=0";
            cursor = db.rawQuery(query, params);
            if (cursor.moveToFirst()) {
                md.setEmmSeq(cursor.getInt(0));
                md.setEmmId(cursor.getInt(1));
                md.setEmmNm(cursor.getString(2));
                md.setEmmKana(cursor.getString(3));
                md.setEmmPrice(cursor.getInt(4));
                md.setImgUrl(cursor.getString(5));
            }
            return md;
        }else{
            return null;
        }
    }

    public void insertLeftmenu(Master master) {
        ContentValues values = new ContentValues();
        values.put("emm_seq", Integer.valueOf(master.getEmmSeq()));
        values.put("emm_nm", master.getEmmNm());
        values.put("emm_kana", master.getEmmKana());
        values.put("leftmenu_name", master.getLeftmenuName());
        values.put("sales_channel", Integer.valueOf(master.getSalesChannel()));
        values.put("emm_category_type", Integer.valueOf(master.getEmmCategoryType()));
        if(master.getSalesChannel() == Constant.SALES_CHANNEL_ALL || master.getSalesChannel() == Constant.SALES_CHANNEL_ONLY_API){
            values.put("status", Integer.valueOf(master.getStatus()));
        }else{
            values.put("status", -1);
        }
        values.put("lastseqno", Integer.valueOf(master.getLastseqno()));
        values.put("show_num", Integer.valueOf(master.getShowNum()));
        values.put("make_time", DateUtil.getDateTime());
        db.insert("master", null, values);
    }

    public void updateLeftmenu(Master master) {
        ContentValues values = new ContentValues();
        values.put("emm_nm", master.getEmmNm());
        values.put("emm_kana", master.getEmmKana());
        values.put("leftmenu_name", master.getLeftmenuName());
        values.put("sales_channel", Integer.valueOf(master.getSalesChannel()));
        values.put("emm_category_type", Integer.valueOf(master.getEmmCategoryType()));
        if(master.getSalesChannel() == Constant.SALES_CHANNEL_ALL || master.getSalesChannel() == Constant.SALES_CHANNEL_ONLY_API){
            values.put("status", Integer.valueOf(master.getStatus()));
        }else{
            values.put("status", -1);
        }
        values.put("lastseqno", Integer.valueOf(master.getLastseqno()));
        values.put("show_num", Integer.valueOf(master.getShowNum()));
        values.put("make_time", DateUtil.getDateTime());
        db.update("master", values, "emm_seq="+master.getEmmSeq(),null);
    }

    public void insertMasterDetail(MasterDetail md) {
        ContentValues values = new ContentValues();
        values.put("emm_seq", Integer.valueOf(md.getEmmSeq()));
        values.put("emm_id", Integer.valueOf(md.getEmmId()));
        values.put("barcode", md.getBarcode());
        values.put("emm_nm", md.getEmmNm());
        values.put("emm_kana", md.getEmmKana());
        values.put("emm_price", Integer.valueOf(md.getEmmPrice()));
        values.put("sales_channel", Integer.valueOf(md.getSalesChannel()));
        if(md.getSalesChannel() == Constant.SALES_CHANNEL_ALL || md.getSalesChannel() == Constant.SALES_CHANNEL_ONLY_API){
            values.put("status", Integer.valueOf(md.getStatus()));
        }else{
            values.put("status", -1);
        }
        values.put("xbigtime", md.getXbigtime());
        values.put("xendtime", md.getXendtime());
        values.put("img_url", md.getImgUrl());
        values.put("lastseqno", Integer.valueOf(md.getLastseqno()));
        values.put("make_time", DateUtil.getDateTime());
        db.insert("masterdetail", null, values);
    }

    public void updateMasterDetail(MasterDetail md) {
        ContentValues values = new ContentValues();
        values.put("barcode", md.getBarcode());
        values.put("emm_nm", md.getEmmNm());
        values.put("emm_kana", md.getEmmKana());
        values.put("emm_price", Integer.valueOf(md.getEmmPrice()));
        if(md.getSalesChannel() == Constant.SALES_CHANNEL_ALL || md.getSalesChannel() == Constant.SALES_CHANNEL_ONLY_API){
            values.put("status", Integer.valueOf(md.getStatus()));
        }else{
            values.put("status", -1);
        }
        values.put("xbigtime", md.getXbigtime());
        values.put("xendtime", md.getXendtime());
        values.put("img_url", md.getImgUrl());
        values.put("lastseqno", Integer.valueOf(md.getLastseqno()));
        values.put("make_time", DateUtil.getDateTime());
        db.update("masterdetail", values, "emm_id="+md.getEmmId(),null);
    }
}
