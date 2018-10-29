package com.vlad.inventory.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.GeneratedValue


@Entity
data class Owner(
        @Id
        @GeneratedValue
        var id: Long,
        @Column(nullable = false)
        var name: String
)