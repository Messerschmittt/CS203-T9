package csd.api.modules.content;

import java.util.List;
import java.util.Optional;

import csd.api.tables.*;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentController {
    private EmployeeRepository employees;
    private CustomerRepository customers;
    private ContentRepository contents;

    public ContentController(ContentRepository contents, EmployeeRepository employees, CustomerRepository customers){
        this.employees = employees;
        this.customers = customers;
        this.contents = contents;
    }

    /**
     * Return information of all content
     * @return
     */
    @GetMapping("/contents")
    public List<Content> getContents(){
        return contents.findAll();
    }

    @PostMapping("/content/approvecontent/{id}")
    public Content setContentStatus(@PathVariable Long id){
        Optional<Content> c = contents.findById(id);
        if(!c.isPresent()){
            throw new ContentNotFoundException(id);
        }

        Content content = c.get();
        content.setApproved(true);
        return contents.save(content);
    }

    
}