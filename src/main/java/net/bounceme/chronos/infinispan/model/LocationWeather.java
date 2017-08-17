package net.bounceme.chronos.infinispan.model;

import java.io.Serializable;

import org.infinispan.commons.marshall.SerializeWith;

import net.bounceme.chronos.infinispan.utils.LWExternalizer;

@SerializeWith(LWExternalizer.class)
public class LocationWeather implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1732369060538972862L;

	final float temperature;

	final String conditions;

	final String country;

	public LocationWeather(float temperature, String conditions, String country) {
		this.temperature = temperature;
		this.conditions = conditions;
		this.country = country;
	}

	/**
	 * @return the temperature
	 */
	public float getTemperature() {
		return temperature;
	}

	/**
	 * @return the conditions
	 */
	public String getConditions() {
		return conditions;
	}

	/**
	 * @return the country
	 */
	public String getCountry() {
		return country;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((conditions == null) ? 0 : conditions.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + Float.floatToIntBits(temperature);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LocationWeather other = (LocationWeather) obj;
		if (conditions == null) {
			if (other.conditions != null)
				return false;
		}
		else if (!conditions.equals(other.conditions))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		}
		else if (!country.equals(other.country))
			return false;
		if (Float.floatToIntBits(temperature) != Float.floatToIntBits(other.temperature))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("Temperature: %.1f° C, Conditions: %s", temperature, conditions);
	}
}
