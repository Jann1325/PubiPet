package io.github.jann1325.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private LoginDTO loginDTO;
    private RegisterDTO registerDTO;
    private ProfileDTO profileDTO;
    private AddressDTO addressDTO;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginDTO {
        private String email;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisterDTO {
        private String email;
        private String userName;
        private String gender;
        private String birth;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProfileDTO {
        private String email;
        private String userName;
        private String phone;
        private String gender;
        private LocalDate birth;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDTO {
        private Integer id;
        private Integer userId;
        private String recipient;
        private String city;
        private String area;
        private String phone;
        private String address;
        private Boolean isDefault;
    }
}
