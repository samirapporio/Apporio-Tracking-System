package com.apporioinfolabs.ats_sdk.models;

import java.util.List;

public class ModelDeviceConnect {

    /**
     * result : 1
     * message : some message from server
     * response : [{"ats_id":"some ats_id","developer_id":"some developer ID"}]
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
         * ats_id : some ats_id
         * developer_id : some developer ID
         */

        private String ats_id;
        private String developer_id;

        public String getAts_id() {
            return ats_id;
        }

        public void setAts_id(String ats_id) {
            this.ats_id = ats_id;
        }

        public String getDeveloper_id() {
            return developer_id;
        }

        public void setDeveloper_id(String developer_id) {
            this.developer_id = developer_id;
        }
    }
}
