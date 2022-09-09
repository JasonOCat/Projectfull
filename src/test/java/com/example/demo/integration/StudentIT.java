package com.example.demo.integration;


import com.example.demo.student.Gender;
import com.example.demo.student.Student;
import com.example.demo.student.StudentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource(
        locations = "classpath:application-it.properties"
)
@AutoConfigureMockMvc
public class StudentIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    private final Faker faker = new Faker();

    @Test
    void canRegisterNewStudent() throws Exception {
        // given
        String name = String.format("%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        Student student = new Student(
                name,
                String.format("%s@gpail.com", StringUtils.trimAllWhitespace(name.trim().toLowerCase())),
                Gender.OTHER
        );

        // when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)));


        //then
        resultActions.andExpect(status().isOk());
        List<Student> students = studentRepository.findAll();

        //chercher le student créé
        assertThat(students)
                .usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(student);
    }


    @Test
    void canDeleteStudent() throws Exception {
        // given
        String name = String.format("%s %s",
                faker.name().firstName(),
                faker.name().lastName()
        );

        Student student = new Student(
                name,
                String.format("%s@gpail.com", StringUtils.trimAllWhitespace(name.trim().toLowerCase())),
                Gender.OTHER
        );


        //we could do a get to retrieve all the students

/*        MvcResult getStudentsResult = mockMvc.perform(get("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = getStudentsResult
                .getResponse()
                .getContentAsString();

        List<Student> students = objectMapper.readValue(
                contentAsString,
                new TypeReference<>() {
                }
        );*/


        mockMvc
                .perform(post("/api/v1/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk());

        Student savedStudent = studentRepository.findAll()
                .stream()
                .filter(s -> s.getEmail().equals(student.getEmail()))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalStateException(
                                "student with email: " + student.getEmail() + " not found"
                        ));

        // when
        ResultActions resultActions = mockMvc
                .perform(delete("/api/v1/students/{id}", savedStudent.getId()));

        // then

        resultActions.andExpect(status().isOk());
        List<Student> students = studentRepository.findAll();
        assertThat(students)
                .usingRecursiveFieldByFieldElementComparator()
                .doesNotContain(savedStudent);
    }

}
