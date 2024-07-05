/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isf.medicalstock.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

import org.assertj.core.api.Condition;
import org.isf.OHCoreTestCase;
import org.isf.generaldata.GeneralData;
import org.isf.medicals.model.Medical;
import org.isf.medicals.service.MedicalsIoOperationRepository;
import org.isf.medicals.test.TestMedical;
import org.isf.medicalstock.manager.MovBrowserManager;
import org.isf.medicalstock.manager.MovStockInsertingManager;
import org.isf.medicalstock.model.Lot;
import org.isf.medicalstock.model.Movement;
import org.isf.medicalstock.service.LotIoOperationRepository;
import org.isf.medicalstock.service.MedicalStockIoOperations;
import org.isf.medicalstock.service.MedicalStockIoOperations.MovementOrder;
import org.isf.medicalstock.service.MovementIoOperationRepository;
import org.isf.medicalstockward.model.MedicalWard;
import org.isf.medicalstockward.service.MedicalStockWardIoOperationRepository;
import org.isf.medicalstockward.service.MovementWardIoOperationRepository;
import org.isf.medstockmovtype.model.MovementType;
import org.isf.medstockmovtype.service.MedicalStockMovementTypeIoOperationRepository;
import org.isf.medstockmovtype.test.TestMovementType;
import org.isf.medtype.model.MedicalType;
import org.isf.medtype.service.MedicalTypeIoOperationRepository;
import org.isf.medtype.test.TestMedicalType;
import org.isf.supplier.model.Supplier;
import org.isf.supplier.service.SupplierIoOperationRepository;
import org.isf.supplier.test.TestSupplier;
import org.isf.utils.exception.OHDataValidationException;
import org.isf.utils.exception.OHException;
import org.isf.utils.exception.OHServiceException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.isf.ward.test.TestWardType;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.transaction.annotation.Transactional;

