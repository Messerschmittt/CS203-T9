package csd.api.modules.portfolio;

import java.util.List;
import java.util.Optional;
import javax.validation.Valid;

import csd.api.tables.*;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
public class AssetsController {
    private AssetsRepository assetsRepo;

    public AssetsController(AssetsRepository assetsRepo){
        this.assetsRepo = assetsRepo;
    }

    /**
     * List all assets in the system
     * @return list of all assets
     */
    @GetMapping("/assets")
    public List<Assets> listAssets(){
        return assetsRepo.findAll();
    }

    @GetMapping("/Assets/{id}")
    public Assets getAssets(@PathVariable Integer id){
        Optional<Assets> a = assetsRepo.findById(id);
        if(!a.isPresent()){
            throw new AssetsNotFoundException(id);
        }
        Assets ass = a.get();
        return ass;
    }
}
