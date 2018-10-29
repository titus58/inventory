package com.vlad.inventory.service

import com.vlad.inventory.model.AttributeType
import org.springframework.data.repository.CrudRepository

interface AttributeTypeRepository: CrudRepository<AttributeType, Long> {
}