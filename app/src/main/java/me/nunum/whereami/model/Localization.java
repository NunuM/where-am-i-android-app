package me.nunum.whereami.model;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

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


    private boolean canOthersSendSamples;


    private Date created;


    /**
     *
     */
    private LocalizationStats stats;


    public Localization() {
        this(0L, "", "", false, false, new LocalizationStats(), new Date());
    }

    public Localization(Long id,
                        String label,
                        String user,
                        boolean isOwner,
                        boolean canOthersSendSamples,
                        LocalizationStats stats,
                        Date created) {
        this.id = id;
        this.label = label;
        this.user = user;
        this.isOwner = isOwner;
        this.stats = stats;
        this.created = created;
        this.canOthersSendSamples = canOthersSendSamples;
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

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
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

    @Override
    public int compareTo(@NonNull Localization o) {
        return o.created.compareTo(this.created);
    }

    public boolean canOthersSendSamples() {
        return canOthersSendSamples;
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
