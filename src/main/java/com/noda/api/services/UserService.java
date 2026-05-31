package com.noda.api.services;

import com.noda.api.dtos.AddressResponseDTO;
import com.noda.api.dtos.UserRequestDTO;
import com.noda.api.dtos.UserResponseDTO;
import com.noda.api.exceptions.CpfAlreadyRegisteredException;
import com.noda.api.exceptions.EmailAlreadyRegisteredException;
import com.noda.api.exceptions.UserNotFoundException;
import com.noda.api.models.Address;
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
    private final ViaCepService viaCepService;

    public UserResponseDTO save(UserRequestDTO dto) {

        validateUniqueFields(dto);
        User user = convertToEntity(dto);
        User savedUser = userRepository.save(user);

        return convertToResponseDTO(savedUser);
    }

    public Page<User> findAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public UserResponseDTO findUserByIdMapped(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        AddressResponseDTO addressDTO = getAddressResponseDTO(user);

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getBirthday(),
                user.getCpf(),
                user.getEmail(),
                addressDTO
        );

    }

    private static AddressResponseDTO getAddressResponseDTO(User user) {
        AddressResponseDTO addressDTO = null;
        if (user.getAddress() != null) {
            addressDTO = new AddressResponseDTO(
                    user.getAddress().getCep(),
                    user.getAddress().getStreet(),
                    user.getAddress().getNumber(),
                    user.getAddress().getCity(),
                    user.getAddress().getState(),
                    user.getAddress().getNeighborhood(),
                    user.getAddress().getComplement()
            );
        }
        return addressDTO;
    }

    private void validateUniqueFields(UserRequestDTO dto) {
        Optional<User> userWithTheSameCpf = userRepository.findByCpf(dto.cpf());
        if (userWithTheSameCpf.isPresent()) {
            throw new CpfAlreadyRegisteredException("This CPF is already registered.");
        }

        Optional<User> userWithTheSameEmail = userRepository.findByEmail(dto.email());
        if (userWithTheSameEmail.isPresent()) {
            throw new EmailAlreadyRegisteredException("This Email is already registered.");
        }
    }

    private User convertToEntity(UserRequestDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setCpf(dto.cpf());
        user.setEmail(dto.email());
        user.setBirthday(dto.birthday());
        user.setPassword(dto.password());

        if (dto.address() != null && dto.address().cep() != null) {
            String targetCep = dto.address().cep();
            var cepData = viaCepService.fetchAddressByCep(targetCep);

            Address address = new Address();
            address.setCep(targetCep);
            address.setNumber(dto.address().number());
            address.setComplement(dto.address().complement());
            address.setStreet(cepData.logradouro());
            address.setNeighborhood(cepData.bairro());
            address.setCity(cepData.localidade());
            address.setState(cepData.uf());

            address.setUser(user);
            user.setAddress(address);
        }
        return user;
    }
    private UserResponseDTO convertToResponseDTO(User savedUser) {
        AddressResponseDTO addressDTO = null;
        if (savedUser.getAddress() != null) {
            addressDTO = new AddressResponseDTO(
                    savedUser.getAddress().getCep(),
                    savedUser.getAddress().getStreet(),
                    savedUser.getAddress().getNumber(),
                    savedUser.getAddress().getCity(),
                    savedUser.getAddress().getState(),
                    savedUser.getAddress().getNeighborhood(),
                    savedUser.getAddress().getComplement()
            );
        }

        return new UserResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getBirthday(),
                savedUser.getCpf(),
                savedUser.getEmail(),
                addressDTO
        );
    }
}

