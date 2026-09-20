package vn.iotstar;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.graphql.ExecutionGraphQlService;
import org.springframework.graphql.test.tester.ExecutionGraphQlServiceTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:graphqltest;MODE=MySQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop", "storage.location=target/test-uploads"
})
@ActiveProfiles("test")
@AutoConfigureMockMvc
class CatalogGraphqlIntegrationTest {
    @Autowired ExecutionGraphQlService service;
    @Autowired ProductRepository products;
    @Autowired CategoryRepository categories;
    @Autowired MockMvc mvc;
    ExecutionGraphQlServiceTester tester;

    @BeforeEach void setup() {
        products.deleteAll();
        categories.deleteAll();
        tester = ExecutionGraphQlServiceTester.create(service);
    }

    String category(String name) {
        return tester.document("mutation($name:String!){createCategory(input:{categoryName:$name}){categoryId}}")
                .variable("name", name).execute().path("createCategory.categoryId").entity(String.class).get();
    }

    String product(String name, double price, String category) {
        return tester.document("""
                mutation($name:String!,$price:Float!,$category:ID!){
                  createProduct(input:{productName:$name,unitPrice:$price,quantity:2,
                    description:"Demo",discount:0,status:1,categoryId:$category}){productId}
                }
                """).variable("name",name).variable("price",price).variable("category",category)
                .execute().path("createProduct.productId").entity(String.class).get();
    }

    @Test void sortingFilteringAndPagination() {
        String a = category("Phones"), b = category("Books");
        category("Accessories");
        String expensive = product("Phone Pro", 900, a);
        String cheap = product("Book", 20, b);
        String medium = product("Phone Mini", 300, a);
        tester.document("{homeProducts{productId}}").execute().path("homeProducts[*].productId")
                .entityList(String.class).containsExactly(cheap,medium,expensive);
        tester.document("query($id:ID!){homeProducts(categoryId:$id){productId category{categoryName}}}")
                .variable("id",a).execute().path("homeProducts[*].productId")
                .entityList(String.class).containsExactly(medium,expensive);
        tester.document("{products(keyword:\"PHONE\",page:1,size:1){content{productId} totalElements totalPages number}}")
                .execute().path("products.content[0].productId").entity(String.class).isEqualTo(expensive)
                .path("products.totalElements").entity(Double.class).isEqualTo(2d)
                .path("products.totalPages").entity(Integer.class).isEqualTo(2)
                .path("products.number").entity(Integer.class).isEqualTo(1);
        tester.document("{categories(keyword:\"PHON\",page:0,size:1){content{categoryName} totalPages}}")
                .execute().path("categories.content[0].categoryName").entity(String.class).isEqualTo("Phones");
        tester.document("{categories(page:1,size:1){content{categoryName} totalPages}}")
                .execute().path("categories.content[0].categoryName").entity(String.class).isEqualTo("Books")
                .path("categories.totalPages").entity(Integer.class).isEqualTo(3);
    }

    @Test void crudAndReferentialIntegrity() {
        String a = category("Old"), b = category("Other");
        String id = product("Original",100,a);
        tester.document("mutation($id:ID!){deleteCategory(id:$id)}").variable("id",a)
                .execute().errors().satisfy(errors -> assertThat(errors).hasSize(1));
        tester.document("mutation($id:ID!){updateCategory(id:$id,input:{categoryName:\"Updated\"}){categoryName}}")
                .variable("id",b).execute().path("updateCategory.categoryName").entity(String.class).isEqualTo("Updated");
        tester.document("""
                mutation($id:ID!,$category:ID!){updateProduct(id:$id,input:{
                productName:"Edited",unitPrice:50,quantity:3,description:"New",
                discount:5,status:0,categoryId:$category}){productName category{categoryId}}}
                """).variable("id",id).variable("category",b).execute()
                .path("updateProduct.productName").entity(String.class).isEqualTo("Edited")
                .path("updateProduct.category.categoryId").entity(String.class).isEqualTo(b);
        tester.document("query($id:ID!){product(id:$id){quantity}}").variable("id",id)
                .execute().path("product.quantity").entity(Integer.class).isEqualTo(3);
        tester.document("mutation($id:ID!){deleteProduct(id:$id)}").variable("id",id)
                .execute().path("deleteProduct").entity(Boolean.class).isEqualTo(true);
        tester.document("mutation($id:ID!){deleteCategory(id:$id)}").variable("id",a)
                .execute().path("deleteCategory").entity(Boolean.class).isEqualTo(true);
        assertThat(products.count()).isZero();
    }

    @Test void rejectsInvalidInputAndMissingIds() {
        tester.document("mutation{createCategory(input:{categoryName:\"   \"}){categoryId}}")
                .execute().errors().satisfy(errors -> assertThat(errors).isNotEmpty());
        tester.document("{products(page:-1){totalPages}}")
                .execute().errors().satisfy(errors -> assertThat(errors).isNotEmpty());
        tester.document("{categories(size:101){totalPages}}")
                .execute().errors().satisfy(errors -> assertThat(errors).isNotEmpty());
        tester.document("{product(id:999999){productName}}")
                .execute().errors().satisfy(errors -> assertThat(errors).isNotEmpty());
        String a = category("Valid");
        tester.document("""
                mutation($category:ID!){createProduct(input:{productName:"Bad",
                unitPrice:-1,quantity:1,description:"",discount:0,status:1,categoryId:$category}){productId}}
                """).variable("category",a).execute().errors()
                .satisfy(errors -> assertThat(errors).isNotEmpty());
        assertThat(products.count()).isZero();
    }

    @Test void thymeleafPagesAndLegacyRoutes() throws Exception {
        mvc.perform(get("/")).andExpect(status().isOk()).andExpect(view().name("graphql/home"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data-endpoint=\"/graphql\"")));
        mvc.perform(get("/graphql/products")).andExpect(status().isOk()).andExpect(view().name("graphql/products"));
        mvc.perform(get("/graphql/categories")).andExpect(status().isOk()).andExpect(view().name("graphql/categories"));
        mvc.perform(get("/admin/products")).andExpect(view().name("admin/product-ajax"));
        mvc.perform(get("/admin/categories")).andExpect(view().name("admin/category-ajax"));
    }

    @Test void ajaxHttpEndpointAndContextPath() throws Exception {
        var result = mvc.perform(post("/graphql")
                .contentType("application/json")
                .content("{\"query\":\"{categories {totalElements content {categoryId}}}\"}"))
                .andReturn();
        if (result.getRequest().isAsyncStarted()) {
            result = mvc.perform(asyncDispatch(result)).andExpect(status().isOk()).andReturn();
        }
        assertThat(result.getResponse().getStatus()).isEqualTo(200);
        assertThat(result.getResponse().getContentAsString()).contains("\"totalElements\":0.0");
        mvc.perform(get("/shop/home").contextPath("/shop"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("data-endpoint=\"/shop/graphql\"")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/shop/js/catalog.js")));
    }
}
