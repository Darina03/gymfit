package daryna.gymfit.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import daryna.gymfit.entities.enums.ClientAccountStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "surname", nullable = false, length = 100)
    private String surname;

    @Column(name = "phone_number", unique = true, nullable = false, length = 20)
    private String phoneNumber;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;

    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "join_date", nullable = false)
    private LocalDate joinDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ClientAccountStatus status;

    @Column(name = "password", nullable = false, length = 200)
    private String password;


    @ManyToMany(mappedBy = "clients")
    private List<Coach> coaches;



    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", birthday=" + birthday +
                ", email='" + email + '\'' +
                ", joinDate=" + joinDate +
                ", status=" + status +
                ", password='" + password + '\'' +
                '}';
    }
}
