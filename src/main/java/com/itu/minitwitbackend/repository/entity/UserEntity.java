package com.itu.minitwitbackend.repository.entity;

import java.util.List;
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
    private String username;
    private String email;
    private String password;
    private String profilePicture;
    private List<String> following;

}
