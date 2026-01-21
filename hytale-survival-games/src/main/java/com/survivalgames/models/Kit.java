package com.survivalgames.models;

import java.util.List;

public class Kit {
    private final String id;
    private final String name;
    private final String description;
    private final String icon;
    private final List<String> items;

    public Kit(String id, String name, String description, String icon, List<String> items) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.items = items;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public List<String> getItems() {
        return items;
    }
}
