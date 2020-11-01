package csd.api.modules.content;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import csd.api.tables.*;
import csd.api.tables.templates.*;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContentController {
    private ContentRepository contents;

    public ContentController(ContentRepository contents){
        this.contents = contents;
    }

    /**
     * Return information of on content. Customers (normal users)
     * can only see approved content while all other users can see
     * all the content stored in the system
     * @return
     */
    @GetMapping("/api/contents")
    public List<Content> getContents(Authentication auth){
        System.out.println(auth.getPrincipal().toString());
        if(auth.getAuthorities().toString().equals("[ROLE_USER]")){
            // normal users can only see approved content
            List<Content> approvedContent = contents.findAll();
            approvedContent.removeIf(c -> !(c.isApproved()));
            return approvedContent;
        }else{
            // all other users can see all the content
            return contents.findAll();
        }
        
    }

    /**
     * Find a specific content by id
     * API is availaible to all users
     * @param id
     * @return content or null if no content with id specified
     */
    @GetMapping("/api/contents/{id}")
    public Content getSpecificContent(@PathVariable Integer id, Authentication auth){
        Optional<Content> c = contents.findById(id);
        if(c.isEmpty()){
            throw new ContentNotFoundException(id);
        }
        Content content = c.get();
        if(auth.getAuthorities().toString().equals("[ROLE_USER]")){
            if(content.isApproved() == false){
                throw new UnauthorizedContentAccessException(id);
            }
        }

        return content;
    }

    /**
     * Create a new content
     * API is restricted to only employees
     * Approved is set to false by default to ensure managers 
     * have to approve content before customers can view
     * @param content
     * @return content created
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/api/contents")
    public Content createContent(@RequestBody Content content){
        // Approval set to false by default regardless of input
        content.setApproved(false);
        return contents.save(content);
    }

    /**
     * Updating of content stored in the system.
     * API is restricted to only employees.
     * The 'approved' field can only be updated by ROLE_MANAGER
     * @param content
     * @param auth
     * @return content updated
     */
    @PutMapping("/api/contents/{id}")
    public Content updateContent(@RequestBody Content content, Authentication auth, @PathVariable Integer id){
        Optional<Content> c = contents.findById(id);
        if(!c.isPresent()){
            throw new ContentNotFoundException(content.getId());
        }

        Content toUpdate = c.get();
        toUpdate.setContent(content.getContent());
        toUpdate.setLink(content.getLink());
        toUpdate.setSummary(content.getSummary());
        toUpdate.setTitle(content.getTitle());
        
        // Only allow updating of approved for manager
        if(auth.getAuthorities().toString().equals("[ROLE_MANAGER]")){
            toUpdate.setApproved(content.isApproved());
        }

        return contents.save(toUpdate);
    }


    /**
     * Delete a specific content by id
     * API is restricted to only employees
     * @param id
     */
    @DeleteMapping("/api/contents/{id}")
    public void deleteContent(@PathVariable Integer id){
        try{
            contents.deleteById(id);
        }catch(IllegalArgumentException e){
            throw new InvalidIDException();
        }

        return;
    }

    @GetMapping("/api/contents/topheadlines")
    public ArrayList<Content> getTopHeadlines(){
        return NewsAPI.apiTopHeadlines();
    }

    @GetMapping("/api/contents/specificQuery/{query}")
    public ArrayList<Content> getSpecificQuery(@PathVariable String query){
        System.out.println("Query: " + query);
        return NewsAPI.apiSpecificQuery(query);
    }

    
}