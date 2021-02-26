package com.metar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.metar.model.Metar;
import com.metar.model.Subscription;
import com.metar.service.MetarService;
import com.metar.service.SubscriptionService;

@RestController
@RequestMapping(path = "/subscriptions")
public class SubscriptionController {

	@Autowired
	private SubscriptionService subscriptionService;
	
	@Autowired
	private MetarService metarService;
	
	@GetMapping
	public List<Subscription> getAllSubscriptions() {
		return subscriptionService.getAllSubscriptions();
	}
	
	@PostMapping
	public void addSubscription(@RequestBody Subscription subscription) {
		if(metarService.isIcaoValid(subscription.getIcao())) {
			subscriptionService.addSubscription(subscription);
			addNewSubscription(subscription);
		} else {
			System.out.println("Not valid ICAO code");	
		}
	}
	
	@DeleteMapping("/{icao}")
	public void deleteSubscription(@PathVariable String icao) {
		metarService.delete(icao);
		subscriptionService.delete(icao);
	}
	
	@PutMapping("/{icao}")
	public void updateSubcription(@RequestBody Subscription subscription, @PathVariable String icao) {
		subscriptionService.updateSubscription(icao, subscription);
	}
	
	@GetMapping("/active")
	public List<Subscription> getAllActiveSubscriptions() {
		return subscriptionService.findByIsActive();
	}
	
	@GetMapping("/{keyword}")
	public List<Subscription> getAllMetarDataByMatchingLetters(@PathVariable String keyword) {
		return subscriptionService.findByMatchingLetters(keyword);
	}
	
	public void addNewSubscription(Subscription subscription) {
		for(int i = 0; i < metarService.getMetarNodeList().getLength(); ++i) {
		    Node node = metarService.getMetarNodeList().item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node; 
				if(element.getElementsByTagName("station_id").item(0).getTextContent().equals(subscription.getIcao())) {
					Metar metarData = new Metar();
			 		metarData.setIcao(element.getElementsByTagName("station_id").item(0).getTextContent());
			 		metarData.setData(element.getElementsByTagName("raw_text").item(0).getTextContent());
			 		metarData.setDataTimestamp(element.getElementsByTagName("observation_time").item(0).getTextContent());
			 		metarData.setTemperature(element.getElementsByTagName("temp_c").item(0).getTextContent());
			 		metarData.setVisibilityStatute(element.getElementsByTagName("visibility_statute_mi").item(0).getTextContent());
			 		metarData.setWindStrength(element.getElementsByTagName("wind_speed_kt").item(0).getTextContent());
			 		metarService.addMetarData(metarData);
			 		break;
				}
			}
		}
	}
}