package com.lxy.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("sport")
    public Sport sport;
    @SerializedName("cw")
    public CarWash carWash;

    public class Comfort{
        @SerializedName("brf")
        public String brief;
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("brf")
        public String brief;
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("brf")
        public String brief;
        @SerializedName("txt")
        public String info;
    }

}
