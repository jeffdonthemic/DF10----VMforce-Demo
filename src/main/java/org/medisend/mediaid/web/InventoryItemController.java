package org.medisend.mediaid.web;

import org.springframework.roo.addon.web.mvc.controller.RooWebScaffold;
import org.medisend.mediaid.domain.InventoryItem;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

@RooWebScaffold(path = "inventoryitems", formBackingObject = InventoryItem.class)
@RequestMapping("/inventoryitems")
@Controller
public class InventoryItemController {
}
