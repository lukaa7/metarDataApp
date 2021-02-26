package com.metar.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.metar.model.Metar;
import com.metar.repository.MetarRepository;

@Service
@Transactional
public class MetarService {
	
	@Autowired
	private MetarRepository metarRepository;
	
	private Map<String, String> metarDataMap = new HashMap<>();
	private NodeList metarNodeList;
	
	public List<Metar> getAllMetarData() {
		return metarRepository.findAll();
	}
	
	public void addMetarData(Metar metar) {
		metarRepository.save(metar);
	}
	
	public NodeList getMetarNodeList() {
		return metarNodeList;
	}

	public void delete(String icao) {
		metarRepository.deleteByIcao(icao);
	}
	
	public Map<String, String> getMetarDataMap() {
		return metarDataMap;
	}
	
	public List<Metar> findByIsActive() {
		return metarRepository.findByIsActive();
	}
	
	public List<Metar> findByMatchingLetters(String keyword) {
		return metarRepository.findByMatchingLetters(keyword);
	}
	
	@Scheduled(fixedRateString = "305000")
	public void parse() throws ParserConfigurationException, IOException, InterruptedException, SAXException {
		File xmlFile = new File("C:\\Users\\Luka\\MDA\\metar.xml");
					
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setValidating(false);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(xmlFile);
		document.getDocumentElement().normalize();
			        
		NodeList metarData = document.getElementsByTagName("METAR");
		metarNodeList = metarData;
        
        for(int i = 0; i < metarData.getLength(); ++i) {
	    	Node node = metarData.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
	               Element element = (Element) node; 
	               metarDataMap.put(element.getElementsByTagName("station_id").item(0).getTextContent(), element.getElementsByTagName("raw_text").item(0).getTextContent());
	        }
		}
        
	}
	
	public boolean isIcaoValid(String icao) {
		for (Map.Entry<String, String> entry : metarDataMap.entrySet()) {
			if(entry.getKey().equals(icao)) {
		    	return true;
		    }
		}
		return false;
	}
	
}
