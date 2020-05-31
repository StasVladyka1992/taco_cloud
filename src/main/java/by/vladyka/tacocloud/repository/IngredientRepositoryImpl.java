package by.vladyka.tacocloud.repository;

import by.vladyka.tacocloud.entity.Ingredient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class IngredientRepositoryImpl implements IngredientRepository {
    private JdbcTemplate template;

    public IngredientRepositoryImpl() {
    }

    @Autowired
    public IngredientRepositoryImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public Iterable<Ingredient> findAll() {
        return template.query("select id, name, type from Ingredient", this::mapRowToIngredient);
    }

    @Override
    public Ingredient findById(String id) {
        return template.queryForObject("select id, name, type from Ingredient where id=?", this::mapRowToIngredient, id);
    }

    @Override
    public Ingredient save(Ingredient ingredient) {
        template.update("insert into Ingredient (id, name, type) values (?,?,?)",
                ingredient.getId(), ingredient.getName(), ingredient.getType().toString());
        return ingredient;
    }

    private Ingredient mapRowToIngredient(ResultSet rs, int rowNum) throws SQLException {
        return new Ingredient(rs.getString("id"),
                rs.getString("name"),
                Ingredient.Type.valueOf(rs.getString("type")));
    }
}
