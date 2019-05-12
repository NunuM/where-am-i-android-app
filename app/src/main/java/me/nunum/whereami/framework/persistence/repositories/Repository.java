/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package me.nunum.whereami.framework.persistence.repositories;


public interface Repository<T, PK, C> {


    void save(T entity, C callback);


    void add(T entity, C callback);


    void findById(Long id, C callback);


    void delete(C callback);
}
