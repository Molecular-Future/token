package org.mos.mcore.actuators.tokens.impl20;

import onight.tfw.ojpa.api.ServiceSpec;
import onight.tfw.outils.conf.PropHelper;
import org.mos.mcore.odb.ODBDao;

public class Contract20Dao extends ODBDao {

	public Contract20Dao(ServiceSpec serviceSpec) {
		super(serviceSpec);
	}

	@Override
	public String getDomainName() {
		if (new PropHelper(null).get("org.mos.mcore.backend.contract20.timeslice", 1) == 1) {
			return "contract20.." + new PropHelper(null).get("org.mos.mcore.backend.contract20.slice", 1) + ".t";
		}
		// return "account.." + new PropHelper(null).get("org.mos.mcore.backend.account.slice", 64);
		return "contract20.." + new PropHelper(null).get("org.mos.mcore.backend.contract20.slice" , 1);
	}
}