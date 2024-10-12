package com.lzh.game.framework.repository.entity;

import com.lzh.game.framework.repository.element.BaseEntity;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@ToString
@Document
public class User extends BaseEntity<String> implements Serializable {

    @Id
    private String id;
    private String name;
    private int age;
    private String address;
    private String tel;

    private String work;
    private float money;
    private Map<String, Equipment> equipStorage;
    private List<Item> items;

    @Override
    public String getKey() {
        return id;
    }

    @Data
    static class Equipment {
        private String name;
        private long id;
        private long updateTime;
        private boolean effect;
    }

    @Data
    public static class Item {
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
        equipment.setName("gold");
        equipment.setUpdateTime(System.nanoTime());
        return equipment;
    }

    private static User.Item item() {
        User.Item item = new User.Item();
        item.setI18n(10086);
        item.setId(100223111);
        item.setName("white");
        item.setModel(1903311);
        item.setSign("Guard_Item");
        item.setType("UserAble");
        return item;
    }

    public static User createUser(String key) {
        User user = new User();
        user.setId(key);
        user.setAddress("福建省宁德市，寿宁县");
        user.setName("lzh");
        user.setAge(28);
        user.setMoney(10.23f);
        user.setWork("37互娱");
        user.setTel("1008611");
        Map<String, Equipment> equipmentMap = new HashMap<>();
        equipmentMap.put("A", equipment());
        equipmentMap.put("B", equipment());
        user.setEquipStorage(equipmentMap);
        List<Item> list = new ArrayList<>();
        list.add(item());
        list.add(item());
        user.setItems(list);
        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }

    public Map<String, Equipment> getEquipStorage() {
        return equipStorage;
    }

    public void setEquipStorage(Map<String, Equipment> equipStorage) {
        this.equipStorage = equipStorage;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
