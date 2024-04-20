package com.example.restaurantreview.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.restaurantreview.data.response.RestaurantResponse;
import com.example.restaurantreview.data.retrofit.ApiConfig;
import com.example.restaurantreview.data.response.PostReviewResponse;
import com.example.restaurantreview.data.response.Restaurant;
import com.example.restaurantreview.data.response.CustomerReviewsItem;
import com.example.restaurantreview.databinding.ActivityMainBinding;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private static final String TAG = "MainActivity";
    private static final String RESTAURANT_ID = "uewq1zg2zlskfw1e867";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.rvReview.setLayoutManager(layoutManager);

        findRestaurant();

        binding.btnSend.setOnClickListener(view -> {
            if (binding.edReview.getText() != null) {
                postReview(binding.edReview.getText().toString());
            }
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        });
    }


    private void postReview(String review) {
        showLoading(true);
        Call<PostReviewResponse> client = ApiConfig.getApiService().postReview(RESTAURANT_ID, "Dicoding", review);
        client.enqueue(new Callback<PostReviewResponse>() {
            @Override
            public void onResponse(Call<PostReviewResponse> call, Response<PostReviewResponse> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        setReviewData(response.body().getCustomerReviews());
                    }
                } else {
                    if (response.body() != null) {
                        Log.e(TAG, "onFailure: " + response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PostReviewResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void findRestaurant() {
        showLoading(true);
        Call<RestaurantResponse> client = ApiConfig.getApiService().getRestaurant(RESTAURANT_ID);
        client.enqueue(new Callback<RestaurantResponse>() {
            @Override
            public void onResponse(Call<RestaurantResponse> call, Response<RestaurantResponse> response) {
                showLoading(false);
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        setRestaurantData(response.body().getRestaurant());
                        setReviewData(response.body().getRestaurant().getCustomerReviews());
                    }
                } else {
                    if (response.body() != null) {
                        Log.e(TAG, "onFailure: " + response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<RestaurantResponse> call, Throwable t) {
                showLoading(false);
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });
    }

    private void setRestaurantData(Restaurant restaurant) {
        binding.tvTitle.setText(restaurant.getName());
        binding.tvDescription.setText(restaurant.getDescription());
        Glide.with(MainActivity.this)
                .load("https://restaurant-api.dicoding.dev/images/large/" + restaurant.getPictureId())
                .into(binding.ivPicture);
    }

    private void setReviewData(List<CustomerReviewsItem> customerReviews) {
        ArrayList<String> listReview = new ArrayList<>();
        for (CustomerReviewsItem review : customerReviews) {
            listReview.add(review.getReview() + "\n- " + review.getName());
        }
        ReviewAdapter adapter = new ReviewAdapter(listReview);
        binding.rvReview.setAdapter(adapter);
        binding.edReview.setText("");
    }


    private void showLoading(Boolean isLoading) {

        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}