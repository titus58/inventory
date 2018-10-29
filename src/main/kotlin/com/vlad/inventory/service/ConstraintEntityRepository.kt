package com.vlad.inventory.service

import com.vlad.inventory.model.ConstraintEntity
import com.vlad.inventory.model.ProductType
import org.springframework.data.repository.CrudRepository

interface ConstraintEntityRepository: CrudRepository<ConstraintEntity, Long> {
    fun findByProductType(productType: ProductType): List<ConstraintEntity>
}