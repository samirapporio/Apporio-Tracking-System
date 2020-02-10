package com.apporioinfolabs.ats_sdk.models;

import java.util.List;

public class ModelTagListener {

    /**
     * result : 1
     * message : Tag updated Successfully for this device. ULS: f3254a3671d22b88_com.apporioinfolabs.ats_tracking_sdkAAAA
     * response : [{"uls":"f3254a3671d22b88_com.apporioinfolabs.ats_tracking_sdkAAAA"}]
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
         * uls : f3254a3671d22b88_com.apporioinfolabs.ats_tracking_sdkAAAA
         */

        private String uls;

        public String getUls() {
            return uls;
        }

        public void setUls(String uls) {
            this.uls = uls;
        }
    }
}
