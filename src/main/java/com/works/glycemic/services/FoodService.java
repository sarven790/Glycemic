package com.works.glycemic.services;

import com.works.glycemic.config.AuditAwareConfig;
import com.works.glycemic.models.Foods;
import com.works.glycemic.repositories.FoodRepository;
import com.works.glycemic.utils.REnum;
import org.apache.commons.text.WordUtils;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FoodService {

    final FoodRepository fRepo;
    final AuditAwareConfig auditAwareConfig;
    final CacheManager cacheManager;
    public FoodService(FoodRepository fRepo, AuditAwareConfig auditAwareConfig,CacheManager cacheManager) {
        this.fRepo = fRepo;
        this.auditAwareConfig = auditAwareConfig;
        this.cacheManager = cacheManager;
    }


    // food save
    public Foods foodsSave( Foods foods ) {
        Optional<Foods> oFoods = fRepo.findByNameEqualsIgnoreCase(foods.getName());
        if (oFoods.isPresent() ) {
            return null;
        }else {
            foods.setEnabled(false);
            String after = foods.getName().trim().replaceAll(" +", " ");
            after = WordUtils.capitalize(after);
            foods.setName(after);
            foods.setDetailsUrl(charConvert(foods.getName()));
            return fRepo.save(foods);
        }
    }

    // food list
    public List<Foods> foodsList() {
        return fRepo.findByEnabledEqualsOrderByGidDesc(true);
    }

    // admin Wait food list
    public List<Foods> adminWaitFoodList() {
        return fRepo.findByEnabledEqualsOrderByGidDesc(false);
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
    public String userFoodDelete(long gid) {

        if(fRepo.findById(gid).isPresent()) {

            Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();

            if (oUserName.isPresent()) {

                if (auditAwareConfig.roles().contains("ROLE_admin")) {

                    fRepo.deleteById(gid);
                    return "Silme işlemi başarılı";

                } else {

                    Optional<Foods> oFoods = fRepo.findByGidAndCreatedByIgnoreCase(gid, oUserName.get());

                    if (oFoods.isPresent()) {

                        //Foods foods = oFoods.get();
                        fRepo.deleteById(gid);

                        return "Silme işlemi başarılı";
                    } else {
                        return "Bu ürün size ait değil";
                    }
                }
            } else {
                return "Bu işlem için yetkiniz yok!";
            }

        }else{
            return "Aradığınız ürün bulunamamıştır";
        }

    }

    //user food update
    public Map<REnum, Object> userUpdateFood(Foods food) {
        Map<REnum, Object> hm = new LinkedHashMap<>();

        hm.put(REnum.status, true);
        hm.put(REnum.message, "Ürün başarıyla güncellendi");
        hm.put(REnum.result, "id: " + food.getGid());

        Optional<String> oUserName = auditAwareConfig.getCurrentAuditor();
        if (oUserName.isPresent()) {
            String userName = oUserName.get();
            try {
                Foods userFood = fRepo.findById(food.getGid()).get();
                //admin food update
                if (auditAwareConfig.roles().contains("ROLE_admin")) {
                    userFood.setCid(food.getCid());
                    String afterName = food.getName().trim().replaceAll(" +", " ");
                    afterName = WordUtils.capitalize(afterName);
                    userFood.setName(afterName);
                    userFood.setGlycemicindex(food.getGlycemicindex());
                    userFood.setImage(food.getImage());
                    userFood.setDetailsUrl(charConvert(userFood.getName()));
                    userFood.setSource(food.getSource());
                    userFood.setEnabled(food.isEnabled());
                    if ( food.isEnabled() ) {
                        cacheManager.getCache("foods_list").clear();
                    }
                    hm.put(REnum.result, fRepo.save(userFood));
                }
                else {
                    //user food update
                    Optional<Foods> oFood = fRepo.findByGidAndCreatedByIgnoreCase(food.getGid(),userName);
                    if (oFood.isPresent()) {
                        userFood.setCid(food.getCid());
                        String afterName = food.getName().trim().replaceAll(" +", " ");
                        afterName = WordUtils.capitalize(afterName);
                        userFood.setName(afterName);
                        userFood.setGlycemicindex(food.getGlycemicindex());
                        userFood.setImage(food.getImage());
                        userFood.setDetailsUrl(charConvert(food.getName()));
                        userFood.setSource(food.getSource());
                        hm.put(REnum.result, fRepo.save(userFood));
                    }
                    else {
                        hm.put(REnum.status, false);
                        hm.put(REnum.message, "Güncellemek istediğiniz ürün size ait değil!");
                    }
                }
            }
            catch (Exception ex) {
                hm.put(REnum.status, false);
                hm.put(REnum.message, "Update işlemi sırasında bir hata oluştu!");
            }
        } else {
            hm.put(REnum.status, false);
            hm.put(REnum.message, "Bu işleme yetkiniz yok!");
        }
        return hm;
    }

    public static String charConvert(String word)
    {
        word = word.trim();
        String convertWord = word.toLowerCase();
        char[] oldValue = new char[] { 'ö', 'ü', 'ç', 'ı', 'ğ', 'ş' };
        char[] newValue = new char[] { 'o', 'u', 'c', 'i', 'g', 's' };
        for (int count = 0; count < oldValue.length; count++)
        {
            convertWord = convertWord.replace(oldValue[count], newValue[count]);
            convertWord = convertWord.replaceFirst(" ", "-");
            convertWord = convertWord.replace(" ","");
        }
        return convertWord;
    }

    public Optional<Foods> singleFoodUrl(String url) {
        return fRepo.findBydetailsUrlEqualsIgnoreCaseAllIgnoreCase(url);
    }


}