package com.weframe.product.generic.service;

import com.weframe.product.generic.model.GenericProduct;
import com.weframe.product.generic.service.exception.InvalidGenericProductPersistenceException;

import java.util.Collection;

public interface GenericProductRepository<T extends GenericProduct> {

    void persist(final T t)
            throws InvalidGenericProductPersistenceException;

    void remove(final Long id)
            throws InvalidGenericProductPersistenceException;

    T get(final Long id)
            throws InvalidGenericProductPersistenceException;

    T get(final String uniqueName)
            throws InvalidGenericProductPersistenceException;

    Long getCount() throws InvalidGenericProductPersistenceException;

    Collection<T> getAll(int size, int page)
            throws InvalidGenericProductPersistenceException;

    Collection<T> getAllWtihNameLike(final String name, final int size, final int page)
            throws InvalidGenericProductPersistenceException;
}
