package com.weframe.product.service.impl;

import com.weframe.product.model.generic.MatType;
import com.weframe.product.service.GenericProductRepository;
import com.weframe.product.service.exception.InvalidGenericProductPersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface MatTypeJpaRepository extends GenericProductRepository<MatType>, JpaRepository<MatType, Long> {

    @Override
    default void persist(final MatType matType) throws InvalidGenericProductPersistenceException {
        try {
            save(matType);
        } catch(DataAccessException e) {
            throw new InvalidGenericProductPersistenceException(e);
        }
    }

    @Override
    default void remove(final Long id) throws InvalidGenericProductPersistenceException {
        try {
            delete(id);
        } catch(DataAccessException e) {
            throw new InvalidGenericProductPersistenceException(e);
        }
    }

    @Override
    default MatType get(final Long id) throws InvalidGenericProductPersistenceException {
        try {
            return findOne(id);
        } catch(DataAccessException e) {
            throw new InvalidGenericProductPersistenceException(e);
        }
    }

    @Override
    default MatType get(final String uniqueName) throws InvalidGenericProductPersistenceException {
        try {
            return findByUniqueName(uniqueName);
        } catch(DataAccessException e) {
            throw new InvalidGenericProductPersistenceException(e);
        }
    }

    @Override
    default Collection<MatType> getAll(final int size,
                                     final int page) throws InvalidGenericProductPersistenceException {
        try {
            return findAll(new PageRequest(page, size)).getContent();
        } catch(DataAccessException e) {
            throw new InvalidGenericProductPersistenceException(e);
        }
    }

    @Override
    default Collection<MatType> getAllWtihNameLike(final String name, final int size, final int page) throws InvalidGenericProductPersistenceException {
        try {
            return findByName(name, new PageRequest(page, size)).getContent();
        } catch(DataAccessException e) {
            throw new InvalidGenericProductPersistenceException(e);
        }
    }

    MatType findByUniqueName(final String uniqueName);
    Page<MatType> findByName(final String name, final Pageable pageable);

}
