package com.example.warehouse.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.warehouse.R;
import com.example.warehouse.database.DatabaseHelper;

/**
 * Màn hình 1: Đăng nhập
 */
public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvErrorMessage;
    private Button btnLogin;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo cơ sở dữ liệu (tự động tạo bảng và chèn tài khoản admin/123456 trong onCreate)
        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ View
        initViews();

        // Xử lý sự kiện đăng nhập
        btnLogin.setOnClickListener(v -> handleLogin());
    }

    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnLogin = findViewById(R.id.btnLogin);
    }

    private void handleLogin() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Kiểm tra không để trống
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            tvErrorMessage.setText(R.string.error_empty_credentials);
            tvErrorMessage.setVisibility(View.VISIBLE);
            return;
        }

        // Kiểm tra tài khoản bằng cách truy vấn cơ sở dữ liệu
        boolean isValid = databaseHelper.checkLogin(username, password);

        if (isValid) {
            tvErrorMessage.setVisibility(View.GONE);
            // Đăng nhập thành công -> Chuyển sang MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            // Kết thúc LoginActivity để nút Back không quay lại màn hình này
            finish();
        } else {
            // Sai tên đăng nhập hoặc mật khẩu
            tvErrorMessage.setText(R.string.error_invalid_credentials);
            tvErrorMessage.setVisibility(View.VISIBLE);
        }
    }
}
