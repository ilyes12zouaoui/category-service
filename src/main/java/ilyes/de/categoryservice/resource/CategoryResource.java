package ilyes.de.categoryservice.resource;

import ilyes.de.categoryservice.config.aop.annotation.WithResourceLogs;
import ilyes.de.categoryservice.config.log.logtype.LogTypeCategoryConstants;
import ilyes.de.categoryservice.mapper.to.CategoryCreateOrUpdateTo;
import ilyes.de.categoryservice.mapper.to.CategoryTo;
import ilyes.de.categoryservice.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app/v1/categories")
public class CategoryResource {

    private final CategoryService categoryService;

    public CategoryResource(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("")
    @WithResourceLogs(logType = LogTypeCategoryConstants.CATEGORY_GET_ALL_HANDLE_REQUEST)
    public List<CategoryTo> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    @WithResourceLogs(logType = LogTypeCategoryConstants.CATEGORY_GET_BY_ID_HANDLE_REQUEST)
    public CategoryTo tryGetCategoryById(@PathVariable long id) {
        return categoryService.tryGetCategoryTOById(id);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    @WithResourceLogs(logType = LogTypeCategoryConstants.CATEGORY_CREATE_HANDLE_REQUEST)
    public CategoryTo createCategory(@Valid @RequestBody CategoryCreateOrUpdateTo categoryTo) {
        return categoryService.createCategory(categoryTo);
    }

    @PutMapping("/{id}")
    @WithResourceLogs(logType = LogTypeCategoryConstants.CATEGORY_UPDATE_HANDLE_REQUEST)
    public CategoryTo updateCategory(@Valid @RequestBody CategoryCreateOrUpdateTo categoryCreateOrUpdateTo, @PathVariable long id) {
        return categoryService.updateCategory(categoryCreateOrUpdateTo, id);
    }

    @DeleteMapping("/{id}")
    @WithResourceLogs(logType = LogTypeCategoryConstants.CATEGORY_DELETE_HANDLE_REQUEST)
    public CategoryTo deleteCategory(@PathVariable long id) {
        return categoryService.deleteCategory(id);
    }

}
