package org.mos.mcore.actuators.tokens.action721;

import com.google.protobuf.ByteString;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import onight.oapi.scala.commons.SessionModules;
import onight.osgi.annotation.NActorProvider;
import onight.tfw.async.CompleteHandler;
import onight.tfw.ntrans.api.annotation.ActorRequire;
import onight.tfw.otransio.api.PacketHelper;
import onight.tfw.otransio.api.beans.FramePacket;
import org.mos.mcore.actuators.tokencontracts20.TokensContract20.TokenRC20Info;
import org.mos.mcore.actuators.tokencontracts721.TokensContract721.*;
import org.mos.mcore.api.ICryptoHandler;
import org.mos.mcore.concurrent.AccountInfoWrapper;
import org.mos.mcore.handler.AccountHandler;
import org.mos.mcore.handler.ChainHandler;
import org.mos.mcore.handler.MCoreServices;
import org.mos.mcore.handler.TransactionHandler;
import org.mos.mcore.model.Account.AccountInfo;

import java.nio.ByteBuffer;

@NActorProvider
@Slf4j
@Data
public class QueryRC721ByOwnerRange extends SessionModules<ReqQueryRC721TokenByOwnerRange> {
	@ActorRequire(name = "bc_chain", scope = "global")
	ChainHandler blockChainHelper;
	@ActorRequire(name = "bc_transaction", scope = "global")
	TransactionHandler transactionHandler;
	@ActorRequire(name = "bc_crypto", scope = "global")
	ICryptoHandler crypto;
	@ActorRequire(name = "bc_account", scope = "global")
	AccountHandler accountHelper;

	@ActorRequire(name = "MCoreServices", scope = "global")
	MCoreServices mcore;

	@Override
	public String[] getCmds() {
		return new String[] { Action721CMD.QOR.name() };
	}

	@Override
	public String getModule() {
		return Action721Module.C21.name();
	}

	@Override
	public void onPBPacket(final FramePacket pack, final ReqQueryRC721TokenByOwnerRange pb, final CompleteHandler handler) {
		RespQueryRC721Tokens.Builder oret = RespQueryRC721Tokens.newBuilder();
		try {
			ByteString tokenAddress = pb.getTokenAddress();
			AccountInfo.Builder tokenAcct = accountHelper.getAccount(tokenAddress);
			if (tokenAcct == null) {
				oret.setRetCode(-1).setRetMessage("contract not found");
			} else {
				TokenRC20Info tokeninfo = TokenRC20Info.parseFrom(tokenAcct.getExtData());
				oret.setName(tokeninfo.getName()).setSymbol(tokeninfo.getSymbol()).setTotalSupply(tokeninfo.getTotalSupply());

				AccountInfoWrapper tokenAW = new AccountInfoWrapper(accountHelper.getAccount(pb.getTokenOwner()));
				tokenAW.loadStorageTrie(mcore.getStateTrie());
				
				for(int index=pb.getStartIndex();index<=pb.getEndIndex();index++) {
					byte[] hashindex = ByteBuffer.allocate(8).putLong(index).array();
					ByteString tokenByIndexAddr = pb.getTokenAddress().concat(ByteString.copyFrom(hashindex));
					byte tokendata[] = tokenAW.getStorage(tokenByIndexAddr.toByteArray());
					if (tokendata != null) {
						ByteString tokenid = ByteString.copyFrom(tokendata);
						ByteString rc721vAddr = ByteString
								.copyFrom(mcore.getCrypto().sha3(tokenAddress.concat(tokenid).toByteArray()), 0, 20);
						AccountInfo.Builder acctv = accountHelper.getAccount(rc721vAddr);
						if (acctv != null) {
							TokenRC721Value tokenv = TokenRC721Value.parseFrom(acctv.getExtData());
							oret.addValues(tokenv);
						} else {
							oret.addValues(TokenRC721Value.newBuilder().build());
						}
					} else {
						oret.addValues(TokenRC721Value.newBuilder().build());
					}
				}
			}
			oret.setRetCode(1);
		} catch (Exception e) {
			log.debug("error in create tx:", e);
			oret.clear();
			oret.setRetCode(-1);
			oret.setRetMessage(e.getMessage());
			handler.onFinished(PacketHelper.toPBReturn(pack, oret.build()));
			return;
		}
		handler.onFinished(PacketHelper.toPBReturn(pack, oret.build()));
	}
}