package com.shop.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@SpringBootTest
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    EntityManager em;

    @Test
    @DisplayName("상품 지정 테스트")
    public void createItemTest(){


        Item item = new Item();
        item.setItemNm("테스트 상품");
        item.setPrice(10000);
        item.setItemDetail("테스트 상품 상세 설명");
        item.setItemsellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        Item savedItem = itemRepository.save(item);
        log.info(savedItem.toString());

    }

    @Test
    @DisplayName("상품 리스트 저장")
    public void createItemList(){
        for (int i=1; i<=10; i++){


        Item item = new Item();
        item.setItemNm("테스트 상품" + i);
        item.setPrice(10000+i);
        item.setItemDetail("테스트 상품 상세 설명" + i);
        item.setItemsellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        Item savedItem = itemRepository.save(item);
        }
    }
    @Test
    @DisplayName("상품명 조회 테스트")
    public void createdItemNmTest(){
        // this.createItemTest();
        List<Item> itemList = itemRepository.findByItemNm("테스트 상품1");
        for (Item item: itemList){
            System.out.println(item.toString());
        }
    }
    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNmOrItemDetailTest() {
        //this.createItemTest();
        List<Item> itemList =
                itemRepository.findByItemNmOrItemDetail("테스트 상품1", "테스트 상품 상세 설명5");
        for (Item item: itemList){
            System.out.println(item.toString());
        }

    }
    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanTest() {
        //this.createItemTest();
        List<Item> itemList =
                itemRepository.findByPriceLessThan(10005);
        for (Item item: itemList){
            System.out.println(item.toString());
        }

    }
    @Test
    @DisplayName("가격 LessThan 테스트")
    public void findByPriceLessThanOrderByPriceDescTest() {
        //this.createItemTest();
        List<Item> itemList =
                itemRepository.findByPriceLessThanOrderByPriceDesc(10005);
        for (Item item: itemList){
            System.out.println(item.toString());
        }

    }
    @Test
    @DisplayName("@Query를 이용한 상품 조회 테스트")
    public void findByItemDetailTest(){
         //    this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("테스트 상품 상세 설명");
        for (Item item : itemList){
            System.out.println(item.toString());
        }
    }
    @Test
    @DisplayName("@NativeQuery를 이용한 상품 조회 테스트")
    public void findByItemDetailByNativeTest(){
        //     this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetailByNative("테스트 상품 상세 설명");
        for (Item item : itemList){
            System.out.println(item.toString());
        }
    }
    @Test
    @DisplayName("Querydsl 조회 테스트1")
    public void queryDslTest(){
        //this.createItemList();
        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
        QItem qItem = QItem.item;
        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
                .where(qItem.itemsellStatus.eq(ItemSellStatus.SELL))
                .where(qItem.itemDetail.like("%" + "테스트 상품 상세 설명" + "%"))
                .orderBy(qItem.price.desc());

        List<Item> itemList = query.fetch();

        for (Item item : itemList){
            System.out.println(item.toString());
        }

    }
    public void createItemList2(){
        for (int i=1; i<=5; i++){
            Item item = new Item();
            item.setItemNm("테스트 상품" + i);
            item.setPrice(10000+i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemsellStatus(ItemSellStatus.SOLD_OUT);
            item.setStockNumber(0);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            itemRepository.save(item);
        }
    }
    @Test
    @DisplayName("상품 Querydsl 조회 테스트 2")
    public void queryDslTest2(){

        this.createItemList2();

        BooleanBuilder booleanBuilder = new BooleanBuilder();
        QItem item = QItem.item;
        String itemDetail = "테스트 상품 상세 설명";
        int price = 10003;
        String itemSellStat = "SELL";

        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
        booleanBuilder.and(item.price.gt(price));

        if (StringUtils.equals(itemSellStat, ItemSellStatus.SELL)){
            booleanBuilder.and(item.itemsellStatus.eq(ItemSellStatus.SELL));
        }

        Pageable pageable = PageRequest.of(0, 5);
        Page<Item> itemPageingResult =
                itemRepository.findAll(booleanBuilder, pageable);
        System.out.println("total elements : " + itemPageingResult. getTotalElements());

        List<Item> resultItemList = itemPageingResult.getContent();
        for (Item resultItem: resultItemList){
            System.out.println(resultItem.toString());
        }
    }
}