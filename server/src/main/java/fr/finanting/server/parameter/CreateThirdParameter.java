package fr.finanting.server.parameter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import fr.finanting.server.parameter.subpart.AddressParameter;
import fr.finanting.server.parameter.subpart.BankDetailsParameter;
import fr.finanting.server.parameter.subpart.ContactParameter;
import lombok.Data;

@Data
public class CreateThirdParameter {

    @NotNull
    @NotEmpty
    private String label;

    @NotNull
    @NotEmpty
    private String abbreviation;

    private String descritpion;

    private Integer defaultCategoryId;

    private String groupName;

    private ContactParameter contactParameter;
    private BankDetailsParameter bankDetailsParameter;
    private AddressParameter addressParameter;  
    
}