package csd.api.tables;

import java.util.List;
import javax.persistence.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;


@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Content {

    @Id @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private String title;
    private String summary;
    private String content;
    private String link;
    private boolean approved = false;

    public Content(String title, String summary, String content, String link){
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.link = link;
    }
    
}