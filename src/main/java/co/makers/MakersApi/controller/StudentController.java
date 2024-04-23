package co.makers.MakersApi.controller;


import co.makers.MakersApi.model.Student;
import co.makers.MakersApi.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    private final StudentRepository studentRepository;

    @Autowired
    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<Student>>> getAllStudents() {
        List<EntityModel<Student>> students = studentRepository.findAll().stream()
                .map(student -> EntityModel.of(student,
                        linkTo(methodOn(StudentController.class).getStudentById(student.getStudentID())).withRel("self"),
                        linkTo(methodOn(StudentController.class).getStudentById(student.getStudentID())).withRel("students")))
                .collect(Collectors.toList());
        if (students.isEmpty())
            return ResponseEntity.notFound().build();
        else
            return ResponseEntity.ok(CollectionModel.of(students));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Student>> getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(student -> EntityModel.of(student,
                        linkTo(methodOn(StudentController.class).getStudentById(student.getStudentID())).withSelfRel(),
                        linkTo(methodOn(StudentController.class).getStudentById(student.getStudentID())).withRel("students")))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EntityModel<Student>> createStudent(@RequestBody Student student) {
        try {
            Student savedStudent = studentRepository.save(student);

            EntityModel<Student> studentResource = EntityModel.of(savedStudent, linkTo(methodOn(StudentController.class).getStudentById(savedStudent.getStudentID())).withSelfRel(),
                    linkTo(methodOn(StudentController.class)).withRel("students"));
            return ResponseEntity.created(studentResource.getRequiredLink("self").toUri()).body(studentResource);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating student", e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Student>> updateStudent(@PathVariable Long id, @RequestBody Student currentStudent) {
        return studentRepository.findById(id)
                .map(student -> {
                    student.setFirstName(currentStudent.getFirstName());
                    student.setLastName(currentStudent.getLastName());
                    student.setEmail(currentStudent.getEmail());

                    Student updatedStudent = studentRepository.save(student);

                    EntityModel<Student> studentResource = EntityModel.of(updatedStudent, linkTo(methodOn(StudentController.class).getStudentById(updatedStudent.getStudentID())).withSelfRel(),
                            linkTo(methodOn(StudentController.class)).withRel("students"));
                    return ResponseEntity.ok(studentResource);
                }).orElseGet(() -> {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStudent(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(student -> {
                    studentRepository.delete(student);
                    return ResponseEntity.noContent().build();
                }).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
