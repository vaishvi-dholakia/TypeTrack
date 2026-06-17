package com.typetrack.model;

import com.typetrack.service.StatisticsManager;
import java.util.function.Predicate;

/**
 * Class representing a performance achievement/badge.
 */
public class Achievement {
    private final String name;
    private final String description;
    private final String icon;
    private final Predicate<StatisticsManager> unlockCriteria;

    public Achievement(String name, String description, String icon, Predicate<StatisticsManager> unlockCriteria) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.unlockCriteria = unlockCriteria;
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

    /**
     * Checks if the user's current statistics meet the criteria for unlocking this badge.
     */
    public boolean isUnlocked(StatisticsManager stats) {
        return unlockCriteria.test(stats);
    }
}
