package programmers.cafe.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import programmers.cafe.item.domain.entity.Item;
import programmers.cafe.item.domain.entity.ItemCategory;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByCategory(ItemCategory category);
}
