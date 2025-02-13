package programmers.cafe.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import programmers.cafe.trade.domain.entity.Trade;

import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    Optional<Trade> findByTradeUUID(String tradeUUID);

    @Query("SELECT t FROM Trade t JOIN FETCH t.tradeItems WHERE t.tradeUUID = :tradeUUID")
    Optional<Trade> findByTradeUUIDWithItems(@Param("tradeUUID") String tradeUUID);

    Optional<Trade> findTopByOrderByIdDesc();
}
