package fashion.coin.wallet.back.fiat.dto;

public class LogsRequestDTO {
    Long start;
    Long end;
    Boolean onlysuccessful;

    public Long getStart() {
        if (start == null) start = 0L;
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        if (end == null) end = 9999999999L;
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Boolean getOnlysuccessful() {
        if (onlysuccessful == null) onlysuccessful = true;
        return onlysuccessful;
    }

    public void setOnlysuccessful(Boolean onlysuccessful) {
        this.onlysuccessful = onlysuccessful;
    }
}
