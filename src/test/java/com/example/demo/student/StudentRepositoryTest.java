package com.example.demo.student;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    @Autowired
    private StudentRepository underTest;


    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void selectExistsEmail_shouldCheckIfEmailExists() {
        // given
        String email = "edouard@gmail.com";
        Student student = new Student(
                "Edouard",
                email,
                Gender.OTHER
        );
        underTest.save(student);

        // when
        Boolean expected = underTest.selectExistsEmail(email);

        // then
        assertThat(expected).isTrue();
    }

    @Test
    void selectExistsEmail_shouldCheckIfEmailDoesNotExist() {
        // given
        String email = "edouard@gmail.com";

        // when
        Boolean expected = underTest.selectExistsEmail(email);

        // then
        assertThat(expected).isFalse();
    }


}