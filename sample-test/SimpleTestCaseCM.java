import junit.framework.TestCase;
import coffeemaker.*;

public class SimpleTestCaseCM extends TestCase {

    protected void setUp() throws Exception {
	super.setUp();
	ch.unisi.inf.datec.instrument.CoverageCalculator.reset();
    }

    public void test1(){
	Recipe r = new Recipe();
	r.setAmtChocolate(5);
	r.setAmtMilk(5);
	r.setAmtSugar(4);
	r.setAmtCoffee(4);
	r.setName("coffee");
	r.setPrice(3);
	CoffeeMaker cm = new CoffeeMaker();
	cm.addRecipe(r);
    }

    public void test2(){
	Recipe r = new Recipe();
	r.setAmtChocolate(5);
	r.setAmtMilk(5);
	r.setAmtSugar(4);
	r.setAmtCoffee(4);
	r.setName("coffee");
	r.setPrice(3);
	CoffeeMaker cm = new CoffeeMaker();
	cm.addRecipe(r);
	cm.deleteRecipe(r);
    }

    public void test3(){
	Recipe r = new Recipe();
	r.setAmtChocolate(5);
	r.setAmtMilk(5);
	r.setAmtSugar(4);
	r.setAmtCoffee(4);
	r.setName("coffee");
	r.setPrice(3);

	Recipe rNew = new Recipe();
	rNew.setAmtChocolate(3);
	rNew.setAmtMilk(3);
	rNew.setAmtSugar(3);
	rNew.setAmtCoffee(4);
	rNew.setName("coffee");
	rNew.setPrice(2);

	CoffeeMaker cm = new CoffeeMaker();
	cm.addRecipe(r);
	cm.editRecipe(r, rNew);
    }

    public void test4(){
	CoffeeMaker cm = new CoffeeMaker();
	cm.addInventory(10, 10, 5, 4);
    }

    public void test5(){
	CoffeeMaker cm = new CoffeeMaker();
	cm.checkInventory();
    }

    public void test6(){
	Recipe r = new Recipe();
	r.setAmtChocolate(5);
	r.setAmtMilk(5);
	r.setAmtSugar(4);
	r.setAmtCoffee(4);
	r.setName("coffee");
	r.setPrice(3);
	CoffeeMaker cm = new CoffeeMaker();
	cm.addRecipe(r);
	cm.makeCoffee(r, 4);
    }

    public void test7(){
	Recipe r = new Recipe();
	r.setAmtChocolate(5);
	r.setAmtMilk(5);
	r.setAmtSugar(4);
	r.setAmtCoffee(4);
	r.setName("coffee");
	r.setPrice(3);
	CoffeeMaker cm = new CoffeeMaker();
	cm.addRecipe(r);
	cm.getRecipes();
    }

    public void test8(){
	Recipe r = new Recipe();
	r.setAmtChocolate(5);
	r.setAmtMilk(5);
	r.setAmtSugar(4);
	r.setAmtCoffee(4);
	r.setName("coffee");
	r.setPrice(3);
	CoffeeMaker cm = new CoffeeMaker();
	cm.addRecipe(r);
	cm.getRecipeForName("coffee");
    }

    public void test9(){
	Recipe r = new Recipe();
	r.setAmtChocolate(5);
	r.setAmtMilk(5);
	r.setAmtSugar(4);
	r.setAmtCoffee(4);
	r.setName("coffee");
	r.setPrice(3);
	CoffeeMaker cm = new CoffeeMaker();
	cm.addRecipe(r);
	cm.makeCoffee(r, 4);
	cm.makeCoffee(r, 3);
    }
}
