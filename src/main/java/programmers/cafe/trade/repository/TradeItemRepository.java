package programmers.cafe.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import programmers.cafe.trade.domain.entity.TradeItem;

public interface TradeItemRepository extends JpaRepository<TradeItem, Long> {
}
