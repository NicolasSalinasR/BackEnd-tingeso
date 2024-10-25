package backend.backend.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Request")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class RequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String typeOfRequest;
    private int Stage;
    private long clientId;
    private int Amount;
    private int yearTerm;

    @Lob
    private byte[] pdfDocument;


}
