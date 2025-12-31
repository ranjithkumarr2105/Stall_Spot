package com.simats.foodstall.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.simats.foodstall.R;
import com.simats.foodstall.adapter.OrderStatusAdapter;
import com.simats.foodstall.model.OrderStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CancelledOrdersFragment extends Fragment {

    private OrderStatusAdapter adapter;
    private List<OrderStatus> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cancelled_orders, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.cancelledRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = getDummyCancelledOrders();
        adapter = new OrderStatusAdapter(orderList, (OrderStatusAdapter.OnReceiptClickListener) getActivity());
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void filter(String text) {
        List<OrderStatus> filteredList = new ArrayList<>();
        for (OrderStatus item : orderList) {
            if (item.getStallName().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                filteredList.add(item);
            }
        }
        if (adapter != null) {
            adapter.filterList(filteredList);
        }
    }

    private List<OrderStatus> getDummyCancelledOrders() {
        List<OrderStatus> list = new ArrayList<>();
        list.add(new OrderStatus("Pizza Hub", "10 Jun, 11:45 AM", "ORD104783", "Chicken Roll x 1\nPuffs x 2", 120.00, "Cancelled"));
        return list;
    }
}