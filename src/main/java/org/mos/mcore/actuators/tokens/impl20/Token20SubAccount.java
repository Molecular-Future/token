package org.mos.mcore.actuators.tokens.impl20;

import com.google.protobuf.ByteString;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mos.mcore.actuators.tokencontracts20.TokensContract20.TokenRC20Value;
import org.mos.mcore.concurrent.AccountInfoWrapper;
import org.mos.mcore.concurrent.AtomicBigInteger;
import org.mos.mcore.concurrent.IAccountBuilder;
import org.mos.mcore.tools.bytes.BytesHelper;

import java.math.BigInteger;

@Data
@Slf4j
public class Token20SubAccount implements IAccountBuilder {

	AccountInfoWrapper account;
	TokenRC20Value.Builder rc20;

	private AtomicBigInteger tokenBalance;
	ByteString tokenAddr;

	public Token20SubAccount(AccountInfoWrapper account, ByteString tokenAddr, TokenRC20Value.Builder rc20) {
		this.account = account;
		this.tokenAddr = tokenAddr;
		this.rc20 = rc20;
		if (rc20.getBalance().isEmpty()) {
			this.tokenBalance = new AtomicBigInteger(BigInteger.ZERO);
		} else {
			this.tokenBalance = new AtomicBigInteger(new BigInteger(rc20.getBalance().toByteArray()));
		}
	}

	@Override
	public synchronized void build(long blocknumber) {
		try {
			rc20.setBalance(ByteString.copyFrom(BytesHelper.bigIntegerToBytes(tokenBalance.get())));
			account.putStorage(tokenAddr.toByteArray(), rc20.build().toByteArray());
		} catch (Exception e) {
			log.error("error in put tokenrc20 to account: ", e);
		}
	}

	public BigInteger zeroSubCheckAndGet(BigInteger bi) {
		return tokenBalance.zeroSubCheckAndGet(bi);
	}

	public BigInteger getBalance() {
		return tokenBalance.get();
	}

	public BigInteger addAndGet(BigInteger bi) {
		return tokenBalance.addAndGet(bi);
	}

}
