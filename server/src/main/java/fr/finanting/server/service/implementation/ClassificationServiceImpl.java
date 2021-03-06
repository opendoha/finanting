package fr.finanting.server.service.implementation;

import java.util.List;

import javax.transaction.Transactional;

import fr.finanting.server.generated.model.ClassificationDTO;
import fr.finanting.server.generated.model.ClassificationParameter;
import fr.finanting.server.generated.model.UpdateClassificationParameter;
import fr.finanting.server.dto.ClassificationDTOBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.finanting.server.exception.ClassificationNoUserException;
import fr.finanting.server.exception.ClassificationNotExistException;
import fr.finanting.server.exception.GroupNotExistException;
import fr.finanting.server.model.Classification;
import fr.finanting.server.model.Group;
import fr.finanting.server.model.User;
import fr.finanting.server.repository.ClassificationRepository;
import fr.finanting.server.repository.GroupRepository;
import fr.finanting.server.repository.UserRepository;
import fr.finanting.server.service.ClassificationService;

@Service
@Transactional
public class ClassificationServiceImpl implements ClassificationService {

    private final ClassificationRepository classificationRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    private static final ClassificationDTOBuilder CLASSIFICATION_DTO_BUILDER = new ClassificationDTOBuilder();

    @Autowired
    public ClassificationServiceImpl(final ClassificationRepository classificationRepository,
                                    final GroupRepository groupRepository,
                                    final UserRepository userRepository){
        this.classificationRepository = classificationRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

	@Override
	public void createClassification(final ClassificationParameter classificationParameter, final String userName) {

        final User user = this.userRepository.findByUserName(userName).orElseThrow();

        final Classification classification = new Classification();

        if(classificationParameter.getGroupName() == null){
            classification.setUser(user);
        } else {
            final Group group = this.groupRepository.findByGroupName(classificationParameter.getGroupName())
                .orElseThrow(() -> new GroupNotExistException(classificationParameter.getGroupName()));

            group.checkAreInGroup(user);

            classification.setGroup(group);
        }

        classification.setAbbreviation(classificationParameter.getAbbreviation().toUpperCase());
        classification.setDescription(classificationParameter.getDescription());
        classification.setLabel(classificationParameter.getLabel());

        this.classificationRepository.save(classification);
		
	}

    @Override
    public void updateClassification(final Integer classificationId,
                                     final UpdateClassificationParameter updateClassificationParameter,
                                     final String userName) {
        
        final User user = this.userRepository.findByUserName(userName).orElseThrow();

        final Classification classification = this.classificationRepository.findById(classificationId)
            .orElseThrow(() -> new ClassificationNotExistException(classificationId));
        
        if(classification.getGroup() == null){
            if(!classification.getUser().getUserName().equals(userName)){
                throw new ClassificationNoUserException(classification.getId());
            }
        } else {
            final Group group = classification.getGroup();
            group.checkAreInGroup(user);
        }

        classification.setAbbreviation(updateClassificationParameter.getAbbreviation().toUpperCase());
        classification.setDescription(updateClassificationParameter.getDescription());
        classification.setLabel(updateClassificationParameter.getLabel());

        this.classificationRepository.save(classification);
        
    }

    @Override
    public void deleteClassification(final Integer classificationId, final String userName) {

        final User user = this.userRepository.findByUserName(userName).orElseThrow();

        final Classification classification = this.classificationRepository.findById(classificationId)
            .orElseThrow(() -> new ClassificationNotExistException(classificationId));
        
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

    @Override
    public List<ClassificationDTO> getUserClassifications(final String userName) {
        final User user = this.userRepository.findByUserName(userName).orElseThrow();
        final List<Classification> classifications = this.classificationRepository.findByUserAndGroupIsNull(user);
        return CLASSIFICATION_DTO_BUILDER.transformAll(classifications);
    }

    @Override
    public List<ClassificationDTO> getGroupClassifications(final Integer groupId, final String userName) {
        final User user = this.userRepository.findByUserName(userName).orElseThrow();

        final Group group = this.groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotExistException(groupId));

        group.checkAreInGroup(user);

        final List<Classification> classifications = this.classificationRepository.findByGroupAndUserIsNull(group);
        return CLASSIFICATION_DTO_BUILDER.transformAll(classifications);
    }

}
