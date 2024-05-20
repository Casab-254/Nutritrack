package com.example.nutritrack;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import okhttp3.Callback;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SearchActivity extends AppCompatActivity {
    private EditText searchEditText;
    private TextView responseTextView;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchEditText = findViewById(R.id.search);
        responseTextView = findViewById(R.id.response);
        searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = searchEditText.getText().toString().trim();
                if (!userInput.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Searching...", Toast.LENGTH_SHORT).show();
                    try {
                        makeAPIRequest(userInput);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        Toast.makeText(SearchActivity.this, "Error processing query", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SearchActivity.this, "Please enter a search query", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void makeAPIRequest(String userInput) throws UnsupportedEncodingException {
        String apiUrl = "https://api.ydc-index.io/search?query=" + URLEncoder.encode(userInput + "make it nutrition based ", "UTF-8");

        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(apiUrl)
                .method("GET", null)
                .addHeader("X-API-Key", "000bfccd-3809-473d-8ccb-1f9e7c7ca92e<__>1OD6O3ETU8N2v5f4sVKzyqWy")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
                SearchActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        responseTextView.setText("Failed to fetch data");
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);
                        JSONArray hitsArray = jsonResponse.getJSONArray("hits");
                        if (hitsArray.length() > 0) {
                            JSONObject firstHit = hitsArray.getJSONObject(0);
                            String description = firstHit.getString("description");

                            // Process the description for display (e.g., trim or format it)
                            // For example, to display the formatted text in responseTextView:
                            String cleanedDescription = cleanDescription(description);

                            SearchActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    responseTextView.setText(cleanedDescription);
                                }
                            });
                } else {
                    SearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            responseTextView.setText("Failed to fetch data");
                        }
                    });
                }
            }
        });
    }
}
