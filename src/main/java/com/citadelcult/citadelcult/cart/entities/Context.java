package com.citadelcult.citadelcult.cart.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Context implements Serializable {
    private String ip;
    @JsonProperty("user_agent")
    private String userAgent;
    private String fbc;
    private String fbp;
}
