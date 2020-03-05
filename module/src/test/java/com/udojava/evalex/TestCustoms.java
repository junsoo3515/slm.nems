package com.udojava.evalex;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.List;

import module.lib.com.udojava.evalex.Expression;
import org.junit.Test;

public class TestCustoms {

	@Test
	public void testCustomOperator() {
		Expression e = new Expression("2.1234 >> 2");

		e.addOperator(e.new Operator(">>", 30, true) {
			@Override
			public BigDecimal eval(BigDecimal v1, BigDecimal v2) {
				return v1.movePointRight(v2.toBigInteger().intValue());
			}
		});

		assertEquals("212.34", e.eval().toPlainString());
	}

//	@Test
//	public void testCustomFunction() {
//		Expression e = new Expression("2 * (AVG18(A,B,C))");
//		e.setVariable("A", new BigDecimal(0));
//		e.setVariable("B", new BigDecimal(10));
//		e.setVariable("C", new BigDecimal(10));
//		e.setVariable("C", new BigDecimal(10));
//		e.setVariable("C", new BigDecimal(10));
//		e.setVariable("C", new BigDecimal(10));
//		e.setVariable("C", new BigDecimal(10));
//
//		assertEquals("20", e.eval().toPlainString());
//	}
//
//	@Test
//	public void testCustomIFCOUNTFunction() {
//		Expression e = new Expression("( W * L * H * (SRS - IFCOUNT7(G1,C1,C2,C3,C4,C5,C6)) ) / ( ( A + B ) / 60 )");
//		e.setVariable("W", new BigDecimal(7.0));
//		e.setVariable("L", new BigDecimal(13.0));
//		e.setVariable("H", new BigDecimal(4.0));
//        e.setVariable("SRS", new BigDecimal(6));
//        e.setVariable("G1", new BigDecimal(0));
//        e.setVariable("C1", new BigDecimal(1));
//        e.setVariable("C2", new BigDecimal(1));
//        e.setVariable("C3", new BigDecimal(1));
//        e.setVariable("C4", new BigDecimal(1));
//        e.setVariable("C5", new BigDecimal(1));
//		e.setVariable("C6", new BigDecimal(1));
//        e.setVariable("A", new BigDecimal(6434.3));
//        e.setVariable("B", new BigDecimal(0));
//
//		assertEquals("20.36586", e.eval().toPlainString());
//	}
}
