package net.bounceme.chronos.infinispan.service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.bounceme.chronos.infinispan.model.LocationWeather;

@Service
public class OpenWeatherMapService extends CachingWeatherService {
	final private static String OWM_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

	private DocumentBuilder db;

	@Value("${infinispan.owmapikey}")
	private String apiKey;

	public OpenWeatherMapService() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			db = dbf.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
		}
	}

	private Document fetchData(String location) {
		HttpURLConnection conn = null;
		try {
			String query = String.format("%s?q=%s&mode=xml&units=metric&APPID=%s", OWM_BASE_URL,
					URLEncoder.encode(location.replaceAll(" ", ""), "UTF-8"), apiKey);
			URL url = new URL(query);
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/xml");
			if (conn.getResponseCode() != 200) {
				throw new Exception();
			}
			return db.parse(conn.getInputStream());
		}
		catch (Exception e) {
			return null;
		}
		finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}

	@Override
	protected LocationWeather fetchWeather(String location) {
		Document dom = fetchData(location);
		Element current = (Element) dom.getElementsByTagName("current").item(0);
		Element temperature = (Element) current.getElementsByTagName("temperature").item(0);
		Element weather = (Element) current.getElementsByTagName("weather").item(0);
		String[] split = location.split(",");
		return new LocationWeather(Float.parseFloat(temperature.getAttribute("value")), weather.getAttribute("value"),
				split[1].trim());
	}
}
