package programmers.cafe.item.domain.entity;

public enum ItemCategory {
    BEVERAGE("음료"),
    DESSERT("디저트"),
    ETC("기타");

    private final String category;

    private ItemCategory(String category) {
        this.category = category;
    }
}
