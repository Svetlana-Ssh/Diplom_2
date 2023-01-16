package praktikum.order;

public class OrderGenerator {
    OrderClient orderClient = new OrderClient();
    public Order simple() {
        return new Order(new String[]{orderClient.getAllAvailableIngredients().extract().body().path("data[0]._id")});
    }

    public Order noIngredients() {
        return new Order(new String[]{});
    }

    public Order notExistingHashIngredients() {
        return new Order(new String[]{"aaaaaaaaaaaaaaaaaaaaaaaa"});
    }
}



