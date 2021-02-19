package com.metar.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.metar.model.Metar;

@Repository
public interface MetarRepository extends JpaRepository<Metar, String> {

	void deleteByIcao(String icao);
	
	@Query(value = "SELECT m FROM Metar m JOIN Subscription s on m.icao = s.icao WHERE s.isActive = 1")
	List<Metar> findByIsActive();
	
	@Query(value = "SELECT m FROM Metar m WHERE m.icao LIKE :keyword% ")
	List<Metar> findByMatchingLetters(@Param("keyword") String keyword);
	
	
	
}
