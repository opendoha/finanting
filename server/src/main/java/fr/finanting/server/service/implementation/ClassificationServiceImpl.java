package fr.finanting.server.service.implementation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.finanting.server.exception.ClassificationNoUserException;
import fr.finanting.server.exception.ClassificationNotExistException;
import fr.finanting.server.exception.GroupNotExistException;
import fr.finanting.server.exception.UserNotInGroupException;
import fr.finanting.server.model.Classification;
import fr.finanting.server.model.Group;
import fr.finanting.server.model.User;
import fr.finanting.server.parameter.CreateClassificationParameter;
import fr.finanting.server.parameter.DeleteClassificationParameter;
import fr.finanting.server.parameter.UpdateClassificationParameter;
import fr.finanting.server.repository.ClassificationRepository;
import fr.finanting.server.repository.GroupRepository;
import fr.finanting.server.repository.UserRepository;
import fr.finanting.server.service.ClassificationService;

@Service
public class ClassificationServiceImpl implements ClassificationService {

    private ClassificationRepository classificationRepository;
    private GroupRepository groupRepository;
    private UserRepository userRepository;

    @Autowired
    public ClassificationServiceImpl(final ClassificationRepository classificationRepository,
                                    final GroupRepository groupRepository,
                                    final UserRepository userRepository){
        this.classificationRepository = classificationRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

	@Override
	public void createClassification(final CreateClassificationParameter createClassificationParameter, final String userName)
        throws GroupNotExistException, UserNotInGroupException {

        final User user = this.userRepository.findByUserName(userName).orElseThrow();

        final Classification classification = new Classification();
        classification.setAbbreviation(createClassificationParameter.getAbbreviation().toUpperCase());
        classification.setDescritpion(createClassificationParameter.getDescritpion());
        classification.setLabel(createClassificationParameter.getLabel());

        if(createClassificationParameter.getGroupName() == null){
            classification.setUser(user);
        } else {
            final Group group = this.groupRepository.findByGroupName(createClassificationParameter.getGroupName())
                .orElseThrow(() -> new GroupNotExistException(createClassificationParameter.getGroupName()));

            group.checkAreInGroup(user);

            classification.setGroup(group);
        }

        this.classificationRepository.save(classification);
		
	}

    @Override
    public void updateClassification(final UpdateClassificationParameter updateClassificationParameter, final String userName)
        throws ClassificationNotExistException, UserNotInGroupException, ClassificationNoUserException {
        
        final User user = this.userRepository.findByUserName(userName).orElseThrow();

        final Classification classification = this.classificationRepository.findById(updateClassificationParameter.getId())
            .orElseThrow(() -> new ClassificationNotExistException(updateClassificationParameter.getId()));
        
        if(classification.getGroup() == null){
            if(!classification.getUser().getUserName().equals(userName)){
                throw new ClassificationNoUserException(classification.getId());
            }
        } else {
            final Group group = classification.getGroup();
            group.checkAreInGroup(user);
        }

        classification.setAbbreviation(updateClassificationParameter.getAbbreviation().toUpperCase());
        classification.setDescritpion(updateClassificationParameter.getDescritpion());
        classification.setLabel(updateClassificationParameter.getLabel());

        this.classificationRepository.save(classification);
        
    }

    @Override
    public void deleteClassification(final DeleteClassificationParameter deleteClassificationParameter, final String userName) throws ClassificationNotExistException, UserNotInGroupException, ClassificationNoUserException {

        final User user = this.userRepository.findByUserName(userName).orElseThrow();

        final Classification classification = this.classificationRepository.findById(deleteClassificationParameter.getId())
            .orElseThrow(() -> new ClassificationNotExistException(deleteClassificationParameter.getId()));
        
        if(classification.getGroup() == null){
            if(!classification.getUser().getUserName().equals(userName)){
                throw new ClassificationNoUserException(classification.getId());
            }
        } else {
            final Group group = classification.getGroup();
            group.checkAreInGroup(user);
        }

        this.classificationRepository.delete(classification);
        
    } 

}
