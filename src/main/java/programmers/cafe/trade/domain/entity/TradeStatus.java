package programmers.cafe.trade.domain.entity;

public enum TradeStatus {
    BUY("거래 요청"),
    PAY("결제 완료"),
    END("거래 완료"),
    REFUND("환불 완료"),
    REFUSED("거래 취소");

    private final String status;

    public String getStatus() {
        return status;
    }

    private TradeStatus(String status) {
        this.status = status;
    }
}
