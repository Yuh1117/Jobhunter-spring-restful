package vn.vpgh.jobhunter.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.vpgh.jobhunter.domain.Company;
import vn.vpgh.jobhunter.domain.User;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.repository.CompanyRepository;
import vn.vpgh.jobhunter.repository.UserRepository;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyService(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    public Company handleSaveCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO getAllCompanies(Specification<Company> specification, Pageable pageable) {
        ResultPaginationDTO res = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();
        Page<Company> pageCompany = this.companyRepository.findAll(specification, pageable);

        meta.setPage(pageCompany.getNumber() + 1);
        meta.setPageSize(pageCompany.getSize());
        meta.setPages(pageCompany.getTotalPages());
        meta.setTotal(pageCompany.getTotalElements());
        res.setMeta(meta);
        res.setResult(pageCompany.getContent());

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
        Company company = this.getCompanyById(id);
        if (company != null) {
            List<User> users = this.userRepository.findByCompany(company);
            this.userRepository.deleteAll(users);
        }
        this.companyRepository.deleteById(id);
    }
}
