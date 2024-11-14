package vn.vpgh.jobhunter.service;

import org.springframework.stereotype.Service;
import vn.vpgh.jobhunter.domain.Company;
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
}
