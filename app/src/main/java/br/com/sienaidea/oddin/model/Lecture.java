package br.com.sienaidea.oddin.model;

/**
 * Created by Siena Idea on 27/07/2016.
 */
public class Lecture {
    private String id, code, name, workload;

    public Lecture() {
    }

    public Lecture(String id, String code, String name, String workload) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.workload = workload;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWorkload() {
        return workload;
    }

    public void setWorkload(String workload) {
        this.workload = workload;
    }
}
