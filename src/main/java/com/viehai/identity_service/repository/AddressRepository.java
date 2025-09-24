package com.viehai.identity_service.repository;

import com.viehai.identity_service.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
