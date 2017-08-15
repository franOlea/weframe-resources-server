package com.weframe.product.generic.service.impl;

import com.weframe.product.generic.model.Frame;
import com.weframe.product.generic.service.GenericProductRepository;
import com.weframe.product.generic.service.exception.InvalidGenericProductPersistenceException;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface FrameJpaRepository extends GenericProductRepository<Frame>, JpaRepository<Frame, Long> {

    @Override
    default void persist(final Frame frame) throws InvalidGenericProductPersistenceException {
        try {
            save(frame);
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
    default Frame get(final Long id) throws InvalidGenericProductPersistenceException {
        try {
            return findOne(id);
        } catch(DataAccessException e) {
            throw new InvalidGenericProductPersistenceException(e);
        }
    }

    @Override
    default Frame get(final String uniqueName) throws InvalidGenericProductPersistenceException {
        try {
            return findByUniqueName(uniqueName);
        } catch(DataAccessException e) {
            throw new InvalidGenericProductPersistenceException(e);
        }
    }

    @Override
    default Long getCount() throws InvalidGenericProductPersistenceException {
        try {
            return count();
        } catch(DataAccessException e) {
            throw new InvalidGenericProductPersistenceException(e);
        }
    }

    @Override
    default Collection<Frame> getAll(final int size,
                                         final int page) throws InvalidGenericProductPersistenceException {
        try {
            return findAll(new PageRequest(page, size)).getContent();
        } catch(DataAccessException e) {
            throw new InvalidGenericProductPersistenceException(e);
        }
    }

    @Override
    default Collection<Frame> getAllWtihNameLike(final String name, final int size, final int page) throws InvalidGenericProductPersistenceException {
        try {
            return findByName(name, new PageRequest(page, size)).getContent();
        } catch(DataAccessException e) {
            throw new InvalidGenericProductPersistenceException(e);
        }
    }

    Frame findByUniqueName(final String uniqueName);
    Page<Frame> findByName(final String name, final Pageable pageable);

}
