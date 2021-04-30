package fr.finanting.server.model.mothergroupuserelement;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import fr.finanting.server.exception.BadAssociationElementException;
import fr.finanting.server.exception.UserNotInGroupException;
import fr.finanting.server.model.Group;
import fr.finanting.server.model.User;
import fr.finanting.server.model.mother.MotherGroupUserElement;
import fr.finanting.server.testhelper.AbstractMotherIntegrationTest;

public class TestCheckIfUsable extends AbstractMotherIntegrationTest {

    private User userOne;
    private User userTwo;

    private Group groupeOne;

    @Override
    protected void initDataBeforeEach() throws Exception {
        this.userOne = this.testFactory.getUser();
        this.userTwo = this.testFactory.getUser();
        this.groupeOne = this.testFactory.getGroup(userOne);
    }

    @Test
    public void testGroupOk() throws BadAssociationElementException, UserNotInGroupException{
        MotherGroupUserElement motherGroupElement = new MotherGroupUserElement();
        motherGroupElement.setGroup(this.groupeOne);
        motherGroupElement.checkIfUsable(this.userOne);
    }

    @Test
    public void testUserOk() throws BadAssociationElementException, UserNotInGroupException{
        MotherGroupUserElement motherGroupElement = new MotherGroupUserElement();
        motherGroupElement.setUser(this.userOne);
        motherGroupElement.checkIfUsable(this.userOne);
    }

    @Test
    public void testGroupWithOtherUser(){
        MotherGroupUserElement motherGroupElement = new MotherGroupUserElement();
        motherGroupElement.setGroup(this.groupeOne);
        Assertions.assertThrows(UserNotInGroupException.class, () -> motherGroupElement.checkIfUsable(this.userTwo));
    }

    @Test
    public void testUserWithOtherUser(){
        MotherGroupUserElement motherGroupElement = new MotherGroupUserElement();
        motherGroupElement.setUser(this.userOne);
        Assertions.assertThrows(BadAssociationElementException.class, () -> motherGroupElement.checkIfUsable(this.userTwo));
    }

}
