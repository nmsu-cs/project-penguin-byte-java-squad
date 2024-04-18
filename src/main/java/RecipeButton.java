import javax.swing.*;

public class RecipeButton extends JButton {
    private Recipe recipe = null;

    public RecipeButton(Recipe recipe) {
        this.recipe = recipe;
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

}
