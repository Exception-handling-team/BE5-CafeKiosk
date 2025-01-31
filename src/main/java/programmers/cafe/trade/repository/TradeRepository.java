package programmers.cafe.trade.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import programmers.cafe.trade.domain.entity.Trade;

import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    Optional<Trade> findByTradeUUid(String tradeUUID);

    @Query("SELECT t FROM Trade t JOIN FETCH t.tradeItems WHERE t.tradeUUid = :tradeUUID")
    Optional<Trade> findByTradeUUidWithItems(@Param("tradeUUID") String tradeUUID);

}
