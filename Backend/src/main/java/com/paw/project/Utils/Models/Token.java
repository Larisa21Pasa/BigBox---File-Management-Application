/**************************************************************************

 File:        Token.java
 Copyright:   (c) 2023 NazImposter
 Description: Token resource
 Designed by:  Pasa Larisa

 Module-History:
 Date        Author                Reason
 21.11.2023  Pasa Larisa           Created for token authentication logic.
 **************************************************************************/
package com.paw.project.Utils.Models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.paw.project.Utils.Models.Enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Token")
@Table(name = "Tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    @Column()
    public String token;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;

    public boolean revoked;

    public boolean expired;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    public User user;
}
