package cn.gotohope.forgive.data;

import com.google.gson.annotations.Expose;
import java.io.Serializable;

public class Game implements Serializable {

    public int column_number = 4;
    public int row_number = 4;

    public String id;
    public String name;
    public String description = "";

    public double a = 0.00004;
    public double v = 0.06;

    public boolean auto_scroll = true;
    public double scroll_v = 0.15;

    @Expose(serialize = false, deserialize = false)
    public int best;

}
