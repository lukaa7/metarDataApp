package com.metar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metar.model.Subscription;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

	void deleteByIcao(String icao);
	
	@Query(value = "SELECT s FROM Subscription s WHERE s.isActive = 1")
	List<Subscription> findByIsActive();
	
	@Query(value = "SELECT s FROM Subscription s WHERE s.icao LIKE :keyword% ")
	List<Subscription> findByMatchingLetters(@Param("keyword") String keyword);
	
}
