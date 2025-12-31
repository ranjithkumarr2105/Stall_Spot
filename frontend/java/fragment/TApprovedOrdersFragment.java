package com.simats.foodstall.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.simats.foodstall.ApiClient;
import com.simats.foodstall.ApiService;
import com.simats.foodstall.OreceiptActivity;
import com.simats.foodstall.R;
import com.simats.foodstall.adapter.OOrdersAdapter;
import com.simats.foodstall.model.OGetOrdersResponse;
import com.simats.foodstall.model.OOrder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TApprovedOrdersFragment extends Fragment implements OOrdersAdapter.OnReceiptClickListener {

    private RecyclerView recyclerView;
    private OOrdersAdapter adapter;
    private FrameLayout loadingOverlay;
    private ImageView loadingIcon;
    private TextView emptyView;
    private String stallId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- START OF FIX: Using the correct SharedPreferences file and key ---
        SharedPreferences prefs = requireActivity().getSharedPreferences("owner_prefs", Context.MODE_PRIVATE);
        stallId = prefs.getString("stall_id", null);
        // --- END OF FIX ---

        recyclerView = view.findViewById(R.id.ordersRecyclerView);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);
        loadingIcon = view.findViewById(R.id.loadingIcon);
        emptyView = view.findViewById(R.id.emptyView);

        setupRecyclerView();
        fetchOrders();
    }

    private void setupRecyclerView() {
        adapter = new OOrdersAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void fetchOrders() {
        startLoadingAnimation();
        recyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);

        ApiClient.getClient().create(ApiService.class).getOwnerOrders(stallId, "approved")
                .enqueue(new Callback<OGetOrdersResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<OGetOrdersResponse> call, @NonNull Response<OGetOrdersResponse> response) {
                        hideLoadingAnimation();
                        if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                            if (response.body().getOrders() != null && !response.body().getOrders().isEmpty()) {
                                adapter.submitList(response.body().getOrders());
                                recyclerView.setVisibility(View.VISIBLE);
                            } else {
                                emptyView.setText("No approved orders found.");
                                emptyView.setVisibility(View.VISIBLE);
                            }
                        } else {
                            emptyView.setText("Failed to load orders.");
                            emptyView.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<OGetOrdersResponse> call, @NonNull Throwable t) {
                        hideLoadingAnimation();
                        emptyView.setText("Network Error.");
                        emptyView.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void startLoadingAnimation() {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(View.VISIBLE);
            Animation rotation = AnimationUtils.loadAnimation(getContext(), R.anim.hourglass_rotation);
            loadingIcon.startAnimation(rotation);
        }
    }

    private void hideLoadingAnimation() {
        if (loadingOverlay != null) {
            loadingIcon.clearAnimation();
            loadingOverlay.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewReceiptClick(OOrder order) {
        Intent intent = new Intent(getActivity(), OreceiptActivity.class);
        intent.putExtra("ORDER_DETAILS", order);
        startActivity(intent);
    }
}