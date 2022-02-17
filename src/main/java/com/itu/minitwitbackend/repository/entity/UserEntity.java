package com.itu.minitwitbackend.repository.entity;

import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("user")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@CompoundIndexes({
        @CompoundIndex(def = "{'username' : 1, 'password': 1}"),
        @CompoundIndex(def = "{ 'email': 1, 'username.value': 1 }")
})
public class UserEntity {
    @Id
    @Indexed
    private String id;

    @NotEmpty(message = "You have to enter a username")
    private String username;

    @NotEmpty(message = "You have to enter a valid email address")
    @Email(message = "You have to enter a valid email address", flags = { Pattern.Flag.CASE_INSENSITIVE })
    private String email;

    @NotEmpty(message = "You have to enter a password")
    private String password;

    private String profilePicture;
    private List<String> followers;

}
