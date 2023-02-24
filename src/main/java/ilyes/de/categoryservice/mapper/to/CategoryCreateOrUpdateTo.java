package ilyes.de.categoryservice.mapper.to;

import jakarta.validation.constraints.NotBlank;

public class CategoryCreateOrUpdateTo {

    @NotBlank
    private String name;

    public CategoryCreateOrUpdateTo(String name) {
        this.name = name;
    }

    public CategoryCreateOrUpdateTo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "CategoryCreateOrUpdateTo{" +
                "name='" + name + '\'' +
                '}';
    }
}
