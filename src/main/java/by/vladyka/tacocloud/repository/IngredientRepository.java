package by.vladyka.tacocloud.repository;

import by.vladyka.tacocloud.entity.Ingredient;

public interface IngredientRepository {
    Iterable<Ingredient> findAll();
    Ingredient findById(String id);
    Ingredient save(Ingredient ingredient);
}
