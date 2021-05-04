package fr.finanting.server.service.bankingtransactionservice;

import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.finanting.server.testhelper.AbstractMotherIntegrationTest;
import fr.finanting.server.dto.BankingTransactionDTO;
import fr.finanting.server.exception.BadAssociationElementException;
import fr.finanting.server.exception.BankingAccountNotExistException;
import fr.finanting.server.exception.BankingTransactionNotExistException;
import fr.finanting.server.exception.CategoryNotExistException;
import fr.finanting.server.exception.ClassificationNotExistException;
import fr.finanting.server.exception.CurrencyNotExistException;
import fr.finanting.server.exception.NotUserElementException;
import fr.finanting.server.exception.ThirdNotExistException;
import fr.finanting.server.exception.UserNotInGroupException;
import fr.finanting.server.model.BankingAccount;
import fr.finanting.server.model.BankingTransaction;
import fr.finanting.server.model.Group;
import fr.finanting.server.model.User;
import fr.finanting.server.parameter.UpdateBankingTransactionParameter;
import fr.finanting.server.repository.BankingAccountRepository;
import fr.finanting.server.repository.BankingTransactionRepository;
import fr.finanting.server.repository.CategoryRepository;
import fr.finanting.server.repository.ClassificationRepository;
import fr.finanting.server.repository.CurrencyRepository;
import fr.finanting.server.repository.ThirdRepository;
import fr.finanting.server.repository.UserRepository;
import fr.finanting.server.service.implementation.BankingTransactionServiceImpl;

public class TestUpdateBankingTransaction extends AbstractMotherIntegrationTest {

    @Autowired
    private BankingTransactionRepository bankingTransactionRepository;
    
    @Autowired
    private BankingAccountRepository bankingAccountRepository;

    @Autowired
    private ThirdRepository thirdRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ClassificationRepository classificationRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private UserRepository userRepository;

    private BankingTransactionServiceImpl bankingTransactionServiceImpl;

    private UpdateBankingTransactionParameter userUpdateBankingTransactionParameter;
    private UpdateBankingTransactionParameter groupUpdateBankingTransactionParameter;

    private User user;
    private Group group;

    @Override
    protected void initDataBeforeEach() throws Exception {
        this.bankingTransactionServiceImpl = new BankingTransactionServiceImpl(this.bankingTransactionRepository,
                                                                                this.bankingAccountRepository,
                                                                                this.thirdRepository,
                                                                                this.categoryRepository,
                                                                                this.classificationRepository,
                                                                                this.currencyRepository,
                                                                                this.userRepository);

        this.user = this.testFactory.getUser();
        this.group = this.testFactory.getGroup(user);

        this.userUpdateBankingTransactionParameter = new UpdateBankingTransactionParameter();
        this.groupUpdateBankingTransactionParameter = new UpdateBankingTransactionParameter();

        final BankingAccount newUserBankingAccount = this.testFactory.getBankingAccount(this.user);
        final BankingAccount newOtherUserBankingAccount = this.testFactory.getBankingAccount(this.user);
        final BankingAccount newGroupBankingAccount = this.testFactory.getBankingAccount(this.group);
        final BankingAccount newOtherGroupBankingAccount = this.testFactory.getBankingAccount(this.group);

        final Double amount = this.testFactory.getRandomDouble();
        final Double currencyAmount = newUserBankingAccount.getDefaultCurrency().getRate() * amount;

        this.userUpdateBankingTransactionParameter.setAccountId(newUserBankingAccount.getId());
        this.userUpdateBankingTransactionParameter.setLinkedAccountId(newOtherUserBankingAccount.getId());
        this.userUpdateBankingTransactionParameter.setThirdId(this.testFactory.getThird(this.user).getId());
        this.userUpdateBankingTransactionParameter.setCategoryId(this.testFactory.getCategory(this.user, true).getId());
        this.userUpdateBankingTransactionParameter.setClassificationId(this.testFactory.getClassification(this.user).getId());
        this.userUpdateBankingTransactionParameter.setTransactionDate(new Date());
        this.userUpdateBankingTransactionParameter.setAmountDate(new Date());
        this.userUpdateBankingTransactionParameter.setAmount(amount);
        this.userUpdateBankingTransactionParameter.setCurrencyAmount(currencyAmount);
        this.userUpdateBankingTransactionParameter.setCurrencyId(this.testFactory.getCurrency().getId());
        this.userUpdateBankingTransactionParameter.setDescription(this.faker.chuckNorris().fact());

        this.groupUpdateBankingTransactionParameter.setAccountId(newGroupBankingAccount.getId());
        this.groupUpdateBankingTransactionParameter.setLinkedAccountId(newOtherGroupBankingAccount.getId());
        this.groupUpdateBankingTransactionParameter.setThirdId(this.testFactory.getThird(this.group).getId());
        this.groupUpdateBankingTransactionParameter.setCategoryId(this.testFactory.getCategory(this.group, true).getId());
        this.groupUpdateBankingTransactionParameter.setClassificationId(this.testFactory.getClassification(this.group).getId());
        this.groupUpdateBankingTransactionParameter.setTransactionDate(new Date());
        this.groupUpdateBankingTransactionParameter.setAmountDate(new Date());
        this.groupUpdateBankingTransactionParameter.setAmount(amount);
        this.groupUpdateBankingTransactionParameter.setCurrencyAmount(currencyAmount);
        this.groupUpdateBankingTransactionParameter.setCurrencyId(this.testFactory.getCurrency().getId());
        this.groupUpdateBankingTransactionParameter.setDescription(this.faker.chuckNorris().fact());

    }

