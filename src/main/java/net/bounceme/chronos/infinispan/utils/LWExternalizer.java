package net.bounceme.chronos.infinispan.utils;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.infinispan.commons.marshall.Externalizer;

import net.bounceme.chronos.infinispan.model.LocationWeather;

public class LWExternalizer implements Externalizer<LocationWeather> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7381102219386570807L;

	@Override
    public void writeObject(ObjectOutput output, LocationWeather object) throws IOException {
       output.writeFloat(object.getTemperature());
       output.writeUTF(object.getConditions());
       output.writeUTF(object.getCountry());
    }

    @Override
    public LocationWeather readObject(ObjectInput input) throws IOException, ClassNotFoundException {
       float temperature = input.readFloat();
       String conditions = input.readUTF();
       String country = input.readUTF();
       return new LocationWeather(temperature, conditions, country);
    }
}
