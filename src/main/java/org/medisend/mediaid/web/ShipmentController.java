package org.medisend.mediaid.web;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.medisend.mediaid.domain.Shipment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "shipments", formBackingObject = Shipment.class)
@RequestMapping("/shipments")
@Controller
public class ShipmentController {
}
