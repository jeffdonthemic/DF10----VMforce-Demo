package org.medisend.mediaid.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping("/home/**")
@Controller
public class HomeController {

	@PersistenceContext
	EntityManager em;
	
    @RequestMapping
    public ModelAndView index() {
    	ModelAndView mav = new ModelAndView("home/index");
    	mav.addObject("shipments",em.createQuery("SELECT s FROM Shipment s ORDER BY CreatedDate DESC")
    			.setMaxResults(5)
    			.getResultList());
    	return mav;
    }
}
