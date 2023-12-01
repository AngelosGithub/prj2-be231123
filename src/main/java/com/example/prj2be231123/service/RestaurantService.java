package com.example.prj2be231123.service;

import com.example.prj2be231123.domain.Restaurant;
import com.example.prj2be231123.domain.RestaurantFile;
import com.example.prj2be231123.domain.RestaurantPurpose;
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


    @Value("${aws.bucketName}")
    private String bucket;

    @Value("${image.file.prefix}")
    private String urlPrefix;

    private final S3Client s3;



    public boolean save(
            Restaurant restaurant, String restaurantTypeName, List<String> restaurantPurpose, MultipartFile[] files
    ) throws IOException {

        //음식 요소
        int typeNo = categoryMapper.getTypeNo(restaurantTypeName);
        restaurant.setRestaurantType(typeNo);

        int cnt = mapper.save(restaurant);


        //RestaurantPurposeJoin 테이블에 정보저장

        if (restaurantPurpose != null) {
            for (int i = 0; i < restaurantPurpose.size(); i++) {

                int purposNo = categoryMapper.getPurposeNo(restaurantPurpose.get(i));

                purposeJoinMapper.insert(purposNo, restaurant.getNo());
            }
        }


        //레스토랑file 테이블에 files 정보 저장
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                fileMapper.insert(restaurant.getNo(), files[i].getOriginalFilename());
                upload(restaurant.getNo(), files[i]);
            }
        }


        return cnt == 1;
    }


    private void upload(Integer restaurantNo, MultipartFile file) throws IOException {
        // 파일 저장 경로
        // c:\Temp\prj2\게시물번호\파일명
        String key = "prj2/restaurant/" + restaurantNo + "/" + file.getOriginalFilename();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        s3.putObject(objectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));


    }


    public Restaurant get(Integer no) {
        Restaurant restaurant = mapper.getId(no);

        // 음식 목적 가기고 오기
        /*
         *  게시물
         *
         * */
        // 해당 게시물에 대한 음식 목적 아이디 가지고 오기
        List<Integer> purposeById = purposeJoinMapper.getPurposeById(no);


        //
        List<RestaurantPurpose> restaurantPurposes = new ArrayList<>();
        for (Integer purposeId : purposeById) {
            restaurantPurposes.add(purposeMapper.getByName(purposeId));

        }

        restaurant.setPurpose(restaurantPurposes);


        // 파일 이미지 가지고 오기
        List<RestaurantFile> files = fileMapper.selectNamesById(no);

        for (RestaurantFile restaurantFile : files) {
            String url = urlPrefix + "prj2/restaurant/" + no + "/" + restaurantFile.getFileName();
            restaurantFile.setUrl(url);

        }

        restaurant.setFiles(files);


        return restaurant;
    }

    public HashMap<String, Object> selectAll(
            Integer page, String keyword,
            String category, List<String> checkBoxIds, Integer typeno) {

        System.out.println("page = " + page);

        System.out.println("checkBoxIds = " + checkBoxIds);
        System.out.println("typeno = " + typeno);



        HashMap<String, Object> map = new HashMap<>();

        HashMap<String, Object> pageInfo = new HashMap<>();

        List<Restaurant> restaurantList = new ArrayList<>();
        //페이징 처리
        int countAll = 0; //총게시판수
        int from = (page - 1) * 6;


       if(checkBoxIds.size()==0){
         countAll =  mapper.countAll(typeno,"%" + keyword + "%", category);
           restaurantList = mapper.selectAll(from,typeno,"%" + keyword + "%", category);

       }

       if (checkBoxIds.size() !=0){
           // 테마 요소 누르는 경우 checkBoxIds 사이즈가 0이 아닌경우
           // 1. 테마 요소 이름으로 no 조회후
           // 2. 레스토랑 다대다 관계 테이블인 join테이블에서 레스토랑 no 가지고 오기
           // 3. 조회한 no 중복 테이블 제거 작업 처리
           // 4. 조회한 레스토랑 no로 레스토랑 데이터 조회후 view에 출력
           // 5. view에 출력시 페이징 처리 및 1페이지 당 6개씩 출력
           List<Integer> purposeNo = new ArrayList<>();
           for (String name : checkBoxIds) {
               purposeNo.add(purposeMapper.getByNo(name));
           }

           List<Integer> restaurantNo = new ArrayList<>();

           for (Integer no : purposeNo){
               restaurantNo.addAll(purposeJoinMapper.selectByRestaurantsNo(no));
           }

           // 리스트 중복 제거 코드
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

        int startPageNumber = (page - 1) / 6 * 6 + 1;

        int endPageNumber = startPageNumber + 5;

        int prevPageNumber = startPageNumber - 6;
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

        // 레스토랑 조인 테이블 기록 삭제
        purposeJoinMapper.deleteByRestaurantNo(no);
        // 레스토랑 폼에 작성된 리뷰 테이블 삭제

        //레스토랑 aws3 버켓 지우기
        //3.
        deleteFile(no);

        return mapper.deleteByNo(no) == 1;
    }


    private void deleteFile(Integer no) {
        //파일명 조회
        List<RestaurantFile> restaurantFiles = fileMapper.selectNamesById(no);

        //  aws3 버켓 지우기
        for (RestaurantFile file : restaurantFiles) {
            String key = "prj2/restaurant/" + no + "/" + file.getFileName();

            DeleteObjectRequest ObjectRequest =
                    DeleteObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build();

            s3.deleteObject(ObjectRequest);
        }

        // 4. 게시물에 달린 이미지 삭제
        fileMapper.deleteByFileRestaurantdNo(no);

    }


    public boolean update(
            Restaurant restaurant, String restaurantTypeName,
            List<String> restaurantPurpose, List<Integer> removeFileIds,
            MultipartFile[] uploadFiles) throws IOException {
        // 카테고리 및 테 마 변경
        System.out.println("removeFile = " + removeFileIds);
        //음식 요소
        int typeNo = categoryMapper.getTypeNo(restaurantTypeName);
        restaurant.setRestaurantType(typeNo);


        //RestaurantPurposeJoin 테이블에 정보저장

        if (restaurantPurpose != null) {
            purposeJoinMapper.deleteByRestaurantNo(restaurant.getNo());
            for (int i = 0; i < restaurantPurpose.size(); i++) {

                int purposNo = categoryMapper.getPurposeNo(restaurantPurpose.get(i));

                purposeJoinMapper.insert(purposNo, restaurant.getNo());
            }
        }


        //파일 지우기
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


        //파일 추가 하기
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

        if(files ==null){
            return false;
        }

        return  true;
    }
}
