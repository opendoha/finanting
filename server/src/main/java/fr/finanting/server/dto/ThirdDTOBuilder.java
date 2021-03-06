package fr.finanting.server.dto;

import fr.finanting.server.generated.model.ThirdDTO;
import fr.finanting.server.model.Third;

import java.util.ArrayList;
import java.util.List;

public class ThirdDTOBuilder implements Transformer<Third, ThirdDTO> {

    private static final AddressDTOBuilder ADDRESS_DTO_BUILDER = new AddressDTOBuilder();
    private static final BankDetailsDTOBuilder BANK_DETAILS_DTO_BUILDER = new BankDetailsDTOBuilder();
    private static final ContactDTOBuilder CONTACT_DTO_BUILDER = new ContactDTOBuilder();
    private static final CategoryDTOBuilder CATEGORY_DTO_BUILDER = new CategoryDTOBuilder();

    @Override
    public ThirdDTO transform(final Third input) {
        final ThirdDTO thirdDTO = new ThirdDTO();
        thirdDTO.setAbbreviation(input.getAbbreviation());
        thirdDTO.setDescription(input.getDescription());
        thirdDTO.setId(input.getId());
        thirdDTO.setLabel(input.getLabel());

        if(input.getAddress() != null){
            thirdDTO.setAddressDTO(ADDRESS_DTO_BUILDER.transform(input.getAddress()));
        }

        if(input.getBankDetails() != null){
            thirdDTO.setBankDetailsDTO(BANK_DETAILS_DTO_BUILDER.transform(input.getBankDetails()));
        }

        if(input.getContact() != null){
            thirdDTO.setContactDTO(CONTACT_DTO_BUILDER.transform(input.getContact()));
        }

        if(input.getDefaultCategory() != null){
            thirdDTO.setCategoryDTO(CATEGORY_DTO_BUILDER.transform(input.getDefaultCategory()));
        }

        return thirdDTO;
    }

    @Override
    public List<ThirdDTO> transformAll(final List<Third> input) {
        final List<ThirdDTO> thirdDTOList = new ArrayList<>();
        input.forEach(third -> thirdDTOList.add(this.transform(third)));
        return thirdDTOList;
    }
}
