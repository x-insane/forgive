package cn.gotohope.forgive.data;

import com.google.gson.annotations.Expose;
import java.io.Serializable;

public class Game implements Serializable {

    public int column_number = 4;
    public int row_number = 4;
    public String type = "";

    public String id;
    public String name;
    public int time_limit = 0;
    public int forgive_bound = 10;
    public boolean has_forgive_till = true;
    public String description = "";

    public float a = 0.00004f;
    public float v = 0.06f;

    public boolean auto_scroll = true;
    public float scroll_v = 0.15f;

    @Expose(serialize = false, deserialize = false)
    public int best;
    @Expose(serialize = false, deserialize = false)
    public int time;
    @Expose(serialize = false, deserialize = false)
    public float best_v;

}
