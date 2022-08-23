package model;

import java.math.BigDecimal;
import java.util.Optional;

public class DefaultSetting extends Setting{

    @Override
    public String getName() {
        return "default";
    }

    @Override
    public Optional<Boolean> getExactMatch(){
        return TRI_UNKNOWN;
    }

    @Override
    public BigDecimal getWinningPoint(){
        return new BigDecimal(1);
    }

    @Override
    public BigDecimal getPenalityPoint(){
        return new BigDecimal(1);
    }
}
