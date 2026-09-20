package vn.iotstar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping({"/", "/home"})
    public String home() { return "graphql/home"; }

    @GetMapping("/graphql/categories")
    public String graphqlCategories() { return "graphql/categories"; }

    @GetMapping("/graphql/products")
    public String graphqlProducts() { return "graphql/products"; }

    @GetMapping("/admin/categories")
    public String categories() {
        return "admin/category-ajax";
    }

    @GetMapping("/admin/products")
    public String products() {
        return "admin/product-ajax";
    }
}
