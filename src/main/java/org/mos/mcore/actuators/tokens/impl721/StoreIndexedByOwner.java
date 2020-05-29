package org.mos.mcore.actuators.tokens.impl721;

import com.google.protobuf.ByteString;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mos.mcore.actuators.tokencontracts721.TokensContract721.TokenRC721Ownership;
import org.mos.mcore.concurrent.AccountInfoWrapper;
import org.mos.mcore.concurrent.IAccountBuilder;

@Data
@Slf4j
public class StoreIndexedByOwner implements IAccountBuilder {

	AccountInfoWrapper account;
	TokenRC721Ownership.Builder rc721;

	ByteString tokenAddr;

	public StoreIndexedByOwner(AccountInfoWrapper account, ByteString tokenAddr, ByteString tokenIndex) {
		this.account = account;
	}

	@Override
	public synchronized void build(long blocknumber) {
		try {
//			rc721.setBalance(ByteString.copyFrom(BytesHelper.bigIntegerToBytes(tokenBalance.get())));
			account.putStorage(tokenAddr.toByteArray(), rc721.build().toByteArray());
		} catch (Exception e) {
			log.error("error in put tokenrc721 to account: ", e);
		}
	}


}
