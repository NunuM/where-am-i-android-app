/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.nunum.whereami.framework.persistence.repositories;


/**
 * @author nuno
 * @param <T>
 * @param <PK>
 */
public interface DeleteableRepository<T, PK>  {

    void delete(T entity);


    boolean deleteById(PK entityId);
}
