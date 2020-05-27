package org.mos.mcore.actuators.tokens.action20;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import onight.oapi.scala.commons.SessionModules;
import onight.osgi.annotation.NActorProvider;
import onight.tfw.async.CompleteHandler;
import onight.tfw.ntrans.api.annotation.ActorRequire;
import onight.tfw.otransio.api.PacketHelper;
import onight.tfw.otransio.api.beans.FramePacket;
import org.mos.mcore.actuators.tokencontracts20.TokensContract20.ActionCMD;
import org.mos.mcore.actuators.tokencontracts20.TokensContract20.ActionModule;
import org.mos.mcore.actuators.tokencontracts20.TokensContract20.ReqCall20Contract;
import org.mos.mcore.actuators.tokencontracts20.TokensContract20.RespCall20Contract;
import org.mos.mcore.api.ICryptoHandler;
import org.mos.mcore.handler.ChainHandler;
import org.mos.mcore.handler.TransactionHandler;

@NActorProvider
@Slf4j
@Data
public class CallRC20Impl extends SessionModules<ReqCall20Contract> {
	@ActorRequire(name = "bc_chain", scope = "global")
	ChainHandler blockChainHelper;
	@ActorRequire(name = "bc_transaction", scope = "global")
	TransactionHandler transactionHandler;
	@ActorRequire(name = "bc_crypto", scope = "global")
	ICryptoHandler crypto;

	@Override
	public String[] getCmds() {
		return new String[] { ActionCMD.FUN.name() };
	}

	@Override
	public String getModule() {
		return ActionModule.C20.name();
	}

	@Override
	public void onPBPacket(final FramePacket pack, final ReqCall20Contract pb, final CompleteHandler handler) {
		RespCall20Contract.Builder oret = RespCall20Contract.newBuilder();
		try {
			oret.setDatas(pb.getData().toByteString());
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