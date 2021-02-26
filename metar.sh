#!/bin/bash
while [ true ]
	do 
		curl https://www.aviationweather.gov/adds/dataserver_current/current/metars.cache.xml > metar.xml
		sleep 5m
	done 