package co.makers.MakersApi.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentRespository studentRespository;
    private final EntityLinks entityLinks;

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Student>>> getAllStudents() {
        List<EntityModel<Student>> students = studentRepository.findAll().stream()
                .map(student -> EntityModel.of(student,
                        entityLinks.linkToItemResource(Student.class, student.getId()).withRel("self"),
                        entityLinks.linkToCollectionResource(Student.class).withRel("students")))
                .collect(Collectors.toList());

        return ResponseEntity.ok(CollectionModel.of(students,
                entityLinks.linkToCollectionResource(Student.class).withSelfRel()));
    }
}
