package vn.vpgh.jobhunter.controller;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.vpgh.jobhunter.domain.Company;
import vn.vpgh.jobhunter.domain.dto.ResultPaginationDTO;
import vn.vpgh.jobhunter.service.CompanyService;

@RestController
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/companies")
    public ResponseEntity<Company> createNewUser(@Valid @RequestBody Company reqCompany) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleSaveCompany(reqCompany));
    }

    @GetMapping("/companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(@RequestParam("current") Optional<String> currentOptional,
            @RequestParam("pageSize") Optional<String> pageSizeOptional) {
                String oCurrent = currentOptional.isPresent() ? currentOptional.get() : "";
                String oPageSize = pageSizeOptional.isPresent() ? pageSizeOptional.get() : "";

                int current = Integer.parseInt(oCurrent.equals("") ? "1" : oCurrent);
                int pageSize = Integer.parseInt(oPageSize.equals("") ? "2" : oPageSize);

                Pageable  pageable = PageRequest.of(current - 1, pageSize);
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.getAllCompanies(pageable));
    }

    @PutMapping("/companies")
    public ResponseEntity<Company> updateCompany(@RequestBody Company reqCompany) {
        return ResponseEntity.status(HttpStatus.OK).body(this.companyService.handleUpdateCompany(reqCompany));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.deleteCompanyById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
