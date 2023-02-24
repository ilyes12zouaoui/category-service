package ilyes.de.categoryservice.service;

import ilyes.de.categoryservice.config.exception.CategoryNotFoundException;
import ilyes.de.categoryservice.mapper.CategoryMapper;
import ilyes.de.categoryservice.mapper.to.CategoryCreateOrUpdateTo;
import ilyes.de.categoryservice.mapper.to.CategoryTo;
import ilyes.de.categoryservice.repository.CategoryRepository;
import ilyes.de.categoryservice.repository.entity.CategoryEntity;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private static final Logger LOGGER = LogManager.getLogger(CategoryService.class);
    private CategoryRepository categoryRepository;
    private CategoryMapper categoryMapper;

    private Category2Service category2Service;

    public CategoryService(Category2Service category2Service,CategoryRepository categoryRepository, CategoryMapper categoryMapper){
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
        this.category2Service = category2Service;
    }

    public CategoryTo createCategory(CategoryCreateOrUpdateTo categoryTo){
        CategoryEntity categoryEntity = categoryMapper.mapToCategoryEntityFields(categoryTo);
        categoryRepository.save(categoryEntity);
        return categoryMapper.mapToCategoryTo(categoryEntity);
    }

    public CategoryTo updateCategory(CategoryCreateOrUpdateTo categoryCreateOrUpdateTo, long id){
        CategoryEntity categoryEntity = tryGetCategoryEntityById(id);

        categoryEntity.setName(categoryCreateOrUpdateTo.getName());

        categoryRepository.save(categoryEntity);

        return  categoryMapper.mapToCategoryTo(categoryEntity);
    }

    @Transactional
    public CategoryTo deleteCategory(long id){
        CategoryEntity categoryEntity = tryGetCategoryEntityById(1);
        categoryRepository.delete(categoryEntity);
        aa();
        throw  new RuntimeException();
       // return  new CategoryTo();
    //    return  categoryMapper.mapToCategoryTo(categoryEntity);
    }
    public void aa(){
        CategoryEntity categoryEntity = tryGetCategoryEntityById(2);
        categoryRepository.delete(categoryEntity);
        throw  new RuntimeException();
    }
    public List<CategoryTo> getAllCategories(){
       return categoryRepository.findAll().stream().map(categoryMapper::mapToCategoryTo).collect(Collectors.toList());
    }

    public CategoryTo tryGetCategoryTOById(long id){
        return categoryMapper.mapToCategoryTo(tryGetCategoryEntityById(id));
    }

    public CategoryEntity tryGetCategoryEntityById(long id,HttpStatus httpStatus,String logType){
        Optional<CategoryEntity> optionalCategoryEntity =  categoryRepository.findById(id);
        return optionalCategoryEntity.orElseThrow(()-> {
            if(logType==null && httpStatus==null) {
                return new CategoryNotFoundException(id, HttpStatus.NOT_FOUND);
            }else if(logType == null){
                return new CategoryNotFoundException(id, httpStatus);
            }else{
                return new CategoryNotFoundException(id, httpStatus, logType);
            }
        });
    }

    public CategoryEntity tryGetCategoryEntityById(long id,HttpStatus httpStatus){
        return this.tryGetCategoryEntityById(id, httpStatus,null);
    }

    public CategoryEntity tryGetCategoryEntityById(long id){
        return this.tryGetCategoryEntityById(id,null,null);
    }
}
