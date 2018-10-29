package com.vlad.inventory.service

import com.vlad.inventory.model.Owner
import org.springframework.data.repository.CrudRepository

interface OwnerRepository: CrudRepository<Owner, Long> {
}