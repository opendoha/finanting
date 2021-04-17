package fr.finanting.server.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.finanting.server.dto.TreeCategoriesDTO;
import fr.finanting.server.exception.BadAssociationCategoryTypeException;
import fr.finanting.server.exception.BadAssociationCategoryUserGroupException;
import fr.finanting.server.exception.CategoryNoUserException;
import fr.finanting.server.exception.CategoryNotExistException;
import fr.finanting.server.exception.DeleteCategoryWithChildException;
import fr.finanting.server.exception.GroupNotExistException;
import fr.finanting.server.exception.UserNotInGroupException;
import fr.finanting.server.model.Category;
import fr.finanting.server.model.Group;
import fr.finanting.server.model.User;
import fr.finanting.server.parameter.CreateCategoryParameter;
import fr.finanting.server.parameter.DeleteCategoryParameter;
import fr.finanting.server.parameter.UpdateCategoryParameter;
import fr.finanting.server.repository.CategoryRepository;
import fr.finanting.server.repository.GroupRepository;
import fr.finanting.server.repository.UserRepository;
import fr.finanting.server.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {
    
    private UserRepository userRepository;
    private GroupRepository groupRepository;
    private CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(final UserRepository userRepository, final GroupRepository groupRepository, final CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void createCategory(final CreateCategoryParameter createCategoryParameter, final String userName)
        throws CategoryNotExistException, BadAssociationCategoryUserGroupException, GroupNotExistException, CategoryNoUserException, UserNotInGroupException, BadAssociationCategoryTypeException{

        final Category category = new Category();

        final User user = this.userRepository.findByUserName(userName).orElseThrow();

        // check if the new category are a sub category
        if(createCategoryParameter.getParentId() != null){
            final Integer id = createCategoryParameter.getParentId();
            final Category parentCategory = this.categoryRepository.findById(id).orElseThrow(() -> new CategoryNotExistException(id));

            if(!parentCategory.getCategoryType().equals(createCategoryParameter.getCategoryType())){
                throw new BadAssociationCategoryTypeException();
            }

            boolean areGoodAssociation = false;

            if(parentCategory.getGroup() != null && createCategoryParameter.getGroupName() != null){
                parentCategory.getGroup().checkAreInGroup(user);

                if(parentCategory.getGroup().getGroupName().equals(createCategoryParameter.getGroupName())){
                    areGoodAssociation = true;
                }

            } else if(parentCategory.getUser() != null  && createCategoryParameter.getGroupName() == null){
                if(parentCategory.getUser().getUserName().equals(userName)){
                    areGoodAssociation = true;
                } else {
                    throw new CategoryNoUserException(id);
                }
            }

            if(!areGoodAssociation){
                throw new BadAssociationCategoryUserGroupException();
            }

            category.setParent(parentCategory);

        }

        category.setLabel(createCategoryParameter.getLabel());
        category.setAbbreviation(createCategoryParameter.getAbbreviation().toUpperCase());
        category.setDescritpion(createCategoryParameter.getDescritpion());
        category.setCategoryType(createCategoryParameter.getCategoryType());

        if(createCategoryParameter.getGroupName() == null){
            category.setUser(user);
        } else {
            final String groupName = createCategoryParameter.getGroupName();
            final Group group = this.groupRepository.findByGroupName(groupName)
                .orElseThrow(() -> new GroupNotExistException(groupName));
            category.setGroup(group);
        }

        this.categoryRepository.saveAndFlush(category);

    }

    @Override
    public void updateCategory(final UpdateCategoryParameter updateCategoryParameter, final String userName)
        throws CategoryNotExistException, CategoryNoUserException, UserNotInGroupException, BadAssociationCategoryUserGroupException, BadAssociationCategoryTypeException{

        final Category category = this.categoryRepository.findById(updateCategoryParameter.getId())
            .orElseThrow(() -> new CategoryNotExistException(updateCategoryParameter.getId()));

        final User user = this.userRepository.findByUserName(userName).orElseThrow();

        if(category.getGroup() != null){
            category.getGroup().checkAreInGroup(user);
        } else if (!category.getUser().getUserName().equals(userName)){
            throw new CategoryNoUserException(updateCategoryParameter.getId());
        }

        if(updateCategoryParameter.getParentId() == null){
            category.setParent(null);
        } else {
            final Integer id = updateCategoryParameter.getParentId();
            final Category parentCategory = this.categoryRepository.findById(id).orElseThrow(() -> new CategoryNotExistException(id));

            if(!parentCategory.getCategoryType().equals(updateCategoryParameter.getCategoryType())){
                throw new BadAssociationCategoryTypeException();
            }

            boolean areGoodAssociation = false;

            if(parentCategory.getUser() != null && category.getUser() != null){
                if(parentCategory.getUser().getUserName().equals(userName)){
                    areGoodAssociation = true;
                } else {
                    throw new CategoryNoUserException(id);
                }
            } else if(parentCategory.getGroup() != null && category.getGroup() != null) {

                parentCategory.getGroup().checkAreInGroup(user);

                if(parentCategory.getGroup().getGroupName().equals(category.getGroup().getGroupName())){
                    areGoodAssociation = true;
                }
            }

            if(!areGoodAssociation){
                throw new BadAssociationCategoryUserGroupException();
            }

            category.setParent(parentCategory);

        }

        category.setLabel(updateCategoryParameter.getLabel());
        category.setAbbreviation(updateCategoryParameter.getAbbreviation().toUpperCase());
        category.setDescritpion(updateCategoryParameter.getDescritpion());
        category.setCategoryType(updateCategoryParameter.getCategoryType());

        this.categoryRepository.saveAndFlush(category);

    }

    @Override
    public List<TreeCategoriesDTO> getUserCategory(final String userName){
        final User user = this.userRepository.findByUserName(userName).orElseThrow();
        final List<Category> motherCategories = this.categoryRepository.findByUserAndGroupIsNullAndParentIsNull(user);
        return this.getTreeCategoryDTO(motherCategories);
    }

    @Override
    public List<TreeCategoriesDTO> getGroupCategory(final String groupName, final String userName)
        throws GroupNotExistException, UserNotInGroupException{

        final User user = this.userRepository.findByUserName(userName).orElseThrow();

        final Group group = this.groupRepository.findByGroupName(groupName)
                .orElseThrow(() -> new GroupNotExistException(groupName));

        group.checkAreInGroup(user);

        final List<Category> motherCategories = this.categoryRepository.findByGroupAndParentIsNull(group);
        return this.getTreeCategoryDTO(motherCategories);
    }

    private List<TreeCategoriesDTO> getTreeCategoryDTO(final List<Category> categories){

        final List<TreeCategoriesDTO> treeCategoriesDTOs = new ArrayList<>();

        for(final Category motherCategory : categories){
            final TreeCategoriesDTO treeCategoriesDTO = new TreeCategoriesDTO(motherCategory);
            treeCategoriesDTOs.add(treeCategoriesDTO);
        }

        return treeCategoriesDTOs;
    }

    @Override
    public void deleteCategory(final DeleteCategoryParameter deleteCategoryParameter, final String userName)
        throws CategoryNotExistException, CategoryNoUserException, UserNotInGroupException, DeleteCategoryWithChildException {

        final User user = this.userRepository.findByUserName(userName).orElseThrow();

        final Category category = this.categoryRepository.findById(deleteCategoryParameter.getId())
            .orElseThrow(() -> new CategoryNotExistException(deleteCategoryParameter.getId()));

        if(category.getGroup() != null){
            category.getGroup().checkAreInGroup(user);
        } else if (!category.getUser().getUserName().equals(userName)){
            throw new CategoryNoUserException(deleteCategoryParameter.getId());
        }

        final List<Category> childs = category.getChild();

        if(!childs.isEmpty()){
            throw new DeleteCategoryWithChildException();
        }

        this.categoryRepository.delete(category);
        
    }

}