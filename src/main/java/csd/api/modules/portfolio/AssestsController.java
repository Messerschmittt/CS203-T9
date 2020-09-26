package csd.api.modules.portfolio;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import csd.api.tables.*;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AssestsController {
    private AssestsRepository assestsRepo;

    public AssestsController(AssestsRepository assestsRepo){
        this.assestsRepo = assestsRepo;
    }

    /**
     * List all assests in the system
     * @return list of all assests
     */
    @GetMapping("/assests")
    public List<Assests> listAssests(){
        return assestsRepo.findAll();
    }

    @GetMapping("/Assests/{id}")
    public Assests getAssests(@PathVariable Integer id){
        Optional<Assests> a = assestsRepo.findById(id);
        if(!a.isPresent()){
            throw new AssestsNotFoundException(id);
        }
        Assests ass = a.get();
        return ass;
    }
}
