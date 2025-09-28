package com.cyberfoot.adapters.persistence.season;

import com.cyberfoot.domain.model.Season;

public class SeasonMapper {
    public static Season toDomain(SeasonEntity entity) {
        return new Season(
            entity.getId(),
            entity.getName(),
            entity.getYearStart(),
            entity.getYearEnd(),
            entity.isActive()
        );
    }

    public static SeasonEntity toEntity(Season season) {
        return new SeasonEntity(
            season.getId(),
            season.getName(),
            season.getYearStart(),
            season.getYearEnd(),
            season.isActive()
        );
    }
}
