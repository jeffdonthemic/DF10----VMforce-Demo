package org.medisend.mediaid.domain;

import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

import com.salesforce.persistence.datanucleus.annotation.CustomObject;

@RooJavaBean
@RooToString
@RooEntity(versionField="lastModifiedDate",versionType=java.util.Calendar.class)
@CustomObject(enableFeeds=true)
public class Shipment {

    @NotNull
    private String name;

    @NotNull
    private String country;

    @NotNull
    private String type;

    @NotNull
    private String status;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "S-")
    private Date shipDate;

    @Value(value = "0")
    private Integer items;

    @Value(value = "0")
    private Integer selected;

    @Value(value = "0")
    private Integer reserved;
}
