package csd.api.tables.templates;

import lombok.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
public class ContentRecord {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String title;
    private String summary;
    private String content;
    private String link;

    public ContentRecord(String title, String summary, String content, String link){
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.link = link;
    }

}
