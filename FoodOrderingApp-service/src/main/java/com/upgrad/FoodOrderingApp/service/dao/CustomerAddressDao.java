package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CustomerAddressDao {

    @PersistenceContext
    private EntityManager entityManager;

    //Retrieves Customer address by Address Id
    public CustomerAddressEntity getCustomerAddressByAddressId(AddressEntity addressEntity){
        try{
            return this.entityManager.createNamedQuery("customerAddressByAddressId", CustomerAddressEntity.class).setParameter("address", addressEntity).getSingleResult();
        }catch(NoResultException nre){
            return null;
        }
    }

    //Retrieve the list of addresses of a particular customer
    public List<CustomerAddressEntity> getCustomerAddressListByCustomer(CustomerEntity customerEntity){
        try{
            return this.entityManager.createNamedQuery("customerAddressesByCustomerId", CustomerAddressEntity.class).setParameter("customer", customerEntity).getResultList();
        }catch (NoResultException nre){
            return null;
        }
    }
}
