package vn.vpgh.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.vpgh.jobhunter.domain.Company;
import vn.vpgh.jobhunter.domain.dto.Meta;
import vn.vpgh.jobhunter.domain.dto.ResultPaginationDTO;
import vn.vpgh.jobhunter.repository.CompanyRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company handleSaveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO getAllCompanies(Specification<Company> specification) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        Meta meta = new Meta();
        List<Company> companies = this.companyRepository.findAll(specification);

        res.setMeta(meta);
        res.setResult(companies);

        return res;
    }

    public Company getCompanyById(long id) {
        Optional<Company> optionalCompany = this.companyRepository.findById(id);
        return optionalCompany.isPresent() ? optionalCompany.get() : null;
    }

    public Company handleUpdateCompany(Company reqCompany) {
        Company company = this.getCompanyById(reqCompany.getId());
        if (company != null) {

            if (reqCompany.getName() != null)
                company.setName(reqCompany.getName());
            if (reqCompany.getDescription() != null)
                company.setDescription(reqCompany.getDescription());
            if (reqCompany.getAddress() != null)
                company.setAddress(reqCompany.getAddress());
            if (reqCompany.getLogo() != null)
                company.setLogo(reqCompany.getLogo());

            return this.companyRepository.save(company);
        }

        return null;
    }

    public void deleteCompanyById(long id) {
        this.companyRepository.deleteById(id);
    }
}
