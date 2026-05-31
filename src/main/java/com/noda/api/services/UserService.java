package com.noda.api.services;

import com.noda.api.dtos.AddressResponseDTO;
import com.noda.api.dtos.UserResponseDTO;
import com.noda.api.exceptions.CpfAlreadyRegisteredException;
import com.noda.api.exceptions.EmailAlreadyRegisteredException;
import com.noda.api.exceptions.UserNotFoundException;
import com.noda.api.models.User;
import com.noda.api.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User save(User user) {

        Optional<User> userWithTheSameCpf = userRepository.findByCpf(user.getCpf());
        if (userWithTheSameCpf.isPresent()) {
            throw new CpfAlreadyRegisteredException("This CPF is already registered.");
        }

        Optional<User> userWithTheSameEmail = userRepository.findByEmail(user.getEmail());
        if (userWithTheSameEmail.isPresent()) {
            throw new EmailAlreadyRegisteredException("This Email is already registered.");
        }

        return userRepository.save(user);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public UserResponseDTO findUserByIdMapped(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        AddressResponseDTO addressDTO = null;
        if(user.getAddress() != null) {
            addressDTO = new AddressResponseDTO(
                    user.getAddress().getCep(),
                    user.getAddress().getStreet(),
                    user.getAddress().getNumber(),
                    user.getAddress().getState(),
                    user.getAddress().getNeighborhood(),
                    user.getAddress().getComplement()
            );
        }

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getBirthday(),
                user.getCpf(),
                user.getEmail(),
                addressDTO
        );

    }
}
