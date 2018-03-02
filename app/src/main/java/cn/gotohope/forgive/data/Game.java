package cn.gotohope.forgive.data;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by xinsane on 2018/2/18.
 */

public class Game implements Serializable {

    private int column_number = 4;
    private int row_number = 4;

    public int column_number() {
        return column_number;
    }
    public int row_number() {
        return row_number;
    }

    private String id;
    private String name;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private double a = 0.00004;
    private double v = 0.06;

    public double a() {
        return a;
    }
    public double v() {
        return v;
    }

    // temp vars & methods
    @Expose(serialize = false, deserialize = false)
    public int best;

}
