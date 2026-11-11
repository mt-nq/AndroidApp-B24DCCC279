package com.example.warehouse.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.warehouse.R;
import com.example.warehouse.database.DatabaseHelper;
import com.example.warehouse.model.Product;
import com.example.warehouse.utils.FormatUtils;

/**
 * Màn hình 4: Chi tiết sản phẩm
 */
public class ProductDetailActivity extends AppCompatActivity {

    private ImageButton btnBackDetail;

    // View thông tin sản phẩm
    private TextView tvDetailName;
    private TextView tvDetailCategory;
    private TextView tvDetailPrice;
    private TextView tvDetailQuantity;
    private TextView tvDetailLowStockBadge;
    private TextView tvDetailTotalValue;
    private TextView tvDetailDescription;

    // View nút chức năng
    private Button btnRestockOne;
    private Button btnSellOne;
    private Button btnBack;
    private Button btnDeleteProduct;

    private DatabaseHelper databaseHelper;
    private int productId = -1;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        databaseHelper = new DatabaseHelper(this);

        // Nhận ID sản phẩm từ Intent
        productId = getIntent().getIntExtra(MainActivity.EXTRA_PRODUCT_ID, -1);

        if (productId == -1) {
            Toast.makeText(this, "Không tìm thấy thông tin sản phẩm", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
        loadProductData();
    }

    private void initViews() {
        btnBackDetail = findViewById(R.id.btnBackDetail);

        tvDetailName = findViewById(R.id.tvDetailName);
        tvDetailCategory = findViewById(R.id.tvDetailCategory);
        tvDetailPrice = findViewById(R.id.tvDetailPrice);
        tvDetailQuantity = findViewById(R.id.tvDetailQuantity);
        tvDetailLowStockBadge = findViewById(R.id.tvDetailLowStockBadge);
        tvDetailTotalValue = findViewById(R.id.tvDetailTotalValue);
        tvDetailDescription = findViewById(R.id.tvDetailDescription);

        btnRestockOne = findViewById(R.id.btnRestockOne);
        btnSellOne = findViewById(R.id.btnSellOne);
        btnBack = findViewById(R.id.btnBack);
        btnDeleteProduct = findViewById(R.id.btnDeleteProduct);
    }

    private void setupListeners() {
        // Nút Back trên header và nút Quay lại phía dưới
        btnBackDetail.setOnClickListener(v -> finish());
        btnBack.setOnClickListener(v -> finish());

        // Nút Nhập thêm 1
        btnRestockOne.setOnClickListener(v -> handleRestockOne());

        // Nút Bán 1
        btnSellOne.setOnClickListener(v -> handleSellOne());

        // Nút Xóa sản phẩm
        btnDeleteProduct.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    /**
     * Tải và hiển thị thông tin sản phẩm từ SQLite theo ID
     */
    private void loadProductData() {
        currentProduct = databaseHelper.getProductById(productId);

        if (currentProduct == null) {
            Toast.makeText(this, "Sản phẩm không tồn tại trong kho", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 1. Tên và Loại sản phẩm
        tvDetailName.setText(currentProduct.getName());
        tvDetailCategory.setText(currentProduct.getCategory());

        // 2. Giá bán
        tvDetailPrice.setText(FormatUtils.formatCurrency(currentProduct.getPrice()));

        // 3. Số lượng tồn kho và cảnh báo
        int qty = currentProduct.getQuantity();
        tvDetailQuantity.setText(String.valueOf(qty));

        if (currentProduct.isLowStock()) {
            tvDetailLowStockBadge.setVisibility(View.VISIBLE);
        } else {
            tvDetailLowStockBadge.setVisibility(View.GONE);
        }

        // Giá trị tồn kho (giá bán * số lượng)
        long totalStockValue = currentProduct.getTotalStockValue();
        tvDetailTotalValue.setText(FormatUtils.formatCurrency(totalStockValue));

        // 4. Mô tả
        String desc = currentProduct.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            tvDetailDescription.setText("(Chưa có mô tả)");
        } else {
            tvDetailDescription.setText(desc);
        }
    }

    /**
     * Tăng số lượng thêm 1, ghi vào SQLite và cập nhật UI
     */
    private void handleRestockOne() {
        if (currentProduct == null) return;

        int newQuantity = currentProduct.getQuantity() + 1;
        int rowsAffected = databaseHelper.updateQuantity(productId, newQuantity);

        if (rowsAffected > 0) {
            Toast.makeText(this, "Đã nhập thêm 1 sản phẩm", Toast.LENGTH_SHORT).show();
            loadProductData();
        } else {
            Toast.makeText(this, "Cập nhật số lượng thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Giảm số lượng đi 1; nếu bằng 0 thông báo Hết hàng
     */
    private void handleSellOne() {
        if (currentProduct == null) return;

        if (currentProduct.getQuantity() <= 0) {
            Toast.makeText(this, "Hết hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        int newQuantity = currentProduct.getQuantity() - 1;
        int rowsAffected = databaseHelper.updateQuantity(productId, newQuantity);

        if (rowsAffected > 0) {
            Toast.makeText(this, "Đã bán 1 sản phẩm", Toast.LENGTH_SHORT).show();
            loadProductData();
        } else {
            Toast.makeText(this, "Cập nhật số lượng thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Xác nhận xóa sản phẩm
     */
    private void showDeleteConfirmationDialog() {
        if (currentProduct == null) return;

        new AlertDialog.Builder(this)
                .setTitle(R.string.confirm_delete_title)
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm \"" + currentProduct.getName() + "\" không?")
                .setPositiveButton(R.string.action_delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int rowsDeleted = databaseHelper.deleteProduct(productId);
                        if (rowsDeleted > 0) {
                            Toast.makeText(ProductDetailActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                            // Quay lại màn hình danh sách và thống kê
                            finish();
                        } else {
                            Toast.makeText(ProductDetailActivity.this, "Xóa sản phẩm thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(R.string.action_cancel, null)
                .show();
    }
}
