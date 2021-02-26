package com.metar.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;


@Entity
@Table(name="metar_data")
public class Metar {

	@Id
	@Column(length = 4)
	private String icao;
	
	private String data;
	private String dataTimestamp;
	private String windStrength;
	private String temperature;
	private String visibilityStatute;
	
	//@JsonIgnore
	public String getIcao() {
		return icao;
	}
	
	//@JsonSetter
	public void setIcao(String icao) {
		this.icao = icao;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getDataTimestamp() {
		return dataTimestamp;
	}

	public void setDataTimestamp(String dataTimestamp) {
		this.dataTimestamp = dataTimestamp;
	}

	public String getWindStrength() {
		return windStrength;
	}

	public void setWindStrength(String windStrength) {
		this.windStrength = windStrength;
	}

	public String getTemperature() {
		return temperature;
	}

	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}

	public String getVisibilityStatute() {
		return visibilityStatute;
	}

	public void setVisibilityStatute(String visibilityStatute) {
		this.visibilityStatute = visibilityStatute;
	}

	
	
}
