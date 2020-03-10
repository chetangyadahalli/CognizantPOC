package com.cognizant.poc;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import androidx.lifecycle.ViewModelProviders;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cognizant.poc.adapters.NewsListAdapter;
import com.cognizant.poc.model.NewsArticle;
import com.cognizant.poc.viewmodels.NewsViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<NewsArticle> articleArrayList = new ArrayList<>();
    NewsListAdapter newsAdapter;
    NewsViewModel newsViewModel;
    ListView articlesList;
    SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pullToRefresh = (SwipeRefreshLayout) findViewById(R.id.pullToRefresh);

        articlesList = findViewById(R.id.lstArticles);
        actionBarSetup("");
        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel.class);
        newsViewModel.init();
        newsViewModel.getNewsRepository().observe(this, newsResponse -> {
            List<NewsArticle> newsArticles = newsResponse.getArticles();
            String Title = newsResponse.getTitle();
            if(Title.equalsIgnoreCase("")) {
                //No title supplied
            } else {
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

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                newsViewModel.getNewsRepository().observe(MainActivity.this, newsResponse -> {
                    List<NewsArticle> newsArticles = newsResponse.getArticles();
                    String Title = newsResponse.getTitle();
                    if(Title.equalsIgnoreCase("")) {
                        //No title supplied
                    } else {
                        actionBarSetup(Title);
                    }
                    for (int i=0;i<newsArticles.size();i++) {
                        if(newsArticles.get(i).getTitle().equalsIgnoreCase("") && newsArticles.get(i).getDescription().equalsIgnoreCase("") && newsArticles.get(i).getUrlToImage().equalsIgnoreCase("")) {
                            newsArticles.remove(i);
                        }
                    }
                    articleArrayList.clear();
                    articleArrayList.addAll(newsArticles);
                    newsAdapter.notifyDataSetChanged();
                });
                pullToRefresh.setRefreshing(false);
            }
        });
        setupListView();
    }

    private void setupListView() {
        if (newsAdapter == null) {
            newsAdapter = new NewsListAdapter(articleArrayList,MainActivity.this);
            articlesList.setAdapter(newsAdapter);
           // articlesList.setNestedScrollingEnabled(true);
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
}
