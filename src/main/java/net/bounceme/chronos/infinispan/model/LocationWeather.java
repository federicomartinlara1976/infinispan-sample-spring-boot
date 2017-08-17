package net.bounceme.chronos.infinispan.model;

public class LocationWeather {
   final float temperature;
   final String conditions;
   final String country;

   public LocationWeather(float temperature, String conditions, String country) {
      this.temperature = temperature;
      this.conditions = conditions;
      this.country = country;
   }

   @Override
   public String toString() {
      return String.format("Temperature: %.1f° C, Conditions: %s", temperature, conditions);
   }
}
