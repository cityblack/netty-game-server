package com.lzh.game.start;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class User  {

    private String name;
    private int age;
    private String address;
    private String tel;

    private String work;
    private float money;
    private Map<String, Equipment> equipStorage;
    private List<Item> items;



    @Data
    static class Equipment {
        private String name;
        private long id;
        private long updateTime;
        private boolean effect;
    }

    @Data
    static class Item {
        private String name;
        private long id;
        private int model;
        private String sign;
        private String type;
        private int i18n;
    }

    public User() {
    }

    private static User.Equipment equipment() {
        User.Equipment equipment = new User.Equipment();
        equipment.setEffect(true);
        equipment.setId(100862211);
        equipment.setName("黄金");
        equipment.setUpdateTime(System.nanoTime());
        return equipment;
    }

    private static User.Item item() {
        User.Item item = new User.Item();
        item.setI18n(10086);
        item.setId(100223111);
        item.setName("白色镜子");
        item.setModel(1903311);
        item.setSign("Guard_Item");
        item.setType("UserAble");
        return item;
    }

    public static User createUser() {
        User user = new User();

        user.setAddress("福建省宁德市，寿宁县");
        user.setName("刘泽弘");
        user.setAge(26);
        user.setMoney(10.23f);
        user.setWork("37互娱");
        user.setTel("18928907870");
        Map<String, User.Equipment> equipmentMap = new HashMap<>();
        equipmentMap.put("A", equipment());
        equipmentMap.put("B", equipment());
        user.setEquipStorage(equipmentMap);
        List<User.Item> list = new ArrayList<>();
        list.add(item());
        list.add(item());

        return user;
    }
}
