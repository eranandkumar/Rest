package com.rest.ws.service;

import com.rest.ws.shared.dto.AddressDTO;

import java.util.List;

public interface AddressesService {
    List<AddressDTO> getAddresses(String id);
    AddressDTO getAddressById(String addressesId);
}
