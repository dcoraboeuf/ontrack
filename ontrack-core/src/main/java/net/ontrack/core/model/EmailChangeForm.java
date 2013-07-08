package net.ontrack.core.model;

import lombok.Data;

@Data
public class EmailChangeForm {

    private String password;
    private String email;

    public EmailChangeForm() {
    }

    public EmailChangeForm(String password, String email) {
        this.password = password;
        this.email = email;
    }
}
