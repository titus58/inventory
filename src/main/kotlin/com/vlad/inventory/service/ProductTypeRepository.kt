package com.vlad.inventory.service

import com.vlad.inventory.model.ProductType
import org.springframework.data.repository.CrudRepository

interface ProductTypeRepository: CrudRepository<ProductType, Long> {
}