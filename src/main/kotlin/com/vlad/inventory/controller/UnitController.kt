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
    fun getAllUnits(
            @RequestParam(value="returnAttributes", required = false, defaultValue = "false") returnAttributes: Boolean,
            @RequestParam(value="validate", required = false, defaultValue = "false") validate: Boolean,
            @RequestParam(value="filter", required = false, defaultValue = "all") filter: String
    ): MultipleUnitsResponse {
        if (!setOf<String>("onlyValid", "onlyInvalid", "all").contains(filter)) {
            throw  ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid value for 'filter' available: all|onlyValid|onlyInvalid")
        }
        val shouldValidate = validate || (filter != "all")
        val units = unitService
                .getAllUnits(returnAttributes = returnAttributes, validate = shouldValidate)
                .filter {
                    if (filter == "all") {
                        true
                    } else {
                        if (filter == "onlyValid") {
                            it.isValid == true
                        }else {
                            it.isValid == false
                        }
                    }
                }
        return MultipleUnitsResponse(units)
    }

    @GetMapping("/units/{unitId}")
    @ResponseBody
    fun getSingleUnit(
            @PathVariable unitId: Long,
            @RequestParam(value="returnAttributes", required = false, defaultValue = "false") returnAttributes: Boolean,
            @RequestParam(value="validate", required = false, defaultValue = "false") validate: Boolean
    ): ProductUnitDTO  {
        return unitService.getSingleUnit(unitId, returnAttributes = returnAttributes, validate = validate)
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

    @GetMapping("/units/{unitId}/location")
    @ResponseBody
    fun getLocation(@PathVariable unitId: Long): Location {
        val location = unitService.getLocation(unitId)
        if (location == null) {
            throw  ResponseStatusException(HttpStatus.NOT_FOUND, "Unit does not have location set")
        }
        return location!!
    }

    @PostMapping("/units/{unitId}/location")
    @ResponseBody
    fun postLocation(@PathVariable unitId: Long, @RequestBody location: Location): Location {
        return unitService.postLocation(unitId, location)
    }
}