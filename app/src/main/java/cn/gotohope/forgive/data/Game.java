package cn.gotohope.forgive.data;

import java.io.Serializable;

/**
 * Created by xinsane on 2018/2/18.
 */

public class Game implements Serializable {

    private String name;
    private int column_number = 5;
    private int row_number = 6;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int column_number() {
        return column_number;
    }

    public int row_number() {
        return row_number;
    }
}
