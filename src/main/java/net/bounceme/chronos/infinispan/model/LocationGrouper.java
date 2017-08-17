package net.bounceme.chronos.infinispan.model;

import org.infinispan.distribution.group.Grouper;

public class LocationGrouper implements Grouper<String> {

    @Override
    @Deprecated
    public String computeGroup(String key, String group) {
       return key.split(",")[1].trim();
    }

    @Override
    public Class<String> getKeyType() {
       return String.class;
    }
 }
