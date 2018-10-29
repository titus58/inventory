package com.vlad.inventory.controller

import com.vlad.inventory.model.*
import com.vlad.inventory.service.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@Controller
class UnitController {
    val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var unitService: UnitService

    @GetMapping("/units")
    @ResponseBody
    fun getAll(): MultipleUnitsResponse {
        return unitService.getAll()
    }

    @PostMapping("/units")
    @ResponseBody
    fun postUnit(@RequestBody productUnitDTO: ProductUnitDTO): ProductUnitDTO {
        return unitService.postUnit(productUnitDTO)
    }

    @PostMapping("/units/{unitId}/attributes")
    @ResponseBody
    fun postAttribute(@PathVariable unitId: Long, @RequestBody attributeValueDTO: AttributeValueDTO): AttributeValueDTO? {
        return unitService.postAttribute(unitId, attributeValueDTO)
    }

    @GetMapping("/units/{unitId}/attributes")
    @ResponseBody
    fun getAllAttributes(@PathVariable unitId: Long): MultipleAttributeValuesResponse {
        return unitService.getAllAttributes(unitId)
    }
}