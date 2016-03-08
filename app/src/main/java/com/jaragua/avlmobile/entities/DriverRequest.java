package com.jaragua.avlmobile.entities;

import com.google.gson.JsonArray;

@SuppressWarnings("unused")
public class DriverRequest {

    private String imei;
    private String product;
    private JsonArray entities;

    public DriverRequest() {
    }

    public DriverRequest(String imei, String product, JsonArray entities) {
        this.imei = imei;
        this.product = product;
        this.entities = entities;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public JsonArray getEntities() {
        return entities;
    }

    public void setEntities(JsonArray entities) {
        this.entities = entities;
    }

}
