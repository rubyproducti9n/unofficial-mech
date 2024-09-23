package com.rubyproducti9n.unofficialmech;

public class ItemBlueprint {

    private String title;
    private String summary;
    private String articleLink;

    public ItemBlueprint(String title, String summary, String articleLink) {
        this.title = title;
        this.summary = summary;
        this.articleLink = articleLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getArticleLink() {
        return articleLink;
    }

    public void setArticleLink(String articleLink) {
        this.articleLink = articleLink;
    }
}
