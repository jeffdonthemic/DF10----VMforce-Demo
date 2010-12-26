package org.medisend.mediaid.domain;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;

import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;

@RooJavaBean
@RooToString
@RooEntity(versionField="lastModifiedDate",versionType=java.util.Calendar.class)
public class InventoryItem {

    @NotNull
    private String name;

    @NotNull
    private String itemNumber;

    @NotNull
    private String category;

    @NotNull
    private String status;

    @Column(nullable=true)
    private String shipment; 
}
