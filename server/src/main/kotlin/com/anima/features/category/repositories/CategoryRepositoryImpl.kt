package com.anima.features.category.repositories

import com.anima.features.category.entities.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
class CategoryRepositoryImpl(private val jpa: CategoryJpaRepository) : CategoryRepository {
    override fun findById(id: String): Optional<CategoryEntity> = jpa.findById(id)
    override fun findAll(): List<CategoryEntity> = jpa.findAll()
    override fun findByDescription(description: String): Optional<CategoryEntity> = jpa.findByDescription(description)
    override fun existsByDescription(description: String): Boolean = jpa.existsByDescription(description)
    override fun save(category: CategoryEntity): CategoryEntity = jpa.save(category)
    override fun deleteById(id: String) = jpa.deleteById(id)
}

interface CategoryJpaRepository : JpaRepository<CategoryEntity, String> {
    fun findByDescription(description: String): Optional<CategoryEntity>
    fun existsByDescription(description: String): Boolean
}
