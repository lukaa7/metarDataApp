package com.metar.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.metar.model.Metar;
import com.metar.model.Subscription;
import com.metar.repository.MetarRepository;

@Service
@Transactional
public class MetarService {
	
	@Autowired
	private MetarRepository metarRepository;
	
	private Map<String, String> metarDataMap = new HashMap<>();
	private NodeList metarNodeList;
	private final String METAR_DATA_URL = "https://www.aviationweather.gov/adds/dataserver_current/current/metars.cache.xml";
	
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
	
	@Scheduled(fixedRateString = "300000")
	public void parse() throws ParserConfigurationException, IOException, InterruptedException, SAXException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(METAR_DATA_URL))
					.build();

		HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
		String xmlFile = httpResponse.body();
					
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setValidating(false);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(new InputSource(new StringReader(xmlFile)));
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
	
	public void addNewSubscription(Subscription subscription) {
		for (Map.Entry<String, String> entry : metarDataMap.entrySet()) {
		    if(entry.getKey().equals(subscription.getIcao())) {
		    	Metar metarData = new Metar();
     		   	metarData.setIcao(entry.getKey());
     		   	metarData.setData(entry.getValue());
     		   	metarRepository.save(metarData);
     		   	break;
		    }
		}
	}
	
	public boolean isIcaoValid(String icao) {
		boolean isValid = false;
		for (Map.Entry<String, String> entry : metarDataMap.entrySet()) {
			if(entry.getKey().equals(icao)) {
		    	isValid = true;
		    	break;
		    }
		}
		return isValid;
	}
	
}
