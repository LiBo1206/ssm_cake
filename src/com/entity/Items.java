package com.entity;

public class Items {
    // 订单项的唯一标识符
    private Integer id;

    // 订单项中商品的价格
    private Integer price;

    // 订单项中商品的数量
    private Integer amount;

    // 订单项所属订单的ID
    private Integer orderId;

    // 订单项中商品的ID
    private Integer goodId;

    // 订单项的总价（可能是由价格和数量计算得出）
    private float total;

    // 订单项关联的商品对象
    private Goods good;

    // 商品对象的getter和setter方法
    public Goods getGood() {
        return good;
    }

    public void setGood(Goods good) {
        this.good = good;
    }

    // 总价的getter和setter方法
    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }

    // 订单项信息的getter和setter方法
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getGoodId() {
        return goodId;
    }

    public void setGoodId(Integer goodId) {
        this.goodId = goodId;
    }
}