package com.vlad.inventory.model

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
data class ProductType(
        @Id @GeneratedValue
        var id: Long?,
        var name: String
) {
    fun toDTO() = ProductTypeDTO(id = id, name = name)
}

data class ProductTypeDTO(
        var id: Long?,
        var name: String
) {
    fun toEntity() = ProductType(id = id, name = name)
}

data class MultipleProductTypesResponse(
        var productTypes: List<ProductTypeDTO>
)