package com.example.yoga_app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yoga_app.R;
import com.example.yoga_app.CartItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;

    public CartAdapter(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    // Hàm nhóm lại các CartItem theo classId
    private List<CartItem> groupByItem(List<CartItem> items) {
        List<CartItem> grouped = new ArrayList<>();
        Map<Long, CartItem> itemMap = new HashMap<>();

        for (CartItem item : items) {
            Long itemKey = item.getClassId();  // Dùng classId làm khóa nhóm

            // Kiểm tra nếu đã có mục với classId giống
            if (itemMap.containsKey(itemKey)) {
                CartItem existingItem = itemMap.get(itemKey);
                existingItem.setQuantity(existingItem.getQuantity() + 1);  // Tăng số lượng
            } else {
                // Nếu chưa có, tạo mới và thêm vào danh sách nhóm
                CartItem newItem = new CartItem(
                        item.getClassId(), item.getComment(), item.getDate(),
                        item.getDuration(), item.getPrice(), item.getTeacher(),
                        item.getTime(), item.getType()
                );
                newItem.setQuantity(1);  // Mặc định số lượng là 1
                grouped.add(newItem);
                itemMap.put(itemKey, newItem);
            }
        }

        return grouped;
    }


    @NonNull
    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.classNameTextView.setText(item.getTeacher() + " - " + item.getDate()); // Ví dụ: hiển thị tên loại hình yoga
        // Bạn có thể thêm các TextView khác nếu cần

        holder.itemView.setOnClickListener(v -> {

//            Bundle bundle = new Bundle();
//            bundle.putInt("id", course.getId());
//            bundle.putString("day", course.getDay());
//            NavHostFragment.findNavController(fragment)
//                    .navigate(R.id.navigation_classes, bundle);

        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView classNameTextView;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            classNameTextView = itemView.findViewById(R.id.class_name);
        }
    }
}
