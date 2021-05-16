package fr.finanting.server.service;

import java.util.List;

import fr.finanting.server.codegen.model.CategoryParameter;
import fr.finanting.server.dto.TreeCategoriesDTO;
import fr.finanting.server.exception.BadAssociationCategoryTypeException;
import fr.finanting.server.exception.BadAssociationCategoryUserGroupException;
import fr.finanting.server.exception.CategoryNoUserException;
import fr.finanting.server.exception.CategoryNotExistException;
import fr.finanting.server.exception.DeleteCategoryWithChildException;
import fr.finanting.server.exception.GroupNotExistException;
import fr.finanting.server.exception.UserNotInGroupException;
import fr.finanting.server.parameter.UpdateCategoryParameter;

public interface CategoryService {

    void createCategory(CategoryParameter categoryParameter, String userName)
        throws CategoryNotExistException, BadAssociationCategoryUserGroupException, GroupNotExistException, CategoryNoUserException, UserNotInGroupException, BadAssociationCategoryTypeException;

    void updateCategory(UpdateCategoryParameter updateCategoryParameter, String userName)
        throws CategoryNotExistException, CategoryNoUserException, UserNotInGroupException, BadAssociationCategoryUserGroupException, BadAssociationCategoryTypeException;

    void deleteCategory(Integer categoryId, String userName)
        throws CategoryNotExistException, CategoryNoUserException, UserNotInGroupException, DeleteCategoryWithChildException;

    List<TreeCategoriesDTO> getUserCategory(String userName);

    List<TreeCategoriesDTO> getGroupCategory(String groupName, String userName)
        throws GroupNotExistException, UserNotInGroupException;
    
}
