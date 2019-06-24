package me.nunum.whereami.model;

import android.support.annotation.NonNull;

import me.nunum.whereami.framework.domain.Identifiable;
import me.nunum.whereami.model.request.SpamRequest;

public class Position implements Identifiable<Long>, Comparable<Position> {

    private Long id;

    private String label;

    private PositionStats stats;

    public Position() {
        this(0L, "", new PositionStats());
    }

    public Position(Long id, String label, PositionStats stats) {
        this.id = id;
        this.label = label;
        this.stats = stats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public PositionStats getStats() {
        return stats;
    }

    public void setStats(PositionStats stats) {
        this.stats = stats;
    }

    public SpamRequest createSpamRequest() {
        return new SpamRequest(this.id, getClass().getSimpleName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;

        Position position = (Position) o;

        if (getId() != null ? !getId().equals(position.getId()) : position.getId() != null)
            return false;
        return getLabel() != null ? getLabel().equals(position.getLabel()) : position.getLabel() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getLabel() != null ? getLabel().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", stats=" + stats +
                '}';
    }

    /**
     * checks if the object is identified by the passed business id
     *
     * @param id the identity to check
     * @return true if the object has that identity
     */
    @Override
    public boolean is(Long id) {
        return this.id.equals(id);
    }

    /**
     * returns the primary <b>business</b> id of the entity
     *
     * @return the primary <b>business</b> id of the entity
     */
    @Override
    public Long id() {
        return id;
    }


    @Override
    public int compareTo(@NonNull Position o) {
        return this.label.toLowerCase().compareTo(o.label.toLowerCase());
    }
}
