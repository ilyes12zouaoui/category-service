package ilyes.de.categoryservice.config;

import ilyes.de.categoryservice.repository.CategoryRepository;
import ilyes.de.categoryservice.repository.entity.CategoryEntity;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartUpData {

    @Autowired
    CategoryRepository categoryRepository;

    @PostConstruct
    public void initDataBase(){
        categoryRepository.save(new CategoryEntity("Chocolate"));
        categoryRepository.save(new CategoryEntity("Car"));
        categoryRepository.save(new CategoryEntity("Fruit"));
    }
}