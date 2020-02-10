package com.apporioinfolabs.ats_sdk.models;

import java.util.List;

public class ModelTag {

    /**
     * result : 1
     * message : Tag update successfully.
     * response : [{"tag":"AAAA","time":"2020-02-07T05:12:49.779Z"}]
     */

    private int result;
    private String message;
    private List<ResponseBean> response;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResponseBean> getResponse() {
        return response;
    }

    public void setResponse(List<ResponseBean> response) {
        this.response = response;
    }

    public static class ResponseBean {
        /**
         * tag : AAAA
         * time : 2020-02-07T05:12:49.779Z
         */

        private String tag;
        private String time;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}
