package com.weframe.product.service.impl;

import com.weframe.controllers.EmptyResultException;
import com.weframe.product.model.generic.FrameGlass;
import com.weframe.product.service.GenericProductRepository;
import com.weframe.product.service.GenericProductService;
import com.weframe.product.service.exception.InvalidGenericProductPersistenceException;

import java.util.Collection;

public class FrameGlassService extends GenericProductService<FrameGlass> {

    public FrameGlassService(GenericProductRepository<FrameGlass> repository) {
        super(repository);
    }

    @Override
    public FrameGlass getById(Long id) throws InvalidGenericProductPersistenceException, EmptyResultException {
        return repository.get(id);
    }

    @Override
    public FrameGlass getByUniqueName(String uniqueName) throws InvalidGenericProductPersistenceException, EmptyResultException {
        return repository.get(uniqueName);
    }

    @Override
    public Collection<FrameGlass> getAll(int page, int size) throws InvalidGenericProductPersistenceException, EmptyResultException {
        return repository.getAll(size, page);
    }

    @Override
    public Collection<FrameGlass> getAllWithNameLike(String name, int page, int size) throws InvalidGenericProductPersistenceException, EmptyResultException {
        return repository.getAllWtihNameLike(name, size, page);
    }

    @Override
    public void persist(FrameGlass frameGlass) throws InvalidGenericProductPersistenceException {
        repository.persist(frameGlass);
    }

    @Override
    public void delete(Long id) throws InvalidGenericProductPersistenceException {
        repository.remove(id);
    }
}