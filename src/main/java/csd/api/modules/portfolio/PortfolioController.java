package csd.api.modules.portfolio;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import csd.api.tables.*;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class PortfolioController {
    private PortfolioRepository portfolioRepo;

    public PortfolioController(PortfolioRepository portfolioRepo){
        this.portfolioRepo = portfolioRepo;
    }

    /**
     * List all portfolio in the system
     * @return list of all portfolio
     */
    @GetMapping("/portfolios")
    public List<Portfolio> listPortfolios(){
        return portfolioRepo.findAll();
    }

    @GetMapping("/portfolio/{id}")
    public Portfolio getPortfolio(@PathVariable Long id){
        Optional<Portfolio> p = portfolioRepo.findById(id);
        if(!p.isPresent()){
            throw new PortfolioNotFoundException(id);
        }

        Portfolio portfolio = p.get();
        return portfolio;
    }

}