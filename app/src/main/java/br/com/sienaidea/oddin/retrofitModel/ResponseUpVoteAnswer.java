package br.com.sienaidea.oddin.retrofitModel;

/**
 * Created by Siena Idea on 23/08/2016.
 */
public class ResponseUpVoteAnswer {
    //    {"up":true,"id":13,"person_id":9,"votable_id":1,"votable_type":"Answer"}
    private int id, person_id, votable_id;
    private boolean up;
    private String votable_type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person_id) {
        this.person_id = person_id;
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
