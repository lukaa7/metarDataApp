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
			metarService.addNewSubscription(subscription);
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
	public void updateTopic(@RequestBody Subscription subscription, @PathVariable String icao) {
		subscriptionService.updateTopic(icao, subscription);
	}
	
}