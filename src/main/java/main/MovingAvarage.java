package main;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.forex.jExpertAdvisor.candles.Candle;
import com.forex.jExpertAdvisor.main.MarketMgr;
import com.forex.jExpertAdvisor.stoplosses.StopLoss;
import com.forex.jExpertAdvisor.trades.ExistingTrades;
import com.forex.jExpertAdvisor.trades.IStrategy;
import com.forex.jExpertAdvisor.trades.TradeMgr;
import com.forex.jExpertAdvisor.trades.TradeType;

public class MovingAvarage implements IStrategy {
	

	

	public void OnDenit() {
		// TODO Auto-generated method stub

	}

	public void OnInit() {
		
    
	}
	
	private BigDecimal getAvg(int duration) {
		 BigDecimal result = new BigDecimal(0);
		List<Candle> candles = MarketMgr.getInstance().getHistoricView().subList(MarketMgr.getInstance().getHistoricView().size()-(duration+1), MarketMgr.getInstance().getHistoricView().size()-1);
		for (Candle candle : candles) {
			result = result.add(candle.getClose());
		}
		return result.divide(new BigDecimal(duration));
		
	}

	public void OnStart() {
		
		if(getAvg(5).compareTo(getAvg(20))>0 && ExistingTrades.getInstance().isEmpty()) {
			TradeMgr.getInstance().open(this, new StopLoss(MarketMgr.getInstance().getAsk().subtract(new BigDecimal(0.1))), TradeType.BUY);
		ExistingTrades.getInstance().forEach((k,v) -> {
			if(v.getType().equals(TradeType.SELL))
				try {
					TradeMgr.getInstance().close(v);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		});
		}
		
		if(getAvg(5).compareTo(getAvg(20))<0 && ExistingTrades.getInstance().isEmpty()) {
			TradeMgr.getInstance().open(this, new StopLoss(MarketMgr.getInstance().getAsk().subtract(new BigDecimal(-0.1))), TradeType.SELL);
			ExistingTrades.getInstance().forEach((k,v) -> {
				if(v.getType().equals(TradeType.BUY))
					try {
						TradeMgr.getInstance().close(v);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			});
			
		}
		

	}

}
