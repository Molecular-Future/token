package org.mos.mcore.actuators.tokens.impl;

import onight.tfw.outils.conf.PropHelper;

import java.math.BigInteger;

public class TConfig {

	public static final PropHelper prop = new PropHelper(null);

	public static final BigInteger PRINT_RC20_COST = new BigInteger(
			prop.get("org.mos.contract.rc20.print.cost", "10000").replaceFirst("0x", ""), 16);
	public static final BigInteger PRINT_RC721_COST = new BigInteger(
			prop.get("org.mos.contract.rc721.print.cost", "10000").replaceFirst("0x", ""), 16);
}