@Transactional
// @RunWith(Parameterized.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public class Tests /*extends OHCoreTestCase*/ {

	@ClassRule
	public static final SpringClassRule scr = new SpringClassRule();

	@Rule
	public final SpringMethodRule smr = new SpringMethodRule();

	private static TestLot testLot;
	private static TestMovement testMovement;
	private static TestMedical testMedical;
	private static TestMedicalType testMedicalType;
	private static TestMovementType testMovementType;
	private static TestWard testWard;
	private static TestSupplier testSupplier;

	private static Ward internalMedWard;
	private static Ward maternityWard;
	private static Ward nurseryWard;
	private static Ward surgeryWard;

	private static MedicalType medicalType;
	private static Medical medInStock;
	private static Medical medOutOfStock;

	@Autowired
	MedicalStockIoOperations medicalStockIoOperation;
	@Autowired
	MovBrowserManager movBrowserManager;
	@Autowired
	MovStockInsertingManager movStockInsertingManager;
	@Autowired
	LotIoOperationRepository lotIoOperationRepository;
	@Autowired
	MedicalStockWardIoOperationRepository medicalStockWardIoOperationRepository;
	@Autowired
	MovementWardIoOperationRepository movementWardIoOperationRepository;
	@Autowired
	MedicalsIoOperationRepository medicalsIoOperationRepository;
	@Autowired
	MedicalTypeIoOperationRepository medicalTypeIoOperationRepository;
	@Autowired
	WardIoOperationRepository wardIoOperationRepository;
	@Autowired
	MovementIoOperationRepository movementIoOperationRepository;
	@Autowired
	MedicalStockMovementTypeIoOperationRepository medicalStockMovementTypeIoOperationRepository;
	@Autowired
	SupplierIoOperationRepository supplierIoOperationRepository;
	@Autowired
	ApplicationEventPublisher applicationEventPublisher;

	// public Tests(boolean in, boolean out, boolean toward) {
	// 	GeneralData.AUTOMATICLOT_IN = in;
	// 	GeneralData.AUTOMATICLOT_OUT = out;
	// 	GeneralData.AUTOMATICLOTWARD_TOWARD = toward;
	// }

	@BeforeClass
	public static void setUpClass() throws OHException {
		testLot = new TestLot();
		testMovement = new TestMovement();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testMovementType = new TestMovementType();
		testWard = new TestWard();
		testSupplier = new TestSupplier();

		// Create test wards
		internalMedWard = testWard.setup(TestWardType.INTERNAL_MEDICINE);
		maternityWard = testWard.setup(TestWardType.MATERNITY);
		nurseryWard = testWard.setup(TestWardType.NURSERY);
		surgeryWard = testWard.setup(TestWardType.SURGERY);

		// Create test medicals
		medicalType = testMedicalType.setup();
		medInStock = testMedical.setup(medicalType, false);
		medOutOfStock = testMedical.setup(medicalType, true);

	}

	// @Before
	// public void setUp() throws OHException {
	// 	cleanH2InMemoryDb();
	// }

	@AfterClass
	public static void tearDownClass() {
		testLot = null;
		testMovement = null;
		testMedical = null;
		testMedicalType = null;
		testMovementType = null;
		testWard = null;
		testSupplier = null;
		internalMedWard = null;
		maternityWard = null;
		nurseryWard = null;
		surgeryWard = null;
		medicalType = null;
		medInStock = null;
		medOutOfStock = null;
	}

	/*
	// @Parameterized.Parameters(name = "Test with AUTOMATICLOT_IN={0}, AUTOMATICLOT_OUT={1}, AUTOMATICLOTWARD_TOWARD={2}")
	// public static Collection<Object[]> automaticlot() {
	// 	return Arrays.asList(new Object[][] {
	// 			{ false, false, false },
	// 			{ false, false, true },
	// 			{ false, true, false },
	// 			{ false, true, true },
	// 			{ true, false, false },
	// 			{ true, false, true },
	// 			{ true, true, false },
	// 			{ true, true, true }
	// 	});
	// }

	// @Test public void TC1() throws OHServiceException {
    //     GregorianCalendar date = new GregorianCalendar(); // pega a data atual
    //     date.add(GregorianCalendar.SECOND, -1); // subtrai 1 segundo para a data ficar no passado

    //     Supplier supplier = new Supplier();
    //     String referenceNo = "1";
    //     Medical medical = new Medical(1);
    //     int quantity = 1;

    //     Lot lot;
    //     // Data de expiração atual e igual à data de preparação
    //     GregorianCalendar lotPrepDate = new GregorianCalendar();
    //     GregorianCalendar lotExpDate = lotPrepDate;
    //     // 0 < length < 50
    //     String lotNo = "1";
    //     // Custo > 0
    //     BigDecimal lotCost = new BigDecimal(0.1);
    //     lot = new Lot(medical, lotNo, lotPrepDate, lotExpDate, lotCost);

    //     // Instanciar os movements
    //     //	public Movement(Medical aMedical,MovementType aType,Ward aWard,Lot aLot,GregorianCalendar aDate,int aQuantity,Supplier aSupplier, String aRefNo){
    //     Movement lastMov = new Movement(medical, movementType, null, lot, date, quantity, supplier, referenceNo);
    //     Movement mov = new Movement(medical, movementType, null, lot, date, quantity, supplier, referenceNo);
    //     assertTrue(movStockInsertingManager.newMultipleChargingMovements(Arrays.asList(lastMov, mov), referenceNo));
	// }
	*/

	@Test
	public void testValidMovement() throws Exception {
		String lotCode = _setupTestLot(medInStock, new GregorianCalendar(), new GregorianCalendar(), "1", new BigDecimal(0.1));
		Lot lot = lotIoOperationRepository.findOne(lotCode);
		Movement movement = _setupTestMovement(medInStock, true, internalMedWard, lot, new GregorianCalendar(), 1, "refNo");
		// Movement movement = movementIoOperationRepository.findOne(code);
		ArrayList<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		assertTrue(movStockInsertingManager.newMultipleChargingMovements(movements, "refNo"));
	}

	@Test
	public void testInvalidMovement() throws Exception {
		String lotCode = _setupTestLot(medInStock, new GregorianCalendar(), new GregorianCalendar(), "1", new BigDecimal(0.1));
		Lot lot = lotIoOperationRepository.findOne(lotCode);
		Movement movement = _setupTestMovement(medInStock, true, internalMedWard, lot, new GregorianCalendar(2024, 1, 1), 1, "refNo");
		// Movement movement = movementIoOperationRepository.findOne(code);
		ArrayList<Movement> movements = new ArrayList<>(1);
		movements.add(movement);
		// assertThrows(OHServiceException.class, () -> movStockInsertingManager.newMultipleChargingMovements(movements, null));
		assertTrue(movStockInsertingManager.newMultipleChargingMovements(movements, "refNo"));
	}

	private String _setupTestLot(Medical medical, GregorianCalendar prepDate, GregorianCalendar dueDate, String lotNo, BigDecimal cost) throws OHException {
		Lot lot = testLot.setup(medical, lotNo, prepDate, dueDate, cost);

		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		lotIoOperationRepository.saveAndFlush(lot);
		return lot.getCode();
	}

	private Movement _setupTestMovement(Medical medical, boolean charge, Ward ward, Lot lot, GregorianCalendar date, int quantity, String refNo) throws OHException {
		MovementType movementType = testMovementType.setup(charge);
		Supplier supplier = testSupplier.setup();
		Movement movement = testMovement.setup(medical, movementType, ward, lot, date, quantity, supplier, refNo);

		supplierIoOperationRepository.saveAndFlush(supplier);
		wardIoOperationRepository.saveAndFlush(ward);
		medicalStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		// movementIoOperationRepository.saveAndFlush(movement);
		return movement;
	}

}
