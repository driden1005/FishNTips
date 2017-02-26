package io.driden.fishtips.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Element(name = "marker")
public class MarkerTag {

    @Attribute(name = "name")
    private String name;
    @Attribute(name = "type")
    private String type;
    @Attribute(name = "description")
    private String description;

    @Attribute(name = "lat")
    private double lat;
    @Attribute(name = "lng")
    private double lng;
    @Attribute(name = "distance")
    private double distance;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "[name]: " + name + " [type]: " + type + " [description]: " + description + " [lat]: " + lat + " [lng]: " + lng + " [distance]: " + distance;
    }

}
