package model;

import java.math.BigDecimal;

public class DefaultSetting extends Setting{

    @Override
    public String getName() {
        return "default";
    }

    @Override
    public Match getExactMatch(){
        return Match.UNKNOWN;
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
