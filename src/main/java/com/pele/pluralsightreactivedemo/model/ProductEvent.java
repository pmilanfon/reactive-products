package com.pele.pluralsightreactivedemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductEvent {

    private Long eventId;

    private String eventType;

}
