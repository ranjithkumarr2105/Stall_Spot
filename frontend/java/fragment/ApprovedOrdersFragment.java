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

public class ApprovedOrdersFragment extends Fragment {

    private OrderStatusAdapter adapter;
    private List<OrderStatus> orderList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_approved_orders, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.approvedRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        orderList = getDummyApprovedOrders();
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

    private List<OrderStatus> getDummyApprovedOrders() {
        List<OrderStatus> list = new ArrayList<>();
        list.add(new OrderStatus("Sivan Unavagam", "10 Jun, 9:00 AM", "ORD104780", "Tea x 3\nChicken Roll x 1", 85.00, "Approved"));
        list.add(new OrderStatus("Aliyas", "09 Jun, 5:30 PM", "ORD104779", "Coffee x 3\nEgg Puffs x 1", 85.00, "Approved"));
        return list;
    }
}