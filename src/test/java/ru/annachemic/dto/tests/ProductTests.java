package ru.annachemic.dto.tests;

import com.github.javafaker.Faker;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.*;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.annachemic.dao.CategoriesMapper;
import ru.annachemic.dao.ProductsMapper;
import ru.annachemic.dto.*;
import ru.annachemic.dto.PrettyLogger;
import ru.annachemic.dto.RetrofitUtils;
import ru.annachemic.dto.CategoryService;
import ru.annachemic.dto.ProductService;
import ru.annachemic.dto.utils.DbUtils;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class ProductTests {
    int productId;
    static ProductsMapper productsMapper;
    static CategoriesMapper categoriesMapper;
    static Retrofit client;
    static ProductService productService;
    static CategoryService categoryService;
    Faker faker = new Faker();
    Product product;
    PrettyLogger prettyLogger = new PrettyLogger();
    private static Product myProduct;


    @BeforeAll
    static void beforeAll() {
        client = RetrofitUtils.getRetrofit();
        productService = client.create(ProductService.class);
        categoryService = client.create(CategoryService.class);
        productsMapper = DbUtils.getProductsMapper();


    }

    @BeforeEach
    void setUp() {
        product = new Product()
                .withTitle(faker.food().dish())
                .withPrice((int) ((Math.random() + 1) * 100))
                .withCategoryTitle(CategoryType.FOOD.getTitle());

    }

    @Order(1)
    @Test
    void postProductTest() throws IOException {
        Integer countProductsBefore = DbUtils.countProducts(productsMapper);
        Response<Product> response = productService.createProduct(product)
                .execute();
        Integer countProductsAfter = DbUtils.countProducts(productsMapper);

        assertThat(countProductsAfter, equalTo(countProductsBefore+1));
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));

        myProduct = response.body();
    }

    @Order(2)
    @Test
    void getCategoryByIdTest() throws IOException {
        DbUtils.createNewCategory(categoriesMapper);

        Integer id = CategoryType.FOOD.getId();
        Response<Category> response = categoryService
                .getCategory(id)
                .execute();
        //prettyLogger.log(response.body().toString());
        assertThat(response.body().getTitle(), equalTo(CategoryType.FOOD.getTitle()));
        assertThat(response.body().getId(), equalTo(id));
    }

    @Order(3)
    @Test
    void getProductsTest() throws IOException {
        Response<ArrayList<Product>> response = productService
                .getProducts()
                .execute();
        Integer countAllProducts = DbUtils.countProducts(productsMapper);
        System.out.println(countAllProducts);
        //prettyLogger.log(response.body().toString());
            }

    @Order(4)
    @Test
    void putModifyProductTest() throws IOException {
        Integer id = myProduct.getId();
        myProduct.setPrice(1000);
        Response<Product> response = productService.modifyProduct(myProduct)
                .execute();
        prettyLogger.log(response.body().toString());
        assertThat(response.body().getTitle(), equalTo(myProduct.getTitle()));
        assertThat(response.body().getPrice(), equalTo(myProduct.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(myProduct.getCategoryTitle()));

    }

    @Order(5)
    @Test
    void getProductsByIdTest() throws IOException {
        Integer id = myProduct.getId();
        Response<Product> response = productService
                .getProduct(id)
                .execute();
        prettyLogger.log(response.body().toString());
        assertThat(response.body().getTitle(), equalTo((myProduct.getTitle())));
        assertThat(response.body().getId(), equalTo(id));
    }

    @Order(6)
    @Test
    void deleteProductsTest() throws IOException {
        Integer countProductsBefore = DbUtils.countProducts(productsMapper);
       Integer id = myProduct.getId();
       Response<ResponseBody> response = productService.deleteProduct(id)
               .execute();
        Integer countProductsAfter = DbUtils.countProducts(productsMapper);
        assertThat(countProductsAfter, equalTo((countProductsBefore-1)));
        assertThat(response.isSuccessful(), equalTo(true));

    }

    @Order(7)
    @Test
    void negativeGetProductsByDeletedIdTest() throws IOException {
        Integer id = myProduct.getId();
        Response<Product> response = productService
                .getProduct(id)
                .execute();
        assertThat(response.isSuccessful(), equalTo(false));
        assertThat(response.code(), equalTo(404));
    }

    @Order(8)
    @Test
    void negativeModifyProductTest() throws IOException {
        Integer id = null;
        myProduct.setPrice(null);
        Response<Product> response = productService.modifyProduct(myProduct)
                .execute();
        assertThat(response.isSuccessful(), equalTo(false));
        assertThat(response.code(), equalTo(400));

    }
}
