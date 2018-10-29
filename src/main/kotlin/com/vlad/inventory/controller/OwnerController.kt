package com.vlad.inventory.controller

import com.vlad.inventory.model.MultipleOwnersResponse
import com.vlad.inventory.model.Owner
import com.vlad.inventory.service.OwnerRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class OwnerController {
    val log = LoggerFactory.getLogger(this::class.java)

    @Autowired
    lateinit var ownerRepository: OwnerRepository

    @GetMapping("/owners")
    @ResponseBody
    fun getAllOwners(): MultipleOwnersResponse {
        return MultipleOwnersResponse(ownerRepository.findAll().toList())
    }

    @PostMapping("/owners")
    @ResponseBody
    fun postOwner(@RequestBody owner: Owner): Owner {
        return ownerRepository.save(owner)
    }
}