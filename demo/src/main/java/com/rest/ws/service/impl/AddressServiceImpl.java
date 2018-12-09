package com.rest.ws.service.impl;

import com.rest.ws.io.entity.AddressEntity;
import com.rest.ws.io.entity.UserEntity;
import com.rest.ws.repository.AddressRepository;
import com.rest.ws.repository.UserRepository;
import com.rest.ws.service.AddressesService;
import com.rest.ws.shared.dto.AddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressesService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDTO> getAddresses(String id) {
        List<AddressDTO> addressDTOListResponse = new ArrayList<>();
        ModelMapper modelMapper = new ModelMapper();

        UserEntity userEntity = userRepository.findByuserId(id);
        if (userEntity == null) return addressDTOListResponse;

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        for (AddressEntity addressEntity: addresses){
            addressDTOListResponse.add(modelMapper.map(addressEntity, AddressDTO.class));
        }
        return addressDTOListResponse;
    }

    @Override
    public AddressDTO getAddressById(String addressesId) {

        AddressEntity addressEntity = addressRepository.findByAddressId(addressesId);

        AddressDTO responseValue = new ModelMapper().map(addressEntity, AddressDTO.class);
        return responseValue;
    }

}
