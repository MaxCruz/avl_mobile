package com.jaragua.avlmobile.entities;

import java.util.ArrayList;

@SuppressWarnings("unused")
public class DriverRequest {

    private String imei;
    private String product;
    private ArrayList<?> entities;

    public DriverRequest() {
    }

    public DriverRequest(String imei, String product, ArrayList<?> entities) {
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

    public ArrayList<?> getEntities() {
        return entities;
    }

    public void setEntities(ArrayList<?> entities) {
        this.entities = entities;
    }

}
