package me.nunum.whereami.model;

import android.support.annotation.NonNull;

import java.io.Serializable;

import me.nunum.whereami.framework.domain.Identifiable;
import me.nunum.whereami.model.request.NewTrainingRequest;
import me.nunum.whereami.model.request.SpamRequest;

public class Localization implements
        Identifiable<Long>,
        Comparable<Localization>,
        Serializable {


    /**
     * Localization ID
     */
    private Long id;

    /**
     * Localization name
     */
    private String label;

    /**
     * User label
     */
    private String user;

    /**
     * Ownership
     */
    private boolean isOwner;


    /**
     *
     */
    private LocalizationStats stats;


    public Localization() {
        this(0L, "", "", true, new LocalizationStats());
    }

    public Localization(Long id, String label, String user, boolean isOwner, LocalizationStats stats) {
        this.id = id;
        this.label = label;
        this.user = user;
        this.isOwner = isOwner;
        this.stats = stats;
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
        return this.id;
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isOwner() {
        return isOwner;
    }

    public void setOwner(boolean owner) {
        isOwner = owner;
    }

    public LocalizationStats getStats() {
        return stats;
    }

    public void setStats(LocalizationStats stats) {
        this.stats = stats;
    }

    public SpamRequest createSpamRequest() {
        return new SpamRequest(this.id, getClass().getSimpleName());
    }

    public NewTrainingRequest createTrainingRequest() {
        return new NewTrainingRequest();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Localization)) return false;

        Localization that = (Localization) o;

        if (isOwner() != that.isOwner()) return false;
        if (!getId().equals(that.getId())) return false;
        if (!getLabel().equals(that.getLabel())) return false;
        return getUser().equals(that.getUser());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getLabel().hashCode();
        result = 31 * result + getUser().hashCode();
        result = 31 * result + (isOwner() ? 1 : 0);
        return result;
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
    public int compareTo(@NonNull Localization o) {
        return this.getLabel().toLowerCase().compareTo(o.getLabel().toLowerCase());
    }


    @Override
    public String toString() {
        return "Localization{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", user='" + user + '\'' +
                ", isOwner=" + isOwner +
                ", stats=" + stats +
                '}';
    }
}
