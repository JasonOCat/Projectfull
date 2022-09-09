package com.example.demo.student;

import com.example.demo.student.exception.BadRequestException;
import com.example.demo.student.exception.StudentNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    StudentService underTest;

    @Mock
    StudentRepository studentRepository;


    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository);
    }

    @Test
    void canGetAllStudents() {
        // when
        underTest.getAllStudents();

        // then
        verify(studentRepository, times(1)).findAll();

    }

    @Test
    void canAddStudent() {
        // given
        String email = "edouard@gmail.com";
        Student student = new Student(
                "Edouard",
                email,
                Gender.OTHER
        );

        // when
        underTest.addStudent(student);

        // then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);

        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student capturedStudent = studentArgumentCaptor.getValue();

        assertThat(capturedStudent).isEqualTo(student);
    }


    @Test
    void addStudent_willThrow_WhenEmailIsTaken() {
        // given
        String email = "edouard@gmail.com";
        Student student = new Student(
                "Edouard",
                email,
                Gender.OTHER
        );

        given(studentRepository.selectExistsEmail(anyString())).willReturn(true);

        // when
        // then
        assertThatThrownBy(() -> underTest.addStudent(student))
                .hasMessageContaining(String.format("Email %s is already taken", student.getEmail()))
                .isInstanceOf(BadRequestException.class);

        //method is never called
        verify(studentRepository, never()).save(any());

    }

    @Test
    void canDeleteStudent() {
        // given
        Long id = 4L;
        given(studentRepository.existsById(id))
                .willReturn(true);

        // when
        underTest.deleteStudent(id);

        // then
        ArgumentCaptor<Long> longArgumentCaptor = ArgumentCaptor.forClass(Long.class);

        verify(studentRepository).deleteById(longArgumentCaptor.capture());
        Long capturedId = longArgumentCaptor.getValue();
        assertThat(capturedId).isEqualTo(id);

    }

    @Test
    void deleteStudent_willThrow_ifStudentDoesNotExist() {
        // given
        Long id = 4L;
        given(studentRepository.existsById(id))
                .willReturn(false);

        // when
        // then
        assertThatThrownBy(() -> underTest.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exists");

        verify(studentRepository, never()).deleteById(any());

    }
}