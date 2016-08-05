package br.com.sienaidea.oddin.retrofitModel;

/**
 * Created by Siena Idea on 05/08/2016.
 */
public class ResponseVote {

    private int id, votable_id;
    private boolean up;
    private String votable_type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVotable_id() {
        return votable_id;
    }

    public void setVotable_id(int votable_id) {
        this.votable_id = votable_id;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public String getVotable_type() {
        return votable_type;
    }

    public void setVotable_type(String votable_type) {
        this.votable_type = votable_type;
    }
}
