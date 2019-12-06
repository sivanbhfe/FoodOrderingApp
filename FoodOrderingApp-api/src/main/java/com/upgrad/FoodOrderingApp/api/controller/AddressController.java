package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressResponse;
import com.upgrad.FoodOrderingApp.api.model.SaveAddressRequest;
import com.upgrad.FoodOrderingApp.api.model.AddressListResponse;
import com.upgrad.FoodOrderingApp.api.model.AddressList;
import com.upgrad.FoodOrderingApp.api.model.AddressListState;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private CustomerService customerService;

    @RequestMapping(value = "/address", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(@RequestBody(required = false) SaveAddressRequest saveAddressRequest,
                                                           @RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException {
        String[] bearerToken = accessToken.split("Bearer ");
        CustomerEntity customerEntity = customerService.getCustomer(bearerToken[1]);
        StateEntity stateEntity = addressService.getStateByUuid(saveAddressRequest.getStateUuid());

        final AddressEntity addressEntity = new AddressEntity();
        addressEntity.setUuid(UUID.randomUUID().toString());
        addressEntity.setFlatBuilNumber(saveAddressRequest.getFlatBuildingName());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setState(stateEntity);
        addressEntity.setActive(1);

        final AddressEntity persistedAddressEntity = addressService.saveAddress(addressEntity);

        final CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setAddress(persistedAddressEntity);
        customerAddressEntity.setCustomer(customerEntity);
        addressService.createCustomerAddress(customerAddressEntity);

        SaveAddressResponse saveAddressResponse = new SaveAddressResponse()
                .id(persistedAddressEntity.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
    }


    @RequestMapping(value = "/address/customer", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getSavedAddresses(@RequestHeader("authorization") final String accessToken) throws AuthorizationFailedException {
        String[] bearerToken = accessToken.split("Bearer ");
        final CustomerEntity customerEntity = customerService.getCustomer(bearerToken[1]);
        final List<CustomerAddressEntity> listCustomerAddressEntity = addressService.getCustomerAddressesByCustomer(customerEntity);

        AddressListResponse addressListResponse = new AddressListResponse();

        for(CustomerAddressEntity customerAddressEntity : listCustomerAddressEntity ){
            AddressEntity addressEntity = customerAddressEntity.getAddress();
            AddressList addressList = new AddressList();
            addressList.setId(UUID.fromString(addressEntity.getUuid()));
            addressList.setFlatBuildingName(addressEntity.getFlatBuilNumber());
            addressList.setLocality(addressEntity.getLocality());
            addressList.setPincode(addressEntity.getPinCode());

            StateEntity stateEntity = addressEntity.getState();
            AddressListState addressListState = new AddressListState();
            addressListState.setId(UUID.fromString(stateEntity.getUuid()));
            addressListState.setStateName(stateEntity.getStateName());

            addressList.state(addressListState);

            addressListResponse.addAddressesItem(addressList);

        }
        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);
    }
}
