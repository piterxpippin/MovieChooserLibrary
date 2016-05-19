package moviefinder.domain;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by andrzej on 20.04.2016.
 */
public enum MovieType {

    COMEDY,
    DRAMA,
    THRILLER,
    CRIME,
    WAR,
    ACTION,
    ADVENTURE,
    ROMANCE,
    SCI_FI,
    FILM_NOIR,
    IMAX,
    MYSTERY,
    WESTERN,
    FANTASY,
    ANIMATION,
    CHILDREN,
    MUSICAL,
    HORROR,
    DOCUMENTARY;



    public String getName() {
        return StringUtils.capitalize(this.name().toLowerCase());
    }
}
