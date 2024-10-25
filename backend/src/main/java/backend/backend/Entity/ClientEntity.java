package backend.backend.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "client")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class ClientEntity {
    // This class represents the user. The user is the person who will use the application, can request credits, and has a savings account


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    // id of the user.
    private long id;
    private String rut;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private int age;
    private int salary;
    private int JobTenure;
    private boolean Dicom;


    public ClientEntity(String rut, String password, String email,String firstName, String lastname, int age,int salary, int JobTenure, boolean Dicom) {
        this.rut = rut;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastname;
        this.age = age;
        this.salary = salary;
        this.JobTenure = JobTenure;
        this.Dicom = Dicom;
    }

    public boolean GetDicom() {
        return Dicom;
    }
}
