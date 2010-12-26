package org.medisend.mediaid.web;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.medisend.mediaid.domain.CaseMessage;
import org.medisend.mediaid.domain.InventoryItem;
import org.medisend.mediaid.domain.Shipment;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/shipmenthome/**")
@Controller
public class ShipmentHomeController {
	
	@PersistenceContext
	EntityManager em;
    
    @RequestMapping(value="/shipmenthome/{shipmentId}", method=RequestMethod.GET)
    public ModelAndView displayShipment(@PathVariable String shipmentId, Model model) {
    	ModelAndView mav = new ModelAndView("shipmenthome/index");
    	mav.addObject("shipment", em.find(Shipment.class, shipmentId));    	
    	return mav;
    }
    
    @RequestMapping(value="/shipmenthome/{shipmentId}/items", method=RequestMethod.GET)
    public ModelAndView displayItems(@PathVariable String shipmentId, Model model) {   	
    	ModelAndView mav = new ModelAndView("shipmenthome/items");
    	Shipment shipment = em.find(Shipment.class, shipmentId);
    	mav.addObject("shipment", shipment);
    	mav.addObject("items",em.createQuery("SELECT i FROM InventoryItem i WHERE i.shipment = :shipment")
    		.setParameter("shipment", shipment.getName()).getResultList());
    	return mav;
    }
    
    @RequestMapping(value="/shipmenthome/{shipmentId}/add", method=RequestMethod.GET)
    public ModelAndView addByCategory(@PathVariable String shipmentId, Model model) {
    	    	
    	ModelAndView mav = new ModelAndView("shipmenthome/add");
    	mav.addObject("shipment", em.find(Shipment.class, shipmentId));
    	mav.addObject("gloves", getCategoryCountByStatus("Gloves","Available"));
    	mav.addObject("nebulizer", getCategoryCountByStatus("Nebulizer","Available"));
    	mav.addObject("hyperinflation",getCategoryCountByStatus("Hyperinflation","Available"));
    	mav.addObject("circuit", getCategoryCountByStatus("Circuits","Available"));
    	mav.addObject("armboard", getCategoryCountByStatus("Armboards","Available"));
    	return mav;
    }
    
    @RequestMapping(value="/shipmenthome/{shipmentId}/addAll/{category}", method=RequestMethod.GET)
    public String addAll(@PathVariable String shipmentId, @PathVariable String category, Model model) {
    	Shipment shipment = em.find(Shipment.class, shipmentId);
    	addItemsByCategory(shipment, category);
    	updateShipmentContents(shipment);
    	return "redirect://shipmenthome/"+shipmentId;
    }
    
    @RequestMapping(value="/shipmenthome/{shipmentId}/remove", method=RequestMethod.GET)
    public String remove(@PathVariable String shipmentId, Model model) {
    	Shipment shipment = em.find(Shipment.class, shipmentId);
    	removeAllItems(shipment);
    	updateShipmentContents(shipment);
    	return "redirect://shipmenthome/"+shipmentId;
    }
    
    @RequestMapping(value="/shipmenthome/{shipmentId}/reserve", method=RequestMethod.GET)
    public String reserve(@PathVariable String shipmentId, Model model) {
    	Shipment shipment = em.find(Shipment.class, shipmentId);
    	reserveAllItems(shipment);
    	updateShipmentContents(shipment);
    	return "redirect://shipmenthome/"+shipmentId;
    }
    
    @RequestMapping(value="/shipmenthome/{shipmentId}/manifest", method=RequestMethod.GET)
    public ModelAndView displayManifest(@PathVariable String shipmentId, Model model) {
    	ModelAndView mav = new ModelAndView("shipmenthome/manifest");
    	Shipment shipment = em.find(Shipment.class, shipmentId);
    	mav.addObject("shipment", shipment);
    	mav.addObject("items",em.createQuery("SELECT i FROM InventoryItem i WHERE i.shipment = :shipment")
    			.setParameter("shipment", shipment.getName()).setMaxResults(25).getResultList());
    	return mav;
    }
    
    @RequestMapping(value="/shipmenthome/{shipmentId}/message", method=RequestMethod.GET)
    public ModelAndView message(@PathVariable String shipmentId, Model model) {
    	ModelAndView mav = new ModelAndView("shipmenthome/message");
    	mav.addObject("shipment", em.find(Shipment.class, shipmentId));
    	CaseMessage cm = new CaseMessage();
    	cm.setShipmentId(shipmentId);
    	mav.addObject("caseMessage", cm);
    	return mav;
    }
    
    @RequestMapping(value = "shipmenthome/messageSubmit", method = RequestMethod.POST)
    public String messageSubmit(@ModelAttribute CaseMessage caseMessage, Model model) {
	    return "redirect://shipmenthome/"+caseMessage.getShipmentId();
    }
    
    @Transactional
    private void updateShipmentContents(Shipment shipment) {
    	
    	Integer selected = (Integer)em.createQuery("SELECT COUNT(i) FROM InventoryItem i WHERE i.shipment =:shipmentName AND i.status = :status")
		    .setParameter("shipmentName", shipment.getName())	
	    	.setParameter("status", "Selected")
	    	.getSingleResult();
    	
    	Integer reserved = (Integer)em.createQuery("SELECT COUNT(i) FROM InventoryItem i WHERE i.shipment =:shipmentName AND i.status = :status")
    		.setParameter("shipmentName", shipment.getName())	
	    	.setParameter("status", "Reserved")
	    	.getSingleResult();
    	
    	shipment.setItems(selected.intValue() + reserved.intValue());
    	shipment.setSelected(selected.intValue());
    	shipment.setReserved(reserved.intValue());
    	em.merge(shipment);
    }
    
    @SuppressWarnings("unchecked")
	@Transactional
    private void addItemsByCategory(Shipment shipment, String category) {
    	List<InventoryItem> items = em.createQuery("SELECT i FROM InventoryItem i WHERE i.category = :category AND i.status = :status")
	    	.setParameter("category", category)
	    	.setParameter("status", "Available")
	    	.getResultList();
    	for (InventoryItem i : items) {
    		i.setShipment(shipment.getName());
    		i.setStatus("Selected");
    		em.persist(i);
    	}
    }
    
    @SuppressWarnings("unchecked")
	@Transactional
    private void removeAllItems(Shipment shipment) {
    	List<InventoryItem> allItems = em.createQuery("SELECT i FROM InventoryItem i WHERE i.shipment =:shipmentName")
	    	.setParameter("shipmentName", shipment.getName())
	    	.getResultList();
    	for (InventoryItem i : allItems) {
    		i.setShipment(" ");
    		i.setStatus("Available");
    		em.persist(i);
    	}
    }
    
    @SuppressWarnings("unchecked")
	@Transactional
    private void reserveAllItems(Shipment shipment) {
    	List<InventoryItem> allItems = em.createQuery("SELECT i FROM InventoryItem i WHERE i.shipment =:shipmentName")
    		.setParameter("shipmentName", shipment.getName())
    		.getResultList();
    	for (InventoryItem i : allItems) {
    		i.setStatus("Reserved");
    		em.persist(i);
    	}
    }
    
    private int getCategoryCountByStatus(String category, String status) {
    	Integer ct = (Integer)em.createQuery("SELECT COUNT(i) FROM InventoryItem i WHERE i.category = :category AND i.status = :status")
	    	.setParameter("category", category)
	    	.setParameter("status", status)
	    	.getSingleResult();
    	
    	return ct.intValue();
    }
	
    @RequestMapping
    public String index() {
        return "shipmenthome/index";
    }
}
