package ams.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Faculty extends BaseEntity {
    @NotNull
    private String name;

    @OneToMany(mappedBy = "faculty")
    @JsonIgnore
    private Set<Trainee> trainee;
}
