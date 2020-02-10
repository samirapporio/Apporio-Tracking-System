package com.apporioinfolabs.ats_sdk.models;

import java.util.List;

public class ModelTripEnd {

    /**
     * result : 1
     * message : Trip with identifier: TRIP_ID_TWO end successfully.
     * response : [{"tripIdentifier":"TRIP_ID_TWO","polyline":"ghllDktfuMAA"}]
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
         * tripIdentifier : TRIP_ID_TWO
         * polyline : ghllDktfuMAA
         */

        private String tripIdentifier;
        private String polyline;

        public String getTripIdentifier() {
            return tripIdentifier;
        }

        public void setTripIdentifier(String tripIdentifier) {
            this.tripIdentifier = tripIdentifier;
        }

        public String getPolyline() {
            return polyline;
        }

        public void setPolyline(String polyline) {
            this.polyline = polyline;
        }
    }
}
