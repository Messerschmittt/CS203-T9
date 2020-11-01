package csd.api.modules.content;

import java.util.ArrayList;

import csd.api.tables.*;
import io.github.ccincharge.newsapi.NewsApi;
import io.github.ccincharge.newsapi.datamodels.Article;
import io.github.ccincharge.newsapi.requests.RequestBuilder;
import io.github.ccincharge.newsapi.responses.ApiArticlesResponse;

public class NewsAPI {
    private static NewsApi newsApi = new NewsApi("c4123521577f4e99971eba10636a2770");

    public static ArrayList<Content> apiTopHeadlines(){
        RequestBuilder topHeadlinesRequest = 
            new RequestBuilder()
            .setLanguage("en")
            .setCountry("us");
            // .setCategory("business");

        ApiArticlesResponse apiArticles = newsApi.sendTopRequest(topHeadlinesRequest);

        ArrayList<Article> newsArticles = apiArticles.articles();
        ArrayList<Content> returnContent = new ArrayList<>();

        for(Article a : newsArticles){
            returnContent.add(parseArticle(a));
        }

        return returnContent;
    }

    public static ArrayList<Content> apiSpecificQuery(String query){
        RequestBuilder specificQueryRequest = 
            new RequestBuilder()
            .setLanguage("en")
            .setQ(query);
            // .setCategory("business");

        ApiArticlesResponse apiArticles = newsApi.sendEverythingRequest(specificQueryRequest);

        ArrayList<Article> newsArticles = apiArticles.articles();
        ArrayList<Content> returnContent = new ArrayList<>();

        int count = 5;
        int added = 0;
        for(Article a : newsArticles){
            returnContent.add(parseArticle(a));

            if(++added > count){
                break;
            }
        }

        return returnContent;
    }

    private static Content parseArticle(Article a){
        Content toAdd = new Content();
        toAdd.setTitle(a.title());
        toAdd.setLink(a.url());
        toAdd.setSummary(a.description().split("\\. ")[0]);
        toAdd.setContent(a.description());

        return toAdd;
    }
}
