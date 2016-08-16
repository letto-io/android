package br.com.sienaidea.oddin.retrofitModel;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Siena Idea on 11/08/2016.
 */
public class Fields {
    private String key;
    private String policy;

    @SerializedName("x-amz-credential")
    private String x_amz_credential;

    @SerializedName("x-amz-algorithm")
    private String x_amz_algorithm;

    @SerializedName("x-amz-date")
    private String x_amz_date;

    @SerializedName("x-amz-signature")
    private String x_amz_signature;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getX_amz_credential() {
        return x_amz_credential;
    }

    public void setX_amz_credential(String x_amz_credential) {
        this.x_amz_credential = x_amz_credential;
    }

    public String getX_amz_algorithm() {
        return x_amz_algorithm;
    }

    public void setX_amz_algorithm(String x_amz_algorithm) {
        this.x_amz_algorithm = x_amz_algorithm;
    }

    public String getX_amz_date() {
        return x_amz_date;
    }

    public void setX_amz_date(String x_amz_date) {
        this.x_amz_date = x_amz_date;
    }

    public String getX_amz_signature() {
        return x_amz_signature;
    }

    public void setX_amz_signature(String x_amz_signature) {
        this.x_amz_signature = x_amz_signature;
    }
}
