package com.weframe.product.generic.controllers;

import com.weframe.controllers.EmptyResultException;
import com.weframe.controllers.ResponseGenerator;
import com.weframe.controllers.errors.Error;
import com.weframe.picture.model.Picture;
import com.weframe.picture.service.PictureService;
import com.weframe.picture.service.exception.InvalidPicturePersistenceException;
import com.weframe.product.generic.model.BackBoard;
import com.weframe.product.generic.service.exception.InvalidGenericProductPersistenceException;
import com.weframe.product.generic.service.impl.BackBoardService;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Collections;

@SuppressWarnings({"unused", "WeakerAccess"})
@RestController
@RequestMapping("/generic-product/backboards")
@CrossOrigin
public class BackBoardController {

    private static final Logger logger = Logger.getLogger(BackBoardController.class);
    
    private final BackBoardService backBoardService;
    private final PictureService pictureService;
    private final ResponseGenerator<BackBoard> responseGenerator;

    public BackBoardController(final BackBoardService backBoardService,
                               final PictureService pictureService,
                               final ResponseGenerator<BackBoard> responseGenerator) {
        this.backBoardService = backBoardService;
        this.pictureService = pictureService;
        this.responseGenerator = responseGenerator;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    private ResponseEntity getBackBoards(
            @RequestParam(value="page", defaultValue="0", required = false) final int page,
            @RequestParam(value="size", defaultValue = "10", required = false) final int size,
            @RequestParam(value="unique-name", required = false) final String uniqueName,
            @RequestParam(value="original", required = false, defaultValue = "false") final boolean isOriginalSize) {
        try {
            if(uniqueName != null) {
                return getBackBoardByUniqueName(uniqueName, isOriginalSize);
            }

            if(page < 0 || size < 0) {
                Error error = new Error(
                        "invalid-request",
                        "The page and size parameters must be above zero."
                );
                return responseGenerator.generateErrorResponse(
                        Collections.singleton(error),
                        HttpStatus.UNPROCESSABLE_ENTITY
                );
            }

            Collection<BackBoard> backBoards = backBoardService.getAll(page, size);
            backBoards.forEach(backBoard -> {
                try {
                    backBoard.getPicture().setImageUrl(pictureService.getPictureThumbnailUrl(backBoard.getPicture().getImageKey()));
                } catch (InvalidPicturePersistenceException e) {
                    logger.error(
                            String.format(
                                    "There has been an error while trying to generate the backboard's picture url " +
                                            "for picture [%s]",
                                    backBoard.getPicture().getImageKey()),
                            e);
                    backBoard.getPicture().setImageUrl("no-url");
                }
            });
            return responseGenerator.generateResponse(backBoards);
        } catch (InvalidGenericProductPersistenceException e) {
            logger.error(
                    String.format(
                            "There has been an error requesting backboards by page [%d] " +
                                    "with size [%d]",
                            page,
                            size),
                    e);
            Error error = new Error(
                    "internal-server-error",
                    "There has been an internal server error. " +
                            "Please try again later.");
            return responseGenerator.generateErrorResponse(
                    Collections.singleton(error),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        } catch (EmptyResultException e) {
            return responseGenerator.generateEmptyResponse();
        }
    }

    @RequestMapping(value = "/{backBoardId}", method = RequestMethod.GET)
    private ResponseEntity getBackBoard(
            @PathVariable Long backBoardId,
            @RequestParam(name = "original", required = false, defaultValue = "false") final boolean originalSize) {
        try {
            BackBoard backBoard = backBoardService.getById(backBoardId);
            if(originalSize) {
                backBoard.getPicture().setImageUrl(pictureService.getPictureUrl(backBoard.getPicture().getImageKey()));
            } else {
                backBoard.getPicture().setImageUrl(pictureService.getPictureThumbnailUrl(backBoard.getPicture().getImageKey()));
            }
            return responseGenerator.generateResponse(
                    backBoardService.getById(backBoardId)
            );
        } catch (EmptyResultException e) {
            return responseGenerator.generateEmptyResponse();
        } catch (Exception e) {
            logger.error(
                    String.format(
                            "There was an unexpected error trying to fetch a " +
                                    "backboard by id [%d].",
                            backBoardId
                    ),
                    e);
            Error error = new Error(
                    "internal-server-error",
                    "There has been an internal server error. Please try again later.");
            return responseGenerator.generateErrorResponse(
                    Collections.singleton(error),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    private ResponseEntity getBackBoardByUniqueName(String backBoardUniqueName, boolean isOriginalSize) {
        try {
            BackBoard backBoard = backBoardService.getByUniqueName(backBoardUniqueName);
            if(isOriginalSize) {
                backBoard.getPicture().setImageUrl(pictureService.getPictureUrl(backBoard.getPicture().getImageKey()));
            } else {
                backBoard.getPicture().setImageUrl(pictureService.getPictureThumbnailUrl(backBoard.getPicture().getImageKey()));
            }
            return responseGenerator.generateResponse(backBoard);
        } catch (EmptyResultException e) {
            return responseGenerator.generateEmptyResponse();
        } catch (Exception e) {
            logger.error(
                    String.format(
                            "There was an unexpected error trying to fetch a " +
                                    "backboard by uniqueName [%s].",
                            backBoardUniqueName
                    ),
                    e);
            Error error = new Error(
                    "internal-server-error",
                    "There has been an internal server error. Please try again later.");
            return responseGenerator.generateErrorResponse(
                    Collections.singleton(error),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    private ResponseEntity create(@RequestBody BackBoard backBoard) {
        try {
            if(backBoard.getId() != null) {
                throw new InvalidGenericProductPersistenceException(
                        "A backboard to be created should not have an id."
                );
            }
            Picture picture = pictureService.getByUniqueName(
                    backBoard.getPicture().getImageKey()
            );
            backBoard.setPicture(picture);
            backBoardService.persist(backBoard);
            return responseGenerator.generateOkResponse();
        } catch(InvalidGenericProductPersistenceException |
                InvalidPicturePersistenceException |
                EmptyResultException e) {
            logger.error(
                    String.format(
                            "There has been an error creating the backboard [%s]",
                            backBoard.getUniqueName()),
                    e);
            Error error = new Error(
                    "internal-server-error",
                    "There has been an internal server error. " +
                            "Please try again later.");
            return responseGenerator.generateErrorResponse(
                    Collections.singleton(error),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    private ResponseEntity update(@RequestBody BackBoard backBoard) {
        try {
            if(backBoard.getId() == null
                    && backBoard.getUniqueName() == null) {
                throw new InvalidGenericProductPersistenceException(
                        "A backboard to be updated should have an id or the unique name set."
                );
            }
            Picture picture = pictureService.getByUniqueName(
                    backBoard.getPicture().getImageKey()
            );
            backBoard.setPicture(picture);
            backBoardService.persist(backBoard);
            return responseGenerator.generateOkResponse();
        } catch(InvalidGenericProductPersistenceException |
                InvalidPicturePersistenceException |
                EmptyResultException e) {
            logger.error(
                    String.format(
                            "There has been an error creating the backboard [%s]",
                            backBoard.getUniqueName()),
                    e);
            Error error = new Error(
                    "internal-server-error",
                    "There has been an internal server error. " +
                            "Please try again later.");
            return responseGenerator.generateErrorResponse(
                    Collections.singleton(error),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }

    @RequestMapping(value = "/{backBoardId}", method = RequestMethod.DELETE)
    private ResponseEntity delete(@PathVariable Long backBoardId) {
        try {
            backBoardService.delete(backBoardId);
            logger.info("Deleted backboard [" + backBoardId + "].");
            return responseGenerator.generateOkResponse();
        } catch (Exception e) {
            logger.error(
                    String.format(
                            "There was an unexpected error trying to delete a backboard" +
                                    " by id [%d].",
                            backBoardId),
                    e);
            Error error = new Error(
                    "internal-server-error",
                    "There has been an internal server error. Please try again later.");
            return responseGenerator.generateErrorResponse(
                    Collections.singleton(error),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }
    }
    
}