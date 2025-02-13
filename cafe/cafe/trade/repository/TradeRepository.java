package programmers.cafe.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import programmers.cafe.trade.domain.entity.Trade;

import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    Optional<Trade> findByTradeUUid(String tradeUUID);
}
