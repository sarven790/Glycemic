package com.works.glycemic.restcontrollers;

import com.works.glycemic.models.Foods;
import com.works.glycemic.services.FoodService;
import com.works.glycemic.utils.REnum;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/foods")
public class FoodsRestController {

    final FoodService foodService;
    public FoodsRestController(FoodService foodService) {
        this.foodService = foodService;
    }

    // Foods Save
    @PostMapping("/save")
    public Map<REnum, Object> save(@RequestBody Foods foods) {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        Foods f = foodService.foodsSave(foods);
        if ( f == null ) {
            hm.put(REnum.status, false);
            hm.put(REnum.message, "Bu ürün daha önce kayıt edilmiş");
            hm.put(REnum.result, f);
        }else {
            hm.put(REnum.status, true);
            hm.put(REnum.message, "Ürün kayıt başarılı");
            hm.put(REnum.result, f);
        }
        return hm;
    }


    // foods List
    @Cacheable("foods_list")
    @GetMapping("/list")
    public Map<REnum, Object> list() {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, true);
        hm.put(REnum.message, "Ürün Listesi");
        hm.put(REnum.result, foodService.foodsList() );
        return hm;
    }


    // foods List
    @GetMapping("/userFoodList")
    public Map<REnum, Object> userFoodList() {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, true);
        hm.put(REnum.message, "Ürün Listesi");
        hm.put(REnum.result, foodService.userFoodList());
        return hm;
    }

    // foods List
    @GetMapping("/adminWaitFoodList")
    public Map<REnum, Object> adminWaitFoodList() {
        Map<REnum, Object> hm = new LinkedHashMap<>();
        hm.put(REnum.status, true);
        hm.put(REnum.message, "Ürün Listesi");
        hm.put(REnum.result, foodService.adminWaitFoodList());
        return hm;
    }

    // food delete
    @DeleteMapping("/foodDelete")
    public Map<REnum,Object> userFoodDelete(@RequestParam("gid") String gid) {

        Map<REnum, Object> hm = new LinkedHashMap<>();

        try {

            Long Ggid = Long.parseLong(gid);

            String f = foodService.userFoodDelete(Ggid);

            if (!f.equals("Silme işlemi başarılı")) {

                hm.put(REnum.status, false);
                hm.put(REnum.message, f);

            } else {

                hm.put(REnum.status, true);
                hm.put(REnum.message, f);

            }

        } catch (Exception ex) {

            hm.put(REnum.status, false);
            hm.put(REnum.message, "Silme işlemi sırasında bir hata oluştu");

        }

        return hm;

    }

    // food update
    @PutMapping("foodUpdate")
    public Map<REnum,Object> foodUpdate(@RequestBody Foods food){
        return foodService.userUpdateFood(food);
    }


    @GetMapping("detail/{url}")
    public Map<REnum, Object> singleFoodUrl(@PathVariable String url){
        Map<REnum, Object> hm = new LinkedHashMap<>();
        Optional<Foods> oFoods = foodService.singleFoodUrl(url);
        if (oFoods.isPresent() ) {
            hm.put(REnum.status, true);
            hm.put(REnum.message, "Ürün detay alındı");
            hm.put(REnum.result, oFoods.get());
        }else {
            hm.put(REnum.status, false);
            hm.put(REnum.message, "Ürün detay bulunamadı");
            hm.put(REnum.result, null);
        }
        return hm;
    }



}