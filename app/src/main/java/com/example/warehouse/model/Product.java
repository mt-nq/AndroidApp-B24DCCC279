package com.example.warehouse.model;

import java.io.Serializable;

/**
 * Lớp Product biểu diễn thông tin một sản phẩm trong kho.
 */
public class Product implements Serializable {
    private int id;
    private String name;
    private int price;
    private int quantity;
    private String category;
    private int active; // 1: đang kinh doanh, 0: ngừng kinh doanh
    private String description;

    public Product() {
    }

    public Product(String name, int price, int quantity, String category, int active, String description) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    public Product(int id, String name, int price, int quantity, String category, int active, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.category = category;
        this.active = active;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Kiểm tra sản phẩm có sắp hết hàng hay không (số lượng < 5).
     */
    public boolean isLowStock() {
        return this.quantity < 5;
    }

    /**
     * Tính tổng giá trị tồn kho của sản phẩm này (giá bán * số lượng).
     */
    public long getTotalStockValue() {
        return (long) this.price * this.quantity;
    }
}
