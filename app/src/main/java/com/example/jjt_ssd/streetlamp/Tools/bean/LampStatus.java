package com.example.jjt_ssd.streetlamp.Tools.bean;

/**
 * Created by LJN on 2017/9/28.
 */

public class LampStatus {

    /**
     * msg_did : msg0001
     * time : 2017-08-21 14:00:00
     * type : 2
     * data : {"pm25":50,"out_temp":37,"in_temp":37,"hum":80,"speed":3,"noise":20,"battery":100}
     */

    private String msg_did;
    private String time;
    private String type;
    private DataBean data;

    public String getMsg_did() {
        return msg_did;
    }

    public void setMsg_did(String msg_did) {
        this.msg_did = msg_did;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * pm25 : 50
         * out_temp : 37
         * in_temp : 37
         * hum : 80
         * speed : 3
         * noise : 20
         * battery : 100
         */

        private String pm25;
        private String out_temp;
        private String in_temp;
        private String hum;
        private String speed;
        private String noise;
        private String battery;

        public String getPm25() {
            return pm25;
        }

        public void setPm25(String pm25) {
            this.pm25 = pm25;
        }

        public String getOut_temp() {
            return out_temp;
        }

        public void setOut_temp(String out_temp) {
            this.out_temp = out_temp;
        }

        public String getIn_temp() {
            return in_temp;
        }

        public void setIn_temp(String in_temp) {
            this.in_temp = in_temp;
        }

        public String getHum() {
            return hum;
        }

        public void setHum(String hum) {
            this.hum = hum;
        }

        public String getSpeed() {
            return speed;
        }

        public void setSpeed(String speed) {
            this.speed = speed;
        }

        public String getNoise() {
            return noise;
        }

        public void setNoise(String noise) {
            this.noise = noise;
        }

        public String getBattery() {
            return battery;
        }

        public void setBattery(String battery) {
            this.battery = battery;
        }
    }
}
