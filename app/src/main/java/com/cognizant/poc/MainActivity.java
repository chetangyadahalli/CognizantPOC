package com.cognizant.poc;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.cognizant.poc.adapters.NewsListAdapter;
import com.cognizant.poc.model.NewsArticle;
import com.cognizant.poc.viewmodels.NewsViewModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    ArrayList<NewsArticle> articleArrayList = new ArrayList<>();
    NewsListAdapter newsAdapter;
    NewsViewModel newsViewModel;
    ListView articlesList;
    SwipeRefreshLayout pullToRefresh;
    SharedPreferences pref;

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pref = getApplicationContext().getSharedPreferences("MyPref", 0);

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);

        articlesList = findViewById(R.id.lstArticles);
        actionBarSetup("");
        newsViewModel = ViewModelProviders.of(MainActivity.this).get(NewsViewModel.class);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if(isOnline(MainActivity.this)) {
                    newsViewModel.init();
                    newsViewModel.getNewsRepository().observe(MainActivity.this, newsResponse -> {
                        if(newsResponse != null) {
                            List<NewsArticle> newsArticles = newsResponse.getArticles();
                            String Title = newsResponse.getTitle();
                            Gson gson = new Gson();
                            String arrayData = gson.toJson(newsArticles);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.remove("articles").apply();
                            editor.remove("title").apply();
                            editor.putString("articles", arrayData);
                            editor.putString("title", Title);
                            editor.commit();

                            if (!Title.equalsIgnoreCase("")) {
                                actionBarSetup(Title);
                            }
                            for (int i = 0; i < newsArticles.size(); i++) {
                                if (newsArticles.get(i).getTitle().equalsIgnoreCase("") && newsArticles.get(i).getDescription().equalsIgnoreCase("") && newsArticles.get(i).getUrlToImage().equalsIgnoreCase("")) {
                                    newsArticles.remove(i);
                                }
                            }
                            articleArrayList.clear();
                            articleArrayList.addAll(newsArticles);
                            setupListView();
                        }
                    });
                    pullToRefresh.setRefreshing(false);
                } else {
                    pullToRefresh.setRefreshing(false);
                    Toast.makeText(MainActivity.this,"Please check your internet connection",Toast.LENGTH_LONG).show();
                }

            }
        });
        if(isOnline(MainActivity.this)) {
            newsViewModel.init();
            newsViewModel.getNewsRepository().observe(this, newsResponse -> {
                List<NewsArticle> newsArticles = newsResponse.getArticles();
                String Title = newsResponse.getTitle();

                Gson gson = new Gson();
                String arrayData = gson.toJson(newsArticles);
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("articles").apply();
                editor.remove("title").apply();
                editor.putString("articles", arrayData);
                editor.putString("title",Title);
                editor.commit();

                if(!Title.equalsIgnoreCase("")) {
                    actionBarSetup(Title);
                }
                for (int i=0;i<newsArticles.size();i++) {
                    if(newsArticles.get(i).getTitle().equalsIgnoreCase("") && newsArticles.get(i).getDescription().equalsIgnoreCase("") && newsArticles.get(i).getUrlToImage().equalsIgnoreCase("")) {
                        newsArticles.remove(i);
                    }
                }
                articleArrayList.addAll(newsArticles);
                newsAdapter.notifyDataSetChanged();
            });


            setupListView();
        } else {
            String offlineArticles = pref.getString("articles",null);
            String Title = pref.getString("title",null);
            if(Title!=null) {
                actionBarSetup(Title);
            }
            articleArrayList.clear();
            if(offlineArticles!=null) {
                try {
                    JSONArray jsonArray = new JSONArray(offlineArticles);
                    for(int i=0;i<jsonArray.length();i++) {
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                        if(!jsonObject.has("title") && !jsonObject.has("description")&& !jsonObject.has("imageHref")) {
                        }else {
                            NewsArticle newsArticle = new NewsArticle();
                            newsArticle.setTitle(jsonObject.has("title") ? jsonObject.getString("title") : "");
                            newsArticle.setDescription(jsonObject.has("description") ? jsonObject.getString("description") : "");
                            newsArticle.setUrlToImage(jsonObject.has("imageHref") ? jsonObject.getString("imageHref") : "");
                            articleArrayList.add(newsArticle);
                        }
                    }
                    setupListView();
                    Log.e("jsonArray",""+jsonArray);
                }catch (JSONException ex) {

                }
            }
            Toast.makeText(MainActivity.this,"Please check your internet connection",Toast.LENGTH_LONG).show();
        }

    }

    private void setupListView() {
        if (newsAdapter == null) {
            newsAdapter = new NewsListAdapter(articleArrayList,MainActivity.this);
            articlesList.setAdapter(newsAdapter);
        } else {
            newsAdapter.notifyDataSetChanged();
        }
    }

    private void actionBarSetup(String Title) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ActionBar ab = getSupportActionBar();
            ab.setTitle(Title);
        }
    }

    public static String serialize(List<NewsArticle> obj) {
        if (obj == null) return "";
        try {
            ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
            objStream.writeObject(obj);
            objStream.close();
            return encodeBytes(serialObj.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object deserialize(String str) {
        if (str == null || str.length() == 0) return null;
        try {
            ByteArrayInputStream serialObj = new ByteArrayInputStream(decodeBytes(str));
            ObjectInputStream objStream = new ObjectInputStream(serialObj);
            return objStream.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encodeBytes(byte[] bytes) {
        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i < bytes.length; i++) {
            strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
        }

        return strBuf.toString();
    }

    public static byte[] decodeBytes(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            char c = str.charAt(i);
            bytes[i / 2] = (byte) ((c - 'a') << 4);
            c = str.charAt(i + 1);
            bytes[i / 2] += (c - 'a');
        }
        return bytes;
    }
}
