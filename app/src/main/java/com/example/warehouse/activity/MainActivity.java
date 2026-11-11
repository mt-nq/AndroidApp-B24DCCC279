package com.example.warehouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouse.R;
import com.example.warehouse.adapter.ProductAdapter;
import com.example.warehouse.database.DatabaseHelper;
import com.example.warehouse.model.Product;
import com.example.warehouse.utils.FormatUtils;

import java.util.List;

/**
 * Màn hình 2: Danh sách sản phẩm và thống kê kho hàng
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "PRODUCT_ID";

    // View thống kê
    private TextView tvTotalProducts;
    private TextView tvTotalValue;
    private TextView tvLowStockCount;

    // View danh sách
    private RecyclerView rvProducts;
    private TextView tvEmptyList;
    private Button btnAddNewProduct;

    // Database & Adapter
    private DatabaseHelper databaseHelper;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHelper = new DatabaseHelper(this);

        initViews();
        setupRecyclerView();

        // Nút chuyển sang màn hình Thêm sản phẩm
        btnAddNewProduct.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
            startActivity(intent);
        });
    }

    private void initViews() {
        tvTotalProducts = findViewById(R.id.tvTotalProducts);
        tvTotalValue = findViewById(R.id.tvTotalValue);
        tvLowStockCount = findViewById(R.id.tvLowStockCount);

        rvProducts = findViewById(R.id.rvProducts);
        tvEmptyList = findViewById(R.id.tvEmptyList);
        btnAddNewProduct = findViewById(R.id.btnAddNewProduct);
    }

    private void setupRecyclerView() {
        rvProducts.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, product -> {
            // Khi nhấn vào một sản phẩm -> Mở ProductDetailActivity và truyền id qua Intent
            Intent intent = new Intent(MainActivity.this, ProductDetailActivity.class);
            intent.putExtra(EXTRA_PRODUCT_ID, product.getId());
            startActivity(intent);
        });
        rvProducts.setAdapter(productAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Luôn làm mới danh sách và thống kê khi quay lại màn hình
        loadData();
    }

    /**
     * Tải lại danh sách sản phẩm và các thông số thống kê từ SQLite
     */
    private void loadData() {
        // 1. Tải danh sách sản phẩm
        List<Product> products = databaseHelper.getAllProducts();
        productAdapter.setProductList(products);

        // Hiển thị hoặc ẩn thông báo danh sách trống
        if (products.isEmpty()) {
            tvEmptyList.setVisibility(View.VISIBLE);
        } else {
            tvEmptyList.setVisibility(View.GONE);
        }

        // 2. Cập nhật khối thống kê
        int totalProducts = databaseHelper.getProductCount();
        long totalValue = databaseHelper.getTotalValue();
        int lowStockCount = databaseHelper.countLowStock();

        tvTotalProducts.setText(String.valueOf(totalProducts));
        tvTotalValue.setText(FormatUtils.formatCurrency(totalValue));
        tvLowStockCount.setText(String.valueOf(lowStockCount));
    }
}
