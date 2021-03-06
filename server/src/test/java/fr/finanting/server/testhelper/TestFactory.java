package fr.finanting.server.testhelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import fr.finanting.server.model.BankingAccount;
import fr.finanting.server.model.BankingTransaction;
import fr.finanting.server.model.Category;
import fr.finanting.server.model.CategoryType;
import fr.finanting.server.model.Classification;
import fr.finanting.server.model.Currency;
import fr.finanting.server.model.Group;
import fr.finanting.server.model.Role;
import fr.finanting.server.model.Third;
import fr.finanting.server.model.User;
import fr.finanting.server.model.embeddable.Address;
import fr.finanting.server.model.embeddable.BankDetails;
import fr.finanting.server.model.embeddable.Contact;
import fr.finanting.server.repository.BankingAccountRepository;
import fr.finanting.server.repository.BankingTransactionRepository;
import fr.finanting.server.repository.CategoryRepository;
import fr.finanting.server.repository.ClassificationRepository;
import fr.finanting.server.repository.CurrencyRepository;
import fr.finanting.server.repository.GroupRepository;
import fr.finanting.server.repository.ThirdRepository;
import fr.finanting.server.repository.UserRepository;

@Component
public class TestFactory {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BankingAccountRepository bankingAccountRepository;

    @Autowired
    private BankingTransactionRepository bankingTransactionRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private ThirdRepository thirdRepository;

    @Autowired
    private UserRepository userRepository;

    private final Faker faker = new Faker();

    public static final Integer NUMBER_MAX = 20_000_000;

    public static final int LENGTH_STANDARD = 30;

    private List<String> listRandomString = new ArrayList<>();
    private List<String> listRandomCaseSensitiveString = new ArrayList<>();
    private List<String> listRandomEmail = new ArrayList<>();
    private List<String> listRandomName = new ArrayList<>();

