package com.voixdesagesse.VoixDeSagesse.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthenticationResponse {
    private final String jwt;
    
}



// package com.voixdesagesse.VoixDeSagesse.jwt;

// import com.voixdesagesse.VoixDeSagesse.dto.AccountType;

// import lombok.AllArgsConstructor;
// import lombok.Data;

// @Data
// @AllArgsConstructor
// public class AuthenticationResponse {
//      private final String jwt;
//     private final AccountType accountType;
//     private final Long userId;
//     private final String name;
    
// }
