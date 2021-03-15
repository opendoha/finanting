package fr.finanting.server.service.implementation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.finanting.server.dto.GroupeDTO;
import fr.finanting.server.exception.GroupeNameAlreadyExistException;
import fr.finanting.server.exception.GroupeNotExistException;
import fr.finanting.server.exception.NotAdminGroupeException;
import fr.finanting.server.exception.UserNotExistException;
import fr.finanting.server.exception.UserNotInGroupeException;
import fr.finanting.server.model.Groupe;
import fr.finanting.server.model.User;
import fr.finanting.server.parameter.AddUsersGroupeParameter;
import fr.finanting.server.parameter.DeleteGroupeParameter;
import fr.finanting.server.parameter.GroupeCreationParameter;
import fr.finanting.server.parameter.RemoveUsersGroupeParameter;
import fr.finanting.server.repository.GroupeRepository;
import fr.finanting.server.repository.UserRepository;
import fr.finanting.server.service.GroupeService;

@Service
@Transactional
public class GroupeServiceImpl implements GroupeService {

    private UserRepository userRepository;
    private GroupeRepository groupeRepository;

    @Autowired
    public GroupeServiceImpl(final UserRepository userRepository, final GroupeRepository groupeRepository){
        this.userRepository = userRepository;
        this.groupeRepository = groupeRepository;
    }

    @Override
    public GroupeDTO createGroupe(final GroupeCreationParameter groupeCreationParameter, final String userName)
        throws GroupeNameAlreadyExistException, UserNotExistException {
        
        final User user = this.userRepository.findByUserName(userName).get();

        final Boolean groupeExist = this.groupeRepository.existsByGroupeName(groupeCreationParameter.getGroupeName());

        if(groupeExist){
            throw new GroupeNameAlreadyExistException(groupeCreationParameter.getGroupeName());
        }

        final Groupe groupe = new Groupe();
        groupe.setGroupeName(groupeCreationParameter.getGroupeName());
        groupe.setUserAdmin(user);

        final List<User> userList = new ArrayList<>();
        userList.add(user);

        for(final String userNameToAdd : groupeCreationParameter.getUsersName()){
            final User userToAdd = this.userRepository.findByUserName(userNameToAdd)
                .orElseThrow(() -> new UserNotExistException(userNameToAdd));

            userList.add(userToAdd);
        }

        groupe.setUsers(userList);

        this.groupeRepository.save(groupe);

        final GroupeDTO groupeDTO = new GroupeDTO(groupe);

        return groupeDTO;
    }

    @Override
    public GroupeDTO addUsersGroupe(final AddUsersGroupeParameter addUsersGroupeParameter, final String userName)
            throws UserNotExistException, GroupeNotExistException, NotAdminGroupeException {
        
        final User user = this.userRepository.findByUserName(userName).get();
        final Groupe groupe = this.groupeRepository.findByGroupeName(addUsersGroupeParameter.getGroupeName())
            .orElseThrow(() -> new GroupeNotExistException(addUsersGroupeParameter.getGroupeName()));

        final User userAdmin = groupe.getUserAdmin();

        if(!userAdmin.getId().equals(user.getId())){
            throw new NotAdminGroupeException(groupe);
        }

        final List<User> userList = groupe.getUsers();

        for(final String userNameToAdd : addUsersGroupeParameter.getUsersName()){
            boolean areAlreadyOnGroupe = false;

            for(final User userGroupe : groupe.getUsers()){
                if(userGroupe.getUserName().equals(userNameToAdd)){
                    areAlreadyOnGroupe = true;
                }
            }

            if(!areAlreadyOnGroupe){
                final User userToAdd = this.userRepository.findByUserName(userNameToAdd)
                    .orElseThrow(() -> new UserNotExistException(userNameToAdd));
                userList.add(userToAdd);
            }
        }

        final GroupeDTO groupeDTO = new GroupeDTO(groupe);

        return groupeDTO;
    }

    @Override
    public GroupeDTO removeUsersGroupe(final RemoveUsersGroupeParameter removeUsersGroupeParameter, final String userName)
        throws GroupeNotExistException, NotAdminGroupeException, UserNotInGroupeException, UserNotExistException {
        
        final User user = this.userRepository.findByUserName(userName).get();
        final Groupe groupe = this.groupeRepository.findByGroupeName(removeUsersGroupeParameter.getGroupeName())
            .orElseThrow(() -> new GroupeNotExistException(removeUsersGroupeParameter.getGroupeName()));

            final User userAdmin = groupe.getUserAdmin();

        if(!userAdmin.getId().equals(user.getId())){
            throw new NotAdminGroupeException(groupe);
        }

        final List<User> userList = groupe.getUsers();

        for(final String userNameToRemove : removeUsersGroupeParameter.getUsersName()){
            final User userToRemove = this.userRepository.findByUserName(userNameToRemove)
                .orElseThrow(() -> new UserNotExistException(userNameToRemove));

            final boolean areInGroupe = userList.remove(userToRemove);

            if(!areInGroupe){
                throw new UserNotInGroupeException(userToRemove, groupe);
            }
        }

        final GroupeDTO groupeDTO = new GroupeDTO(groupe);

        return groupeDTO;
    }

    @Override
    public void deleteGroupe(final DeleteGroupeParameter deleteGroupeParameter, final String userName)
            throws GroupeNotExistException, NotAdminGroupeException {

        final User user = this.userRepository.findByUserName(userName).get();
        final Groupe groupe = this.groupeRepository.findByGroupeName(deleteGroupeParameter.getGroupeName())
            .orElseThrow(() -> new GroupeNotExistException(deleteGroupeParameter.getGroupeName()));

        final User userAdmin = groupe.getUserAdmin();

        if(!userAdmin.getId().equals(user.getId())){
            throw new NotAdminGroupeException(groupe);
        }

        this.groupeRepository.delete(groupe);
    }
    
}
