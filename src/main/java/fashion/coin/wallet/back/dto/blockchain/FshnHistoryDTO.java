package fashion.coin.wallet.back.dto.blockchain;

import java.util.ArrayList;
import java.util.List;

public class FshnHistoryDTO {
    List<FshnHistoryTxDTO> result = new ArrayList<>();

    public List<FshnHistoryTxDTO> getResult() {
        return result;
    }

    public void setResult(List<FshnHistoryTxDTO> result) {
        this.result = result;
    }
}