    public void resetAllList(){

        this.listRandomString = new ArrayList<>();
        this.listRandomCaseSensitiveString = new ArrayList<>();
        this.listRandomEmail = new ArrayList<>();
        this.listRandomName = new ArrayList<>();
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

    public String getUniqueRandomAlphanumericStringCaseSensitive(final int length){
        boolean isNotUnique = true;
        String randomString = "";

        while (isNotUnique){
            randomString = RandomStringUtils.randomAlphanumeric(length).toLowerCase();
            isNotUnique = listRandomCaseSensitiveString.contains(randomString);
        }

        listRandomCaseSensitiveString.add(randomString);

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

    public int getRandomInteger(final Integer max){
        final double random = Math.random() * max;
        return (int) random;
    }

    public double getRandomDouble(){
        return this.faker.random().nextDouble();
    }

    public int getRandomInteger(){
        return this.getRandomInteger(NUMBER_MAX);
    }

    public User getUser(){
        final User user = new User();
        user.setEmail(this.faker.internet().emailAddress());
        final String firstName = StringUtils.capitalize(this.faker.name().firstName().toLowerCase());
        user.setFirstName(firstName);
        user.setLastName(this.faker.name().lastName().toUpperCase());
        user.setUserName(this.faker.name().username().toLowerCase());
        user.setPassword(this.passwordEncoder.encode(this.getRandomAlphanumericString()));
        
        final List<Role> roles = new ArrayList<>();
        roles.add(Role.USER);
        user.setRoles(roles);

        return this.userRepository.save(user);
    }

    public Group getGroup(){
        final Group group = new Group();
        group.setGroupName(this.faker.company().name());
        final User user = this.getUser();
        group.setUserAdmin(user);
        final List<User> users = new ArrayList<>();
        users.add(user);
        group.setUsers(users);
        return this.groupRepository.save(group);
    }

    public Group getGroup(final User userToAdd){
        final Group group = new Group();
        group.setGroupName(this.getUniqueRandomAlphanumericStringCaseSensitive(10));
        final User user = this.getUser();
        group.setUserAdmin(user);
        final List<User> users = new ArrayList<>();
        users.add(user);
        users.add(userToAdd);
        group.setUsers(users);
        return this.groupRepository.save(group);
    }

    private Address getAddress(){
        final com.github.javafaker.Address addressFaker = this.faker.address();
        final Address address = new Address();
        address.setCity(addressFaker.city());
        address.setStreet(addressFaker.streetAddress());
        address.setZipCode(addressFaker.zipCode());
        return address;
    }

    private BankDetails getBankDetails(){
        final BankDetails bankDetails = new BankDetails();
        bankDetails.setAccountNumber(this.getRandomAlphanumericString());
        bankDetails.setIban(this.getRandomAlphanumericString());
        bankDetails.setBankName(this.getRandomAlphanumericString());
        return bankDetails;
    }

    public Currency getCurrency(final boolean isDefault){
        final Currency currency = new Currency();
        currency.setDecimalPlaces(this.getRandomInteger());
        currency.setDefaultCurrency(isDefault);
        currency.setIsoCode(this.getUniqueRandomAlphanumericStringCaseSensitive(3).toUpperCase());
        currency.setSymbol(this.getUniqueRandomAlphanumericString(3).toUpperCase());

        final String label = StringUtils.capitalize(this.getUniqueRandomAlphanumericString().toLowerCase());
        currency.setLabel(label);

        currency.setRate(this.getRandomInteger());

        return this.currencyRepository.save(currency);
    }

    public Currency getCurrency(){
        return this.getCurrency(false);
    }

    private BankingAccount getBankingAccount(final User user, final Group group){
        final BankingAccount bankingAccount = new BankingAccount();

        bankingAccount.setAddress(this.getAddress());
        bankingAccount.setBankDetails(this.getBankDetails());

        bankingAccount.setAbbreviation(this.getRandomAlphanumericString(6).toUpperCase());
        bankingAccount.setInitialBalance(0.0);
        bankingAccount.setLabel(this.getRandomAlphanumericString());

        bankingAccount.setGroup(group);
        bankingAccount.setUser(user);

        final Currency defaultCurrency = this.getCurrency();
        bankingAccount.setDefaultCurrency(defaultCurrency);

        return this.bankingAccountRepository.save(bankingAccount);
    }

    public BankingAccount getBankingAccount(final User user){
        return this.getBankingAccount(user, null);
    }

    public BankingAccount getBankingAccount(final Group group){
        return this.getBankingAccount(null, group);
    }

    private Category getCategory(final User user, final Group group, final boolean isExpense){
        final Category category = new Category();

        category.setAbbreviation(this.getRandomAlphanumericString(6).toUpperCase());
        CategoryType categoryType;

        if(isExpense){
            categoryType = CategoryType.EXPENSE;
        } else {
            categoryType = CategoryType.REVENUE;
        }

        category.setCategoryType(categoryType);
        category.setDescription(this.faker.superhero().descriptor());
        category.setLabel(this.faker.company().catchPhrase());
        category.setGroup(group);
        category.setUser(user);

        return this.categoryRepository.save(category);
    }

    public Category getCategory(final User user, final boolean isExpense){
        return this.getCategory(user, null, isExpense);
    }

    public Category getCategory(final Group group, final boolean isExpense){
        return this.getCategory(null, group, isExpense);
    }

    private Classification getClassification(final User user, final Group group){

        final Classification classification = new Classification();

        classification.setAbbreviation(this.getRandomAlphanumericString(6).toUpperCase());
        classification.setDescription(this.faker.superhero().descriptor());
        classification.setLabel(this.faker.company().catchPhrase());
        classification.setUser(user);
        classification.setGroup(group);

        return this.classificationRepository.save(classification);

    }

    public Classification getClassification(final User user){
        return this.getClassification(user, null);
    }

    public Classification getClassification(final Group group){
        return this.getClassification(null, group);
    }

    private Contact getContact(){
        final Contact contact = new Contact();
        contact.setEmail(this.faker.internet().emailAddress());
        contact.setHomePhone(this.faker.phoneNumber().phoneNumber());
        contact.setPortablePhone(this.faker.phoneNumber().cellPhone());
        contact.setWebsite(this.faker.internet().url());
        return contact;
    }

    private Third getThird(final User user, final Group group, final Category category){
        final Third third = new Third();

        third.setAbbreviation(this.getRandomAlphanumericString(5).toUpperCase());
        third.setDescription(this.faker.superhero().descriptor());
        third.setLabel(this.faker.company().name());
        third.setAddress(this.getAddress());
        third.setBankDetails(this.getBankDetails());
        third.setContact(this.getContact());
        third.setDefaultCategory(category);
        third.setDefaultCurrency(this.getCurrency());

        third.setUser(user);
        third.setGroup(group);

        return this.thirdRepository.save(third);
    }

    public Third getThird(final User user){
        return this.getThird(user, null, null);
    }

    public Third getThird(final User user, final Category category){
        return this.getThird(user, null, category);
    }

    public Third getThird(final Group group){
        return this.getThird(null, group, null);
    }

    public Third getThird(final Group group, final Category category){
        return this.getThird(null, group, category);
    }

    public BankingTransaction getBankingTransaction(final User user, final Group group, final BankingAccount bankingAccount, final Boolean isLinked){
        final BankingTransaction bankingTransaction = new BankingTransaction();
        bankingTransaction.setAccount(bankingAccount);
        Double amount = this.getRandomDouble();
        bankingTransaction.setAmount(amount);
        bankingTransaction.setAmountDate(new Date());
        bankingTransaction.setTransactionDate(new Date());
        bankingTransaction.setCategory(this.getCategory(user, group, true));
        bankingTransaction.setClassification(this.getClassification(user, group));
        bankingTransaction.setCreateTimestamp(new Date());
        final Currency currency = this.getCurrency();
        bankingTransaction.setCurrency(this.getCurrency());
        final Double amountCurrency = amount * currency.getRate();
        bankingTransaction.setCurrencyAmount(amountCurrency);
        bankingTransaction.setDescription(this.faker.chuckNorris().fact());
        bankingTransaction.setThird(this.getThird(user, group, null));
        if(isLinked){
            final BankingAccount linkedBankingAccount = this.getBankingAccount(user, group);
            bankingTransaction.setLinkedAccount(linkedBankingAccount);

            BankingTransaction mirrorTransaction = new BankingTransaction();
            mirrorTransaction.setAccount(linkedBankingAccount);
            mirrorTransaction.setLinkedAccount(bankingAccount);

            mirrorTransaction.setTransactionDate(new Date());
            mirrorTransaction.setAmountDate(bankingTransaction.getAmountDate());

            mirrorTransaction.setCategory(bankingTransaction.getCategory());
            mirrorTransaction.setClassification(bankingTransaction.getClassification());
            mirrorTransaction.setCreateTimestamp(bankingTransaction.getCreateTimestamp());

            mirrorTransaction.setCurrency(this.getCurrency());
            mirrorTransaction.setCurrencyAmount(amountCurrency);

            amount = amountCurrency
                * Double.valueOf(bankingTransaction.getAccount().getDefaultCurrency().getRate())
                / Double.valueOf(bankingTransaction.getLinkedAccount().getDefaultCurrency().getRate());

                mirrorTransaction.setAmount(amount);

            mirrorTransaction.setDescription(bankingTransaction.getDescription());
            mirrorTransaction.setThird(bankingTransaction.getThird());

            mirrorTransaction = this.bankingTransactionRepository.save(mirrorTransaction);
            bankingTransaction.setMirrorTransaction(mirrorTransaction);
        }

        return this.bankingTransactionRepository.save(bankingTransaction);
    }

    public BankingTransaction getBankingTransaction(final User user, final BankingAccount bankingAccount, final Boolean isLinked){
        return this.getBankingTransaction(user, null, bankingAccount, isLinked);
    }

    public BankingTransaction getBankingTransaction(final Group group, final BankingAccount bankingAccount, final Boolean isLinked){
        return this.getBankingTransaction(null, group, bankingAccount, isLinked);
    }

    public BankingTransaction getBankingTransaction(final User user, final Boolean isLinked){
        final BankingAccount bankingAccount = this.getBankingAccount(user);
        return this.getBankingTransaction(user, null, bankingAccount, isLinked);
    }

    public BankingTransaction getBankingTransaction(final Group group, final Boolean isLinked){
        final BankingAccount bankingAccount = this.getBankingAccount(group);
        return this.getBankingTransaction(null, group, bankingAccount, isLinked);
    }
    
}
