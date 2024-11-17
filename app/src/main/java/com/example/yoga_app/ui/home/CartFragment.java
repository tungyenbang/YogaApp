package com.example.yoga_app.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yoga_app.R;
import com.example.yoga_app.CartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItems;
    private final String DATABASE_URL = "https://courseapp-bee7c-default-rtdb.europe-west1.firebasedatabase.app/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_cart, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems);  // Chỉ khởi tạo adapter khi đã có dữ liệu
        recyclerView.setAdapter(cartAdapter);

        ImageButton btnClose = view.findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> {
            getParentFragmentManager().popBackStack(); // Quay lại màn hình trước
        });

        // Lấy dữ liệu từ Firebase
        fetchDataFromFirebase();

        return view;
    }

    private void fetchDataFromFirebase() {
        DatabaseReference cartRef = FirebaseDatabase.getInstance(DATABASE_URL).getReference("cart");

        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cartItems.clear(); // Xóa dữ liệu cũ

                // Duyệt qua các phần tử con trong "cart"
                for (DataSnapshot data : snapshot.getChildren()) {
                    CartItem item = data.getValue(CartItem.class);
                    if (item != null) {
                        cartItems.add(item);

                    }
                }

                // Cập nhật RecyclerView sau khi có dữ liệu

                cartAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi nếu có
            }
        });
    }
}
