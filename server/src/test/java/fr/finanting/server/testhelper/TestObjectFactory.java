package fr.finanting.server.testhelper;

import fr.finanting.server.model.BankingAccount;
import fr.finanting.server.model.embeddable.Address;
import fr.finanting.server.model.embeddable.BankDetails;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import fr.finanting.server.model.Group;
import fr.finanting.server.model.Role;
import fr.finanting.server.model.User;

import java.util.ArrayList;
import java.util.List;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;

/**
 * Factory to help during tests
 */
public class TestObjectFactory {

    public static final Integer NUMBER_MAX = 20_000_000;

    public static final int LENGTH_STANDARD = 30;
    public static final int LENGTH_URI = 15;
    public static final int LENGTH_EMAIL_NAME = 10;
    public static final int LENGTH_DOMAIN = 10;

    private List<String> listRandomString = new ArrayList<>();
    private List<String> listRandomEmail = new ArrayList<>();
    private List<Integer> listRandomNumber = new ArrayList<>();
    private List<String> listRandomName = new ArrayList<>();

    protected final Faker faker = new Faker();
    
    public void resetAllList(){
        
        this.listRandomString = new ArrayList<>();
        this.listRandomNumber = new ArrayList<>();
        this.listRandomEmail = new ArrayList<>();
        this.listRandomName = new ArrayList<>();
    }

    public Boolean getRandomBoolean(){

        return this.faker.random().nextBoolean();
    }

    public String getUniqueRandomAlphanumericString(final int length){

        boolean isNotUnique = true;
        String randomString = "";

        while (isNotUnique){
            randomString = RandomStringUtils.randomAlphanumeric(length);
            isNotUnique = listRandomString.contains(randomString);
        }

        listRandomString.add(randomString);

        return randomString;
    }

    public String getUniqueRandomAlphanumericString(){

        boolean isNotUnique = true;
        String randomString = "";

        while (isNotUnique){
            randomString = this.getRandomAlphanumericString();
            isNotUnique = listRandomString.contains(randomString);
        }

        listRandomString.add(randomString);

        return randomString;
    }

    public String getRandomAlphanumericString(final int length){

        return RandomStringUtils.randomAlphanumeric(length);
    }

    public String getRandomAlphanumericString(){

        return RandomStringUtils.randomAlphanumeric(LENGTH_STANDARD);
    }

    public String getUniqueRandomURI(){

        boolean isNotUnique = true;
        String randomUri = "";

        while (isNotUnique){
            randomUri = this.faker.internet().url();
            isNotUnique = listRandomString.contains(randomUri);
        }

        listRandomString.add(randomUri);

        return randomUri;
    }

    public Name getUniqueRandomName(){

        boolean isNotUnique = true;
        Name randomName = this.faker.name();

        while (isNotUnique){
            randomName = this.faker.name();
            isNotUnique = listRandomName.contains(randomName.username());
        }

        listRandomName.add(randomName.username());

        return randomName;
    }

    public String getUniqueRandomEmail(){
        boolean isNotUnique = true;
        String email = "";

        while (isNotUnique){
            email = this.faker.internet().emailAddress();
            isNotUnique = listRandomEmail.contains(email);
        }

        listRandomEmail.add(email);

        return email;

    }

    public Integer getUniqueRandomInteger(final Integer min, final Integer max){
        boolean isNotUnique = true;
        Integer randomNumber = 0;

        while (isNotUnique){
            randomNumber = this.getRandomInteger(min, max);
            isNotUnique = listRandomNumber.contains(randomNumber);
        }

        listRandomNumber.add(randomNumber);

        return randomNumber;
    }

    public Integer getUniqueRandomInteger(final Integer max){
        boolean isNotUnique = true;
        Integer randomNumber = 0;

        while (isNotUnique){
            randomNumber = this.getRandomInteger(max);
            isNotUnique = listRandomNumber.contains(randomNumber);
        }

        listRandomNumber.add(randomNumber);

        return randomNumber;
    }

    public Integer getUniqueRandomInteger(){
        return this.getUniqueRandomInteger(NUMBER_MAX);
    }

    public int getRandomInteger(final Integer max){
        final double random = Math.random() * max;
        return (int) random;
    }

    public long getRandomLong(final Integer max){
        final double random = Math.random() * max;
        return (long) random;
    }

    public Integer getRandomInteger(){
        return this.getRandomInteger(NUMBER_MAX);
    }

    public Long getRandomLong(){
        return this.getRandomLong(NUMBER_MAX);
    }

    public Integer getRandomInteger(final Integer min, final Integer max){
        return (int) (min + (Math.random() * (max - min)));
    }

    public User getUser(){
        final User user = new User();
        user.setEmail(this.faker.internet().emailAddress());
        final String firstName = StringUtils.capitalize(this.faker.name().firstName().toLowerCase());
        user.setFirstName(firstName);
        user.setLastName(this.faker.name().lastName().toUpperCase());
        user.setUserName(this.faker.name().username().toLowerCase());
        user.setPassword(this.getRandomAlphanumericString());
        
        final List<Role> roles = new ArrayList<>();
        roles.add(Role.USER);
        user.setRoles(roles);

        return user;
    }

    /**
     * Method to get a new group
     */
    public Group getGroup(){
        final Group group = new Group();
        group.setGroupName(this.faker.company().name());
        final User user = this.getUser();
        group.setUserAdmin(user);
        final List<User> users = new ArrayList<>();
        users.add(user);
        group.setUsers(users);
        return group;
    }

    private BankingAccount getAccount(final User user, final Group group){
        final BankingAccount account = new BankingAccount();

        final com.github.javafaker.Address addressFaker = this.faker.address();
        final Address address = new Address();
        address.setCity(addressFaker.city());
        address.setStreet(addressFaker.streetAddress());
        address.setZipCode(addressFaker.zipCode());
        account.setAddress(address);

        final BankDetails bankDetailsDetails = new BankDetails();
        bankDetailsDetails.setAccountNumber(this.getRandomAlphanumericString());
        bankDetailsDetails.setIban(this.getRandomAlphanumericString());
        bankDetailsDetails.setBankName(this.getRandomAlphanumericString());
        account.setBankDetails(bankDetailsDetails);

        account.setAbbreviation(this.getRandomAlphanumericString(6));
        account.setInitialBalance(0);
        account.setLabel(this.getRandomAlphanumericString());

        account.setGroup(group);
        account.setUser(user);

        return account;
    }

    public BankingAccount getAccount(final User user){
        return this.getAccount(user, null);
    }

    public BankingAccount getAccount(final Group group){
        return this.getAccount(null, group);
    }

}