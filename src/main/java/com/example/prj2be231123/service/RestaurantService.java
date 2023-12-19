package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.*;
import com.example.prj2be231123.mapper.*;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantMapper mapper;

    private final CategoryMapper categoryMapper;

    private final RestaurantPurposeMapper purposeMapper;

    private final RestaurantPurposeJoinMapper purposeJoinMapper;

    private final RestaurantFileMapper fileMapper;

    private final  ReviewMapper reviewMapper;

    private final ReviewService reviewSercive;

    @Value("${aws.bucketName}")
    private String bucket;

    @Value("${image.file.prefix}")
    private String urlPrefix;

    private final S3Client s3;



    public boolean save(
            Restaurant restaurant, String restaurantTypeName, List<String> restaurantPurpose, MultipartFile[] files,
            Member login) throws IOException {
        restaurant.setWriter(login.getId());

        int typeNo = categoryMapper.getTypeNo(restaurantTypeName);
        restaurant.setRestaurantType(typeNo);

        int cnt = mapper.save(restaurant);

        if (restaurantPurpose != null) {
            for (int i = 0; i < restaurantPurpose.size(); i++) {

                int purposNo = categoryMapper.getPurposeNo(restaurantPurpose.get(i));

                purposeJoinMapper.insert(purposNo, restaurant.getNo());
            }
        }

        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                fileMapper.insert(restaurant.getNo(), files[i].getOriginalFilename());
                upload(restaurant.getNo(), files[i]);
            }
        }
        return cnt == 1;
    }


    private void upload(Integer restaurantNo, MultipartFile file) throws IOException {
        String key = "prj2/restaurant/" + restaurantNo + "/" + file.getOriginalFilename();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3.putObject(objectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
    }


    public HashMap<String, Object> get(Integer no) {

        HashMap<String, Object> map = new HashMap<>();

        Restaurant restaurant = mapper.getId(no);

        List<Integer> purposeById = purposeJoinMapper.getPurposeById(no);

        List<RestaurantPurpose> restaurantPurposes = new ArrayList<>();

        for (Integer purposeId : purposeById) {
            restaurantPurposes.add(purposeMapper.getByName(purposeId));

        }

        restaurant.setPurpose(restaurantPurposes);

        List<RestaurantFile> files = fileMapper.selectNamesById(no);

        for (RestaurantFile restaurantFile : files) {
            String url = urlPrefix + "prj2/restaurant/" + no + "/" + restaurantFile.getFileName();
            restaurantFile.setUrl(url);

        }

        restaurant.setFiles(files);

        List<Review> reivews = new ArrayList<>();

        reivews.addAll(reviewMapper.selectByRestaurant(no));

        map.put("restaurant",restaurant);
        map.put("reviews",reivews);
        return map;
    }

    public HashMap<String, Object> selectAll(
            Integer page, String keyword,
            String category, List<String> checkBoxIds, Integer typeno) {

        HashMap<String, Object> map = new HashMap<>();

        HashMap<String, Object> pageInfo = new HashMap<>();

        List<Restaurant> restaurantList = new ArrayList<>();

        int countAll = 0;
        int from = (page - 1) * 6;

        if(checkBoxIds.size()==0){

            countAll =  mapper.countAll(typeno,"%" + keyword + "%", category);
            restaurantList = mapper.selectAll(from,typeno,"%" + keyword + "%", category);

        }

        if (checkBoxIds.size() !=0){
            List<Integer> purposeNo = new ArrayList<>();
            for (String name : checkBoxIds) {
                purposeNo.add(purposeMapper.getByNo(name));
            }

            List<Integer> restaurantNo = new ArrayList<>();

            for (Integer no : purposeNo){
                restaurantNo.addAll(purposeJoinMapper.selectByRestaurantsNo(no));
            }

            List<Integer> newRestaurantNo = restaurantNo
                    .stream()
                    .distinct()
                    .toList();
            int limit = (newRestaurantNo.size()-(newRestaurantNo.size()-(from+6)));

            if(newRestaurantNo.size() < limit) {
                int num = limit-newRestaurantNo.size();
                int newLimit = limit-num;
                for (int j = from; j < newLimit; j++) {
                    Integer no = newRestaurantNo.get(j);
                    restaurantList.addAll(mapper.getIdSelectAll(no));
                }
            }else{
                for (int j = from; j < limit; j++) {
                    Integer no = newRestaurantNo.get(j);
                    restaurantList.addAll(mapper.getIdSelectAll(no));
                }
            }
            countAll =newRestaurantNo.size();
        }

        int lastPageNumber = (countAll - 1) / 6+ 1;

        int startPageNumber = (page - 1) / 5 * 5+ 1;

        int endPageNumber = startPageNumber + 4;

        int prevPageNumber = startPageNumber - 5;
        int nextPageNumber = endPageNumber + 1;

        endPageNumber = Math.min(endPageNumber, lastPageNumber);

        pageInfo.put("currentPageNumber", page);
        pageInfo.put("startPageNumber", startPageNumber);
        pageInfo.put("endPageNumber", endPageNumber);
        pageInfo.put("lastPageNumber", lastPageNumber);
        if (prevPageNumber > 0) {
            pageInfo.put("prevPageNumber", prevPageNumber);
        }

        if (nextPageNumber <= lastPageNumber) {
            pageInfo.put("nextPageNumber", nextPageNumber);
        }

        for (Restaurant restaurant : restaurantList) {
            List<RestaurantFile> files = fileMapper.selectAllNamesById(restaurant.getNo());

            for (RestaurantFile file : files) {
                String url = urlPrefix + "prj2/restaurant/" + restaurant.getNo() + "/" + file.getFileName();
                file.setUrl(url);
            }
            restaurant.setFiles(files);
        }

        map.put("restaurantList", restaurantList);
        map.put("pageInfo", pageInfo);
        map.put("restaurantTypes", categoryMapper.getTypes());
        map.put("restaurantPurpose", categoryMapper.getPurpose());
        return map;
    }


    public boolean remove(Integer no) {
        purposeJoinMapper.deleteByRestaurantNo(no);

        List<Integer> reviews = reviewMapper.selectListByRestaurantN(no);

        reviews.forEach((reviewNo)->  reviewSercive.remove(reviewNo));

        deleteFile(no);

        return mapper.deleteByNo(no) == 1;
    }


    private void deleteFile(Integer no) {
        List<RestaurantFile> restaurantFiles = fileMapper.selectNamesById(no);

        for (RestaurantFile file : restaurantFiles) {
            String key = "prj2/restaurant/" + no + "/" + file.getFileName();

            DeleteObjectRequest ObjectRequest =
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build();

            s3.deleteObject(ObjectRequest);
        }

        fileMapper.deleteByFileRestaurantdNo(no);

    }


    public boolean update(
            Restaurant restaurant, String restaurantTypeName,
            List<String> restaurantPurpose, List<Integer> removeFileIds,
            MultipartFile[] uploadFiles) throws IOException {

        int typeNo = categoryMapper.getTypeNo(restaurantTypeName);
        restaurant.setRestaurantType(typeNo);

        if (restaurantPurpose != null) {
            purposeJoinMapper.deleteByRestaurantNo(restaurant.getNo());
            for (int i = 0; i < restaurantPurpose.size(); i++) {

                int purposNo = categoryMapper.getPurposeNo(restaurantPurpose.get(i));

                purposeJoinMapper.insert(purposNo, restaurant.getNo());
            }
        }


        if (removeFileIds != null) {
            for (Integer no : removeFileIds) {
                RestaurantFile file = fileMapper.selectByNo(no);
                String key = "prj2/restaurant/" + restaurant.getNo() + "/" + file.getFileName();

                DeleteObjectRequest ObjectRequest =
                        DeleteObjectRequest.builder()
                                .bucket(bucket)
                                .key(key)
                                .build();

                s3.deleteObject(ObjectRequest);
                fileMapper.deleteById(no);
            }
        }

        if (uploadFiles != null) {
            for (MultipartFile files : uploadFiles) {

                upload(restaurant.getNo(), files);

                fileMapper.insert(restaurant.getNo(), files.getOriginalFilename());
            }
        }

        return mapper.update(restaurant) == 1;

    }

    public boolean restaurantValidate(
            Restaurant restaurant, String restaurantTypeName,
            List<String> restaurantPurpose, MultipartFile[] files) {

        if (restaurant == null){
            return false;
        }

        if (restaurant.getPlace() == null ||restaurant.getPlace().isBlank() ){
            return false;
        }

        if (restaurant.getInfo() == null || restaurant.getInfo().isBlank()){
            return false;
        }

        if(restaurant.getPhone() == null || restaurant.getPhone().isBlank()){
            return false;
        }


        if (restaurant.getAddress() == null || restaurant.getAddress().isBlank()){
            return false;
        }


        if (restaurant.getCity() == null || restaurant.getCity().isBlank()){
            return false;
        }

        if (restaurant.getDistrict() == null || restaurant.getDistrict().isBlank()){
            return false;
        }

        if (restaurant.getX()==null || restaurant.getX().isBlank()){
            return false;
        }

        if (restaurant.getY()== null || restaurant.getY().isBlank()){
            return false;
        }

        if(restaurantTypeName.isBlank() || restaurantTypeName==null){
            return false;
        }

        if (restaurantPurpose==null ){
            return false;
        }

        //
        if(restaurant.getNo()==0 && files ==null){

            return false;
        }


        return  true;
    }

    public boolean hasAccess(Member login) {

        if (login == null){
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

    public HashMap<String,Object> selectTypeList() {
        //TODO : 리스트에 저장된 배열을 담을 HashMap
        HashMap<String,Object> map=new HashMap<>();

        //TODO : 레스토랑 정보를 담을 리스트
        List<Restaurant> restaurantList = new ArrayList<>();

        //TODO : 카테고리 이름 전부 찾아서 리스트에 저장
        List<String> typeName = categoryMapper.getTypesName();

        //TODO : 각 카테고리에 총 테이블 수를 RestaurantType 객체 리스트에 저장
        List<RestaurantType> restaurantTypeList = categoryMapper.getCount();


        for (String name:typeName){
            restaurantList.addAll(mapper.getTypeName(name));
        }

        for (Restaurant restaurant : restaurantList) {
            List<RestaurantFile> files = fileMapper.selectNameById(restaurant.getNo());

            for (RestaurantFile file : files) {
                String url = urlPrefix + "prj2/restaurant/" + restaurant.getNo() + "/" + file.getFileName();
                file.setUrl(url);
            }
            restaurant.setFiles(files);
        }

        map.put("typeName",restaurantTypeList);

        map.put("restaurantList",restaurantList);

        return map;
    }


    public boolean validFileNumber(List<Integer> removeFileIds, MultipartFile[] uploadFiles, Restaurant restaurant) {

        // 파일 갯수 조회해서 removerFileIds 랑 같은 상황에서  uploadFiles가 null 인경우

        Integer fileCount = mapper.selectFileCount(restaurant.getNo()); // 레스토랑 파일 이미지 count 조회
        System.out.println("fileCount = " + fileCount);

       if (removeFileIds != null){
           if(removeFileIds.size() == fileCount && uploadFiles==null){
               return false;
           }
       }

        return true;
    }
}