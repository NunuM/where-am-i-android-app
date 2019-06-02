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

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * <p>
     * <p>The implementor must ensure <tt>sgn(x.compareTo(y)) ==
     * -sgn(y.compareTo(x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>x.compareTo(y)</tt> must throw an exception iff
     * <tt>y.compareTo(x)</tt> throws an exception.)
     * <p>
     * <p>The implementor must also ensure that the relation is transitive:
     * <tt>(x.compareTo(y)&gt;0 &amp;&amp; y.compareTo(z)&gt;0)</tt> implies
     * <tt>x.compareTo(z)&gt;0</tt>.
     * <p>
     * <p>Finally, the implementor must ensure that <tt>x.compareTo(y)==0</tt>
     * implies that <tt>sgn(x.compareTo(z)) == sgn(y.compareTo(z))</tt>, for
     * all <tt>z</tt>.
     * <p>
     * <p>It is strongly recommended, but <i>not</i> strictly required that
     * <tt>(x.compareTo(y)==0) == (x.equals(y))</tt>.  Generally speaking, any
     * class that implements the <tt>Comparable</tt> interface and violates
     * this condition should clearly indicate this fact.  The recommended
     * language is "Note: this class has a natural ordering that is
     * inconsistent with equals."
     * <p>
     * <p>In the foregoing description, the notation
     * <tt>sgn(</tt><i>expression</i><tt>)</tt> designates the mathematical
     * <i>signum</i> function, which is defined to return one of <tt>-1</tt>,
     * <tt>0</tt>, or <tt>1</tt> according to whether the value of
     * <i>expression</i> is negative, zero or positive.
     *
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     * @throws NullPointerException if the specified object is null
     * @throws ClassCastException   if the specified object's type prevents it
     *                              from being compared to this object.
     */
    @Override
    public int compareTo(@NonNull Position o) {
        return this.label.toLowerCase().compareTo(o.label.toLowerCase());
    }
}
