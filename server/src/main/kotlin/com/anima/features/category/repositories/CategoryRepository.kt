package com.anima.features.category.repositories
import com.anima.features.category.entities.CategoryEntity
import java.util.Optional

interface CategoryRepository {
    fun findById(id: String): Optional<CategoryEntity>
    fun findAll(): List<CategoryEntity>
    fun findByDescription(description: String): Optional<CategoryEntity>
    fun existsByDescription(description: String): Boolean
    fun save(category: CategoryEntity): CategoryEntity
    fun deleteById(id: String)
}
