package com.vlad.inventory.service

import com.vlad.inventory.model.AttributeValue
import com.vlad.inventory.model.ProductUnit
import org.springframework.data.repository.CrudRepository

interface AttributeValueRepository: CrudRepository<AttributeValue, Long> {
    // TODO: add a secondary index such that this query becomes efficient
    fun findByProductUnit(productUnit: ProductUnit): List<AttributeValue>
}