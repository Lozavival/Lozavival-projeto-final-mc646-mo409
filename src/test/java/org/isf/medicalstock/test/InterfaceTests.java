package org.isf.medicalstock.test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.isf.medicals.model.Medical;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.utils.exception.OHServiceException;
import org.junit.Test;

public class InterfaceTests extends TestsBase {

    @Test
	public void testValidCharge() throws Exception {
		// boolean medicalInDb = false;
		Medical medical = _setupTestMedical(false);
		Lot lot = _setupTestLot(medical, currentDate, currentDate, "1", new BigDecimal(0.1));
		Movement movement = _setupTestMovement(medical, true, internalMedWard, lot, currentDate, 1, "refNo");
		
		ArrayList<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		assertTrue(movStockInsertingManager.newMultipleChargingMovements(movements, "refNo"));
	}

	@Test
	public void testInvalidCharge() throws Exception {
		Medical medical = _setupTestMedical(false);
		Lot lot = _setupTestLot(medical, currentDate, currentDate, "1", new BigDecimal(0.1));
		Movement movement = _setupTestMovement(medical, true, internalMedWard, lot, currentDate, 1, "");
		
		ArrayList<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		assertThrows(OHServiceException.class, () -> movStockInsertingManager.newMultipleChargingMovements(movements, ""));
	}

	@Test
	public void testValidDischarge() throws Exception {
		// boolean medicalInDb = false;
		Medical medical = _setupTestMedical(false);
		Lot lot = _setupTestLot(medical, currentDate, currentDate, "1", new BigDecimal(0.1), 1);
		Movement movement = _setupTestMovement(medical, false, internalMedWard, lot, currentDate, 1, "refNo");
		
		ArrayList<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		assertTrue(movStockInsertingManager.newMultipleDischargingMovements(movements, "refNo"));
	}

	@Test
	public void testInvalidDischarge() throws Exception {
		// boolean medicalInDb = false;
		Medical medical = _setupTestMedical(false);
		Lot lot = _setupTestLot(medical, currentDate, currentDate, "1", new BigDecimal(0.1), 1);
		Movement movement = _setupTestMovement(medical, false, internalMedWard, lot, currentDate, 2, "refNo");
		
		ArrayList<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		assertThrows(OHServiceException.class, () ->movStockInsertingManager.newMultipleDischargingMovements(movements, "refNo"));
	}
}
