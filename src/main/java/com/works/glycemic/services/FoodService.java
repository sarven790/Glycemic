package com.works.glycemic.services;

import com.works.glycemic.config.AuditAwareConfig;
import com.works.glycemic.models.Foods;
import com.works.glycemic.repositories.FoodRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FoodService {

    final FoodRepository fRepo;
    final AuditAwareConfig auditAwareConfig;
    public FoodService(FoodRepository fRepo, AuditAwareConfig auditAwareConfig) {
        this.fRepo = fRepo;
        this.auditAwareConfig = auditAwareConfig;
    }


    // food save
    public Foods foodsSave( Foods foods ) {
        Optional<Foods> oFoods = fRepo.findByNameEqualsIgnoreCase(foods.getName());
        if (oFoods.isPresent() ) {
            return null;
        }else {
            foods.setEnabled(false);
            return fRepo.save(foods);
        }
    }

    // food list
    public List<Foods> foodsList() {
        return fRepo.findAll();
    }


    // user food list
    public List<Foods> userFoodList() {
        Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();
        if (oUserName.isPresent() ) {
            return fRepo.findByCreatedByEqualsIgnoreCase( oUserName.get() );
        }else {
            return new ArrayList<Foods>();
        }

    }

    //user food delete
    public Foods userFoodDelete(long gid) {

        Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();

        if (oUserName.isPresent()) {

            Optional<Foods> oFoods = fRepo.findByGidAndCreatedByIgnoreCase(gid, oUserName.get());

            if (oFoods.isPresent()) {

                Foods foods = oFoods.get();
                fRepo.deleteById(gid);

                return foods;
            }
        }

        return null;
    }

    //user food update
    public Foods userFoodUpdate(Foods foods){

        Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();

        if(oUserName.isPresent()) {
            Optional<Foods> oFoods = fRepo.findByGidAndCreatedByIgnoreCase(foods.getGid(),oUserName.get());

            if(oFoods.isPresent()) {
                Foods f = oFoods.get();
                f.setCid(foods.getCid());
                f.setName(foods.getName());
                f.setGlycemicindex(foods.getGlycemicindex());
                f.setImage(foods.getImage());
                f.setSource(foods.getSource());

                fRepo.saveAndFlush(f);

                return f;

            }

        }

        return null;

    }

}