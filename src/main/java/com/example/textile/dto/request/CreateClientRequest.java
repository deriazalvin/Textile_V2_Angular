package com.example.textile.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateClientRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 120, message = "Le nom ne doit pas depasser 120 caracteres")
    private String name;

    @NotBlank(message = "Le telephone est obligatoire")
    @Pattern(regexp = "^[0-9+\\- ]{6,30}$", message = "Le telephone est invalide")
    private String phone;

    @Email(message = "L'email est invalide")
    @Size(max = 150, message = "L'email ne doit pas depasser 150 caracteres")
    private String email;

    @Size(max = 250, message = "L'adresse ne doit pas depasser 250 caracteres")
    private String address;

    @Size(max = 60, message = "La ville ne doit pas depasser 60 caracteres")
    private String city;

    @Size(max = 400, message = "Les notes ne doivent pas depasser 400 caracteres")
    private String notes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}