package edu.northeastern.a6_group8;

public class WeatherModel {
    String city;
    String condition;
    double temperature;
    String icon;


    public WeatherModel(String city, double temperature, String condition, String icon) {
        this.city = city;
        this.condition = condition;
        this.temperature = temperature;
        this.icon = icon;
    }

    public String getDescription() {
            return condition;
        }

        public double getTemperature() {
            return temperature;
        }
        public String getIcon() {
            return icon;
        }
        public String getCity() {
            return city;
        }
    }

