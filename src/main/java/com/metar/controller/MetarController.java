package com.metar.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
			for (Map.Entry<String, String> entry : metarService.getMetarDataMap().entrySet()) {
				if(entry.getKey().equals(subscription.getIcao())) {
					Metar metarData = new Metar();
	 		   		metarData.setIcao(entry.getKey());
	 		   		metarData.setData(entry.getValue());
	 		   		metarService.addMetarData(metarData);
				}
			}
		}
	}
	
}
