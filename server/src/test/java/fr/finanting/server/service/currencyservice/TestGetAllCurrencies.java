package fr.finanting.server.service.currencyservice;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.finanting.server.dto.CurrencyDTO;
import fr.finanting.server.model.Currency;
import fr.finanting.server.repository.CurrencyRepository;
import fr.finanting.server.service.implementation.CurrencyServiceImpl;
import fr.finanting.server.testhelper.AbstractMotherIntegrationTest;

public class TestGetAllCurrencies extends AbstractMotherIntegrationTest {

    @Autowired
    private CurrencyRepository currencyRepository;

    private CurrencyServiceImpl currencyServiceImpl;

    @Override
    protected void initDataBeforeEach() throws Exception {
        this.currencyServiceImpl = new CurrencyServiceImpl(this.currencyRepository);
    }

    private void checkData(final Currency expectedData, final CurrencyDTO currentData){
        Assertions.assertEquals(expectedData.getDecimalPlaces(), currentData.getDecimalPlaces());
        Assertions.assertEquals(expectedData.getId(), currentData.getId());
        Assertions.assertEquals(expectedData.getRate(), currentData.getRate());
        Assertions.assertEquals(expectedData.getDefaultCurrency(), currentData.getDefaultCurrency());
        Assertions.assertEquals(expectedData.getIsoCode(), currentData.getIsoCode());
        Assertions.assertEquals(expectedData.getLabel(), currentData.getLabel());
        Assertions.assertEquals(expectedData.getSymbol(), currentData.getSymbol());
    }

    @Test
    public void testGetAllCurrencies(){
        final Currency currency1 = this.currencyRepository.save(this.factory.getCurrency());
        final Currency currency2 = this.currencyRepository.save(this.factory.getCurrency());
        final Currency currency3 = this.currencyRepository.save(this.factory.getCurrency());

        final List<CurrencyDTO> currencyDTOs = this.currencyServiceImpl.getAllCurrencies();

        Assertions.assertEquals(3, currencyDTOs.size());

        for(final CurrencyDTO currencyDTO : currencyDTOs){
            if(currencyDTO.getId().equals(currency1.getId())){
                this.checkData(currency1, currencyDTO);
            } else if(currencyDTO.getId().equals(currency2.getId())){
                this.checkData(currency2, currencyDTO);
            } else if(currencyDTO.getId().equals(currency3.getId())){
                this.checkData(currency3, currencyDTO);
            } else {
                Assertions.fail();
            }
        }
    }
    
}