    private void checkData(final BankingTransactionDTO bankingTransactionDTO, final BankingTransaction bankingTransaction, final UpdateBankingTransactionParameter parameter){
        
        Assertions.assertEquals(parameter.getAccountId(), bankingTransactionDTO.getBankingAccountDTO().getId());
        Assertions.assertEquals(parameter.getAccountId(), bankingTransaction.getAccount().getId());

        if(parameter.getLinkedAccountId() == null){
            Assertions.assertNull(bankingTransactionDTO.getLinkedBankingAccountDTO());
            Assertions.assertNull(bankingTransaction.getLinkedAccount());
            Assertions.assertNull(bankingTransaction.getMirrorTransaction());
        } else {
            Assertions.assertEquals(parameter.getLinkedAccountId(), bankingTransactionDTO.getLinkedBankingAccountDTO().getId());
            Assertions.assertEquals(parameter.getLinkedAccountId(), bankingTransaction.getLinkedAccount().getId());

            final BankingTransaction mirrorTransaction = bankingTransaction.getMirrorTransaction();
            Assertions.assertEquals(parameter.getAccountId(), mirrorTransaction.getLinkedAccount().getId());
            Assertions.assertEquals(parameter.getLinkedAccountId(), mirrorTransaction.getAccount().getId());

            Assertions.assertEquals(bankingTransaction.getAccount().getId(), mirrorTransaction.getLinkedAccount().getId());
            
            if(parameter.getThirdId() == null){
                Assertions.assertNull(mirrorTransaction.getThird());
            } else {
                Assertions.assertEquals(parameter.getThirdId(), mirrorTransaction.getThird().getId());
            }

            if(parameter.getCategoryId() == null){
                Assertions.assertNull(mirrorTransaction.getCategory());
            } else {
                Assertions.assertEquals(parameter.getCategoryId(), mirrorTransaction.getCategory().getId());
            }

            if(parameter.getClassificationId() == null){
                Assertions.assertNull(mirrorTransaction.getClassification());
            } else {
                Assertions.assertEquals(parameter.getClassificationId(), mirrorTransaction.getClassification().getId());
            }

            Assertions.assertEquals(parameter.getTransactionDate(), mirrorTransaction.getTransactionDate());
            Assertions.assertEquals(parameter.getAmountDate(), mirrorTransaction.getAmountDate());

            Double mirrorAmount;
            final Double mirrorCurrencyAmount = parameter.getCurrencyAmount() * -1;

            if(bankingTransaction.getLinkedAccount().getDefaultCurrency().equals(bankingTransaction.getAccount().getDefaultCurrency())){
                mirrorAmount = bankingTransaction.getAmount() * -1;
            } else {
                mirrorAmount = mirrorCurrencyAmount
                    * Double.valueOf(bankingTransaction.getAccount().getDefaultCurrency().getRate())
                    / Double.valueOf(bankingTransaction.getLinkedAccount().getDefaultCurrency().getRate());
            }

            Assertions.assertEquals(mirrorAmount, mirrorTransaction.getAmount());
            Assertions.assertEquals(mirrorCurrencyAmount, mirrorTransaction.getCurrencyAmount());

            Assertions.assertEquals(parameter.getCurrencyId(), mirrorTransaction.getCurrency().getId());
            Assertions.assertEquals(parameter.getDescription(), mirrorTransaction.getDescription());

            if(parameter.getThirdId() == null){
                Assertions.assertNull(mirrorTransaction.getThird());
            } else {
                Assertions.assertEquals(parameter.getThirdId(), mirrorTransaction.getThird().getId());
            }
    
            if(parameter.getCategoryId() == null){
                Assertions.assertNull(mirrorTransaction.getCategory());
            } else {
                Assertions.assertEquals(parameter.getCategoryId(), mirrorTransaction.getCategory().getId());
            }
    
            if(parameter.getClassificationId() == null){
                Assertions.assertNull(mirrorTransaction.getClassification());
            } else {
                Assertions.assertEquals(parameter.getClassificationId(), mirrorTransaction.getClassification().getId());
            }

        }

        if(parameter.getThirdId() == null){
            Assertions.assertNull(bankingTransactionDTO.getThirdDTO());
            Assertions.assertNull(bankingTransaction.getThird());
        } else {
            Assertions.assertEquals(parameter.getThirdId(), bankingTransactionDTO.getThirdDTO().getId());
            Assertions.assertEquals(parameter.getThirdId(), bankingTransaction.getThird().getId());
        }

        if(parameter.getCategoryId() == null){
            Assertions.assertNull(bankingTransactionDTO.getCategoryDTO());
            Assertions.assertNull(bankingTransaction.getCategory());
        } else {
            Assertions.assertEquals(parameter.getCategoryId(), bankingTransactionDTO.getCategoryDTO().getId());
            Assertions.assertEquals(parameter.getCategoryId(), bankingTransaction.getCategory().getId());
        }

        if(parameter.getClassificationId() == null){
            Assertions.assertNull(bankingTransactionDTO.getClassificationDTO());
            Assertions.assertNull(bankingTransaction.getClassification());
        } else {
            Assertions.assertEquals(parameter.getClassificationId(), bankingTransactionDTO.getClassificationDTO().getId());
            Assertions.assertEquals(parameter.getClassificationId(), bankingTransaction.getClassification().getId());
        }

        Assertions.assertEquals(parameter.getTransactionDate(), bankingTransactionDTO.getTransactionDate());
        Assertions.assertEquals(parameter.getTransactionDate(), bankingTransaction.getTransactionDate());

        Assertions.assertEquals(parameter.getAmountDate(), bankingTransactionDTO.getAmountDate());
        Assertions.assertEquals(parameter.getAmountDate(), bankingTransaction.getAmountDate());

        Assertions.assertEquals(parameter.getAmount(), bankingTransactionDTO.getAmount());
        Assertions.assertEquals(parameter.getAmount(), bankingTransaction.getAmount());

        Assertions.assertEquals(parameter.getCurrencyAmount(), bankingTransactionDTO.getCurrencyAmount());
        Assertions.assertEquals(parameter.getCurrencyAmount(), bankingTransaction.getCurrencyAmount());

        Assertions.assertEquals(parameter.getCurrencyId(), bankingTransactionDTO.getCurrencyDTO().getId());
        Assertions.assertEquals(parameter.getCurrencyId(), bankingTransaction.getCurrency().getId());

        Assertions.assertEquals(parameter.getDescription(), bankingTransactionDTO.getDescription());
        Assertions.assertEquals(parameter.getDescription(), bankingTransaction.getDescription());

    }

