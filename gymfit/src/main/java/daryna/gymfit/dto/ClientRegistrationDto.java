package daryna.gymfit.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

public record ClientRegistrationDto(

        String firstName,

        String lastName,

        LocalDate birthDate,

        String phone,

        String email,

        String password) {}
