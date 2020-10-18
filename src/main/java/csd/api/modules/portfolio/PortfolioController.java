package csd.api.modules.portfolio;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import csd.api.tables.*;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

@RestController
public class PortfolioController {
    private PortfolioRepository portfolioRepo;
    private CustomerRepository customerRepo;
    private AssetsRepository assetsRepo;

    public PortfolioController(PortfolioRepository portfolioRepo, 
            CustomerRepository customerRepo, AssetsRepository assetsRepo){
        this.portfolioRepo = portfolioRepo;
        this.customerRepo = customerRepo;
    }

    /**   this one for testing 
     * List all portfolio in the system
     * @return list of all portfolio
     */
    @GetMapping("/portfolios")
    public List<Portfolio> listPortfolios(){
        return portfolioRepo.findAll();
    }
    /**
     * List portfolio owned by customer
     * @return portfoli of customer
     */
    @GetMapping("/portfolio")
    public Portfolio listPortfolioforuser(Authentication auth){
        Portfolio p = null;
        if(auth.getAuthorities().toString().equals("[ROLE_USER]")){
            Customer c = customerRepo.findByUsername(auth.getName());
            p =  portfolioRepo.findByCustomer_Id(c.getId());
        }
        return p;
    }
    
}