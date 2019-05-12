/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.nunum.whereami.framework.persistence.repositories;


import java.util.List;

import me.nunum.whereami.framework.OnResponse;

/**
 * A Repository which can be efficiently iterated.
 * <p>
 * the implementation class must provide for an efficient iterator over all
 * entities in the repository. this is particular the case for database-backed
 * up persistence stores where one expects a cursor like behaviour. it is the
 * responsibility of the implementation class to handle disconnected scenarios
 * or not.
 *
 * @author nuno
 */
public interface PaginateRepository<T, PK> {
    void paginate(int currentPage, OnResponse<List<PK>> onResponse);
}
