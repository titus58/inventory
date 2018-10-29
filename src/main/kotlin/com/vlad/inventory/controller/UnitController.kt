package com.vlad.inventory.controller

import com.vlad.inventory.model.MultipleUnitsResponse
import com.vlad.inventory.model.ProductUnit
import com.vlad.inventory.model.ProductUnitDTO
import com.vlad.inventory.service.OwnerRepository
import com.vlad.inventory.service.ProductUnitRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.server.ResponseStatusException

@Controller
class UnitController {
    val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var productUnitRepository: ProductUnitRepository

    @Autowired
    lateinit var ownerRepository: OwnerRepository

    @GetMapping("/units")
    @ResponseBody
    fun getAll(): MultipleUnitsResponse {
        return MultipleUnitsResponse(productUnitRepository
                .findAll()
                .map { it -> it.toDTO() }
                .toList())
    }

    @PostMapping("/units")
    @ResponseBody
    fun postUnit(@RequestBody productUnitDTO: ProductUnitDTO): ProductUnitDTO {
        val owner = ownerRepository
                .findById(productUnitDTO.ownerId)
                .orElseThrow { ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot find owner with id " + productUnitDTO.ownerId) }
        val productUnit = ProductUnit(
                id = null,
                name = productUnitDTO.name,
                owner = owner
        )
        return productUnitRepository.save(productUnit).toDTO()
    }
}