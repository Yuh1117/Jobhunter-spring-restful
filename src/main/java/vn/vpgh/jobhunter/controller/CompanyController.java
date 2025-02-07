package vn.vpgh.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.vpgh.jobhunter.domain.Company;
import vn.vpgh.jobhunter.domain.response.ResultPaginationDTO;
import vn.vpgh.jobhunter.service.CompanyService;
import vn.vpgh.jobhunter.util.annotation.ApiMessage;
import vn.vpgh.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createNewCompany(@Valid @RequestBody Company reqCompany) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleSaveCompany(reqCompany));
    }


    @GetMapping("/companies/{id}")
    @ApiMessage("Get a company")
    public ResponseEntity<Company> getCompany(@PathVariable("id") long id) throws IdInvalidException {
        Company company = this.companyService.getCompanyById(id);
        if (company == null) {
            throw new IdInvalidException("Company not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(company);
    }

    @GetMapping("/companies")
    @ApiMessage("Get all companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(@Filter Specification<Company> specification, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.getAllCompanies(specification, pageable));
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@RequestBody Company reqCompany) {
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.handleUpdateCompany(reqCompany));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.deleteCompanyById(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
