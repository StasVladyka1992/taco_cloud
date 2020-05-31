package by.vladyka.tacocloud.controller;

import by.vladyka.tacocloud.entity.Ingredient;
import by.vladyka.tacocloud.entity.Order;
import by.vladyka.tacocloud.entity.Taco;
import by.vladyka.tacocloud.repository.IngredientRepository;
import by.vladyka.tacocloud.repository.TacoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping(value = "/design")
//говорим, что attribute модели мы будем хранить в сессисии.
@SessionAttributes("order")
public class DesignTacoController {
    private final IngredientRepository ingredientRepo;
    private final TacoRepository tacoRepository;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepo, TacoRepository tacoRepository) {
        this.ingredientRepo = ingredientRepo;
        this.tacoRepository = tacoRepository;
    }

    //Метод, который говорит, что в моделе будет создан атрибут order. Создается при первом обращении к классу.
    @ModelAttribute(name = "order")
    public Order order() {
        return new Order();
    }

    //Метод, который говорит, что в моделе будет создан атрибут taco. Создается при первом обращении к классу
    @ModelAttribute(name = "taco")
    public Taco taco() {
        return new Taco();
    }


    @PostMapping
    public String processDesign(@Valid Taco taco, Errors errors,
                                @ModelAttribute Order order,
                                HttpServletRequest request) {
        if (errors.hasErrors()) {
            return "design";
        }

        log.info("Processing design: " + taco);

        Taco saved = tacoRepository.save(taco);
        order.addDesign(saved);

        return "redirect:/orders/current";
    }

    @GetMapping
    public String showDesignForm(Model model, HttpServletRequest request) {
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepo.findAll().forEach(ingredients::add);

        Ingredient.Type[] types = Ingredient.Type.values();
        for (Ingredient.Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }
        return "design";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients, Ingredient.Type type) {
        return ingredients
                .stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }
}
