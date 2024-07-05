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

import java.math.BigDecimal;
import java.util.GregorianCalendar;

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
import org.isf.medicalstock.service.MovementIoOperationRepository;
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
import org.isf.utils.exception.OHException;
import org.isf.ward.model.Ward;
import org.isf.ward.service.WardIoOperationRepository;
import org.isf.ward.test.TestWard;
import org.isf.ward.test.TestWardType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
public abstract class TestsBase extends OHCoreTestCase {

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

	protected static GregorianCalendar currentDate;
	protected static GregorianCalendar pastDate;
	protected static GregorianCalendar pastDate2;
	protected static GregorianCalendar futureDate;
	protected static GregorianCalendar futureDate2;

	protected static Ward internalMedWard;
	protected static Ward maternityWard;
	protected static Ward nurseryWard;
	protected static Ward surgeryWard;

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

	public TestsBase() {
		GeneralData.AUTOMATICLOT_IN = false;
		GeneralData.AUTOMATICLOT_OUT = false;
		GeneralData.AUTOMATICLOTWARD_TOWARD = false;
	}

	@BeforeClass
	public static void setUpClass() throws OHException {
		testLot = new TestLot();
		testMovement = new TestMovement();
		testMedical = new TestMedical();
		testMedicalType = new TestMedicalType();
		testMovementType = new TestMovementType();
		testWard = new TestWard();
		testSupplier = new TestSupplier();

		// Create test dates
		currentDate = new GregorianCalendar();
		pastDate = new GregorianCalendar();
		pastDate.add(GregorianCalendar.SECOND, -1);
		pastDate2 = new GregorianCalendar();
		pastDate2.add(GregorianCalendar.SECOND, -2);
		futureDate = new GregorianCalendar();
		futureDate.add(GregorianCalendar.SECOND, 1);
		futureDate2 = new GregorianCalendar();
		futureDate2.add(GregorianCalendar.SECOND, 2);

		// Create test wards
		internalMedWard = testWard.setup(TestWardType.INTERNAL_MEDICINE);
		maternityWard = testWard.setup(TestWardType.MATERNITY);
		nurseryWard = testWard.setup(TestWardType.NURSERY);
		surgeryWard = testWard.setup(TestWardType.SURGERY);
	}

	@BeforeEach
	public void setUp() throws OHException {
		cleanH2InMemoryDb();
	}

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
	}

	protected Medical _setupTestMedical(boolean outOfStock) throws OHException {
		MedicalType medicalType = testMedicalType.setup();
		Medical medical = testMedical.setup(medicalType, outOfStock);
		medicalTypeIoOperationRepository.saveAndFlush(medicalType);
		medicalsIoOperationRepository.saveAndFlush(medical);
		return medical;
	}

	protected Lot _setupTestLot(Medical medical, GregorianCalendar prepDate, GregorianCalendar dueDate, String lotNo, BigDecimal cost, int quantity) throws OHException {
		Lot lot = testLot.setup(medical, lotNo, prepDate, dueDate, cost, quantity);
		lotIoOperationRepository.saveAndFlush(lot);
		return lot;
	}

	// In case quantity is not needed (ie. charging)
	protected Lot _setupTestLot(Medical medical, GregorianCalendar prepDate, GregorianCalendar dueDate, String lotNo, BigDecimal cost) throws OHException {
		return _setupTestLot(medical, prepDate, dueDate, lotNo, cost, 0);
	}

	protected Movement _setupTestMovement(Medical medical, boolean charge, Ward ward, Lot lot, GregorianCalendar date, int quantity, String refNo) throws OHException {
		MovementType movementType = testMovementType.setup(charge);
		Supplier supplier = testSupplier.setup();
		Movement movement = testMovement.setup(medical, movementType, ward, lot, date, quantity, supplier, refNo);
		supplierIoOperationRepository.saveAndFlush(supplier);
		wardIoOperationRepository.saveAndFlush(ward);
		medicalStockMovementTypeIoOperationRepository.saveAndFlush(movementType);
		return movement;
	}

}