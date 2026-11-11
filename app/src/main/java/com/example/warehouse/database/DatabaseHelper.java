package com.example.warehouse.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.warehouse.model.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Lớp quản trị cơ sở dữ liệu SQLite warehouse.db
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Tên và phiên bản cơ sở dữ liệu
    private static final String DATABASE_NAME = "warehouse.db";
    private static final int DATABASE_VERSION = 1;

    // Bảng users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Bảng products
    public static final String TABLE_PRODUCTS = "products";
    public static final String COLUMN_PRODUCT_ID = "id";
    public static final String COLUMN_PRODUCT_NAME = "name";
    public static final String COLUMN_PRODUCT_PRICE = "price";
    public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
    public static final String COLUMN_PRODUCT_CATEGORY = "category";
    public static final String COLUMN_PRODUCT_ACTIVE = "active";
    public static final String COLUMN_PRODUCT_DESCRIPTION = "description";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. Tạo bảng users
        String createTableUsers = "CREATE TABLE " + TABLE_USERS + " ("
                + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, "
                + COLUMN_PASSWORD + " TEXT NOT NULL"
                + ");";
        db.execSQL(createTableUsers);

        // 2. Tạo bảng products
        String createTableProducts = "CREATE TABLE " + TABLE_PRODUCTS + " ("
                + COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL, "
                + COLUMN_PRODUCT_CATEGORY + " TEXT NOT NULL, "
                + COLUMN_PRODUCT_ACTIVE + " INTEGER NOT NULL, "
                + COLUMN_PRODUCT_DESCRIPTION + " TEXT"
                + ");";
        db.execSQL(createTableProducts);

        // 3. Chèn tài khoản mẫu vào bảng users: admin / 123456
        insertUser(db, "admin", "123456");

        // 4. Chèn 3 sản phẩm mẫu vào bảng products theo đề bài (active = 1)
        insertProduct(db, new Product("Chuột không dây", 150000, 20, "Điện tử", 1, "Chuột quang không dây tiện lợi"));
        insertProduct(db, new Product("Bàn phím cơ", 450000, 3, "Điện tử", 1, "Bàn phím cơ switch xanh, có đèn nền"));
        insertProduct(db, new Product("Sổ tay A5", 25000, 40, "Văn phòng phẩm", 1, "Sổ tay ghi chép kích thước A5"));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ==============================================================
    // 3.3. CÁC PHƯƠNG THỨC THAO TÁC CƠ SỞ DỮ LIỆU
    // ==============================================================

    /**
     * Kiểm tra đăng nhập với bảng users.
     * Trả về true nếu có dòng khớp, false nếu sai hoặc không có.
     */
    public boolean checkLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_USER_ID + " FROM " + TABLE_USERS
                + " WHERE " + COLUMN_USERNAME + " = ? AND " + COLUMN_PASSWORD + " = ?";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, new String[]{username, password});
            return cursor != null && cursor.getCount() > 0;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * Thêm tài khoản mới vào bảng users (nhận SQLiteDatabase từ onCreate).
     */
    public long insertUser(SQLiteDatabase db, String username, String password) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_PASSWORD, password);
        return db.insert(TABLE_USERS, null, values);
    }

    /**
     * Thêm tài khoản mới vào bảng users (dùng khi chèn dữ liệu mẫu hoặc đăng ký).
     */
    public long insertUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        return insertUser(db, username, password);
    }

    /**
     * Thêm sản phẩm mới vào bảng products (nhận SQLiteDatabase từ onCreate).
     */
    public long insertProduct(SQLiteDatabase db, Product p) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, p.getName());
        values.put(COLUMN_PRODUCT_PRICE, p.getPrice());
        values.put(COLUMN_PRODUCT_QUANTITY, p.getQuantity());
        values.put(COLUMN_PRODUCT_CATEGORY, p.getCategory());
        values.put(COLUMN_PRODUCT_ACTIVE, p.getActive());
        values.put(COLUMN_PRODUCT_DESCRIPTION, p.getDescription());
        return db.insert(TABLE_PRODUCTS, null, values);
    }

    /**
     * Thêm sản phẩm mới vào bảng products, trả về id vừa thêm.
     */
    public long insertProduct(Product p) {
        SQLiteDatabase db = this.getWritableDatabase();
        return insertProduct(db, p);
    }

    /**
     * Lấy toàn bộ sản phẩm để hiển thị danh sách.
     */
    public List<Product> getAllProducts() {
        List<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " ORDER BY " + COLUMN_PRODUCT_ID + " DESC";
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                    int price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY));
                    String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY));
                    int active = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ACTIVE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION));

                    Product product = new Product(id, name, price, quantity, category, active, description);
                    productList.add(product);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return productList;
    }

    /**
     * Lấy một sản phẩm theo id.
     */
    public Product getProductById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_ID + " = ?";
        Cursor cursor = null;
        Product product = null;

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(id)});
            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_PRICE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_QUANTITY));
                String category = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CATEGORY));
                int active = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ACTIVE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_DESCRIPTION));

                product = new Product(id, name, price, quantity, category, active, description);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return product;
    }

    /**
     * Cập nhật số lượng (dùng cho Nhập thêm / Bán).
     */
    public int updateQuantity(int id, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_QUANTITY, quantity);
        return db.update(TABLE_PRODUCTS, values, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Xóa sản phẩm theo id.
     */
    public int deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * Tổng giá trị kho: SUM(price * quantity).
     */
    public long getTotalValue() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT SUM(" + COLUMN_PRODUCT_PRICE + " * " + COLUMN_PRODUCT_QUANTITY + ") FROM " + TABLE_PRODUCTS;
        Cursor cursor = null;
        long total = 0;

        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                total = cursor.getLong(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return total;
    }

    /**
     * Số sản phẩm sắp hết hàng: COUNT với quantity < 5.
     */
    public int countLowStock() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_QUANTITY + " < 5";
        Cursor cursor = null;
        int count = 0;

        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }

    /**
     * Tổng số lượng các mặt hàng trong bảng products.
     */
    public int getProductCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_PRODUCTS;
        Cursor cursor = null;
        int count = 0;

        try {
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return count;
    }
}
