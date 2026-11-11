package com.example.warehouse.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.warehouse.R;
import com.example.warehouse.database.DatabaseHelper;
import com.example.warehouse.model.Product;

public class AddProductActivity extends AppCompatActivity {

    private ImageButton btnBackAdd;
    private EditText etProductName;
    private EditText etProductPrice;
    private EditText etProductQuantity;
    private Spinner spProductCategory;
    private SwitchCompat swProductActive;
    private EditText etProductDescription;
    private Button btnSaveProduct;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        databaseHelper = new DatabaseHelper(this);

        initViews();
        setupSpinner();

        btnBackAdd.setOnClickListener(v -> finish());

        btnSaveProduct.setOnClickListener(v -> handleSaveProduct());
    }

    private void initViews() {
        btnBackAdd = findViewById(R.id.btnBackAdd);
        etProductName = findViewById(R.id.etProductName);
        etProductPrice = findViewById(R.id.etProductPrice);
        etProductQuantity = findViewById(R.id.etProductQuantity);
        spProductCategory = findViewById(R.id.spProductCategory);
        swProductActive = findViewById(R.id.swProductActive);
        etProductDescription = findViewById(R.id.etProductDescription);
        btnSaveProduct = findViewById(R.id.btnSaveProduct);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.product_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProductCategory.setAdapter(adapter);
    }

    private void handleSaveProduct() {
        String name = etProductName.getText().toString().trim();
        String priceStr = etProductPrice.getText().toString().trim();
        String quantityStr = etProductQuantity.getText().toString().trim();
        String category = spProductCategory.getSelectedItem().toString();
        int active = swProductActive.isChecked() ? 1 : 0;
        String description = etProductDescription.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etProductName.setError("Vui lòng nhập tên sản phẩm");
            etProductName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(priceStr)) {
            etProductPrice.setError("Vui lòng nhập giá bán");
            etProductPrice.requestFocus();
            return;
        }

        int price;
        try {
            price = Integer.parseInt(priceStr);
            if (price <= 0) {
                etProductPrice.setError("Giá bán phải lớn hơn 0");
                etProductPrice.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etProductPrice.setError("Giá bán không hợp lệ");
            etProductPrice.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(quantityStr)) {
            etProductQuantity.setError("Vui lòng nhập số lượng");
            etProductQuantity.requestFocus();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
            if (quantity < 0) {
                etProductQuantity.setError("Số lượng không được âm");
                etProductQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etProductQuantity.setError("Số lượng không hợp lệ");
            etProductQuantity.requestFocus();
            return;
        }

        Product newProduct = new Product(name, price, quantity, category, active, description);
        long result = databaseHelper.insertProduct(newProduct);

        if (result != -1) {
            Toast.makeText(this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lỗi khi lưu sản phẩm vào cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
        }
    }
}
