package ams.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class University extends BaseEntity {
//    @NotNull
    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "university")
    @JsonIgnore
    private Set<Trainee> trainee;
}
