package co.makers.MakersApi.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Student")
@Relation(collectionRelation = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StudentID")
    private Long studentID;
    @Column(name = "FirstName")
    private String firstName;
    @Column(name = "LastName")
    private String lastName;
    @Column(name = "Email")
    private String email;
}
