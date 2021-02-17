package com.metar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/airport/METAR")
public class MetarController {

	@Autowired
	private MetarService metarService;
	
	@Autowired
	private SubscriptionService subscriptionService;
	
	@GetMapping
	public List<Metar> getAllMetarData() {
		return metarService.getAllMetarData();
	}
	
	@PostMapping
	public void saveMetarData(@RequestBody Metar metar) {
		if(metarService.isIcaoValid(metar.getIcao())) {
			Subscription newSubscription = new Subscription();
			newSubscription.setIcao(metar.getIcao());
			subscriptionService.addSubscription(newSubscription);
			metarService.addMetarData(metar);
		} else {
			System.out.println("Not valid ICAO code");	
		}
	}
	
	@DeleteMapping("/{icao}")
	public void deleteMetarData(@PathVariable String icao) {
		metarService.delete(icao);
		subscriptionService.delete(icao);
	}
	
	@GetMapping("/active")
	public List<Metar> getAllActiveMetarData() {
		return metarService.findByIsActive();
	}
	
	@GetMapping("/{keyword}")
	public List<Metar> getAllMetarDataByMatchingLetters(@PathVariable String keyword) {
		return metarService.findByMatchingLetters(keyword);
	}
	
	@Scheduled(fixedRateString = "300000", initialDelayString = "5000")
	public void saveAllMetarData() {
		for(Subscription subscription : subscriptionService.getAllSubscriptions()) {
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
						}
		        }
			}
		}
	}
		
}