    @Test
    public void testUpdateUserTransactionWithoutLinkedAccountToAnotherAccountAndLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName());

        userBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, userBankingTransaction, this.userUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateUserTransactionWithoutLinkedAccountToAnotherAccountAndWithoutLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setLinkedAccountId(null);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName());

        userBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, userBankingTransaction, this.userUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateUserTransactionWithoutLinkedAccountToAnotherLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setAccountId(userBankingTransaction.getAccount().getId());

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName());

        userBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, userBankingTransaction, this.userUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateUserTransactionWithLinkedAccountToAnotherAccountAndLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, true);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName());

        userBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, userBankingTransaction, this.userUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateUserTransactionWithLinkedAccountToAnotherAccountAndWithoutLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, true);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setLinkedAccountId(null);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName());

        userBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, userBankingTransaction, this.userUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateUserTransactionWithLinkedAccountToAnotherLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, true);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setAccountId(userBankingTransaction.getAccount().getId());

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName());

        userBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, userBankingTransaction, this.userUpdateBankingTransactionParameter);

    }

        @Test
    public void testUpdateGroupTransactionWithoutLinkedAccountToAnotherAccountAndLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction groupBankingTransaction = this.testFactory.getBankingTransaction(this.group, false);

        final Integer id = groupBankingTransaction.getId();

        this.groupUpdateBankingTransactionParameter.setId(id);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.groupUpdateBankingTransactionParameter, this.user.getUserName());

        groupBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, groupBankingTransaction, this.groupUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateGroupTransactionWithoutLinkedAccountToAnotherAccountAndWithoutLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction groupBankingTransaction = this.testFactory.getBankingTransaction(this.group, false);

        final Integer id = groupBankingTransaction.getId();

        this.groupUpdateBankingTransactionParameter.setId(id);
        this.groupUpdateBankingTransactionParameter.setLinkedAccountId(null);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.groupUpdateBankingTransactionParameter, this.user.getUserName());

        groupBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, groupBankingTransaction, this.groupUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateGroupTransactionWithoutLinkedAccountToAnotherLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction groupBankingTransaction = this.testFactory.getBankingTransaction(this.group, false);

        final Integer id = groupBankingTransaction.getId();

        this.groupUpdateBankingTransactionParameter.setId(id);
        this.groupUpdateBankingTransactionParameter.setAccountId(groupBankingTransaction.getAccount().getId());

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.groupUpdateBankingTransactionParameter, this.user.getUserName());

        groupBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, groupBankingTransaction, this.groupUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateGroupTransactionData()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction groupBankingTransaction = this.testFactory.getBankingTransaction(this.group, false);

        final Integer id = groupBankingTransaction.getId();

        this.groupUpdateBankingTransactionParameter.setId(id);
        this.groupUpdateBankingTransactionParameter.setAccountId(groupBankingTransaction.getAccount().getId());
        this.groupUpdateBankingTransactionParameter.setLinkedAccountId(null);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.groupUpdateBankingTransactionParameter, this.user.getUserName());

        groupBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, groupBankingTransaction, this.groupUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateGroupTransactionWithLinkedAccountToAnotherAccountAndLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction groupBankingTransaction = this.testFactory.getBankingTransaction(this.group, true);

        final Integer id = groupBankingTransaction.getId();

        this.groupUpdateBankingTransactionParameter.setId(id);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.groupUpdateBankingTransactionParameter, this.user.getUserName());

        groupBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, groupBankingTransaction, this.groupUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateGroupTransactionWithLinkedAccountToAnotherAccountAndWithoutLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction groupBankingTransaction = this.testFactory.getBankingTransaction(this.group, true);

        final Integer id = groupBankingTransaction.getId();
        final Integer mirrorTransactionId = groupBankingTransaction.getMirrorTransaction().getId();

        this.groupUpdateBankingTransactionParameter.setId(id);
        this.groupUpdateBankingTransactionParameter.setLinkedAccountId(null);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.groupUpdateBankingTransactionParameter, this.user.getUserName());

        groupBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();
        
        final Optional<BankingTransaction> optionalMirrorBankingTransaction = this.bankingTransactionRepository.findById(mirrorTransactionId);

        Assertions.assertFalse(optionalMirrorBankingTransaction.isPresent());

        this.checkData(bankingTransactionDTO, groupBankingTransaction, this.groupUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateGroupTransactionWithLinkedAccountToAnotherLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction groupBankingTransaction = this.testFactory.getBankingTransaction(this.group, true);

        final Integer id = groupBankingTransaction.getId();

        this.groupUpdateBankingTransactionParameter.setId(id);
        this.groupUpdateBankingTransactionParameter.setAccountId(groupBankingTransaction.getAccount().getId());

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.groupUpdateBankingTransactionParameter, this.user.getUserName());

        groupBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, groupBankingTransaction, this.groupUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateUserTransactionDataWithoutLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setAccountId(userBankingTransaction.getAccount().getId());
        this.userUpdateBankingTransactionParameter.setLinkedAccountId(null);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName());

        userBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, userBankingTransaction, this.userUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateUserTransactionDataWithLinkedAccount()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, true);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setAccountId(userBankingTransaction.getAccount().getId());
        this.userUpdateBankingTransactionParameter.setLinkedAccountId(userBankingTransaction.getLinkedAccount().getId());

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName());

        userBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, userBankingTransaction, this.userUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateUserTransactionDataWithoutThird()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setAccountId(userBankingTransaction.getAccount().getId());
        this.userUpdateBankingTransactionParameter.setLinkedAccountId(null);

        this.userUpdateBankingTransactionParameter.setThirdId(null);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName());

        userBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, userBankingTransaction, this.userUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateUserTransactionDataWithoutCategory()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setAccountId(userBankingTransaction.getAccount().getId());
        this.userUpdateBankingTransactionParameter.setLinkedAccountId(null);

        this.userUpdateBankingTransactionParameter.setCategoryId(null);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName());

        userBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, userBankingTransaction, this.userUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateUserTransactionDataWithoutClassification()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setAccountId(userBankingTransaction.getAccount().getId());
        this.userUpdateBankingTransactionParameter.setLinkedAccountId(null);

        this.userUpdateBankingTransactionParameter.setClassificationId(null);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName());

        userBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, userBankingTransaction, this.userUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateUserTransactionDataWithoutDescription()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException,
            UserNotInGroupException, ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException,
            CurrencyNotExistException, NotUserElementException{
        BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setAccountId(userBankingTransaction.getAccount().getId());
        this.userUpdateBankingTransactionParameter.setLinkedAccountId(null);

        this.userUpdateBankingTransactionParameter.setDescription(null);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName());

        userBankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, userBankingTransaction, this.userUpdateBankingTransactionParameter);

    }

    @Test
    public void testUpdateNoExistedTransaction() {

        this.userUpdateBankingTransactionParameter.setId(this.testFactory.getRandomInteger());

        Assertions.assertThrows(BankingTransactionNotExistException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName()));

    }

    @Test
    public void testUpdateTransactionWithBankingAccountNotExist() {
        final BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);
        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setAccountId(this.testFactory.getRandomInteger());

        Assertions.assertThrows(BankingAccountNotExistException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName()));

    }

    @Test
    public void testUpdateTransactionWithLinkedBankingAccountNotExist() {
        final BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);
        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setAccountId(userBankingTransaction.getAccount().getId());
        this.userUpdateBankingTransactionParameter.setLinkedAccountId(this.testFactory.getRandomInteger());

        Assertions.assertThrows(BankingAccountNotExistException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName()));

    }

    @Test
    public void testUpdateUserTransactionWithOtherUser() {
        final BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setAccountId(userBankingTransaction.getAccount().getId());

        final User otherUser = this.testFactory.getUser();

        Assertions.assertThrows(NotUserElementException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, otherUser.getUserName()));

    }

    @Test
    public void testUpdateUserTransactionWithOtherUserBankingAccount() {
        final BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final User otherUser = this.testFactory.getUser();

        final BankingAccount otherUserBankingAccount = this.testFactory.getBankingAccount(otherUser);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setAccountId(otherUserBankingAccount.getId());

        Assertions.assertThrows(NotUserElementException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName()));

    }

    @Test
    public void testUpdateUserTransactionWithOtherUserLinkedBankingAccount() {
        final BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final User otherUser = this.testFactory.getUser();

        final BankingAccount otherUserBankingAccount = this.testFactory.getBankingAccount(otherUser);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setLinkedAccountId(otherUserBankingAccount.getId());

        Assertions.assertThrows(NotUserElementException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName()));

    }

    @Test
    public void testUpdateUserTransactionWithOtherUserThird() {
        final BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final User otherUser = this.testFactory.getUser();

        final Integer id = userBankingTransaction.getId();
        final Integer thirdId = this.testFactory.getThird(otherUser).getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setThirdId(thirdId);

        Assertions.assertThrows(NotUserElementException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName()));

    }

    @Test
    public void testUpdateUserTransactionWithOtherUserCategory() {
        final BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final User otherUser = this.testFactory.getUser();

        final Integer id = userBankingTransaction.getId();
        final Integer categoryId = this.testFactory.getCategory(otherUser, true).getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setCategoryId(categoryId);

        Assertions.assertThrows(NotUserElementException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName()));

    }

    @Test
    public void testUpdateUserTransactionWithOtherUserClassification() {
        final BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final User otherUser = this.testFactory.getUser();

        final Integer id = userBankingTransaction.getId();
        final Integer classificationId = this.testFactory.getClassification(otherUser).getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setClassificationId(classificationId);

        Assertions.assertThrows(NotUserElementException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName()));

    }

    @Test
    public void testUpdateUserTransactionWithGroupThird() {
        final BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setThirdId(this.groupUpdateBankingTransactionParameter.getThirdId());

        Assertions.assertThrows(BadAssociationElementException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName()));

    }

    @Test
    public void testUpdateUserTransactionWithGroupCategory() {
        final BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setCategoryId(this.groupUpdateBankingTransactionParameter.getCategoryId());

        Assertions.assertThrows(BadAssociationElementException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName()));

    }

    @Test
    public void testUpdateUserTransactionWithGroupClassification() {
        final BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setClassificationId(this.groupUpdateBankingTransactionParameter.getClassificationId());

        Assertions.assertThrows(BadAssociationElementException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName()));

    }

    @Test
    public void testUpdateUserTransactionToGroupTransaction()
        throws BankingTransactionNotExistException, BankingAccountNotExistException, BadAssociationElementException, UserNotInGroupException,
            ThirdNotExistException, CategoryNotExistException, ClassificationNotExistException, CurrencyNotExistException, NotUserElementException {

        BankingTransaction bankingTransaction = this.testFactory.getBankingTransaction(this.user, false);

        final Integer id = bankingTransaction.getId();

        this.groupUpdateBankingTransactionParameter.setId(id);

        final BankingTransactionDTO bankingTransactionDTO = this.bankingTransactionServiceImpl.updateBankingTransaction(this.groupUpdateBankingTransactionParameter, this.user.getUserName());

        bankingTransaction = this.bankingTransactionRepository.findById(id).orElseThrow();

        this.checkData(bankingTransactionDTO, bankingTransaction, this.groupUpdateBankingTransactionParameter);
    }

    @Test
    public void testUpdateGroupTransactionWithOtherGroupBankingAccount() {
        final BankingTransaction userBankingTransaction = this.testFactory.getBankingTransaction(this.group, false);

        final Group group = this.testFactory.getGroup(this.user);

        final BankingAccount otherUserBankingAccount = this.testFactory.getBankingAccount(group);

        final Integer id = userBankingTransaction.getId();

        this.userUpdateBankingTransactionParameter.setId(id);
        this.userUpdateBankingTransactionParameter.setLinkedAccountId(otherUserBankingAccount.getId());

        Assertions.assertThrows(BadAssociationElementException.class,
            () -> this.bankingTransactionServiceImpl.updateBankingTransaction(this.userUpdateBankingTransactionParameter, this.user.getUserName()));

    }

}