package ilyes.de.categoryservice.mapper;

import ilyes.de.categoryservice.config.aop.annotation.WithMethodLogs;
import ilyes.de.categoryservice.repository.entity.CategoryEntity;
import ilyes.de.categoryservice.mapper.to.CategoryTo;
import ilyes.de.categoryservice.mapper.to.CategoryCreateOrUpdateTo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

   private static final Logger LOGGER = LogManager.getLogger(CategoryMapper.class);

   @WithMethodLogs
   public CategoryEntity mapToCategoryEntityFields(CategoryCreateOrUpdateTo categoryTo) {
       if(categoryTo == null) {
           return null;
       }
       return new CategoryEntity(
               categoryTo.getName()
       );
   }

    public CategoryTo mapToCategoryTo(CategoryEntity categoryEntity) {

        if(categoryEntity == null) {
            return null;
        }

        return new CategoryTo(
                categoryEntity.getId(),
                categoryEntity.getName(),
                categoryEntity.getCreatedAt(),
                categoryEntity.getModifiedAt()
        );
    }
}
