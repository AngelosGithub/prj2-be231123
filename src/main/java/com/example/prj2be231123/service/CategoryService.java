package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.Member;
import com.example.prj2be231123.domain.RestaurantPurpose;
import com.example.prj2be231123.domain.RestaurantType;
import com.example.prj2be231123.mapper.CategoryMapper;
import com.example.prj2be231123.mapper.RestaurantMapper;
import com.example.prj2be231123.mapper.RestaurantPurposeJoinMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    private final RestaurantMapper restaurantMapper;

    private final RestaurantService restaurantService;

    private final RestaurantPurposeJoinMapper joinMapper;


    public Map<String, Object> getCategoryList() {
        Map<String, Object> map = new HashMap<>();

        map.put("restaurantTypes", categoryMapper.getTypes());
        map.put("restaurantPurpose", categoryMapper.getPurpose());

        return map;
    }

    public boolean restaurantTypeValidate(RestaurantType restaurantType) {

        if (restaurantType.getName()==null ||restaurantType.getName().isBlank()) {
            return false;
        }

        if (categoryMapper.selectTypeName(restaurantType.getName()) != null){
            return false;
        }

        return true;
    }


    public boolean addTypes(RestaurantType restaurantType) {


        return  categoryMapper.typesAdd(restaurantType)==1;
    }

    public boolean restaurantPurposeValidate(RestaurantPurpose restaurantPurpose) {
        if (restaurantPurpose.getName()==null ||restaurantPurpose.getName().isBlank()) {
            return false;
        }

        if (categoryMapper.selectPurposeName(restaurantPurpose.getName()) !=null){
            return false;
        }

        return true;
    }

    public boolean addPurpose(RestaurantPurpose restaurantPurpose) {

        return  categoryMapper.purposeAdd(restaurantPurpose)==1;
    }

    public RestaurantType getTypes(Integer no) {

        return categoryMapper.getByNoType(no);
    }

    public boolean typesUpdate(RestaurantType restaurantType) {

        return categoryMapper.typeUpdate(restaurantType) ==1;
    }

    public RestaurantPurpose getPurpose(Integer no) {

        return categoryMapper.getByNoPurpose(no);
    }

    public boolean purposeUpdate(RestaurantPurpose restaurantPurpose) {

        return categoryMapper.purposeUpdate(restaurantPurpose) ==1;
    }

    public boolean typeRemove(Integer no) {

        List<Integer> categoryTypeList = restaurantMapper.selectListByCategoryNo(no);

        categoryTypeList.forEach((typeNo)-> restaurantService.remove(typeNo));

        return categoryMapper.typeRemove(no) ==1;
    }

    public boolean purposeRemove(Integer no) {

        List<Integer> restaurantByNo = joinMapper.selectByRestaurantNo(no);

        restaurantByNo.forEach((restaurantNo)->   restaurantService.remove(restaurantNo));

        return categoryMapper.purposeRemove(no) ==1;
    }

    public boolean hasAccess(Member login) {

        if(login==null){
            return false;
        }

        boolean admin = login.getAuth().stream()
                .map(n -> n.getManager())
                .anyMatch(a -> a.equals("admin"));

        if(!admin){
            return false;
        }


        return admin;
    }


}
