package jp.ne.jinoxst.mat.itg.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jp.ne.jinoxst.mat.itg.pojo.Item;

public class Cart {
    private static Cart instance;
    private static HashMap<String, OrderedItem> cartList;

    public synchronized static Cart getInstance(){
        if(instance == null){
            instance = new Cart();
        }

        return instance;
    }

    private Cart(){
        cartList = new HashMap<String, OrderedItem>();
    }

    public synchronized HashMap<String, OrderedItem> getCartList(){
        return cartList;
    }

    public synchronized ArrayList<OrderedItem> getOrderedItem(){
        ArrayList<OrderedItem> list = new ArrayList<OrderedItem>();
        for(String emmId : cartList.keySet()){
            OrderedItem item = cartList.get(emmId);
            if(item.getOrderedCnt() > 0){
                list.add(item);
            }
        }
        return list;
    }

    public synchronized String getOrderMatrix(){
        String str = "";
        ArrayList<OrderedItem> list = getOrderedItem();
        for(OrderedItem item : list){
            str += item.getEmmId() + ":" + item.getOrderedCnt() + ",";
        }
        return str;
    }

    public synchronized void plusItemIntoCart(int emmId){
        if(cartList.containsKey(String.valueOf(emmId))){
            OrderedItem sItem = cartList.get(String.valueOf(emmId));
            sItem.plusOrderedCnt(1);
        }
    }

    public synchronized void minusItemFromCart(int emmId){
        if(cartList.containsKey(String.valueOf(emmId))){
            OrderedItem sItem = cartList.get(String.valueOf(emmId));
            sItem.minusOrderedCnt();
        }
    }

    public synchronized void putItemToCart(Item item, int cnt){
        if(cartList.containsKey(String.valueOf(item.getEmmId()))){
            OrderedItem sItem = cartList.get(String.valueOf(item.getEmmId()));
            sItem.plusOrderedCnt(cnt);
        }else{
            OrderedItem sItem = instance.new OrderedItem();
            sItem.setEmmId(item.getEmmId());
            sItem.setEmmSeq(item.getEmmSeq());
            sItem.setEmmNm(item.getEmmNm());
            sItem.setUnitPrice(item.getEmmPrice());
            sItem.setBrandImg(item.getBrandImg());
            sItem.setOrderedCnt(cnt);
            cartList.put(String.valueOf(item.getEmmId()), sItem);
        }
    }

    public synchronized int getOrderedItemCount(Item item){
        if(cartList.containsKey(String.valueOf(item.getEmmId()))){
            OrderedItem sItem = cartList.get(String.valueOf(item.getEmmId()));
            return sItem.getOrderedCnt();
        }else{
            return 0;
        }
    }

    public synchronized int getTotalCount(){
        int cnt = 0;
        Iterator<String> it = cartList.keySet().iterator();
        while (it.hasNext()) {
            String emmId = it.next();
            OrderedItem item = cartList.get(emmId);
            cnt += item.getOrderedCnt();
        }

        return cnt;
    }

    public synchronized int getTotalPrice(){
        int sum = 0;
        Iterator<String> it = cartList.keySet().iterator();
        while (it.hasNext()) {
            String emmId = it.next();
            OrderedItem item = cartList.get(emmId);
            sum += item.getOrderedCnt() * item.getUnitPrice();
        }

        return sum;
    }

    public synchronized void deleteItemFromCart(int emmId){
        cartList.remove(String.valueOf(emmId));
    }

    public synchronized void clearCartList(){
        cartList.clear();
    }

    public class OrderedItem{
        private int emmId;
        private int emmSeq;
        private String emmNm;
        private String brandImg;
        private int unitPrice;
        private int orderedCnt;

        public int getEmmId() {
            return emmId;
        }
        public void setEmmId(int emmId) {
            this.emmId = emmId;
        }
        public int getEmmSeq() {
            return emmSeq;
        }
        public void setEmmSeq(int emmSeq) {
            this.emmSeq = emmSeq;
        }
        public String getEmmNm() {
            return emmNm;
        }
        public void setEmmNm(String emmNm) {
            this.emmNm = emmNm;
        }
        public String getBrandImg() {
            return brandImg;
        }
        public void setBrandImg(String brandImg) {
            this.brandImg = brandImg;
        }
        public int getUnitPrice() {
            return unitPrice;
        }
        public void setUnitPrice(int unitPrice) {
            this.unitPrice = unitPrice;
        }
        public int getOrderedCnt() {
            return orderedCnt;
        }
        public void plusOrderedCnt(int orderCnt) {
            this.orderedCnt += orderCnt;
        }
        public void setOrderedCnt(int orderCnt) {
            this.orderedCnt = orderCnt;
        }
        public void minusOrderedCnt() {
            if(this.orderedCnt > 0){
                this.orderedCnt--;
            }
        }
    }
}
