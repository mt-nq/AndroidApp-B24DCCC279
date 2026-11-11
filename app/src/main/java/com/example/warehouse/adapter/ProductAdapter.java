package com.example.warehouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.warehouse.R;
import com.example.warehouse.model.Product;
import com.example.warehouse.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách sản phẩm trong RecyclerView
 */
public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    public ProductAdapter(Context context, OnProductClickListener listener) {
        this.context = context;
        this.productList = new ArrayList<>();
        this.listener = listener;
    }

    public void setProductList(List<Product> products) {
        this.productList = (products != null) ? products : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("Giá bán: " + FormatUtils.formatCurrency(product.getPrice()));
        holder.tvCategory.setText(product.getCategory());

        // Hiển thị số lượng
        holder.tvQuantity.setText("SL: " + product.getQuantity());

        // Kiểm tra sản phẩm sắp hết hàng (số lượng < 5)
        if (product.isLowStock()) {
            // Số lượng màu đỏ
            holder.tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.warning_red));
            // Hiển thị nhãn cảnh báo
            holder.tvLowStockBadge.setVisibility(View.VISIBLE);
        } else {
            // Số lượng màu bình thường
            holder.tvQuantity.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            // Ẩn nhãn cảnh báo
            holder.tvLowStockBadge.setVisibility(View.GONE);
        }

        // Bắt sự kiện click vào item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvQuantity, tvCategory, tvLowStockBadge;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            tvQuantity = itemView.findViewById(R.id.tvItemQuantity);
            tvCategory = itemView.findViewById(R.id.tvItemCategory);
            tvLowStockBadge = itemView.findViewById(R.id.tvItemLowStockBadge);
        }
    }
}
