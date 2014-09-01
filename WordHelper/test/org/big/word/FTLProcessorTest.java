package org.big.word;

import static org.junit.Assert.*;

import java.sql.SQLException;

import org.junit.Test;

public class FTLProcessorTest {

	@Test
	public void testProcessFTL() throws SQLException {
		FTLProcessor fp=new FTLProcessor();
		fp.ProcessFTL();
	}

}
