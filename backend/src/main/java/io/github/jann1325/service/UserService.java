package io.github.jann1325.service;

import io.github.jann1325.domain.UserAddress;
import io.github.jann1325.domain.UserInfo;
import io.github.jann1325.exception.ApiException;
import io.github.jann1325.repository.UserAddressRepository;
import io.github.jann1325.repository.UserInfoRepository;
import io.github.jann1325.response.ErrorCode;
import io.github.jann1325.utils.JwtUtils;
import io.github.jann1325.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class UserService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    @Autowired
    private UserAddressRepository userAddressRepository;

    // 新建帳號
    public void register(UserDTO.RegisterDTO registerDTO) {
        // 檢查 email 是否已註冊
        Optional<UserInfo> existingUser = Optional.ofNullable(userInfoRepository.findByEmail(registerDTO.getEmail()));
        if (existingUser.isPresent()) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        UserInfo user = new UserInfo();
        BeanUtils.copyProperties(user, registerDTO);
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());

        if (registerDTO.getBirth() != null && !registerDTO.getBirth().isEmpty()) {
            try {
                LocalDate birthDate = LocalDate.parse(registerDTO.getBirth());
                user.setBirth(birthDate);
            } catch (DateTimeParseException e) {
                throw new ApiException(ErrorCode.INVALID_USER_INPUT);
            }
        }
        userInfoRepository.save(user);
    }

    // 取得用戶基本資料
    public UserDTO.ProfileDTO getUserProfile(String authorizationHeader) {
        Integer userId = JwtUtils.extractUserId(authorizationHeader.replace("Bearer ", ""));
        Optional<UserInfo> user = userInfoRepository.findById(userId);
        if (user.isPresent()) {
            UserDTO.ProfileDTO profileDTO = new UserDTO.ProfileDTO();
            BeanUtils.copyProperties(user.get(), profileDTO);
            return profileDTO;
        } else {
            throw new ApiException(ErrorCode.USER_NOT_FOUND);
        }
    }

    // 更新用戶基本資料
    public void updateUserProfile(String authorizationHeader, UserDTO.ProfileDTO profileDTO) {
        Integer userId = JwtUtils.extractUserId(authorizationHeader.replace("Bearer ", ""));
        Optional<UserInfo> optionalUser = userInfoRepository.findById(userId);
        if (optionalUser.isPresent()) {
            UserInfo user = optionalUser.get();
            BeanUtils.copyProperties(profileDTO, user);
            user.setUpdatedTime(LocalDateTime.now());
            userInfoRepository.save(user);
        }
    }

    // 取得用戶地址資料
    public List<UserDTO.AddressDTO> getUserAddress(String authorizationHeader) {
        Integer userId = JwtUtils.extractUserId(authorizationHeader.replace("Bearer ", ""));
        // 查詢使用者地址資料
        List<UserAddress> userAddresses = userAddressRepository.findByUserId(userId);

        List<UserDTO.AddressDTO> addressDTOS = userAddresses.stream().map(address -> {
            UserDTO.AddressDTO dto = new UserDTO.AddressDTO();
            BeanUtils.copyProperties(address, dto);
            return dto;
        }).collect(Collectors.toList());

        return addressDTOS;
    }

    //修改、新增用戶地址
    @Transactional
    public void updateUserAddress(String authorizationHeader, UserDTO.AddressDTO addressDTO) {
        Integer userId = JwtUtils.extractUserId(authorizationHeader.replace("Bearer ", ""));
        addressDTO.setUserId(userId);
        // 檢查此筆是否設為預設，是則先將此使用者的其他地址取消預設
        if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
            userAddressRepository.clearDefaultAddress(userId);
        }
        if (addressDTO.getId() == null) {
            // 新增
            UserAddress userAddress = new UserAddress();
            BeanUtils.copyProperties(addressDTO, userAddress);
            userAddress.setCreatedTime(LocalDateTime.now());
            userAddress.setUpdatedTime(LocalDateTime.now());
            userAddressRepository.save(userAddress);
        } else {
            // 更新
            Optional<UserAddress> optionalUserAddress = userAddressRepository.findById(addressDTO.getId());
            if (optionalUserAddress.isPresent()) {
                UserAddress userAddress = optionalUserAddress.get();
                BeanUtils.copyProperties(addressDTO, userAddress);
                userAddress.setUpdatedTime(LocalDateTime.now());
                userAddressRepository.save(userAddress);
            } else {
                throw new ApiException(ErrorCode.INVALID_USER_INPUT, "找不到對應的地址資料，無法更新");
            }
        }
    }

    // 刪除用戶地址
    @Transactional
    public void deleteUserAddress(Integer addressId) {
        Optional<UserAddress> optional = userAddressRepository.findById(addressId);
        if (optional.isEmpty()) {
            throw new ApiException(ErrorCode.INVALID_USER_INPUT, "找不到此地址");
        }
        userAddressRepository.deleteById(addressId);
    }

}
