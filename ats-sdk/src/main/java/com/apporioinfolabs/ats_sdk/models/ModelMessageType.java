package com.apporioinfolabs.ats_sdk.models;

public class ModelMessageType {

    /**
     * result : 1
     * message : Data incoming from API
     * response : {"type":"API","message":"Message from API goes here accordingly.","identification_no":"BJKFY97H9U9-JNDJ0I-JH5JH"}
     */

    private int result;
    private String message;
    private ResponseBean response;

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

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

    public static class ResponseBean {
        /**
         * type : API
         * message : Message from API goes here accordingly.
         * identification_no : BJKFY97H9U9-JNDJ0I-JH5JH
         */

        private String type;
        private String message;
        private String identification_no;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getIdentification_no() {
            return identification_no;
        }

        public void setIdentification_no(String identification_no) {
            this.identification_no = identification_no;
        }
    }
}
