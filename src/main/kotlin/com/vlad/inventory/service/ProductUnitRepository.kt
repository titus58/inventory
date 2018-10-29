package com.vlad.inventory.service

import com.vlad.inventory.model.ProductUnit
import org.springframework.data.repository.CrudRepository

interface ProductUnitRepository: CrudRepository<ProductUnit, Long> {
}