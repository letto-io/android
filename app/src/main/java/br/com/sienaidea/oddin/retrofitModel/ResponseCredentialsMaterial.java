package br.com.sienaidea.oddin.retrofitModel;

/**
 * Created by Siena Idea on 11/08/2016.
 */
public class ResponseCredentialsMaterial {
    private Fields fields;
    private int id;
    private String url;

    public Fields getFields() {
        return fields;
    }

    public void setFields(Fields fields) {
        this.fields = fields;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
