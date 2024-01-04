package com.omkokate.newsapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class NewsActivity extends AppCompatActivity {

    private interface NewsApiService {
        @GET("top-headlines?country=us&apiKey=06ed7c087e774bec9430ccba420c2258")
        Call<ApiResponse> getNews();

        class ApiResponse {
            private List<NewsItem> articles;

            public List<NewsItem> getArticles() {
                return articles;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        fetchData();
    }

    private void fetchData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NewsApiService apiService = retrofit.create(NewsApiService.class);

        Call<NewsApiService.ApiResponse> call = apiService.getNews();

        call.enqueue(new Callback<NewsApiService.ApiResponse>() {
            @Override
            public void onResponse(Call<NewsApiService.ApiResponse> call, Response<NewsApiService.ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NewsApiService.ApiResponse apiResponse = response.body();
                    List<NewsItem> newsItems = apiResponse.getArticles();

                    // Set up RecyclerView
                    RecyclerView recyclerView = findViewById(R.id.recyclerView);
                    recyclerView.setLayoutManager(new LinearLayoutManager(NewsActivity.this));

                    // Create and set adapter
                    NewsAdapter newsAdapter = new NewsAdapter(NewsActivity.this, newsItems);
                    recyclerView.setAdapter(newsAdapter);

                    Toast.makeText(NewsActivity.this, "Data fetched successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NewsApiService.ApiResponse> call, Throwable t) {
                Log.e("Retrofit", "Error: " + t.getMessage());
                Toast.makeText(NewsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
