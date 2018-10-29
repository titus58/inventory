package com.vlad.inventory.controller

import com.vlad.inventory.model.MultipleProductTypesResponse
import com.vlad.inventory.model.ProductType
import com.vlad.inventory.model.ProductTypeDTO
import com.vlad.inventory.service.ProductTypeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class ProductTypeController {
    // TODO: Controller should not be wired to repositories
    @Autowired
    lateinit var productTypeRepository: ProductTypeRepository

    @GetMapping("/product-types")
    @ResponseBody
    fun getAllProductTypes(): MultipleProductTypesResponse {
        val productTypes = productTypeRepository
                .findAll()
                .map { it -> it.toDTO() }
        return MultipleProductTypesResponse(productTypes)
    }

    @PostMapping("/product-types")
    @ResponseBody
    fun postProductType(@RequestBody productTypeDTO: ProductTypeDTO): ProductTypeDTO {
        val productType = productTypeDTO.toEntity()
        return productTypeRepository.save(productType).toDTO()
    }
}