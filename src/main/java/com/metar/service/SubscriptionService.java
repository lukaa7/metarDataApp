package com.metar.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.metar.model.Subscription;
import com.metar.repository.SubscriptionRepository;

@Service
@Transactional
public class SubscriptionService {

	@Autowired
	private SubscriptionRepository subscriptionRepository;
	
	public List<Subscription> getAllSubscriptions() {
		return subscriptionRepository.findAll();
	}

	public void addSubscription(Subscription subscription) {
		subscriptionRepository.save(subscription);
	}

	public void delete(String icao) {
		subscriptionRepository.deleteByIcao(icao);
	}
	
}
