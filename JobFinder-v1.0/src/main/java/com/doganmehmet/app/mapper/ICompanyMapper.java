package com.doganmehmet.app.mapper;

import com.doganmehmet.app.dto.company.CompanyDTO;
import com.doganmehmet.app.dto.company.CompanyRequestDTO;
import com.doganmehmet.app.entity.Company;
import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;

@Mapper(implementationName = "CompanyMapperImpl", componentModel = "spring")
public interface ICompanyMapper {

    Company toCompany(CompanyRequestDTO companyRequestDTO);

    CompanyDTO toCompanyDTO(Company company);

    default Page<CompanyDTO> toCompanyDTOPage(Page<Company> companies)
    {
        return companies.map(this::toCompanyDTO);
    }
}
